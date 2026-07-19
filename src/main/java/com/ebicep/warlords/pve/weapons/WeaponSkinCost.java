package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public record WeaponSkinCost(Spendable currency, long amount) {

    private static final Map<Weapons, WeaponSkinCost> OVERRIDES = new EnumMap<>(Weapons.class);

    static {
        registerOverrides();
    }

    public WeaponSkinCost {
        Objects.requireNonNull(currency, "currency");
        if (amount < 0) {
            throw new IllegalArgumentException("Weapon skin cost cannot be negative");
        }
    }

    private static void registerOverrides() {
    }

    public static void set(Weapons weaponSkin, Spendable currency, long amount) {
        OVERRIDES.put(Objects.requireNonNull(weaponSkin, "weaponSkin"), new WeaponSkinCost(currency, amount));
    }

    public static WeaponSkinCost get(Weapons weaponSkin, WeaponsPvE weaponRarity) {
        WeaponSkinCost override = OVERRIDES.get(Objects.requireNonNull(weaponSkin, "weaponSkin"));
        if (override != null) {
            return override;
        }
        return new WeaponSkinCost(
                Currencies.FAIRY_ESSENCE,
                Objects.requireNonNull(weaponRarity, "weaponRarity").fairyEssenceCost
        );
    }

    public static Spendable getCurrency(Weapons weaponSkin) {
        WeaponSkinCost override = OVERRIDES.get(Objects.requireNonNull(weaponSkin, "weaponSkin"));
        return override == null ? Currencies.FAIRY_ESSENCE : override.currency();
    }
}
