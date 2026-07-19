package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public record WeaponSkinCost(Spendable currency, long amount) {

    private static final Map<Weapons, WeaponSkinCost> COSTS = new EnumMap<>(Weapons.class);

    static {
        for (Weapons weaponSkin : Weapons.VALUES) {
            COSTS.put(
                    weaponSkin,
                    new WeaponSkinCost(Currencies.FAIRY_ESSENCE, weaponSkin.weaponsPvE.fairyEssenceCost)
            );
        }
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
        COSTS.put(Objects.requireNonNull(weaponSkin, "weaponSkin"), new WeaponSkinCost(currency, amount));
    }

    public static WeaponSkinCost get(Weapons weaponSkin) {
        return Objects.requireNonNull(COSTS.get(weaponSkin), "No weapon skin cost configured for " + weaponSkin);
    }
}
