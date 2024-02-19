package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard.DatabaseGamePlayerPvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard.DatabaseGamePvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.classes.*;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.DatabasePlayerPvEEventLibraryForgottenCodexDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardDifficultyStats;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.gameevents.libraryarchives.PlayerCodex;
import com.ebicep.warlords.pve.gameevents.libraryarchives.PlayerCodexEarnEvent;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

public class DatabasePlayerPvEEventLibraryArchivesDifficultyStats extends PvEEventLibraryArchivesDatabaseStatInformation implements DatabaseWarlordsClasses<PvEEventLibraryArchivesDatabaseStatInformation>, EventMode {

    private DatabaseMagePvEEventLibraryArchives mage = new DatabaseMagePvEEventLibraryArchives();
    private DatabaseWarriorPvEEventLibraryArchives warrior = new DatabaseWarriorPvEEventLibraryArchives();
    private DatabasePaladinPvEEventLibraryArchives paladin = new DatabasePaladinPvEEventLibraryArchives();
    private DatabaseShamanPvEEventLibraryArchives shaman = new DatabaseShamanPvEEventLibraryArchives();
    private DatabaseRoguePvEEventLibraryArchives rogue = new DatabaseRoguePvEEventLibraryArchives();
    private DatabaseArcanistPvEEventLibraryArchives arcanist = new DatabaseArcanistPvEEventLibraryArchives();
    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventLibraryArchivesPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventLibraryArchivesPlayerCountStats());
        put(2, new DatabasePlayerPvEEventLibraryArchivesPlayerCountStats());
        put(3, new DatabasePlayerPvEEventLibraryArchivesPlayerCountStats());
        put(4, new DatabasePlayerPvEEventLibraryArchivesPlayerCountStats());
    }};
    @Field("event_points_spent")
    private long eventPointsSpent;
    @Field("rewards_purchased")
    private Map<String, Long> rewardsPurchased = new LinkedHashMap<>();
    @Field("forgotten_codex_stats")
    private DatabasePlayerPvEEventLibraryForgottenCodexDifficultyStats forgottenCodexStats = new DatabasePlayerPvEEventLibraryForgottenCodexDifficultyStats();
    @Field("grimoires_graveyard_stats")
    private DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardDifficultyStats grimoiresGraveyardStats = new DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardDifficultyStats();
    @Field("codexes_earned")
    private Map<PlayerCodex, Integer> codexesEarned = new HashMap<>();

    @Field("completed_bounties")
    private Map<Bounty, Long> completedBounties = new HashMap<>();
    @Field("bounties_completed")
    private int bountiesCompleted = 0;
    @Field("active_bounties")
    private List<AbstractBounty> activeBounties = new ArrayList<>();

    public DatabasePlayerPvEEventLibraryArchivesDifficultyStats() {
    }

    @Override
    public void updateCustomStats(
            com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer databasePlayer,
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert gamePlayer instanceof DatabaseGamePlayerPvEWaveDefense;

        super.updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        //UPDATE UNIVERSAL EXPERIENCE
        this.experience += gamePlayer.getExperienceEarnedUniversal() * multiplier;
        this.experiencePvE += gamePlayer.getExperienceEarnedUniversal() * multiplier;

        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        this.getSpec(gamePlayer.getSpec()).updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        //UPDATE PLAYER COUNT STATS
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEEventLibraryArchivesPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }

        //MODES
        if (databaseGame instanceof DatabaseGamePvEEventForgottenCodex) {
            this.forgottenCodexStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else if (databaseGame instanceof DatabaseGamePvEEventGrimoiresGraveyard) {
            DatabaseGamePlayerPvEEventGrimoiresGraveyard gamePlayerGrimoiresGraveyard = (DatabaseGamePlayerPvEEventGrimoiresGraveyard) gamePlayer;
            PlayerCodex codexEarned = gamePlayerGrimoiresGraveyard.getCodexEarned();
            if (codexEarned != null) {
                this.codexesEarned.merge(codexEarned, multiplier, Integer::sum);
                if (multiplier > 0) {
                    Bukkit.getPluginManager().callEvent(new PlayerCodexEarnEvent(databasePlayer.getUuid(), codexEarned));
                }
            }
            this.grimoiresGraveyardStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        }

    }

    @Override
    public DatabaseBasePvEEventLibraryArchives getSpec(Specializations specializations) {
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
    public DatabaseBasePvEEventLibraryArchives getClass(Classes classes) {
        return switch (classes) {
            case MAGE -> mage;
            case WARRIOR -> warrior;
            case PALADIN -> paladin;
            case SHAMAN -> shaman;
            case ROGUE -> rogue;
            case ARCANIST -> arcanist;
        };
    }

    @Override
    public DatabaseBasePvEEventLibraryArchives[] getClasses() {
        return new DatabaseBasePvEEventLibraryArchives[]{mage, warrior, paladin, shaman, rogue};
    }

    @Override
    public PvEEventLibraryArchivesDatabaseStatInformation getMage() {
        return mage;
    }

    @Override
    public PvEEventLibraryArchivesDatabaseStatInformation getWarrior() {
        return warrior;
    }

    @Override
    public PvEEventLibraryArchivesDatabaseStatInformation getPaladin() {
        return paladin;
    }

    @Override
    public PvEEventLibraryArchivesDatabaseStatInformation getShaman() {
        return shaman;
    }

    @Override
    public PvEEventLibraryArchivesDatabaseStatInformation getRogue() {
        return rogue;
    }

    @Override
    public PvEEventLibraryArchivesDatabaseStatInformation getArcanist() {
        return arcanist;
    }

    public DatabasePlayerPvEEventLibraryArchivesPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventLibraryArchivesPlayerCountStats());
    }

    @Override
    public long getEventPointsSpent() {
        return eventPointsSpent;
    }

    @Override
    public void addEventPointsSpent(long eventPointsSpent) {
        this.eventPointsSpent += eventPointsSpent;
    }

    @Override
    public Map<String, Long> getRewardsPurchased() {
        return rewardsPurchased;
    }

    @Override
    public Map<Bounty, Long> getCompletedBounties() {
        return completedBounties;
    }

    @Override
    public int getBountiesCompleted() {
        return bountiesCompleted;
    }

    @Override
    public void addBountiesCompleted() {
        this.bountiesCompleted++;
    }

    @Override
    public List<AbstractBounty> getActiveBounties() {
        return activeBounties;
    }

    public DatabasePlayerPvEEventLibraryForgottenCodexDifficultyStats getForgottenCodexStats() {
        return forgottenCodexStats;
    }

    public void setForgottenCodexStats(DatabasePlayerPvEEventLibraryForgottenCodexDifficultyStats forgottenCodexStats) {
        this.forgottenCodexStats = forgottenCodexStats;
    }

    public DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardDifficultyStats getGrimoiresGraveyardStats() {
        return grimoiresGraveyardStats;
    }

    public void setGrimoiresGraveyardStats(DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardDifficultyStats grimoiresGraveyardStats) {
        this.grimoiresGraveyardStats = grimoiresGraveyardStats;
    }

    public Map<PlayerCodex, Integer> getCodexesEarned() {
        return codexesEarned;
    }
}
