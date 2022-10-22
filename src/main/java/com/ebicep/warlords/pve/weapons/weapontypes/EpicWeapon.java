package com.ebicep.warlords.pve.weapons.weapontypes;

import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.general.WeaponsRarity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.AbstractTierTwoWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.weaponaddons.Salvageable;
import com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore;
import com.ebicep.warlords.util.java.Utils;
import org.bukkit.ChatColor;

import java.util.*;

import static com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore.getAverageValue;

public class EpicWeapon extends AbstractTierTwoWeapon implements Salvageable, WeaponScore {

    public static final int MELEE_DAMAGE_MIN = 120;
    public static final int MELEE_DAMAGE_MAX = 180;
    public static final int CRIT_CHANCE_MIN = 12;
    public static final int CRIT_CHANCE_MAX = 20;
    public static final int CRIT_MULTIPLIER_MIN = 150;
    public static final int CRIT_MULTIPLIER_MAX = 200;
    public static final int HEALTH_BONUS_MIN = 200;
    public static final int HEALTH_BONUS_MAX = 500;
    public static final int SPEED_BONUS_MIN = 2;
    public static final int SPEED_BONUS_MAX = 8;

    public EpicWeapon() {
    }

    public EpicWeapon(UUID uuid) {
        super(uuid);
        this.selectedWeaponSkin = Weapons.getRandomWeaponFromRarity(WeaponsRarity.EPIC);
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
    }

    public EpicWeapon(WarlordsPlayer warlordsPlayer) {
        super(warlordsPlayer);
        this.selectedWeaponSkin = Weapons.getRandomWeaponFromRarity(WeaponsRarity.EPIC);
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
    }

    @Override
    public WeaponsPvE getRarity() {
        return WeaponsPvE.EPIC;
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.DARK_PURPLE;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>(super.getLore());
        lore.add("");
        lore.add(getWeaponScoreString());
        return lore;
    }

    @Override
    public void generateStats() {
        this.meleeDamage = Utils.generateRandomValueBetweenInclusive(MELEE_DAMAGE_MIN, MELEE_DAMAGE_MAX - getMeleeDamageRange());
        this.critChance = Utils.generateRandomValueBetweenInclusive(CRIT_CHANCE_MIN, CRIT_CHANCE_MAX);
        this.critMultiplier = Utils.generateRandomValueBetweenInclusive(CRIT_MULTIPLIER_MIN, CRIT_MULTIPLIER_MAX);
        this.healthBonus = Utils.generateRandomValueBetweenInclusive(HEALTH_BONUS_MIN, HEALTH_BONUS_MAX);
        this.speedBonus = Utils.generateRandomValueBetweenInclusive(SPEED_BONUS_MIN, SPEED_BONUS_MAX);
    }

    @Override
    public int getMeleeDamageRange() {
        return 25;
    }

    @Override
    public List<Double> getWeaponScoreAverageValues() {
        return Arrays.asList(
                getAverageValue(MELEE_DAMAGE_MIN, MELEE_DAMAGE_MAX - getMeleeDamageRange(), meleeDamage / Math.pow(getUpgradeMultiplier(), upgradeLevel)),
                getAverageValue(CRIT_CHANCE_MIN, CRIT_CHANCE_MAX, critChance),
                getAverageValue(CRIT_MULTIPLIER_MIN, CRIT_MULTIPLIER_MAX, critMultiplier),
                getAverageValue(HEALTH_BONUS_MIN, HEALTH_BONUS_MAX, healthBonus / Math.pow(getUpgradeMultiplier(), upgradeLevel)),
                getAverageValue(SPEED_BONUS_MIN, SPEED_BONUS_MAX, speedBonus / Math.pow(getUpgradeMultiplier(), upgradeLevel))
        );
    }

    @Override
    public int getMinSalvageAmount() {
        return 42;
    }

    @Override
    public int getMaxSalvageAmount() {
        return 48;
    }

    @Override
    public int getMaxUpgradeLevel() {
        return 2;
    }

    @Override
    public LinkedHashMap<Currencies, Long> getUpgradeCost(int tier) {
        LinkedHashMap<Currencies, Long> cost = new LinkedHashMap<>();
        if (tier == 1) {
            cost.put(Currencies.COIN, 10000L);
            cost.put(Currencies.SYNTHETIC_SHARD, 1000L);
        } else if (tier == 2) {
            cost.put(Currencies.COIN, 25000L);
            cost.put(Currencies.SYNTHETIC_SHARD, 2000L);
        }
        return cost;
    }
}
