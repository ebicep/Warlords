package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePvEEventTartarus;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.theacropolis.DatabaseGamePvEEventTheAcropolis;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.classes.*;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.DatabasePlayerPvEEventGardenOfHesperidesTartarusDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.DatabasePlayerPvEEventGardenOfHesperidesAcropolisDifficultyStats;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

public class DatabasePlayerPvEEventGardenOfHesperidesDifficultyStats extends PvEEventGardenOfHesperidesDatabaseStatInformation implements DatabaseWarlordsClasses<PvEEventGardenOfHesperidesDatabaseStatInformation>, EventMode {

    private DatabaseMagePvEEventGardenOfHesperides mage = new DatabaseMagePvEEventGardenOfHesperides();
    private DatabaseWarriorPvEEventGardenOfHesperides warrior = new DatabaseWarriorPvEEventGardenOfHesperides();
    private DatabasePaladinPvEEventGardenOfHesperides paladin = new DatabasePaladinPvEEventGardenOfHesperides();
    private DatabaseShamanPvEEventGardenOfHesperides shaman = new DatabaseShamanPvEEventGardenOfHesperides();
    private DatabaseRoguePvEEventGardenOfHesperides rogue = new DatabaseRoguePvEEventGardenOfHesperides();
    private DatabaseArcanistPvEEventGardenOfHesperides arcanist = new DatabaseArcanistPvEEventGardenOfHesperides();
    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventGardenOfHesperidesPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventGardenOfHesperidesPlayerCountStats());
        put(2, new DatabasePlayerPvEEventGardenOfHesperidesPlayerCountStats());
        put(3, new DatabasePlayerPvEEventGardenOfHesperidesPlayerCountStats());
        put(4, new DatabasePlayerPvEEventGardenOfHesperidesPlayerCountStats());
    }};
    @Field("event_points_spent")
    private long eventPointsSpent;
    @Field("rewards_purchased")
    private Map<String, Long> rewardsPurchased = new LinkedHashMap<>();
    @Field("acropolis_stats")
    private DatabasePlayerPvEEventGardenOfHesperidesAcropolisDifficultyStats acropolisStats = new DatabasePlayerPvEEventGardenOfHesperidesAcropolisDifficultyStats();
    @Field("tartarus_stats")
    private DatabasePlayerPvEEventGardenOfHesperidesTartarusDifficultyStats tartarusStats = new DatabasePlayerPvEEventGardenOfHesperidesTartarusDifficultyStats();
    @Field("tartarus_auto_ready")
    private boolean tartarusAutoReady = false;

    @Field("completed_bounties")
    private Map<Bounty, Long> completedBounties = new HashMap<>();
    @Field("bounties_completed")
    private int bountiesCompleted = 0;
    @Field("active_bounties")
    private List<AbstractBounty> activeBounties = new ArrayList<>();

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

    public DatabasePlayerPvEEventGardenOfHesperidesDifficultyStats() {
    }

    @Override
    public void updateCustomStats(
            com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
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
        DatabasePlayerPvEEventGardenOfHesperidesPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }

        //MODES
        if (databaseGame instanceof DatabaseGamePvEEventTheAcropolis) {
            this.acropolisStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else if (databaseGame instanceof DatabaseGamePvEEventTartarus) {
            this.tartarusStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        }

    }

    @Override
    public DatabaseBasePvEEventGardenOfHesperides getSpec(Specializations specializations) {
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
    public DatabaseBasePvEEventGardenOfHesperides getClass(Classes classes) {
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
    public DatabaseBasePvEEventGardenOfHesperides[] getClasses() {
        return new DatabaseBasePvEEventGardenOfHesperides[]{mage, warrior, paladin, shaman, rogue};
    }

    @Override
    public PvEEventGardenOfHesperidesDatabaseStatInformation getMage() {
        return mage;
    }

    @Override
    public PvEEventGardenOfHesperidesDatabaseStatInformation getWarrior() {
        return warrior;
    }

    @Override
    public PvEEventGardenOfHesperidesDatabaseStatInformation getPaladin() {
        return paladin;
    }

    @Override
    public PvEEventGardenOfHesperidesDatabaseStatInformation getShaman() {
        return shaman;
    }

    @Override
    public PvEEventGardenOfHesperidesDatabaseStatInformation getRogue() {
        return rogue;
    }

    @Override
    public PvEEventGardenOfHesperidesDatabaseStatInformation getArcanist() {
        return arcanist;
    }

    public DatabasePlayerPvEEventGardenOfHesperidesPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventGardenOfHesperidesPlayerCountStats());
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

    public DatabasePlayerPvEEventGardenOfHesperidesAcropolisDifficultyStats getAcropolisStats() {
        return acropolisStats;
    }

    public DatabasePlayerPvEEventGardenOfHesperidesTartarusDifficultyStats getTartarusStats() {
        return tartarusStats;
    }

    public void setAcropolisStats(DatabasePlayerPvEEventGardenOfHesperidesAcropolisDifficultyStats acropolisStats) {
        this.acropolisStats = acropolisStats;
    }

    public void setTartarusStats(DatabasePlayerPvEEventGardenOfHesperidesTartarusDifficultyStats tartarusStats) {
        this.tartarusStats = tartarusStats;
    }

    public boolean isTartarusAutoReady() {
        return tartarusAutoReady;
    }

    public void setTartarusAutoReady(boolean tartarusAutoReady) {
        this.tartarusAutoReady = tartarusAutoReady;
    }
}
