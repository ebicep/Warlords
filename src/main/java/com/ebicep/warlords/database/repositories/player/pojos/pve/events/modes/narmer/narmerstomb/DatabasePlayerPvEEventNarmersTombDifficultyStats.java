package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventNarmersTombDifficultyStats extends PvEEventNarmersTombDatabaseStatInformation implements DatabaseWarlordsClasses<PvEEventNarmersTombDatabaseStatInformation> {

    private DatabaseMagePvEEventNarmersTomb mage = new DatabaseMagePvEEventNarmersTomb();
    private DatabaseWarriorPvEEventNarmersTomb warrior = new DatabaseWarriorPvEEventNarmersTomb();
    private DatabasePaladinPvEEventNarmersTomb paladin = new DatabasePaladinPvEEventNarmersTomb();
    private DatabaseShamanPvEEventNarmersTomb shaman = new DatabaseShamanPvEEventNarmersTomb();
    private DatabaseRoguePvEEventNarmersTomb rogue = new DatabaseRoguePvEEventNarmersTomb();
    private DatabaseArcanistPvEEventNarmersTomb arcanist = new DatabaseArcanistPvEEventNarmersTomb();
    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventNarmersTombPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventNarmersTombPlayerCountStats());
        put(2, new DatabasePlayerPvEEventNarmersTombPlayerCountStats());
        put(3, new DatabasePlayerPvEEventNarmersTombPlayerCountStats());
        put(4, new DatabasePlayerPvEEventNarmersTombPlayerCountStats());
    }};

    public DatabasePlayerPvEEventNarmersTombDifficultyStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert gamePlayer instanceof DatabaseGamePlayerPvEWaveDefense;

        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        //UPDATE UNIVERSAL EXPERIENCE
        this.experience += gamePlayer.getExperienceEarnedUniversal() * multiplier;
        this.experiencePvE += gamePlayer.getExperienceEarnedUniversal() * multiplier;

        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        this.getSpec(gamePlayer.getSpec()).updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        //UPDATE PLAYER COUNT STATS
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEEventNarmersTombPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }

    }

    @Override
    public DatabaseBasePvEEventNarmersTomb getSpec(Specializations specializations) {
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
    public DatabaseBasePvEEventNarmersTomb getClass(Classes classes) {
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
    public DatabaseBasePvEEventNarmersTomb[] getClasses() {
        return new DatabaseBasePvEEventNarmersTomb[]{mage, warrior, paladin, shaman, rogue};
    }

    @Override
    public PvEEventNarmersTombDatabaseStatInformation getMage() {
        return mage;
    }

    @Override
    public PvEEventNarmersTombDatabaseStatInformation getWarrior() {
        return warrior;
    }

    @Override
    public PvEEventNarmersTombDatabaseStatInformation getPaladin() {
        return paladin;
    }

    @Override
    public PvEEventNarmersTombDatabaseStatInformation getShaman() {
        return shaman;
    }

    @Override
    public PvEEventNarmersTombDatabaseStatInformation getRogue() {
        return rogue;
    }

    @Override
    public PvEEventNarmersTombDatabaseStatInformation getArcanist() {
        return arcanist;
    }

    public DatabasePlayerPvEEventNarmersTombPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventNarmersTombPlayerCountStats());
    }

}
