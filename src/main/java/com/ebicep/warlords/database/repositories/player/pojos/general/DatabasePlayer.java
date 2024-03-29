package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.achievements.Achievement;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.achievements.types.TieredAchievements;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabasePlayerCTF;
import com.ebicep.warlords.database.repositories.player.pojos.duel.DatabasePlayerDuel;
import com.ebicep.warlords.database.repositories.player.pojos.interception.DatabasePlayerInterception;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.DatabasePlayerTDM;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Settings;
import com.ebicep.warlords.player.general.Specializations;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Document(collection = "Players_Information")
public class DatabasePlayer extends DatabasePlayerGeneral {

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
    @Field("ctf_stats")
    private DatabasePlayerCTF ctfStats = new DatabasePlayerCTF();
    @Field("tdm_stats")
    private DatabasePlayerTDM tdmStats = new DatabasePlayerTDM();
    @Field("interception_stats")
    private DatabasePlayerInterception interceptionStats = new DatabasePlayerInterception();
    @Field("duel_stats")
    private DatabasePlayerDuel duelStats = new DatabasePlayerDuel();
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
    private List<String> permissions = new ArrayList<>();

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
    public void updateCustomStats(
            DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        //UPDATE UNIVERSAL EXPERIENCE
        this.experience += gamePlayer.getExperienceEarnedUniversal() * multiplier;
        //PvE outside all base stats besides universal experience
        AbstractDatabaseStatInformation classStats = this.getClass(Specializations.getClass(gamePlayer.getSpec()));
        DatabaseSpecialization specStats = this.getSpec(gamePlayer.getSpec());
        if (GameMode.isPvE(gameMode)) {
            //this.experience += gamePlayer.getExperienceEarnedSpec() * multiplier;
            classStats.setExperience(classStats.getExperience() + gamePlayer.getExperienceEarnedSpec() * multiplier);
            specStats.setExperience(specStats.getExperience() + gamePlayer.getExperienceEarnedSpec() * multiplier);
            this.pveStats.updateStats(this, databaseGame, gamePlayer, multiplier, playersCollection);
            return;
        }
        //UPDATE CLASS, SPEC
        classStats.updateStats(this, databaseGame, gamePlayer, multiplier, playersCollection);
        specStats.updateStats(this, databaseGame, gamePlayer, multiplier, playersCollection);
        //UPDATE GAMEMODES
        switch (gameMode) {
            case CAPTURE_THE_FLAG -> this.ctfStats.updateStats(this, databaseGame, gamePlayer, multiplier, playersCollection);
            case TEAM_DEATHMATCH -> this.tdmStats.updateStats(this, databaseGame, gamePlayer, multiplier, playersCollection);
            case INTERCEPTION -> this.interceptionStats.updateStats(this, databaseGame, gamePlayer, multiplier, playersCollection);
            case DUEL -> this.duelStats.updateStats(this, databaseGame, gamePlayer, multiplier, playersCollection);
        }
        //UPDATE COMP/PUB GENERAL, GAMEMODE, GAMEMODE CLASS, GAMEMODE SPEC
        List<GameAddon> gameAddons = databaseGame.getGameAddons();
        if (gameAddons.contains(GameAddon.TOURNAMENT_MODE)) {
            this.tournamentStats.getCurrentTournamentStats().updateStats(this, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            if (gameAddons.isEmpty()) {
                this.pubStats.updateStats(this, databaseGame, gamePlayer, multiplier, playersCollection);
            } else if (gameAddons.contains(GameAddon.PRIVATE_GAME) && !gameAddons.contains(GameAddon.CUSTOM_GAME)) {
                this.compStats.updateStats(this, databaseGame, gamePlayer, multiplier, playersCollection);
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

    public DatabasePlayerCTF getCtfStats() {
        return ctfStats;
    }

    public void setCtfStats(DatabasePlayerCTF ctfStats) {
        this.ctfStats = ctfStats;
    }

    public DatabasePlayerTDM getTdmStats() {
        return tdmStats;
    }

    public void setTdmStats(DatabasePlayerTDM tdmStats) {
        this.tdmStats = tdmStats;
    }

    public DatabasePlayerInterception getInterceptionStats() {
        return interceptionStats;
    }

    public void setInterceptionStats(DatabasePlayerInterception interceptionStats) {
        this.interceptionStats = interceptionStats;
    }

    public DatabasePlayerDuel getDuelStats() {
        return duelStats;
    }

    public void setDuelStats(DatabasePlayerDuel duelStats) {
        this.duelStats = duelStats;
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

}
