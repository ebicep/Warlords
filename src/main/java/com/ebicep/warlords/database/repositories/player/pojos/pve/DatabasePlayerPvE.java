package com.ebicep.warlords.database.repositories.player.pojos.pve;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairEntry;
import com.ebicep.warlords.pve.events.supplydrop.SupplyDropEntry;
import com.ebicep.warlords.pve.rewards.Currencies;
import com.ebicep.warlords.pve.rewards.MasterworksFairReward;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabasePlayerPvE extends PvEDatabaseStatInformation implements DatabasePlayer {

    private DatabaseMagePvE mage = new DatabaseMagePvE();
    private DatabaseWarriorPvE warrior = new DatabaseWarriorPvE();
    private DatabasePaladinPvE paladin = new DatabasePaladinPvE();
    private DatabaseShamanPvE shaman = new DatabaseShamanPvE();
    private DatabaseRoguePvE rogue = new DatabaseRoguePvE();
    //GENERAL
    @Field("masterworks_fair_rewards")
    private List<MasterworksFairReward> masterworksFairRewards = new ArrayList<>();
    //WEAPONS
    @Field("weapon_inventory")
    private List<AbstractWeapon> weaponInventory = new ArrayList<>();
    //MASTERWORKS FAIR
    @Field("masterworks_fair_submissions")
    private List<MasterworksFairEntry> masterworksFairEntries = new ArrayList<>();
    //SUPPLY DROP
    @Field("supply_drop_rewards")
    private List<SupplyDropEntry> supplyDropEntries = new ArrayList<>();
    //CURRENCIES
    private Map<Currencies, Long> currencies = new LinkedHashMap<>() {{
        for (Currencies value : Currencies.VALUES) {
            put(value, 0L);
        }
    }};

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
    public PvEDatabaseStatInformation getSpec(Specializations specializations) {
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
    public PvEDatabaseStatInformation getClass(Classes classes) {
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

    public List<MasterworksFairEntry> getMasterworksFairEntries() {
        return masterworksFairEntries;
    }

    public void addMasterworksFairEntry(MasterworksFairEntry entry) {
        this.masterworksFairEntries.add(entry);
    }

    public List<SupplyDropEntry> getSupplyDropEntries() {
        return supplyDropEntries;
    }

    public void addSupplyDropEntry(SupplyDropEntry entry) {
        this.supplyDropEntries.add(entry);
    }

    public List<MasterworksFairReward> getRewards() {
        return masterworksFairRewards;
    }

    public void addReward(MasterworksFairReward reward) {
        this.masterworksFairRewards.add(reward);
    }

    public Map<Currencies, Long> getCurrencies() {
        return currencies;
    }

    public Long getCurrencyValue(Currencies currency) {
        return this.currencies.getOrDefault(currency, 0L);
    }

    public void addCurrency(Currencies currency, Long amount) {
        if (!currencies.containsKey(currency)) {
            currencies.put(currency, 0L);
        }
        this.currencies.put(currency, this.currencies.get(currency) + amount);
    }

    public void addCurrency(Currencies currency, int amount) {
        this.addCurrency(currency, (long) amount);
    }

    public void addOneCurrency(Currencies currency) {
        this.addCurrency(currency, 1L);
    }

    public void subtractCurrency(Currencies currency, Long amount) {
        this.addCurrency(currency, -amount);
    }

    public void subtractCurrency(Currencies currency, int amount) {
        this.subtractCurrency(currency, (long) amount);
    }

    public void subtractOneCurrency(Currencies currency) {
        this.subtractCurrency(currency, 1L);
    }

}
