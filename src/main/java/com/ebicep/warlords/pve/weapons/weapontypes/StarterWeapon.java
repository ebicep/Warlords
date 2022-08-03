package com.ebicep.warlords.pve.weapons.weapontypes;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weaponaddons.Salvageable;
import org.bukkit.ChatColor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class StarterWeapon extends AbstractWeapon implements Salvageable {

    public static final int MELEE_DAMAGE_MIN = 76;
    public static final int MELEE_DAMAGE_MAX = 103;
    public static final int HEALTH_BONUS = 160;

    public StarterWeapon() {
        generateStats();
    }

    public StarterWeapon(UUID uuid) {
        super(uuid);
        this.selectedWeaponSkin = Weapons.STEEL_SWORD;
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
        this.isBound = true;
    }

    public StarterWeapon(UUID uuid, Specializations specialization) {
        super(uuid);
        this.selectedWeaponSkin = Weapons.STEEL_SWORD;
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
        this.specialization = specialization;
        this.isBound = true;
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.GRAY;
    }

    @Override
    public List<String> getLore() {
        return Collections.emptyList();
    }

    @Override
    public void generateStats() {
        this.meleeDamage = MELEE_DAMAGE_MIN;
        this.healthBonus = HEALTH_BONUS;
    }

    @Override
    public int getMeleeDamageRange() {
        return 27; //MAX - MIN = 27
    }

    @Override
    public float getMeleeDamageMin() {
        return MELEE_DAMAGE_MIN;
    }

    @Override
    public float getMeleeDamageMax() {
        return MELEE_DAMAGE_MAX;
    }

    @Override
    public float getCritChance() {
        return -1;
    }

    @Override
    public float getCritMultiplier() {
        return 0;
    }

    @Override
    public float getHealthBonus() {
        return Math.round(healthBonus);
    }

    @Override
    public int getMinSalvageAmount() {
        return 1;
    }

    @Override
    public int getMaxSalvageAmount() {
        return 1;
    }
}
