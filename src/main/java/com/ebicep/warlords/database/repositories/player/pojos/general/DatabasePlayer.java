package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.achievements.Achievement;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.achievements.types.TieredAchievements;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.database.repositories.items.pojos.WeeklyBlessings;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.general.classes.*;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsSpecs;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Settings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.ItemsManager;
import com.ebicep.warlords.pve.items.menu.ItemMichaelMenu;
import com.ebicep.warlords.pve.rewards.types.CompensationReward;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Document(collection = "Players_Information")
public class DatabasePlayer implements MultiStatsGeneral {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;
    private String name;
    @Field("last_login")
    private Instant lastLogin;
    @Field("skin_base_64")
    private String skinBase64;
    @Field("discord_id")
    private Long discordID = null;
    @Field("future_messages")
    private List<FutureMessage> futureMessages = new ArrayList<>();
    private DatabaseMage mage = new DatabaseMage();
    private DatabaseWarrior warrior = new DatabaseWarrior();
    private DatabasePaladin paladin = new DatabasePaladin();
    private DatabaseShaman shaman = new DatabaseShaman();
    private DatabaseRogue rogue = new DatabaseRogue();
    private DatabaseArcanist arcanist = new DatabaseArcanist();
    private long experience;

    @Field("comp_stats")
    private DatabasePlayerCompStats compStats = new DatabasePlayerCompStats();
    @Field("public_queue_stats")
    private DatabasePlayerPubStats pubStats = new DatabasePlayerPubStats();

    @Field("pve_stats")
    private DatabasePlayerPvE pveStats = new DatabasePlayerPvE();

    @Field("tournament_stats")
    private TournamentStats tournamentStats = new TournamentStats();

    @Field("last_spec")
    private Specializations lastSpec = Specializations.PYROMANCER;
    @Field("hotkeymode")
    private Settings.HotkeyMode hotkeyMode = Settings.HotkeyMode.NEW_MODE;
    @Field("particle_quality")
    private Settings.ParticleQuality particleQuality = Settings.ParticleQuality.HIGH;
    @Field("flag_message")
    private Settings.FlagMessageMode flagMessageMode = Settings.FlagMessageMode.ABSOLUTE;
    @Field("glowing_mode")
    private Settings.GlowingMode glowingMode = Settings.GlowingMode.ON;
    @Field("fast_wave_mode")
    private Settings.FastWaveMode fastWaveMode = Settings.FastWaveMode.OFF;
    @Field("chat_damage")
    private Settings.ChatSettings.ChatDamage chatDamageMode = Settings.ChatSettings.ChatDamage.ALL;
    @Field("chat_healing")
    private Settings.ChatSettings.ChatHealing chatHealingMode = Settings.ChatSettings.ChatHealing.ALL;
    @Field("chat_energy")
    private Settings.ChatSettings.ChatEnergy chatEnergyMode = Settings.ChatSettings.ChatEnergy.ALL;
    @Field("chat_kills")
    private Settings.ChatSettings.ChatKills chatKillsMode = Settings.ChatSettings.ChatKills.ALL;
    @Field("chat_insignia")
    private Settings.ChatSettings.ChatInsignia chatInsigniaMode = Settings.ChatSettings.ChatInsignia.ALL;
    @Field("chat_event_points")
    private Settings.ChatSettings.ChatEventPoints chatEventPointsMode = Settings.ChatSettings.ChatEventPoints.ALL;
    @Field("chat_upgrade")
    private Settings.ChatSettings.ChatUpgrade chatUpgradeMode = Settings.ChatSettings.ChatUpgrade.ALL;

    private List<Achievement.AbstractAchievementRecord<?>> achievements = new ArrayList<>();
    private List<UUID> ignored = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();
    @Field("patches_applied")
    private List<Patches> patchesApplied = new ArrayList<>();

    public DatabasePlayer() {
    }

    public DatabasePlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DatabasePlayer that = (DatabasePlayer) o;
        return uuid.equals(that.uuid);
    }


    @Override
    public String toString() {
        return "DatabasePlayer{" +
                "id='" + id + '\'' +
                ", uuid=" + uuid +
                ", name='" + name + '\'' +
                '}';
    }

    public void loadInCollection(PlayersCollections collection) {
        pveStats.loadInCollection(collection);
        pveStats.setDatabasePlayer(this);
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        DatabaseSpecialization spec = getSpec(gamePlayer.getSpec());
        spec.setExperience(spec.getExperience() + gamePlayer.getExperienceEarnedSpec() * multiplier);
        this.experience += gamePlayer.getExperienceEarnedUniversal() * multiplier;
        //PvE outside all base stats besides universal experience
        if (GameMode.isPvE(gameMode) && databaseGame instanceof DatabaseGamePvEBase gamePvEBase && gamePlayer instanceof DatabaseGamePlayerPvEBase gamePlayerPvEBase) {
            this.pveStats.updateStats(this, gamePvEBase, gameMode, gamePlayerPvEBase, result, multiplier, playersCollection);
            return;
        }
        //UPDATE COMP/PUB GENERAL, GAMEMODE, GAMEMODE CLASS, GAMEMODE SPEC
        List<GameAddon> gameAddons = databaseGame.getGameAddons();
        if (gameAddons.contains(GameAddon.TOURNAMENT_MODE)) {
            this.tournamentStats.updateStats(this, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        } else {
            if (gameAddons.isEmpty()) {
                this.pubStats.updateStats(this, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
            } else if (gameAddons.contains(GameAddon.PRIVATE_GAME) && !gameAddons.contains(GameAddon.CUSTOM_GAME)) {
                this.compStats.updateStats(this, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
            }
        }
    }


    public String getName() {
        if (name == null) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                name = player.getName();
            }
        }
        if (name == null) {
            name = Bukkit.getOfflinePlayer(uuid).getName();
        }
        if (name == null) {
            name = "?";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Instant getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Instant lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getSkinBase64() {
        return skinBase64;
    }

    public void setSkinBase64(String skinBase64) {
        this.skinBase64 = skinBase64;
    }

    public Long getDiscordID() {
        return discordID;
    }

    public void setDiscordID(Long discordID) {
        this.discordID = discordID;
    }

    public List<FutureMessage> getFutureMessages() {
        return futureMessages;
    }

    public void addFutureMessage(FutureMessage futureMessage) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            futureMessage.sendToPlayer(player);
        } else {
            this.futureMessages.add(futureMessage);
        }
    }

    public DatabaseSpecialization getSpec(Specializations specializations) {
        return switch (specializations) {
            case PYROMANCER -> mage.getPyromancer();
            case CRYOMANCER -> mage.getCryomancer();
            case AQUAMANCER -> mage.getAquamancer();
            case BERSERKER -> warrior.getBerserker();
            case DEFENDER -> warrior.getDefender();
            case REVENANT -> warrior.getRevenant();
            case AVENGER -> paladin.getAvenger();
            case CRUSADER -> paladin.getCrusader();
            case PROTECTOR -> paladin.getProtector();
            case THUNDERLORD -> shaman.getThunderlord();
            case SPIRITGUARD -> shaman.getSpiritguard();
            case EARTHWARDEN -> shaman.getEarthwarden();
            case ASSASSIN -> rogue.getAssassin();
            case VINDICATOR -> rogue.getVindicator();
            case APOTHECARY -> rogue.getApothecary();
            case CONJURER -> arcanist.getConjurer();
            case SENTINEL -> arcanist.getSentinel();
            case LUMINARY -> arcanist.getLuminary();
        };
    }

    public DatabaseBaseGeneral getClass(Classes classes) {
        return switch (classes) {
            case MAGE -> mage;
            case WARRIOR -> warrior;
            case PALADIN -> paladin;
            case SHAMAN -> shaman;
            case ROGUE -> rogue;
            case ARCANIST -> arcanist;
        };
    }

    public DatabasePlayerCompStats getCompStats() {
        return compStats;
    }

    public void setCompStats(DatabasePlayerCompStats compStats) {
        this.compStats = compStats;
    }

    public DatabasePlayerPubStats getPubStats() {
        return pubStats;
    }

    public void setPubStats(DatabasePlayerPubStats pubStats) {
        this.pubStats = pubStats;
    }

    public DatabasePlayerPvE getPveStats() {
        return pveStats;
    }

    public void setPveStats(DatabasePlayerPvE pveStats) {
        this.pveStats = pveStats;
    }

    public TournamentStats getTournamentStats() {
        return tournamentStats;
    }

    public void setTournamentStats(TournamentStats tournamentStats) {
        this.tournamentStats = tournamentStats;
    }

    public Specializations getLastSpec() {
        return lastSpec;
    }

    public void setLastSpec(Specializations lastSpec) {
        this.lastSpec = lastSpec;
    }

    public Settings.HotkeyMode getHotkeyMode() {
        return hotkeyMode;
    }

    public void setHotkeyMode(Settings.HotkeyMode hotkeyMode) {
        this.hotkeyMode = hotkeyMode;
    }

    public Settings.ParticleQuality getParticleQuality() {
        return particleQuality;
    }

    public void setParticleQuality(Settings.ParticleQuality particleQuality) {
        this.particleQuality = particleQuality;
    }

    public Settings.FlagMessageMode getFlagMessageMode() {
        return flagMessageMode;
    }

    public void setFlagMessageMode(Settings.FlagMessageMode flagMessageMode) {
        this.flagMessageMode = flagMessageMode;
    }

    public Settings.GlowingMode getGlowingMode() {
        return glowingMode;
    }

    public void setGlowingMode(Settings.GlowingMode glowingMode) {
        this.glowingMode = glowingMode;
    }

    public Settings.FastWaveMode getFastWaveMode() {
        return fastWaveMode;
    }

    public void setFastWaveMode(Settings.FastWaveMode fastWaveMode) {
        this.fastWaveMode = fastWaveMode;
    }

    public void addAchievement(Achievement.AbstractAchievementRecord<?> achievementRecord) {
        this.achievements.add(achievementRecord);
    }

    public void addAchievements(List<Achievement.AbstractAchievementRecord<?>> achievements) {
        this.achievements.addAll(achievements);
    }

    public List<Achievement.AbstractAchievementRecord<?>> getAchievements() {
        return achievements;
    }

    public boolean hasAchievement(TieredAchievements achievement) {
        return this.achievements.stream()
                                .anyMatch(achievementRecord -> achievementRecord instanceof TieredAchievements.TieredAchievementRecord &&
                                        ((TieredAchievements.TieredAchievementRecord) achievementRecord).getAchievement() == achievement);
    }

    public boolean hasAchievement(ChallengeAchievements achievement) {
        return this.achievements.stream()
                                .anyMatch(achievementRecord -> achievementRecord instanceof ChallengeAchievements.ChallengeAchievementRecord &&
                                        ((ChallengeAchievements.ChallengeAchievementRecord) achievementRecord).getAchievement() == achievement);
    }

    public Settings.ChatSettings.ChatDamage getChatDamageMode() {
        return chatDamageMode;
    }

    public void setChatDamageMode(Settings.ChatSettings.ChatDamage chatDamageMode) {
        this.chatDamageMode = chatDamageMode;
    }

    public Settings.ChatSettings.ChatHealing getChatHealingMode() {
        return chatHealingMode;
    }

    public void setChatHealingMode(Settings.ChatSettings.ChatHealing chatHealingMode) {
        this.chatHealingMode = chatHealingMode;
    }

    public Settings.ChatSettings.ChatEnergy getChatEnergyMode() {
        return chatEnergyMode;
    }

    public void setChatEnergyMode(Settings.ChatSettings.ChatEnergy chatEnergyMode) {
        this.chatEnergyMode = chatEnergyMode;
    }

    public Settings.ChatSettings.ChatKills getChatKillsMode() {
        return chatKillsMode;
    }

    public void setChatKillsMode(Settings.ChatSettings.ChatKills chatKillsMode) {
        this.chatKillsMode = chatKillsMode;
    }

    public Settings.ChatSettings.ChatInsignia getChatInsigniaMode() {
        return chatInsigniaMode;
    }

    public void setChatInsigniaMode(Settings.ChatSettings.ChatInsignia chatInsigniaMode) {
        this.chatInsigniaMode = chatInsigniaMode;
    }

    public Settings.ChatSettings.ChatEventPoints getChatEventPointsMode() {
        return chatEventPointsMode;
    }

    public void setChatEventPointsMode(Settings.ChatSettings.ChatEventPoints chatEventPointsMode) {
        this.chatEventPointsMode = chatEventPointsMode;
    }

    public Settings.ChatSettings.ChatUpgrade getChatUpgradeMode() {
        return chatUpgradeMode;
    }

    public void setChatUpgradeMode(Settings.ChatSettings.ChatUpgrade chatUpgradeMode) {
        this.chatUpgradeMode = chatUpgradeMode;
    }

    public String getId() {
        return id;
    }

    public List<UUID> getIgnored() {
        return ignored;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public boolean isPatreon() {
        return hasPermission("group.patreon") || hasPermission("group.contentcreator");
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public List<Patches> getPatchesApplied() {
        return patchesApplied;
    }

    @Override
    public Collection<StatsWarlordsClasses<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase, Stats<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase>, StatsWarlordsSpecs<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase, Stats<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase>>>> getStats() {
        List<StatsWarlordsClasses<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase, Stats<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase>, StatsWarlordsSpecs<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase, Stats<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase>>>> stats = new ArrayList<>();
        stats.addAll(pubStats.getStats());
        stats.addAll(compStats.getStats());
        for (PvEStatsWarlordsClasses<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase, PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>, PvEStatsWarlordsSpecs<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase, PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>>> stat : pveStats.getStats()) {
            stats.add((StatsWarlordsClasses<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase, Stats<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase>, StatsWarlordsSpecs<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase, Stats<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase>>>) (Object) stat);
        }
        stats.addAll(tournamentStats.getStats());
        return stats;
    }

    @Override
    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public enum Patches {

        EOD_ITEMS {
            @Override
            public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
                DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                pveStats.getItemsManager().getItemInventory().forEach(item -> item.applyRandomModifier());
                return true;
            }
        },
        EOD_ASCENDANT_SHARD_2 {
            @Override
            public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
                // fixes incorrect amount from EOD_ASCENDANT_SHARD that gave 1 per pres instead of 3.
                if (!databasePlayer.getPatchesApplied().contains(EOD_ASCENDANT_SHARD)) {
                    return true;
                }
                DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                for (CompensationReward compensationReward : pveStats.getCompensationRewards()) {
                    if (compensationReward instanceof CompensationReward.AscendantShardPrestigePatch prestigePatch) {
                        Long previousValue = prestigePatch.getRewards().get(Currencies.ASCENDANT_SHARD);
                        if (previousValue == null) {
                            ChatUtils.MessageType.WARLORDS.sendErrorMessage("EOD_ASCENDANT_SHARD_2: previousValue is null");
                            ChatUtils.MessageType.WARLORDS.sendErrorMessage(String.valueOf(prestigePatch));
                            return true;
                        }
                        pveStats.getCompensationRewards().add(new CompensationReward.AscendantShardPrestigePatch(previousValue * 2L));
                        return true;
                    }
                }
                return true;
            }
        },
        EOD_ASCENDANT_SHARD {
            @Override
            public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
                DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                int totalPrestige = Arrays.stream(Specializations.VALUES)
                                          .mapToInt(spec -> databasePlayer.getSpec(spec).getPrestige())
                                          .sum();
                if (totalPrestige > 0) {
                    pveStats.getCompensationRewards().add(new CompensationReward.AscendantShardPrestigePatch(totalPrestige * 3L));
                }
                return true;
            }
        },
        EOD_CELESTIAL_BRONZE {
            @Override
            public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
                DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                Long celestialBronze = pveStats.getCurrencyValue(Currencies.CELESTIAL_BRONZE);
                if (celestialBronze > 0) {
                    pveStats.getCompensationRewards().add(new CompensationReward.CelestialBronzePatch(celestialBronze));
                }
                pveStats.setCurrency(Currencies.CELESTIAL_BRONZE, 0L);
                return true;
            }
        },
        EOD_BLESSINGS {
            @Override
            public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
                List<WeeklyBlessings> weeklyBlessings = WeeklyBlessings.allWeeklyBlessings;
                if (weeklyBlessings.isEmpty()) {
                    return false;
                }
                DatabasePlayerPvE pveStats = databasePlayer.getPveStats();

                Map<Integer, Integer> blessingsBought = new HashMap<>();
                for (WeeklyBlessings weeklyBlessing : weeklyBlessings) {
                    Map<Integer, Integer> bought = weeklyBlessing.getPlayerOrders().get(uuid);
                    if (bought == null) {
                        continue;
                    }
                    for (Map.Entry<Integer, Integer> entry : bought.entrySet()) {
                        blessingsBought.merge(entry.getKey(), entry.getValue(), Integer::sum);
                    }
                }
                if (!blessingsBought.isEmpty()) {
                    LinkedHashMap<Spendable, Long> rewards = new LinkedHashMap<>();
                    blessingsBought.forEach((tier, amount) -> {
                        LinkedHashMap<Spendable, Long> cost = ItemMichaelMenu.BuyABlessingMenu.COSTS.get(tier);
                        cost.forEach((spendable, aLong) -> rewards.merge(spendable, aLong * amount, Long::sum));
                    });
                    ItemsManager itemsManager = pveStats.getItemsManager();
                    rewards.merge(Currencies.LEGEND_FRAGMENTS, itemsManager.getBlessingsFound() * 15L, Long::sum);

                    //sort rewards by highest first into new map
                    LinkedHashMap<Spendable, Long> sortedRewards = new LinkedHashMap<>();
                    rewards.entrySet().stream()
                           .sorted(Map.Entry.<Spendable, Long>comparingByValue().reversed())
                           .forEachOrdered(x -> sortedRewards.put(x.getKey(), x.getValue()));

                    pveStats.getCompensationRewards().add(new CompensationReward.BlessingPatch(sortedRewards));
                    itemsManager.setBlessingsFound(0);
                    itemsManager.getBlessingsBought().clear();
                }
                return true;
            }
        },
        EOD_ASCENDANT_SHARD_3 {
            @Override
            public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
                DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                AtomicInteger masteriesUnlocked = new AtomicInteger();
                pveStats.getAlternativeMasteriesUnlocked().forEach((specializations, integerInstantMap) -> masteriesUnlocked.addAndGet(integerInstantMap.keySet().size()));
                pveStats.subtractCurrency(Currencies.ASCENDANT_SHARD, masteriesUnlocked.get());
                return true;
            }
        },
        EOD_ASCENDANT_SHARD_4 {
            @Override
            public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
                DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                AtomicInteger masteriesUnlocked = new AtomicInteger();
                Map<Specializations, Map<Integer, Instant>> unlocked = pveStats.getAlternativeMasteriesUnlocked();
                unlocked.forEach((specializations, integerInstantMap) -> masteriesUnlocked.addAndGet(integerInstantMap.keySet().size()));
                unlocked.clear();
                pveStats.getAlternativeMasteriesUnlockedAbilities().clear();
                pveStats.addCurrency(Currencies.ASCENDANT_SHARD, masteriesUnlocked.get());
                return true;
            }
        },

        ;

        public static final Patches[] VALUES = values();

        public boolean run(UUID uuid, DatabasePlayer databasePlayer) {
            return false;
        }
    }
}
