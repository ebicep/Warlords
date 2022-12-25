package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvE;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventDifficultyStats extends PvEEventDatabaseStatInformation implements DatabasePlayer {

    private DatabaseMagePvEEvent mage = new DatabaseMagePvEEvent();
    private DatabaseWarriorPvEEvent warrior = new DatabaseWarriorPvEEvent();
    private DatabasePaladinPvEEvent paladin = new DatabasePaladinPvEEvent();
    private DatabaseShamanPvEEvent shaman = new DatabaseShamanPvEEvent();
    private DatabaseRoguePvEEvent rogue = new DatabaseRoguePvEEvent();
    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventPlayerCountStats());
        put(2, new DatabasePlayerPvEEventPlayerCountStats());
        put(3, new DatabasePlayerPvEEventPlayerCountStats());
        put(4, new DatabasePlayerPvEEventPlayerCountStats());
    }};

    public DatabasePlayerPvEEventDifficultyStats() {
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
        DatabasePlayerPvEEventPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageTypes.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }

    }

    @Override
    public DatabaseBasePvEEvent getSpec(Specializations specializations) {
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
    public DatabaseBasePvEEvent getClass(Classes classes) {
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
    public DatabaseBasePvEEvent[] getClasses() {
        return new DatabaseBasePvEEvent[]{mage, warrior, paladin, shaman, rogue};
    }

    public DatabasePlayerPvEEventPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventPlayerCountStats());
    }
}
