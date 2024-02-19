package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEWaveDefenseDifficultyStats extends WaveDefenseDatabaseStatInformation implements DatabaseWarlordsClasses<WaveDefenseDatabaseStatInformation> {

    private DatabaseMagePvEWaveDefense mage = new DatabaseMagePvEWaveDefense();
    private DatabaseWarriorPvEWaveDefense warrior = new DatabaseWarriorPvEWaveDefense();
    private DatabasePaladinPvEWaveDefense paladin = new DatabasePaladinPvEWaveDefense();
    private DatabaseShamanPvEWaveDefense shaman = new DatabaseShamanPvEWaveDefense();
    private DatabaseRoguePvEWaveDefense rogue = new DatabaseRoguePvEWaveDefense();
    private DatabaseArcanistPvEWaveDefense arcanist = new DatabaseArcanistPvEWaveDefense();
    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEWaveDefensePlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEWaveDefensePlayerCountStats());
        put(2, new DatabasePlayerPvEWaveDefensePlayerCountStats());
        put(3, new DatabasePlayerPvEWaveDefensePlayerCountStats());
        put(4, new DatabasePlayerPvEWaveDefensePlayerCountStats());
    }};

    public DatabasePlayerPvEWaveDefenseDifficultyStats() {
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

        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        this.getSpec(gamePlayer.getSpec()).updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        //UPDATE PLAYER COUNT STATS
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEWaveDefensePlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }
    }

    @Override
    public DatabaseBasePvEWaveDefense getSpec(Specializations specializations) {
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
    public DatabaseBasePvEWaveDefense getClass(Classes classes) {
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
    public DatabaseBasePvEWaveDefense[] getClasses() {
        return new DatabaseBasePvEWaveDefense[]{mage, warrior, paladin, shaman, rogue};
    }

    public DatabasePlayerPvEWaveDefensePlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEWaveDefensePlayerCountStats());
    }

    public float getClearRate() {
        return plays == 0 ? 0 : (float) wins / plays;
    }

    public DatabaseMagePvEWaveDefense getMage() {
        return mage;
    }

    public DatabaseWarriorPvEWaveDefense getWarrior() {
        return warrior;
    }

    public DatabasePaladinPvEWaveDefense getPaladin() {
        return paladin;
    }

    public DatabaseShamanPvEWaveDefense getShaman() {
        return shaman;
    }

    public DatabaseRoguePvEWaveDefense getRogue() {
        return rogue;
    }

    @Override
    public WaveDefenseDatabaseStatInformation getArcanist() {
        return arcanist;
    }

    public Map<Integer, DatabasePlayerPvEWaveDefensePlayerCountStats> getPlayerCountStats() {
        return playerCountStats;
    }
}
