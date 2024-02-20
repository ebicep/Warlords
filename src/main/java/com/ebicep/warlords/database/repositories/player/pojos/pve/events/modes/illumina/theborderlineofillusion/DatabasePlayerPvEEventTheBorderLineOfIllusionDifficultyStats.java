package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventTheBorderLineOfIllusionDifficultyStats extends PvEEventTheBorderLineOfIllusionDatabaseStatInformation implements DatabaseWarlordsClasses<PvEEventTheBorderLineOfIllusionDatabaseStatInformation> {

    private DatabaseMagePvEEventTheBorderLineOfIllusion mage = new DatabaseMagePvEEventTheBorderLineOfIllusion();
    private DatabaseWarriorPvEEventTheBorderLineOfIllusion warrior = new DatabaseWarriorPvEEventTheBorderLineOfIllusion();
    private DatabasePaladinPvEEventTheBorderLineOfIllusion paladin = new DatabasePaladinPvEEventTheBorderLineOfIllusion();
    private DatabaseShamanPvEEventTheBorderLineOfIllusion shaman = new DatabaseShamanPvEEventTheBorderLineOfIllusion();
    private DatabaseRoguePvEEventTheBorderLineOfIllusion rogue = new DatabaseRoguePvEEventTheBorderLineOfIllusion();
    private DatabaseArcanistPvEEventTheBorderLineOfIllusion arcanist = new DatabaseArcanistPvEEventTheBorderLineOfIllusion();
    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats());
        put(2, new DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats());
        put(3, new DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats());
        put(4, new DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats());
    }};

    public DatabasePlayerPvEEventTheBorderLineOfIllusionDifficultyStats() {
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
        DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }

    }

    @Override
    public DatabaseBasePvEEventTheBorderLineOfIllusion getSpec(Specializations specializations) {
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
    public DatabaseBasePvEEventTheBorderLineOfIllusion getClass(Classes classes) {
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
    public DatabaseBasePvEEventTheBorderLineOfIllusion[] getClasses() {
        return new DatabaseBasePvEEventTheBorderLineOfIllusion[]{mage, warrior, paladin, shaman, rogue};
    }

    @Override
    public PvEEventTheBorderLineOfIllusionDatabaseStatInformation getMage() {
        return mage;
    }

    @Override
    public PvEEventTheBorderLineOfIllusionDatabaseStatInformation getWarrior() {
        return warrior;
    }

    @Override
    public PvEEventTheBorderLineOfIllusionDatabaseStatInformation getPaladin() {
        return paladin;
    }

    @Override
    public PvEEventTheBorderLineOfIllusionDatabaseStatInformation getShaman() {
        return shaman;
    }

    @Override
    public PvEEventTheBorderLineOfIllusionDatabaseStatInformation getRogue() {
        return rogue;
    }

    @Override
    public PvEEventTheBorderLineOfIllusionDatabaseStatInformation getArcanist() {
        return arcanist;
    }

    public DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats());
    }

}
