package com.ebicep.warlords.player.general;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.FutureMessage;
import com.ebicep.warlords.events.player.ingame.WarlordsGiveExperienceEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.option.ExperienceGainOption;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.onslaught.OnslaughtOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ExperienceManager {

    public static final Map<Integer, Long> LEVEL_TO_EXPERIENCE;
    public static final Map<Long, Integer> EXPERIENCE_TO_LEVEL;
    public static final DecimalFormat EXPERIENCE_DECIMAL_FORMAT = new DecimalFormat("#,###.#");
    public static final HashMap<UUID, LinkedHashMap<String, Long>> CACHED_PLAYER_EXP_SUMMARY = new HashMap<>();
    public static final int LEVEL_TO_PRESTIGE = 100;
    public static final List<NamedTextColor> PRESTIGE_COLORS = Arrays.asList(
            NamedTextColor.GRAY,
            NamedTextColor.RED,
            NamedTextColor.YELLOW,
            NamedTextColor.GREEN,
            NamedTextColor.AQUA,
            NamedTextColor.BLUE,
            NamedTextColor.LIGHT_PURPLE,
            NamedTextColor.BLACK,
            NamedTextColor.WHITE,
            NamedTextColor.DARK_GRAY,
            NamedTextColor.DARK_RED,
            NamedTextColor.GOLD,
            NamedTextColor.DARK_AQUA,
            NamedTextColor.DARK_BLUE,
            NamedTextColor.DARK_PURPLE,
            NamedTextColor.GRAY,
            NamedTextColor.RED,
            NamedTextColor.YELLOW,
            NamedTextColor.GREEN,
            NamedTextColor.AQUA,
            NamedTextColor.BLUE,
            NamedTextColor.LIGHT_PURPLE,
            NamedTextColor.BLACK,
            NamedTextColor.WHITE,
            NamedTextColor.DARK_GRAY,
            NamedTextColor.DARK_RED,
            NamedTextColor.GOLD,
            NamedTextColor.DARK_AQUA,
            NamedTextColor.DARK_BLUE,
            NamedTextColor.DARK_PURPLE
    );
    public static final HashMap<Classes, Pair<Integer, Integer>> CLASSES_MENU_LOCATION = new HashMap<>() {{
        put(Classes.MAGE, new Pair<>(2, 1));
        put(Classes.WARRIOR, new Pair<>(4, 1));
        put(Classes.PALADIN, new Pair<>(6, 1));
        put(Classes.SHAMAN, new Pair<>(3, 3));
        put(Classes.ROGUE, new Pair<>(5, 3));
    }};
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
                        databasePlayer.addFutureMessage(new FutureMessage(awardSummary.getMessages(), true));
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    }).execute();
        });
    }

    public static LinkedHashMap<String, Long> getExpFromGameStats(WarlordsEntity warlordsPlayer, boolean recalculate) {
        if (!recalculate && CACHED_PLAYER_EXP_SUMMARY.containsKey(warlordsPlayer.getUuid()) && CACHED_PLAYER_EXP_SUMMARY.get(
                warlordsPlayer.getUuid()) != null) {
            return CACHED_PLAYER_EXP_SUMMARY.get(warlordsPlayer.getUuid());
        }

        LinkedHashMap<String, Long> expGain = new LinkedHashMap<>();

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
                    int maxWaves = difficulty.getMaxWaves();
                    int wavesCleared = Math.min(waveDefenseOption.getWavesCleared(), maxWaves);
                    perBonus = new Pair<>("Waves Cleared", (long) wavesCleared * experienceGainOption.getPlayerExpPer());
                    if (wavesCleared == maxWaves) {
                        winBonus = new Pair<>("Wave " + maxWaves + " Clear Bonus",
                                (long) (experienceGainOption.getPlayerExpGameWinBonus() * difficulty.getRewardsMultiplier())
                        );
                    }
                } else if (pveOption instanceof OnslaughtOption) {
                    perBonus = new Pair<>("Minutes Elapsed", pveOption.getTicksElapsed() / 20 / 60 * experienceGainOption.getPlayerExpPer());
                }
                if (experienceGainOption.getPlayerExpPer() != 0 && perBonus != null) {
                    expGain.put(perBonus.getA(), perBonus.getB());
                }
                if (experienceGainOption.getPlayerExpGameWinBonus() != 0 && winBonus != null) {
                    expGain.put(winBonus.getA(), winBonus.getB());
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
                            expGain.put("Seconds Survived",
                                    (long) (secondsElapsed / expPerXSec.getB() * expPerXSec.getA() * difficulty.getRewardsMultiplier())
                            );
                        });
                }
            }
            Bukkit.getPluginManager().callEvent(new WarlordsGiveExperienceEvent(warlordsPlayer, expGain));
        } else {
            boolean isCompGame = game.getAddons().contains(GameAddon.PRIVATE_GAME);
            float multiplier = 1;
            //pubs
//            if (!isCompGame) {
//                multiplier *= .1;
//            }
            //duels
            if (game.getGameMode() == GameMode.DUEL) {
                multiplier *= .1;
            }

            boolean won = game.getPoints(warlordsPlayer.getTeam()) > game
                    .getPoints(warlordsPlayer.getTeam().enemy());
            long winLossExp = won ? 500 : 250;
            long kaExp = 5L * (warlordsPlayer.getMinuteStats().total().getKills() + warlordsPlayer.getMinuteStats()
                                                                                                  .total()
                                                                                                  .getAssists());

            double damageMultiplier;
            double healingMultiplier;
            double absorbedMultiplier;
            Specializations specializations = warlordsPlayer.getSpecClass();
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
            double calculatedDHP = warlordsPlayer.getMinuteStats()
                                                 .total()
                                                 .getDamage() * damageMultiplier + warlordsPlayer.getMinuteStats()
                                                                                                 .total()
                                                                                                 .getHealing() * healingMultiplier + warlordsPlayer.getMinuteStats()
                                                                                                                                                   .total()
                                                                                                                                                   .getAbsorbed() * absorbedMultiplier;
            long dhpExp = (long) (calculatedDHP / 500L);
            long flagCapExp = warlordsPlayer.getFlagsCaptured() * 150L;
            long flagRetExp = warlordsPlayer.getFlagsReturned() * 50L;

            expGain.put(won ? "Win" : "Loss", (long) (winLossExp * multiplier));
            if (kaExp != 0) {
                expGain.put("Kills/Assists", (long) (kaExp * multiplier));
            }
            if (dhpExp != 0) {
                expGain.put("DHP", (long) (dhpExp * multiplier));
            }
            if (flagCapExp != 0) {
                expGain.put("Flags Captured", (long) (flagCapExp * multiplier));
            }
            if (flagRetExp != 0) {
                expGain.put("Flags Returned", (long) (flagRetExp * multiplier));
            }

            DatabaseManager.getPlayer(warlordsPlayer.getUuid(),
                    PlayersCollections.DAILY,
                    databasePlayer -> {
                        int plays = isCompGame ? databasePlayer.getCompStats().getPlays() : databasePlayer.getPubStats().getPlays();
                        switch (plays) {
                            case 0 -> expGain.put("First Game of the Day", 500L / (isCompGame ? 1 : 10));
                            case 1 -> expGain.put("Second Game of the Day", 250L / (isCompGame ? 1 : 10));
                            case 2 -> expGain.put("Third Game of the Day", 100L / (isCompGame ? 1 : 10));
                        }
                    },
                    () -> {
                        ChatUtils.MessageType.WARLORDS.sendErrorMessage("ERROR: Could not find player: " + warlordsPlayer.getName() + " during experience calculation");
                    }
            );
        }


        CACHED_PLAYER_EXP_SUMMARY.put(warlordsPlayer.getUuid(), expGain);
        return expGain;
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
        DatabaseManager.getPlayer(uuid, databasePlayer -> experience.set(databasePlayer.getClass(classes).getExperience()));
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

    public static int getLevelForSpec(UUID uuid, Specializations spec) {
        return (int) calculateLevelFromExp(getExperienceFromSpec(uuid, spec));
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
                    .append(Component.text("PRESTIGE", PRESTIGE_COLORS.get(currentPrestige + 1)))
                    .append(Component.text(": ", NamedTextColor.GRAY));
        } else {
            progress.append(Component.text("Progress to Level " + nextLevel, NamedTextColor.GRAY))
                    .append(Component.text(": ", NamedTextColor.GRAY));
        }
        return getProgressString(currentExperience, nextLevel, progress.build());
    }

    public static TextComponent getPrestigeLevelString(UUID uuid, Specializations spec) {
        if (DatabaseManager.playerService == null) {
            return Component.text("[-]", PRESTIGE_COLORS.get(0));
        }
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid);
        if (databasePlayer == null) {
            return Component.text("[-]", PRESTIGE_COLORS.get(0));
        }
        int prestigeLevel = databasePlayer.getSpec(spec).getPrestige();
        return Component.text("[", NamedTextColor.DARK_GRAY)
                        .append(Component.text(prestigeLevel, PRESTIGE_COLORS.get(prestigeLevel)))
                        .append(Component.text("]", NamedTextColor.DARK_GRAY));
    }

    public static TextComponent getPrestigeLevelString(int prestigeLevel) {
        return Component.text("[", NamedTextColor.DARK_GRAY)
                        .append(Component.text(prestigeLevel, PRESTIGE_COLORS.get(prestigeLevel)))
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
                             .append(Component.text("LEVEL UP!", NamedTextColor.AQUA))
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
