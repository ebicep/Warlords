package com.ebicep.warlords.database.repositories.player.pojos.pve;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.pve.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEDifficultyStats extends PvEDatabaseStatInformation implements DatabaseWarlordsClasses<PvEDatabaseStatInformation> {

    private DatabaseMagePvE mage = new DatabaseMagePvE();
    private DatabaseWarriorPvE warrior = new DatabaseWarriorPvE();
    private DatabasePaladinPvE paladin = new DatabasePaladinPvE();
    private DatabaseShamanPvE shaman = new DatabaseShamanPvE();
    private DatabaseRoguePvE rogue = new DatabaseRoguePvE();
    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEPlayerCountStats());
        put(2, new DatabasePlayerPvEPlayerCountStats());
        put(3, new DatabasePlayerPvEPlayerCountStats());
        put(4, new DatabasePlayerPvEPlayerCountStats());
    }};

    public DatabasePlayerPvEDifficultyStats() {
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
        assert gamePlayer instanceof DatabaseGamePlayerPvEBase;

        super.updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        //UPDATE UNIVERSAL EXPERIENCE
        this.experience += gamePlayer.getExperienceEarnedUniversal() * multiplier;

        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        this.getSpec(gamePlayer.getSpec()).updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);

        //UPDATE PLAYER COUNT STATS
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageTypes.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }
    }

    @Override
    public DatabaseBasePvE getSpec(Specializations specializations) {
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
    public DatabaseBasePvE getClass(Classes classes) {
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
    public DatabaseBasePvE[] getClasses() {
        return new DatabaseBasePvE[]{mage, warrior, paladin, shaman, rogue};
    }

    public DatabaseMagePvE getMage() {
        return mage;
    }

    public DatabaseWarriorPvE getWarrior() {
        return warrior;
    }

    public DatabasePaladinPvE getPaladin() {
        return paladin;
    }

    public DatabaseShamanPvE getShaman() {
        return shaman;
    }

    public DatabaseRoguePvE getRogue() {
        return rogue;
    }

    public Map<Integer, DatabasePlayerPvEPlayerCountStats> getPlayerCountStats() {
        return playerCountStats;
    }

    public DatabasePlayerPvEPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEPlayerCountStats());
    }

    public float getClearRate() {
        return plays == 0 ? 0 : (float) wins / plays;
    }

    public void setMage(DatabaseMagePvE mage) {
        this.mage = mage;
    }

    public void setWarrior(DatabaseWarriorPvE warrior) {
        this.warrior = warrior;
    }

    public void setPaladin(DatabasePaladinPvE paladin) {
        this.paladin = paladin;
    }

    public void setShaman(DatabaseShamanPvE shaman) {
        this.shaman = shaman;
    }

    public void setRogue(DatabaseRoguePvE rogue) {
        this.rogue = rogue;
    }

    public void setPlayerCountStats(Map<Integer, DatabasePlayerPvEPlayerCountStats> playerCountStats) {
        this.playerCountStats = playerCountStats;
    }
}
