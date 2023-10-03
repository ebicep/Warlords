package com.ebicep.warlords.pve.weapons.weapontypes;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.weapons.AbstractTierOneWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class StarterWeapon extends AbstractTierOneWeapon {

    public static final int MELEE_DAMAGE_MIN = 76;
    public static final int MELEE_DAMAGE_MAX = 103;
    public static final int HEALTH_BONUS = 160;

    public StarterWeapon() {
    }

    public StarterWeapon(UUID uuid) {
        super(uuid);
        this.selectedWeaponSkin = Weapons.STEEL_SWORD;
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
        this.isBound = true;
    }

    @Override
    public WeaponsPvE getRarity() {
        return WeaponsPvE.NONE;
    }

    public StarterWeapon(UUID uuid, Specializations specialization) {
        super(uuid);
        this.selectedWeaponSkin = Weapons.STEEL_SWORD;
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
        this.specialization = specialization;
        this.isBound = true;
    }

    @Override
    public TextColor getTextColor() {
        return NamedTextColor.GRAY;
    }

    @Override
    public List<Component> getLore() {
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
    public List<Component> getBaseStats() {
        return Arrays.asList(
                Component.text("Damage: ", NamedTextColor.GRAY)
                         .append(Component.text(NumberFormat.formatOptionalTenths(getMeleeDamageMin()) + " - " + NumberFormat.formatOptionalHundredths(
                                 getMeleeDamageMax()), NamedTextColor.RED)),
                Component.empty(),
                Component.text("Health: ", NamedTextColor.GRAY)
                         .append(Component.text(NumberFormat.formatOptionalTenths(getHealthBonus()), NamedTextColor.GREEN))
        );
    }

    @Override
    public float getMeleeDamageMin() {
        return meleeDamage;
    }

    @Override
    public float getMeleeDamageMax() {
        return meleeDamage + getMeleeDamageRange();
    }

    @Override
    public float getCritChance() {
        return 0;
    }

    @Override
    public float getCritMultiplier() {
        return 0;
    }

    @Override
    public float getHealthBonus() {
        return healthBonus;
    }

}
