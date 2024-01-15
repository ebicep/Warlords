package com.ebicep.warlords.database.repositories.player.pojos.siege;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGamePlayerSiege;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.siege.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatUtils;

public class DatabasePlayerSiege extends SiegeDatabaseStatInformation implements DatabaseWarlordsClasses<SiegeDatabaseStatInformation> {

    private DatabaseMageSiege mage = new DatabaseMageSiege();
    private DatabaseWarriorSiege warrior = new DatabaseWarriorSiege();
    private DatabasePaladinSiege paladin = new DatabasePaladinSiege();
    private DatabaseShamanSiege shaman = new DatabaseShamanSiege();
    private DatabaseRogueSiege rogue = new DatabaseRogueSiege();
    private DatabaseArcanistSiege arcanist = new DatabaseArcanistSiege();

    @Override
    public void updateCustomStats(
            DatabasePlayer databasePlayer,
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        //UPDATE UNIVERSAL EXPERIENCE
        this.experience += gamePlayer.getExperienceEarnedUniversal() * multiplier;

        if (gamePlayer instanceof DatabaseGamePlayerSiege databaseGamePlayerSiege) {
            databaseGamePlayerSiege.getSpecStats().forEach((specializations, siegePlayer) -> {
                //UPDATE CLASS, SPEC
                this.getClass(Specializations.getClass(specializations)).updateStats(databasePlayer, databaseGame, siegePlayer, multiplier, playersCollection);
                this.getSpec(specializations).updateStats(databasePlayer, databaseGame, siegePlayer, multiplier, playersCollection);
            });
        } else {
            ChatUtils.MessageType.GAME.sendErrorMessage("DatabaseGamePlayerSiege is not an instance of DatabaseGamePlayerSiege");
        }
    }

    @Override
    public DatabaseBaseSiege getSpec(Specializations specializations) {
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
    public DatabaseBaseSiege getClass(Classes classes) {
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
    public DatabaseBaseSiege[] getClasses() {
        return new DatabaseBaseSiege[]{mage, warrior, paladin, shaman, rogue};
    }

    @Override
    public SiegeDatabaseStatInformation getMage() {
        return mage;
    }

    @Override
    public SiegeDatabaseStatInformation getWarrior() {
        return warrior;
    }

    @Override
    public SiegeDatabaseStatInformation getPaladin() {
        return paladin;
    }

    @Override
    public SiegeDatabaseStatInformation getShaman() {
        return shaman;
    }

    @Override
    public SiegeDatabaseStatInformation getRogue() {
        return rogue;
    }

    @Override
    public SiegeDatabaseStatInformation getArcanist() {
        return arcanist;
    }
}
