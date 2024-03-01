package com.ebicep.warlords.player.general;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.FutureMessage;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.SpecPrestigeEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsGiveExperienceEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.option.ExperienceGainOption;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.onslaught.OnslaughtOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.ingame.PlayerStatisticsMinute;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ExperienceManager {

    public static final Map<Integer, Long> LEVEL_TO_EXPERIENCE;
    public static final Map<Long, Integer> EXPERIENCE_TO_LEVEL;
    public static final DecimalFormat EXPERIENCE_DECIMAL_FORMAT = new DecimalFormat("#,###.#");
    public static final HashMap<UUID, ExperienceSummary> CACHED_PLAYER_EXP_SUMMARY = new HashMap<>();
    public static final int LEVEL_TO_PRESTIGE = 100;
    private static final Map<String, int[]> awardOrder = new LinkedHashMap<>() {{
        put("wins", new int[]{1000, 750, 500});
        put("losses", new int[]{200, 150, 100});
        put("kills", new int[]{850, 600, 350});
        put("assists", new int[]{850, 600, 350});
        put("deaths", new int[]{200, 150, 100});
        put("dhp", new int[]{1000, 750, 500});
        put("dhp_per_game", new int[]{1000, 750, 500});
        put("damage", new int[]{850, 600, 350});
        put("healing", new int[]{850, 600, 350});
        put("absorbed", new int[]{850, 600, 350});
        put("flags_captured", new int[]{600, 400, 200});
        put("flags_returned", new int[]{600, 400, 200});
    }};
    private static final Map<Integer, TextColor> CACHED_PRESTIGE_COLORS = new HashMap<>();

    static {
        //caching all levels/experience
        Map<Integer, Long> levelExperienceNew = new HashMap<>();
        Map<Long, Integer> experienceLevelNew = new HashMap<>();
        for (int i = 0; i < 501; i++) {
            long exp = (long) calculateExpFromLevel(i);
            levelExperienceNew.put(i, exp);
            experienceLevelNew.put(exp, i);
        }

        LEVEL_TO_EXPERIENCE = Collections.unmodifiableMap(levelExperienceNew);
        EXPERIENCE_TO_LEVEL = Collections.unmodifiableMap(experienceLevelNew);

        EXPERIENCE_DECIMAL_FORMAT.setDecimalSeparatorAlwaysShown(false);
    }

    public static void awardWeeklyExperience(Document weeklyDocument) {
        if (DatabaseManager.playerService == null) {
            ChatUtils.MessageType.PLAYER_SERVICE.sendErrorMessage("WARNING - Could not give weekly experience bonus - playerService is null");
            return;
        }

        HashMap<String, AwardSummary> playerAwardSummary = new HashMap<>();

        awardOrder.forEach((key, rewards) -> {
            String name = weeklyDocument.getEmbedded(Arrays.asList(key, "name"), String.class);
            List<Document> top = weeklyDocument.getEmbedded(Arrays.asList(key, "top"), new ArrayList<>());
            for (int i = 0; i < top.size(); i++) {
                Document topDocument = top.get(i);
                String[] uuids = topDocument.getString("uuids").split(",");
                for (String uuid : uuids) {
                    int experienceGain = rewards[i];
                    playerAwardSummary.putIfAbsent(uuid, new AwardSummary());
                    AwardSummary awardSummary = playerAwardSummary.get(uuid);
                    awardSummary.getMessages()
                                .add(Component.textOfChildren(
                                        Component.text("#" + (i + 1) + ". ", NamedTextColor.YELLOW),
                                        Component.text(name, NamedTextColor.AQUA),
                                        Component.text(": ", NamedTextColor.WHITE),
                                        Component.text("+", NamedTextColor.DARK_GRAY),
                                        Component.text(experienceGain, NamedTextColor.DARK_AQUA),
                                        Component.text(" Universal Experience", NamedTextColor.GOLD)
                                ));
                    awardSummary.addTotalExperienceGain(experienceGain);
                }
            }
        });

        ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("---------------------------------------------------");
        ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("Giving players weekly experience bonuses");
        ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("---------------------------------------------------");
        playerAwardSummary.forEach((s, awardSummary) -> {
            long totalExperienceGain = awardSummary.getTotalExperienceGain();

            awardSummary.addMessage(Component.textOfChildren(
                    Component.text("Total Experience Gain", NamedTextColor.GOLD),
                    Component.text(": ", NamedTextColor.WHITE),
                    Component.text("+", NamedTextColor.DARK_GRAY),
                    Component.text(totalExperienceGain, NamedTextColor.DARK_AQUA)
            ));
            awardSummary.addMessage(Component.text("---------------------------------------------------", NamedTextColor.BLUE));

            Warlords.newChain()
                    .asyncFirst(() -> DatabaseManager.playerService.findByUUID(UUID.fromString(s)))
                    .syncLast(databasePlayer -> {
                        databasePlayer.setExperience(databasePlayer.getExperience() + totalExperienceGain);
                        databasePlayer.addFutureMessage(FutureMessage.create(awardSummary.getMessages(), true));
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    }).execute();
        });
    }

    public static ExperienceSummary getExpFromGameStats(WarlordsEntity warlordsPlayer, boolean recalculate) {
        if (!recalculate && CACHED_PLAYER_EXP_SUMMARY.containsKey(warlordsPlayer.getUuid()) && CACHED_PLAYER_EXP_SUMMARY.get(
                warlordsPlayer.getUuid()) != null) {
            return CACHED_PLAYER_EXP_SUMMARY.get(warlordsPlayer.getUuid());
        }

        ExperienceSummary experienceSummary = new ExperienceSummary();

        LinkedHashMap<String, Long> universalExpGain = new LinkedHashMap<>();

        Game game = warlordsPlayer.getGame();
        if (GameMode.isPvE(game.getGameMode())) {
            ExperienceGainOption experienceGainOption = game
                    .getOptions()
                    .stream()
                    .filter(ExperienceGainOption.class::isInstance)
                    .map(ExperienceGainOption.class::cast)
                    .findAny()
                    .orElse(null);
            PveOption pveOption = game
                    .getOptions()
                    .stream()
                    .filter(option -> option instanceof PveOption)
                    .map(PveOption.class::cast)
                    .findAny()
                    .orElse(null);
            if (experienceGainOption != null && pveOption != null) {
                DifficultyIndex difficulty = pveOption.getDifficulty();
                Pair<String, Long> perBonus = null;
                Pair<String, Long> winBonus = null;
                if (pveOption instanceof WaveDefenseOption waveDefenseOption) {
                    int maxWaves = waveDefenseOption.getMaxWave();
                    int wavesCleared = Math.min(waveDefenseOption.getWavesCleared(), maxWaves);
                    perBonus = new Pair<>("Waves Cleared", (long) wavesCleared * experienceGainOption.getPlayerExpPer());
                    if (wavesCleared == maxWaves) {
                        winBonus = new Pair<>("Win Bonus",
                                (long) (experienceGainOption.getPlayerExpGameWinBonus() * difficulty.getRewardsMultiplier())
                        );
                    }
                } else if (pveOption instanceof OnslaughtOption) {
                    perBonus = new Pair<>("Minutes Elapsed", pveOption.getTicksElapsed() / 20 / 60 * experienceGainOption.getPlayerExpPer());
                }
                if (experienceGainOption.getPlayerExpPer() != 0 && perBonus != null) {
                    universalExpGain.put(perBonus.getA(), perBonus.getB());
                }
                if (experienceGainOption.getPlayerExpGameWinBonus() != 0 && winBonus != null) {
                    universalExpGain.put(winBonus.getA(), winBonus.getB());
                }
                if (experienceGainOption.getPlayerExpPerXSec() != null) {
                    game.getOptions()
                        .stream()
                        .filter(option -> option instanceof RecordTimeElapsedOption)
                        .map(RecordTimeElapsedOption.class::cast)
                        .findAny()
                        .ifPresent(recordTimeElapsedOption -> {
                            int secondsElapsed = recordTimeElapsedOption.getTicksElapsed() / 20;
                            Pair<Long, Integer> expPerXSec = experienceGainOption.getPlayerExpPerXSec();
                            universalExpGain.put("Seconds Survived",
                                    (long) (secondsElapsed / expPerXSec.getB() * expPerXSec.getA() * difficulty.getRewardsMultiplier())
                            );
                        });
                }
            }
            Bukkit.getPluginManager().callEvent(new WarlordsGiveExperienceEvent(warlordsPlayer, universalExpGain));

            experienceSummary.getSpecExpGainSummary().put(warlordsPlayer.getSpecClass(), universalExpGain);
        } else {
            boolean isCompGame = game.getAddons().contains(GameAddon.PRIVATE_GAME);
            float multiplier = 1.5f;
            //pubs
//            if (!isCompGame) {
//                multiplier *= .1;
//            }
            //duels
            if (game.getGameMode() == GameMode.DUEL) {
                multiplier *= .1;
            }

            boolean won = game.getPoints(warlordsPlayer.getTeam()) > game.getPoints(warlordsPlayer.getTeam().enemy());
            long winLossExp = won ? 500 : 250;
            universalExpGain.put(won ? "Win" : "Loss", (long) (winLossExp * multiplier));

            for (Map.Entry<Specializations, PlayerStatisticsMinute> entry : warlordsPlayer.getSpecMinuteStats().entrySet()) {
                Specializations specializations = entry.getKey();
                PlayerStatisticsMinute entries = entry.getValue();
                PlayerStatisticsMinute.Entry total = entries.total();

                LinkedHashMap<String, Long> specExpGain = new LinkedHashMap<>();

                long kaExp = 5L * (total.getKills() + total.getAssists());

                double damageMultiplier;
                double healingMultiplier;
                double absorbedMultiplier;
                if (specializations.specType == SpecType.DAMAGE) {
                    damageMultiplier = .80;
                    healingMultiplier = .10;
                    absorbedMultiplier = .10;
                } else if (specializations.specType == SpecType.HEALER) {
                    damageMultiplier = .275;
                    healingMultiplier = .65;
                    absorbedMultiplier = .75;
                } else { //tank
                    damageMultiplier = .575;
                    healingMultiplier = .1;
                    absorbedMultiplier = .325;
                }
                double calculatedDHP = total.getDamage() * damageMultiplier +
                        total.getHealing() * healingMultiplier +
                        total.getAbsorbed() * absorbedMultiplier;
                long dhpExp = (long) (calculatedDHP / 500L);
                long flagCapExp = total.getFlagsCaptured() * 150L;
                long flagRetExp = total.getFlagsReturned() * 50L;

                if (kaExp != 0) {
                    specExpGain.put("Kills/Assists", (long) (kaExp * multiplier));
                }
                if (dhpExp != 0) {
                    specExpGain.put("DHP", (long) (dhpExp * multiplier));
                }
                if (flagCapExp != 0) {
                    specExpGain.put("Flags Captured", (long) (flagCapExp * multiplier));
                }
                if (flagRetExp != 0) {
                    specExpGain.put("Flags Returned", (long) (flagRetExp * multiplier));
                }

                experienceSummary.getSpecExpGainSummary().put(specializations, specExpGain);
            }

            DatabaseManager.getPlayer(warlordsPlayer.getUuid(),
                    PlayersCollections.DAILY,
                    databasePlayer -> {
                        int plays = isCompGame ? databasePlayer.getCompStats().getPlays() : databasePlayer.getPubStats().getPlays();
                        switch (plays) {
                            case 0 -> universalExpGain.put("First Game of the Day", 500L / (isCompGame ? 1 : 10));
                            case 1 -> universalExpGain.put("Second Game of the Day", 250L / (isCompGame ? 1 : 10));
                            case 2 -> universalExpGain.put("Third Game of the Day", 100L / (isCompGame ? 1 : 10));
                        }
                    },
                    () -> {
                        ChatUtils.MessageType.WARLORDS.sendErrorMessage("ERROR: Could not find player: " + warlordsPlayer.getName() + " during experience calculation");
                    }
            );
        }

        experienceSummary.getUniversalExpGainSummary().putAll(universalExpGain);

        CACHED_PLAYER_EXP_SUMMARY.put(warlordsPlayer.getUuid(), experienceSummary);
        return experienceSummary;
    }

    public static long getSpecExpFromSummary(LinkedHashMap<String, Long> expSummary) {
        return expSummary.values().stream().mapToLong(Long::longValue).sum()
                - expSummary.getOrDefault("First Game of the Day", 0L)
                - expSummary.getOrDefault("Second Game of the Day", 0L)
                - expSummary.getOrDefault("Third Game of the Day", 0L);
    }

    public static int getLevelForClass(UUID uuid, Classes classes) {
        return (int) calculateLevelFromExp(getExperienceForClass(uuid, classes));
    }

    public static double calculateLevelFromExp(long exp) {
        return Math.sqrt(exp / 25.0);
    }

    public static long getExperienceForClass(UUID uuid, Classes classes) {
        AtomicLong experience = new AtomicLong(0);
        DatabaseManager.getPlayer(uuid, databasePlayer -> experience.set(databasePlayer.getStat(classes, Stats::getExperience, Long::sum, 0L)));
        return experience.get();
    }

    public static long getExperienceForSpec(UUID uuid, Specializations spec) {
        return getExperienceFromSpec(uuid, spec);
    }

    private static long getExperienceFromSpec(UUID uuid, Specializations specializations) {
        AtomicLong experience = new AtomicLong(0);
        DatabaseManager.getPlayer(uuid, databasePlayer -> experience.set(databasePlayer.getSpec(specializations).getExperience()));
        return experience.get();
    }

    public static int getLevelFromExp(long experience) {
        return (int) calculateLevelFromExp(experience);
    }

    public static String getLevelString(int level) {
        return level < 10 ? "0" + level : String.valueOf(level);
    }

    public static List<Component> getProgressString(long currentExperience, int nextLevel) {
        TextComponent progress = Component.text("Progress to Level " + nextLevel + ": ", NamedTextColor.GRAY);
        return getProgressString(currentExperience, nextLevel, progress);
    }

    private static List<Component> getProgressString(long currentExperience, int nextLevel, TextComponent progress) {
        Long exp = LEVEL_TO_EXPERIENCE.get(nextLevel);
        Long nextExp = LEVEL_TO_EXPERIENCE.get(nextLevel - 1);
        if (exp == null || nextExp == null) {
            return Collections.singletonList(progress.append(Component.text("Report this!")));
        }
        long experience = currentExperience - nextExp;
        long experienceNeeded = exp - nextExp;
        double progressPercentage = (double) experience / experienceNeeded * 100;

        int greenBars = (int) Math.round(progressPercentage * 20 / 100);
        return Arrays.asList(
                progress.append(Component.text(NumberFormat.formatOptionalTenths(progressPercentage) + "%", NamedTextColor.YELLOW)),
                Component.text("-".repeat(Math.max(0, greenBars)), NamedTextColor.GREEN)
                         .append(Component.text("-".repeat(Math.max(0, 20 - greenBars)) + " ", NamedTextColor.WHITE))
                         .append(Component.text(EXPERIENCE_DECIMAL_FORMAT.format(experience), NamedTextColor.YELLOW))
                         .append(Component.text("/", NamedTextColor.GOLD))
                         .append(Component.text(NumberFormat.getSimplifiedNumber(experienceNeeded), NamedTextColor.YELLOW))
        );
    }

    public static List<Component> getProgressStringWithPrestige(long currentExperience, int nextLevel, int currentPrestige) {
        TextComponent.Builder progress = Component.text();
        if (nextLevel == 100) {
            progress.append(Component.text("Progress to ", NamedTextColor.GRAY))
                    .append(Component.text("PRESTIGE", getPrestigeColor(currentPrestige + 1)))
                    .append(Component.text(": ", NamedTextColor.GRAY));
        } else {
            progress.append(Component.text("Progress to Level " + nextLevel, NamedTextColor.GRAY))
                    .append(Component.text(": ", NamedTextColor.GRAY));
        }
        return getProgressString(currentExperience, nextLevel, progress.build());
    }

    public static TextColor getPrestigeColor(int prestigeLevel) {
        return CACHED_PRESTIGE_COLORS.computeIfAbsent(prestigeLevel, integer -> {
            float hue = (float) prestigeLevel / 100.0f;
            float saturation = 1f;
            float brightness = 1f;
            return TextColor.color(Color.HSBtoRGB(hue, saturation, brightness));
        });
    }

    public static TextComponent getPrestigeLevelString(UUID uuid, Specializations spec) {
        if (DatabaseManager.playerService == null) {
            return Component.text("[-]", getPrestigeColor(0));
        }
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid);
        if (databasePlayer == null) {
            return Component.text("[-]", getPrestigeColor(0));
        }
        int prestigeLevel = databasePlayer.getSpec(spec).getPrestige();
        return Component.text("[", NamedTextColor.DARK_GRAY)
                        .append(Component.text(prestigeLevel, getPrestigeColor(prestigeLevel)))
                        .append(Component.text("]", NamedTextColor.DARK_GRAY));
    }

    public static TextComponent getPrestigeLevelString(int prestigeLevel) {
        return Component.text("[", NamedTextColor.DARK_GRAY)
                        .append(Component.text(prestigeLevel, getPrestigeColor(prestigeLevel)))
                        .append(Component.text("]", NamedTextColor.DARK_GRAY));
    }

    public static double calculateExpFromLevel(int level) {
        return Math.pow(level, 2) * 25;
    }

    public static void giveExperienceBar(Player player) {
        //long experience = warlordsPlayersDatabase.getCollection("Players_Information_Test").find().filter(eq("uuid", player.getUniqueId().toString())).first().getLong("experience");
        long experience = getUniversalLevel(player.getUniqueId());
        int level = (int) calculateLevelFromExp(experience);
        player.setLevel(level);
        Long exp = LEVEL_TO_EXPERIENCE.get(level);
        Long nextExp = LEVEL_TO_EXPERIENCE.get(level + 1);
        if (exp != null && nextExp != null) {
            player.setExp((float) (experience - exp) / (nextExp - exp));
        }
    }

    public static long getUniversalLevel(UUID uuid) {
        if (DatabaseManager.playerService == null) {
            return 0;
        }
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid);
        return databasePlayer == null ? 0L : databasePlayer.getExperience();
    }

    public static void giveLevelUpMessage(Player player, long expBefore, long expAfter) {
        int levelBefore = (int) calculateLevelFromExp(expBefore);
        int levelAfter = (int) calculateLevelFromExp(expAfter);
        if (levelBefore != levelAfter) {
            ChatUtils.sendMessage(player,
                    true,
                    Component.text().color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.BOLD)
                             .append(Component.text("   ", NamedTextColor.GREEN, TextDecoration.OBFUSCATED))
                             .append(Component.text("LEVEL UP! ", NamedTextColor.AQUA))
                             .append(Component.text("["))
                             .append(Component.text(levelBefore, NamedTextColor.GRAY))
                             .append(Component.text("]"))
                             .append(Component.text(" > ", NamedTextColor.GREEN))
                             .append(Component.text("["))
                             .append(Component.text(levelAfter, NamedTextColor.GRAY))
                             .append(Component.text("]"))
                             .append(Component.text("   ", NamedTextColor.GREEN, TextDecoration.OBFUSCATED))
                             .build()
            );
        }
    }

    public static void checkForPrestige(Player player, UUID uuid, DatabasePlayer databasePlayer) {
        //check all spec prestige
        for (Specializations value : Specializations.VALUES) {
            int level = getLevelForSpec(uuid, value);
            if (level < LEVEL_TO_PRESTIGE) {
                continue;
            }
            databasePlayer.getSpec(value).addPrestige();
            int prestige = databasePlayer.getSpec(value).getPrestige();
            EffectUtils.playFirework(
                    player.getLocation(),
                    FireworkEffect.builder()
                                  .with(FireworkEffect.Type.BALL)
                                  .withColor(org.bukkit.Color.fromRGB(getPrestigeColor(prestige).value()))
                                  .build()
            );
            player.showTitle(Title.title(
                    Component.textOfChildren(
                            Component.text("###", NamedTextColor.WHITE, TextDecoration.OBFUSCATED),
                            Component.text(" Prestige " + value.name + " ", NamedTextColor.GOLD, TextDecoration.BOLD),
                            Component.text("###", NamedTextColor.WHITE, TextDecoration.OBFUSCATED)
                    ),
                    Component.text(prestige - 1, getPrestigeColor(prestige - 1))
                             .append(Component.text(" > ", NamedTextColor.GRAY))
                             .append(Component.text(prestige, getPrestigeColor(prestige))),
                    Title.Times.times(Ticks.duration(20), Ticks.duration(140), Ticks.duration(20))
            ));
            //sumSmash is now prestige level 5 in Pyromancer!
            Bukkit.broadcast(Permissions.getPrefixWithColor(player, false)
                                        .append(Component.text(player.getName()))
                                        .append(Component.text(" is now prestige level ", NamedTextColor.GRAY))
                                        .append(Component.text(prestige, getPrestigeColor(prestige)))
                                        .append(Component.text(" in ", NamedTextColor.GRAY))
                                        .append(Component.text(value.name, NamedTextColor.GOLD)));

            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

            Bukkit.getPluginManager().callEvent(new SpecPrestigeEvent(player.getUniqueId(), value, prestige));
        }
    }

    public static int getLevelForSpec(UUID uuid, Specializations spec) {
        return (int) calculateLevelFromExp(getExperienceFromSpec(uuid, spec));
    }

    public static class ExperienceSummary {

        private final LinkedHashMap<String, Long> universalExpGainSummary = new LinkedHashMap<>();
        private final Map<Specializations, LinkedHashMap<String, Long>> specExpGainSummary = new HashMap<>();

        public LinkedHashMap<String, Long> getUniversalExpGainSummary() {
            return universalExpGainSummary;
        }

        public Map<Specializations, LinkedHashMap<String, Long>> getSpecExpGainSummary() {
            return specExpGainSummary;
        }

        public long getUniversalExpGain() {
            return universalExpGainSummary.values().stream().mapToLong(Long::longValue).sum();
        }

        public long getSpecExpGain(Specializations specializations) {
            return specExpGainSummary.getOrDefault(specializations, new LinkedHashMap<>()).values().stream().mapToLong(Long::longValue).sum();
        }

        public Component getUniversalSummary() {
            return getHoverSummary(universalExpGainSummary);
        }

        public Component getSpecSummary(Specializations specializations) {
            return getHoverSummary(specExpGainSummary.getOrDefault(specializations, new LinkedHashMap<>()));
        }

        public Component getHoverSummary(LinkedHashMap<String, Long> expGain) {
            int counter = 0;
            TextComponent.Builder expSummary = Component.empty().toBuilder();
            for (Map.Entry<String, Long> entry : expGain.entrySet()) {
                String key = entry.getKey();
                Long value = entry.getValue();
                expSummary.append(Component.text(key, NamedTextColor.AQUA))
                          .append(Component.text(": ", NamedTextColor.WHITE))
                          .append(Component.text("+", NamedTextColor.DARK_GRAY))
                          .append(Component.text(value, NamedTextColor.DARK_GREEN));
                if (counter != expGain.size() - 1) {
                    expSummary.append(Component.newline());
                }
                counter++;
            }
            return expSummary.build();
        }

    }


    static class AwardSummary {
        List<Component> messages = new ArrayList<>();
        long totalExperienceGain = 0L;

        public AwardSummary() {
            messages.add(Component.text("---------------------------------------------------", NamedTextColor.BLUE));
            messages.add(Component.text("Weekly Experience Bonus", NamedTextColor.GREEN));
            messages.add(Component.empty());
        }

        public List<Component> getMessages() {
            return messages;
        }

        public long getTotalExperienceGain() {
            return totalExperienceGain;
        }

        public void addMessage(Component message) {
            messages.add(message);
        }

        public void addTotalExperienceGain(long amount) {
            totalExperienceGain += amount;
        }
    }
}
