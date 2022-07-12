package com.ebicep.warlords.database.repositories.player.pojos.pve;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

public class DatabasePlayerPvE extends PvEDatabaseStatInformation implements DatabasePlayer {

    private DatabaseMagePvE mage = new DatabaseMagePvE();
    private DatabaseWarriorPvE warrior = new DatabaseWarriorPvE();
    private DatabasePaladinPvE paladin = new DatabasePaladinPvE();
    private DatabaseShamanPvE shaman = new DatabaseShamanPvE();
    private DatabaseRoguePvE rogue = new DatabaseRoguePvE();
    @Field("weapon_inventory")
    private List<AbstractWeapon> weaponInventory = new ArrayList<>();
    @Field("synthetic_shards")
    private int amountOfSyntheticShards = 0;
    @Field("legend_fragments")
    private int amountOfLegendFragments = 0;
    @Field("fairy_essence")
    private int amountOfFairyEssence = 0;
    @Field("common_star_pieces")
    private int amountOfCommonStarPieces = 0;
    @Field("rare_star_pieces")
    private int amountOfRareStarPieces = 0;
    @Field("epic_star_pieces")
    private int amountOfEpicStarPieces = 0;
    @Field("legendary_star_pieces")
    private int amountOfLegendaryStarPieces = 0;


    @Override
    public void updateCustomStats(DatabaseGameBase databaseGame, GameMode gameMode, DatabaseGamePlayerBase gamePlayer, DatabaseGamePlayerResult result, boolean add) {
        super.updateCustomStats(databaseGame, gameMode, gamePlayer, result, add);

        //UPDATE UNIVERSAL EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedUniversal() : -gamePlayer.getExperienceEarnedUniversal();

        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateStats(databaseGame, gamePlayer, add);
        this.getSpec(gamePlayer.getSpec()).updateStats(databaseGame, gamePlayer, add);
    }

    @Override
    public AbstractDatabaseStatInformation getSpec(Specializations specializations) {
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
    public AbstractDatabaseStatInformation getClass(Classes classes) {
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

    public List<AbstractWeapon> getWeaponInventory() {
        return weaponInventory;
    }

    public int getAmountOfSyntheticShards() {
        return amountOfSyntheticShards;
    }

    public void addSyntheticAlloy(int amount) {
        this.amountOfSyntheticShards += amount;
    }

    public int getAmountOfLegendFragments() {
        return amountOfLegendFragments;
    }

    public void addLegendFragments(int amount) {
        this.amountOfLegendFragments += amount;
    }

    public int getAmountOfFairyEssence() {
        return amountOfFairyEssence;
    }

    public void setAmountOfFairyEssence(int amountOfFairyEssence) {
        this.amountOfFairyEssence = amountOfFairyEssence;
    }

    public void addFairyEssence(int amount) {
        this.amountOfFairyEssence += amount;
    }

    public int getAmountOfCommonStarPieces() {
        return amountOfCommonStarPieces;
    }

    public int getAmountOfRareStarPieces() {
        return amountOfRareStarPieces;
    }

    public int getAmountOfEpicStarPieces() {
        return amountOfEpicStarPieces;
    }

    public int getAmountOfLegendaryStarPieces() {
        return amountOfLegendaryStarPieces;
    }
}
