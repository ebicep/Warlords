package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventGardenOfHesperidesAcropolisDifficultyStats extends PvEEventGardenOfHesperidesAcropolisDatabaseStatInformation implements DatabaseWarlordsClasses<PvEEventGardenOfHesperidesAcropolisDatabaseStatInformation> {

    private DatabaseMagePvEEventGardenOfHesperidesAcropolis mage = new DatabaseMagePvEEventGardenOfHesperidesAcropolis();
    private DatabaseWarriorPvEEventGardenOfHesperidesAcropolis warrior = new DatabaseWarriorPvEEventGardenOfHesperidesAcropolis();
    private DatabasePaladinPvEEventGardenOfHesperidesAcropolis paladin = new DatabasePaladinPvEEventGardenOfHesperidesAcropolis();
    private DatabaseShamanPvEEventGardenOfHesperidesAcropolis shaman = new DatabaseShamanPvEEventGardenOfHesperidesAcropolis();
    private DatabaseRoguePvEEventGardenOfHesperidesAcropolis rogue = new DatabaseRoguePvEEventGardenOfHesperidesAcropolis();
    private DatabaseArcanistPvEEventGardenOfHesperidesAcropolis arcanist = new DatabaseArcanistPvEEventGardenOfHesperidesAcropolis();
    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats());
        put(2, new DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats());
        put(3, new DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats());
        put(4, new DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats());
    }};

    public DatabasePlayerPvEEventGardenOfHesperidesAcropolisDifficultyStats() {
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
        DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }

    }

    @Override
    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getSpec(Specializations specializations) {
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
    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getClass(Classes classes) {
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
    public DatabaseBasePvEEventGardenOfHesperidesAcropolis[] getClasses() {
        return new DatabaseBasePvEEventGardenOfHesperidesAcropolis[]{mage, warrior, paladin, shaman, rogue};
    }

    @Override
    public PvEEventGardenOfHesperidesAcropolisDatabaseStatInformation getMage() {
        return mage;
    }

    @Override
    public PvEEventGardenOfHesperidesAcropolisDatabaseStatInformation getWarrior() {
        return warrior;
    }

    @Override
    public PvEEventGardenOfHesperidesAcropolisDatabaseStatInformation getPaladin() {
        return paladin;
    }

    @Override
    public PvEEventGardenOfHesperidesAcropolisDatabaseStatInformation getShaman() {
        return shaman;
    }

    @Override
    public PvEEventGardenOfHesperidesAcropolisDatabaseStatInformation getRogue() {
        return rogue;
    }

    @Override
    public PvEEventGardenOfHesperidesAcropolisDatabaseStatInformation getArcanist() {
        return arcanist;
    }

    public DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats());
    }

}
