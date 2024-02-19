package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.theborderlineofillusion.DatabaseGamePvEEventTheBorderlineOfIllusion;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.classes.*;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.DatabasePlayerPvEEventTheBorderLineOfIllusionDifficultyStats;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

public class DatabasePlayerPvEEventIlluminaDifficultyStats extends PvEEventIlluminaDatabaseStatInformation implements DatabaseWarlordsClasses<PvEEventIlluminaDatabaseStatInformation>, EventMode {

    private DatabaseMagePvEEventIllumina mage = new DatabaseMagePvEEventIllumina();
    private DatabaseWarriorPvEEventIllumina warrior = new DatabaseWarriorPvEEventIllumina();
    private DatabasePaladinPvEEventIllumina paladin = new DatabasePaladinPvEEventIllumina();
    private DatabaseShamanPvEEventIllumina shaman = new DatabaseShamanPvEEventIllumina();
    private DatabaseRoguePvEEventIllumina rogue = new DatabaseRoguePvEEventIllumina();
    private DatabaseArcanistPvEEventIllumina arcanist = new DatabaseArcanistPvEEventIllumina();
    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventIlluminaPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventIlluminaPlayerCountStats());
        put(2, new DatabasePlayerPvEEventIlluminaPlayerCountStats());
        put(3, new DatabasePlayerPvEEventIlluminaPlayerCountStats());
        put(4, new DatabasePlayerPvEEventIlluminaPlayerCountStats());
    }};
    @Field("event_points_spent")
    private long eventPointsSpent;
    @Field("rewards_purchased")
    private Map<String, Long> rewardsPurchased = new LinkedHashMap<>();
    @Field("the_borderline_of_illusion_stats")
    private DatabasePlayerPvEEventTheBorderLineOfIllusionDifficultyStats borderLineOfIllusionStats = new DatabasePlayerPvEEventTheBorderLineOfIllusionDifficultyStats();

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

    public DatabasePlayerPvEEventIlluminaDifficultyStats() {
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
        DatabasePlayerPvEEventIlluminaPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }

        //MODES
        if (databaseGame instanceof DatabaseGamePvEEventTheBorderlineOfIllusion) {
            this.borderLineOfIllusionStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        }
    }

    @Override
    public DatabaseBasePvEEventIllumina getSpec(Specializations specializations) {
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
    public DatabaseBasePvEEventIllumina getClass(Classes classes) {
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
    public DatabaseBasePvEEventIllumina[] getClasses() {
        return new DatabaseBasePvEEventIllumina[]{mage, warrior, paladin, shaman, rogue};
    }

    @Override
    public PvEEventIlluminaDatabaseStatInformation getMage() {
        return mage;
    }

    @Override
    public PvEEventIlluminaDatabaseStatInformation getWarrior() {
        return warrior;
    }

    @Override
    public PvEEventIlluminaDatabaseStatInformation getPaladin() {
        return paladin;
    }

    @Override
    public PvEEventIlluminaDatabaseStatInformation getShaman() {
        return shaman;
    }

    @Override
    public PvEEventIlluminaDatabaseStatInformation getRogue() {
        return rogue;
    }

    @Override
    public PvEEventIlluminaDatabaseStatInformation getArcanist() {
        return arcanist;
    }

    public DatabasePlayerPvEEventIlluminaPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventIlluminaPlayerCountStats());
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

    public DatabasePlayerPvEEventTheBorderLineOfIllusionDifficultyStats getBorderLineOfIllusionStats() {
        return borderLineOfIllusionStats;
    }
}
