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
        set(Weapons.WARLORDS_II_HERO, Currencies.VEILKEEPER_INSIGNIA, 1);
        set(Weapons.WARLORDS_II_PLAGUE_LONGSWORD, Currencies.VEILKEEPER_INSIGNIA, 1);
        set(Weapons.WARLORDS_II_FRACTALIZE, Currencies.VEILKEEPER_INSIGNIA, 1);
        set(Weapons.WARLORDS_II_STAR_S_EDGE, Currencies.VEILKEEPER_INSIGNIA, 1);
        set(Weapons.WARLORDS_II_EMBERBLADE, Currencies.VEILKEEPER_INSIGNIA, 1);
        set(Weapons.WARLORDS_II_SOULRENDER, Currencies.VEILKEEPER_INSIGNIA, 1);
        set(Weapons.WARLORDS_II_EDGE_OF_THE_ASTRAL_PLANE, Currencies.VEILKEEPER_INSIGNIA, 1);
        set(Weapons.WARLORDS_II_REQUIEM_OF_THE_NINTH_ABYSS, Currencies.VEILKEEPER_INSIGNIA, 1);
        set(Weapons.WARLORDS_II_ARCANETHYST, Currencies.VEILKEEPER_INSIGNIA, 1);
        set(Weapons.WARLORDS_II_TRUE_EXCALIBUR, Currencies.VEILKEEPER_INSIGNIA, 1);
        set(Weapons.WARLORDS_II_AWAKENED_LICHBLADE, Currencies.VEILKEEPER_INSIGNIA, 1);
        set(Weapons.WARLORDS_II_CREATION_SPLITTER, Currencies.VEILKEEPER_INSIGNIA, 1);
        set(Weapons.WARLORDS_II_DEMONIC_BLADE, Currencies.VEILKEEPER_INSIGNIA, 1);
        set(Weapons.WARLORDS_II_THUNDER_BRINGER, Currencies.VEILKEEPER_INSIGNIA, 1);
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
