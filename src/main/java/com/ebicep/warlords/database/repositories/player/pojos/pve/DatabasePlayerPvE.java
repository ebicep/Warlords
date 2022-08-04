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
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairEntry;
import com.ebicep.warlords.pve.events.supplydrop.SupplyDropEntry;
import com.ebicep.warlords.pve.rewards.MasterworksFairReward;
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
    //GENERAL
    @Field("masterworks_fair_rewards")
    private List<MasterworksFairReward> masterworksFairRewards = new ArrayList<>();
    @Field("coins")
    private int coins = 0;
    //WEAPONS
    @Field("weapon_inventory")
    private List<AbstractWeapon> weaponInventory = new ArrayList<>();
    @Field("synthetic_shards")
    private int syntheticShards = 0;
    @Field("legend_fragments")
    private int legendFragments = 0;
    @Field("fairy_essence")
    private int fairyEssence = 0;
    @Field("common_star_pieces")
    private int commonStarPieces = 0;
    @Field("rare_star_pieces")
    private int rareStarPieces = 0;
    @Field("epic_star_pieces")
    private int epicStarPieces = 0;
    @Field("legendary_star_pieces")
    private int legendaryStarPieces = 0;
    @Field("skill_boost_modifiers")
    private int skillBoostModifiers = 0;
    //MASTERWORKS FAIR
    @Field("masterworks_fair_submissions")
    private List<MasterworksFairEntry> masterworksFairEntries = new ArrayList<>();
    //SUPPLY DROP
    @Field("supply_drop_rewards")
    private List<SupplyDropEntry> supplyDropEntries = new ArrayList<>();
    @Field("supply_drop_tokens")
    private int supplyDropTokens = 0;

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

    public int getSyntheticShards() {
        return syntheticShards;
    }

    public void addSyntheticShards(int amount) {
        this.syntheticShards += amount;
    }

    public int getLegendFragments() {
        return legendFragments;
    }

    public void addLegendFragments(int amount) {
        this.legendFragments += amount;
    }

    public int getFairyEssence() {
        return fairyEssence;
    }

    public void addFairyEssence(int amount) {
        this.fairyEssence += amount;
    }

    public int getCommonStarPieces() {
        return commonStarPieces;
    }

    public int getRareStarPieces() {
        return rareStarPieces;
    }

    public int getEpicStarPieces() {
        return epicStarPieces;
    }

    public int getLegendaryStarPieces() {
        return legendaryStarPieces;
    }

    public void addCommonStarPiece() {
        this.commonStarPieces += 1;
    }

    public void addRareStarPiece() {
        this.rareStarPieces += 1;
    }

    public void addEpicStarPiece() {
        this.epicStarPieces += 1;
    }

    public void addLegendaryStarPiece() {
        this.legendaryStarPieces += 1;
    }

    public void subtractCommonStarPiece() {
        this.commonStarPieces -= 1;
    }

    public void subtractRareStarPiece() {
        this.rareStarPieces -= 1;
    }

    public void subtractEpicStarPiece() {
        this.epicStarPieces -= 1;
    }

    public void subtractLegendaryStarPiece() {
        this.legendaryStarPieces -= 1;
    }

    public int getSkillBoostModifiers() {
        return skillBoostModifiers;
    }

    public void addSkillBoostModifier() {
        this.skillBoostModifiers += 1;
    }

    public List<MasterworksFairEntry> getMasterworksFairEntries() {
        return masterworksFairEntries;
    }

    public void addMasterworksFairEntry(MasterworksFairEntry entry) {
        this.masterworksFairEntries.add(entry);
    }

    public int getSupplyDropTokens() {
        return supplyDropTokens;
    }

    public void setSupplyDropTokens(int supplyDropTokens) {
        this.supplyDropTokens = supplyDropTokens;
    }

    public void addSupplyDropToken(int amount) {
        this.supplyDropTokens += amount;
    }

    public List<SupplyDropEntry> getSupplyDropEntries() {
        return supplyDropEntries;
    }

    public void addSupplyDropEntry(SupplyDropEntry entry) {
        this.supplyDropEntries.add(entry);
    }

    public int getCoins() {
        return coins;
    }

    public void addCoins(int amount) {
        this.coins += amount;
    }

    public List<MasterworksFairReward> getRewards() {
        return masterworksFairRewards;
    }

    public void addReward(MasterworksFairReward reward) {
        this.masterworksFairRewards.add(reward);
    }

}
