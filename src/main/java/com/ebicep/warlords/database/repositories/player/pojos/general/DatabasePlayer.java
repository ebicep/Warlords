package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.achievements.Achievement;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.achievements.types.TieredAchievements;
import com.ebicep.warlords.commands.miscellaneouscommands.StreamChaptersCommand;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.*;
import com.ebicep.warlords.database.repositories.player.pojos.general.classes.*;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsSpecs;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.player.general.settings.*;
import com.ebicep.warlords.player.general.settings.actionbar.ActionBarSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Document(collection = "Players_Information")
public class DatabasePlayer implements MultiStatsGeneral, TracksMultiAbilityStats {

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
    @Field("spec_boosts")
    private Map<Specializations, Integer> specBoosts = new HashMap<>();
    @Field("hotkeymode")
    private HotkeyMode hotkeyMode = HotkeyMode.NEW_MODE;
    @Field("particle_quality")
    private ParticleQuality particleQuality = ParticleQuality.HIGH;
    @Field("flag_message")
    private FlagMessageMode flagMessageMode = FlagMessageMode.ABSOLUTE;
    @Field("glowing_mode")
    private GlowingMode glowingMode = GlowingMode.ON;
    @Field("fast_wave_mode")
    private FastWaveMode fastWaveMode = FastWaveMode.OFF;
    @Field("chat_damage")
    private ChatSettings.ChatDamage chatDamageMode = ChatSettings.ChatDamage.ALL;
    @Field("chat_healing")
    private ChatSettings.ChatHealing chatHealingMode = ChatSettings.ChatHealing.ALL;
    @Field("chat_energy")
    private ChatSettings.ChatEnergy chatEnergyMode = ChatSettings.ChatEnergy.ALL;
    @Field("chat_kills")
    private ChatSettings.ChatKills chatKillsMode = ChatSettings.ChatKills.ALL;
    @Field("chat_insignia")
    private ChatSettings.ChatInsignia chatInsigniaMode = ChatSettings.ChatInsignia.ALL;
    @Field("chat_event_points")
    private ChatSettings.ChatEventPoints chatEventPointsMode = ChatSettings.ChatEventPoints.ALL;
    @Field("chat_upgrade")
    private ChatSettings.ChatUpgrade chatUpgradeMode = ChatSettings.ChatUpgrade.ALL;
    @Field("cooldown_display_settings")
    private CooldownDisplaySettings cooldownDisplaySettings = new CooldownDisplaySettings();
    @Field("action_bar_settings")
    private ActionBarSettings actionBarSettings = new ActionBarSettings();
    @Field("advanced_hover_messages")
    private AdvancedHoverMessages advancedHoverMessages = AdvancedHoverMessages.OFF;

    private List<Achievement.AbstractAchievementRecord<?>> achievements = new ArrayList<>();
    private List<UUID> ignored = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();
    @Field("patches_applied")
    private List<DatabasePlayerPatches> patchesApplied = new ArrayList<>();

    @Field("game_logs")
    private List<StreamChaptersCommand.GameTime> gameLogs = new ArrayList<>();

    @Transient
    private Team wantedTeam = Team.BLUE;

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
        if (GameMode.isPvE(gameMode) && databaseGame instanceof DatabaseGamePvEBase gamePvEBase && gamePlayer instanceof DatabaseGamePlayerPvEBase gamePlayerPvEBase) {
            this.pveStats.updateStats(this, gamePvEBase, gameMode, gamePlayerPvEBase, result, multiplier, playersCollection);
            return;
        }
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

    @Override
    public Collection<TracksAbilityStats> getAllAbilityStats() {
        return Stream.of(pubStats, compStats, tournamentStats, pveStats)
                     .flatMap(s -> s.getAllAbilityStats().stream())
                     .collect(Collectors.toList());
    }

    public void loadInCollection(PlayersCollections collection) {
        pveStats.loadInCollection(collection);
        pveStats.setDatabasePlayer(this);
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

    public List<ArmorManager.Helmets> getHelmets() {
        List<ArmorManager.Helmets> armorSets = new ArrayList<>();
        for (Classes value : Classes.VALUES) {
            armorSets.add(getClass(value).getHelmet());
        }
        return armorSets;
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

    public List<ArmorManager.ArmorSets> getArmorSets() {
        List<ArmorManager.ArmorSets> armorSets = new ArrayList<>();
        for (Classes value : Classes.VALUES) {
            armorSets.add(getClass(value).getArmor());
        }
        return armorSets;
    }

    public void setHelmet(Classes classes, ArmorManager.Helmets helmet) {
        getClass(classes).setHelmet(helmet);
    }

    public void setArmor(Classes classes, ArmorManager.ArmorSets armor) {
        getClass(classes).setArmor(armor);
    }

    public ArmorManager.ArmorSets getArmorSet(Classes classes) {
        return getClass(classes).getArmor();
    }

    public ArmorManager.ArmorSets getArmorSet(Specializations spec) {
        return getClass(Specializations.getClass(spec)).getArmor();
    }

    public ArmorManager.ArmorSets getArmorSet() {
        return getClass(Specializations.getClass(lastSpec)).getArmor();
    }

    public ArmorManager.Helmets getHelmet(Classes classes) {
        return getClass(classes).getHelmet();
    }

    public ArmorManager.Helmets getHelmet() {
        return getClass(Specializations.getClass(lastSpec)).getHelmet();
    }

    public ArmorManager.Helmets getHelmet(Specializations spec) {
        return getClass(Specializations.getClass(spec)).getHelmet();
    }

    public Weapons getLastSpecWeapon() {
        return getSpec(lastSpec).getWeapon();
    }

    public void setWeaponSkin(Specializations specializations, Weapons weapon) {
        getSpec(specializations).setWeapon(weapon);
    }

    public SkillBoosts getSkillBoostForSpec(Specializations specializations) {
        return getSpec(specializations).getSkillBoost();
    }

    public void setSkillBoostForSpec(Specializations specializations, SkillBoosts skillBoosts) {
        getSpec(specializations).setSkillBoost(skillBoosts);
    }

    public Map<Specializations, Integer> getSpecBoosts() {
        return specBoosts;
    }

    public int getSelectedSpecBoost(Specializations specializations) {
        return specBoosts.computeIfAbsent(specializations, k -> 0);
    }

    public HotkeyMode getHotkeyMode() {
        return hotkeyMode;
    }

    public void setHotkeyMode(HotkeyMode hotkeyMode) {
        this.hotkeyMode = hotkeyMode;
    }

    public ParticleQuality getParticleQuality() {
        return particleQuality;
    }

    public void setParticleQuality(ParticleQuality particleQuality) {
        this.particleQuality = particleQuality;
    }

    public FlagMessageMode getFlagMessageMode() {
        return flagMessageMode;
    }

    public void setFlagMessageMode(FlagMessageMode flagMessageMode) {
        this.flagMessageMode = flagMessageMode;
    }

    public GlowingMode getGlowingMode() {
        return glowingMode;
    }

    public void setGlowingMode(GlowingMode glowingMode) {
        this.glowingMode = glowingMode;
    }

    public FastWaveMode getFastWaveMode() {
        return fastWaveMode;
    }

    public void setFastWaveMode(FastWaveMode fastWaveMode) {
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

    public ChatSettings.ChatDamage getChatDamageMode() {
        return chatDamageMode;
    }

    public void setChatDamageMode(ChatSettings.ChatDamage chatDamageMode) {
        this.chatDamageMode = chatDamageMode;
    }

    public ChatSettings.ChatHealing getChatHealingMode() {
        return chatHealingMode;
    }

    public void setChatHealingMode(ChatSettings.ChatHealing chatHealingMode) {
        this.chatHealingMode = chatHealingMode;
    }

    public ChatSettings.ChatEnergy getChatEnergyMode() {
        return chatEnergyMode;
    }

    public void setChatEnergyMode(ChatSettings.ChatEnergy chatEnergyMode) {
        this.chatEnergyMode = chatEnergyMode;
    }

    public ChatSettings.ChatKills getChatKillsMode() {
        return chatKillsMode;
    }

    public void setChatKillsMode(ChatSettings.ChatKills chatKillsMode) {
        this.chatKillsMode = chatKillsMode;
    }

    public ChatSettings.ChatInsignia getChatInsigniaMode() {
        return chatInsigniaMode;
    }

    public void setChatInsigniaMode(ChatSettings.ChatInsignia chatInsigniaMode) {
        this.chatInsigniaMode = chatInsigniaMode;
    }

    public ChatSettings.ChatEventPoints getChatEventPointsMode() {
        return chatEventPointsMode;
    }

    public void setChatEventPointsMode(ChatSettings.ChatEventPoints chatEventPointsMode) {
        this.chatEventPointsMode = chatEventPointsMode;
    }

    public ChatSettings.ChatUpgrade getChatUpgradeMode() {
        return chatUpgradeMode;
    }

    public void setChatUpgradeMode(ChatSettings.ChatUpgrade chatUpgradeMode) {
        this.chatUpgradeMode = chatUpgradeMode;
    }

    public CooldownDisplaySettings getCooldownDisplaySettings() {
        return cooldownDisplaySettings;
    }

    public ActionBarSettings getActionBarSettings() {
        return actionBarSettings;
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

    public List<DatabasePlayerPatches> getPatchesApplied() {
        return patchesApplied;
    }

    public List<StreamChaptersCommand.GameTime> getGameLogs() {
        return gameLogs;
    }

    public AdvancedHoverMessages getAdvancedHoverMessages() {
        return advancedHoverMessages;
    }

    public void setAdvancedHoverMessages(AdvancedHoverMessages advancedHoverMessages) {
        this.advancedHoverMessages = advancedHoverMessages;
    }

    public Team getWantedTeam() {
        return wantedTeam;
    }

    public void setWantedTeam(Team team) {
        this.wantedTeam = team;
    }

}
