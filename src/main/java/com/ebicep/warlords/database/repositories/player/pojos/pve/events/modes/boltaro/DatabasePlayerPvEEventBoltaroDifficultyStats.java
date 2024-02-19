package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePvEEventBoltaroLair;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.DatabasePlayerPvEEventBoltaroBonanzaDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.DatabasePlayerPvEEventBoltaroLairDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

public class DatabasePlayerPvEEventBoltaroDifficultyStats extends PvEEventBoltaroDatabaseStatInformation implements DatabaseWarlordsClasses<PvEEventBoltaroDatabaseStatInformation>, EventMode {

    private DatabaseMagePvEEventBoltaro mage = new DatabaseMagePvEEventBoltaro();
    private DatabaseWarriorPvEEventBoltaro warrior = new DatabaseWarriorPvEEventBoltaro();
    private DatabasePaladinPvEEventBoltaro paladin = new DatabasePaladinPvEEventBoltaro();
    private DatabaseShamanPvEEventBoltaro shaman = new DatabaseShamanPvEEventBoltaro();
    private DatabaseRoguePvEEventBoltaro rogue = new DatabaseRoguePvEEventBoltaro();
    private DatabaseArcanistPvEEventBoltaro arcanist = new DatabaseArcanistPvEEventBoltaro();
    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventBoltaroPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventBoltaroPlayerCountStats());
        put(2, new DatabasePlayerPvEEventBoltaroPlayerCountStats());
        put(3, new DatabasePlayerPvEEventBoltaroPlayerCountStats());
        put(4, new DatabasePlayerPvEEventBoltaroPlayerCountStats());
    }};
    @Field("event_points_spent")
    private long eventPointsSpent;
    @Field("rewards_purchased")
    private Map<String, Long> rewardsPurchased = new LinkedHashMap<>();
    @Field("lair_stats")
    private DatabasePlayerPvEEventBoltaroLairDifficultyStats lairStats = new DatabasePlayerPvEEventBoltaroLairDifficultyStats();
    @Field("bonanza_stats")
    private DatabasePlayerPvEEventBoltaroBonanzaDifficultyStats bonanzaStats = new DatabasePlayerPvEEventBoltaroBonanzaDifficultyStats();

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

    public DatabasePlayerPvEEventBoltaroDifficultyStats() {
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
        DatabasePlayerPvEEventBoltaroPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }

        //MODES
        if (databaseGame instanceof DatabaseGamePvEEventBoltaroLair) {
            this.lairStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else if (databaseGame instanceof DatabaseGamePvEEventBoltaroBonanza) {
            this.bonanzaStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        }

    }

    @Override
    public DatabaseBasePvEEventBoltaro getSpec(Specializations specializations) {
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
    public DatabaseBasePvEEventBoltaro getClass(Classes classes) {
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
    public DatabaseBasePvEEventBoltaro[] getClasses() {
        return new DatabaseBasePvEEventBoltaro[]{mage, warrior, paladin, shaman, rogue};
    }

    @Override
    public PvEEventBoltaroDatabaseStatInformation getMage() {
        return mage;
    }

    @Override
    public PvEEventBoltaroDatabaseStatInformation getWarrior() {
        return warrior;
    }

    @Override
    public PvEEventBoltaroDatabaseStatInformation getPaladin() {
        return paladin;
    }

    @Override
    public PvEEventBoltaroDatabaseStatInformation getShaman() {
        return shaman;
    }

    @Override
    public PvEEventBoltaroDatabaseStatInformation getRogue() {
        return rogue;
    }

    @Override
    public PvEEventBoltaroDatabaseStatInformation getArcanist() {
        return arcanist;
    }

    public DatabasePlayerPvEEventBoltaroPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventBoltaroPlayerCountStats());
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

    public DatabasePlayerPvEEventBoltaroLairDifficultyStats getLairStats() {
        return lairStats;
    }

    public DatabasePlayerPvEEventBoltaroBonanzaDifficultyStats getBonanzaStats() {
        return bonanzaStats;
    }

    public void setLairStats(DatabasePlayerPvEEventBoltaroLairDifficultyStats lairStats) {
        this.lairStats = lairStats;
    }

    public void setBonanzaStats(DatabasePlayerPvEEventBoltaroBonanzaDifficultyStats bonanzaStats) {
        this.bonanzaStats = bonanzaStats;
    }
}
