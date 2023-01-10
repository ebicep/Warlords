package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvE;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePvEEventBoltaroLair;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.DatabasePlayerPvEEventBoltaroBonanzaDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.DatabasePlayerPvEEventBoltaroLairDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventBoltaroDifficultyStats extends PvEEventBoltaroDatabaseStatInformation implements DatabasePlayer, EventMode {

    private DatabaseMagePvEEventBoltaro mage = new DatabaseMagePvEEventBoltaro();
    private DatabaseWarriorPvEEventBoltaro warrior = new DatabaseWarriorPvEEventBoltaro();
    private DatabasePaladinPvEEventBoltaro paladin = new DatabasePaladinPvEEventBoltaro();
    private DatabaseShamanPvEEventBoltaro shaman = new DatabaseShamanPvEEventBoltaro();
    private DatabaseRoguePvEEventBoltaro rogue = new DatabaseRoguePvEEventBoltaro();
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

    public DatabasePlayerPvEEventBoltaroDifficultyStats() {
    }

    @Override
    public void updateCustomStats(
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert gamePlayer instanceof DatabaseGamePlayerPvE;

        super.updateCustomStats(databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        //UPDATE UNIVERSAL EXPERIENCE
        this.experience += gamePlayer.getExperienceEarnedUniversal() * multiplier;
        this.experiencePvE += gamePlayer.getExperienceEarnedUniversal() * multiplier;

        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateStats(databaseGame, gamePlayer, multiplier, playersCollection);
        this.getSpec(gamePlayer.getSpec()).updateStats(databaseGame, gamePlayer, multiplier, playersCollection);

        //UPDATE PLAYER COUNT STATS
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEEventBoltaroPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageTypes.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }

        //MODES
        if (databaseGame instanceof DatabaseGamePvEEventBoltaroLair) {
            this.lairStats.updateStats(databaseGame, gamePlayer, multiplier, playersCollection);
        } else if (databaseGame instanceof DatabaseGamePvEEventBoltaroBonanza) {
            this.bonanzaStats.updateStats(databaseGame, gamePlayer, multiplier, playersCollection);
        }

    }

    @Override
    public DatabaseBasePvEEventBoltaro getSpec(Specializations specializations) {
        switch (specializations) {
            case PYROMANCER:
                return mage.getPyromancer();
            case CRYOMANCER:
                return mage.getCryomancer();
            case AQUAMANCER:
                return mage.getAquamancer();
            case BERSERKER:
                return warrior.getBerserker();
            case DEFENDER:
                return warrior.getDefender();
            case REVENANT:
                return warrior.getRevenant();
            case AVENGER:
                return paladin.getAvenger();
            case CRUSADER:
                return paladin.getCrusader();
            case PROTECTOR:
                return paladin.getProtector();
            case THUNDERLORD:
                return shaman.getThunderlord();
            case SPIRITGUARD:
                return shaman.getSpiritguard();
            case EARTHWARDEN:
                return shaman.getEarthwarden();
            case ASSASSIN:
                return rogue.getAssassin();
            case VINDICATOR:
                return rogue.getVindicator();
            case APOTHECARY:
                return rogue.getApothecary();
        }
        return null;
    }

    @Override
    public DatabaseBasePvEEventBoltaro getClass(Classes classes) {
        switch (classes) {
            case MAGE:
                return mage;
            case WARRIOR:
                return warrior;
            case PALADIN:
                return paladin;
            case SHAMAN:
                return shaman;
            case ROGUE:
                return rogue;
        }
        return null;
    }

    @Override
    public DatabaseBasePvEEventBoltaro[] getClasses() {
        return new DatabaseBasePvEEventBoltaro[]{mage, warrior, paladin, shaman, rogue};
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