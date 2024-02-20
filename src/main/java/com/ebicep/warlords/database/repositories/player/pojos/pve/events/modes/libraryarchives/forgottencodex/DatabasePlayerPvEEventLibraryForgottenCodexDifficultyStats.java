package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventLibraryForgottenCodexDifficultyStats extends PvEEventLibraryForgottenCodexDatabaseStatInformation implements DatabaseWarlordsClasses<PvEEventLibraryForgottenCodexDatabaseStatInformation> {

    private DatabaseMagePvEEventLibraryForgottenCodex mage = new DatabaseMagePvEEventLibraryForgottenCodex();
    private DatabaseWarriorPvEEventLibraryForgottenCodex warrior = new DatabaseWarriorPvEEventLibraryForgottenCodex();
    private DatabasePaladinPvEEventLibraryForgottenCodex paladin = new DatabasePaladinPvEEventLibraryForgottenCodex();
    private DatabaseShamanPvEEventLibraryForgottenCodex shaman = new DatabaseShamanPvEEventLibraryForgottenCodex();
    private DatabaseRoguePvEEventLibraryForgottenCodex rogue = new DatabaseRoguePvEEventLibraryForgottenCodex();
    private DatabaseArcanistPvEEventLibraryForgottenCodex arcanist = new DatabaseArcanistPvEEventLibraryForgottenCodex();
    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats());
        put(2, new DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats());
        put(3, new DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats());
        put(4, new DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats());
    }};

    public DatabasePlayerPvEEventLibraryForgottenCodexDifficultyStats() {
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
        DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }

    }

    @Override
    public DatabaseBasePvEEventLibraryForgottenCodex getSpec(Specializations specializations) {
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
    public DatabaseBasePvEEventLibraryForgottenCodex getClass(Classes classes) {
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
    public DatabaseBasePvEEventLibraryForgottenCodex[] getClasses() {
        return new DatabaseBasePvEEventLibraryForgottenCodex[]{mage, warrior, paladin, shaman, rogue};
    }

    @Override
    public PvEEventLibraryForgottenCodexDatabaseStatInformation getMage() {
        return mage;
    }

    @Override
    public PvEEventLibraryForgottenCodexDatabaseStatInformation getWarrior() {
        return warrior;
    }

    @Override
    public PvEEventLibraryForgottenCodexDatabaseStatInformation getPaladin() {
        return paladin;
    }

    @Override
    public PvEEventLibraryForgottenCodexDatabaseStatInformation getShaman() {
        return shaman;
    }

    @Override
    public PvEEventLibraryForgottenCodexDatabaseStatInformation getRogue() {
        return rogue;
    }

    @Override
    public PvEEventLibraryForgottenCodexDatabaseStatInformation getArcanist() {
        return arcanist;
    }

    public DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats());
    }

}
