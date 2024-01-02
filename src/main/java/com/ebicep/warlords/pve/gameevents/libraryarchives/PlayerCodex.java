package com.ebicep.warlords.pve.gameevents.libraryarchives;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.player.general.Specializations;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static com.ebicep.warlords.abilities.internal.Ability.*;
import static com.ebicep.warlords.player.general.Specializations.*;

public enum PlayerCodex implements Codex {

    MENACING_CODEX("Menacing Codex", CONJURER, POISONOUS_HEX, SEISMIC_WAVE_BERSERKER, ENERGY_SEER_SENTINEL, HOLY_RADIANCE_AVENGER, VINDICATE),
    ELUSIVE_CODEX("Elusive Codex", ASSASSIN, JUDGEMENT_STRIKE, FREEZING_BREATH, VITALITY_LIQUOR, CHAIN_HEAL, VINDICATE),
    THUNDEROUS_CODEX("Thunderous Codex", THUNDERLORD, LIGHTNING_BOLT, SEISMIC_WAVE_BERSERKER, WINDFURY_WEAPON, PRISM_GUARD, HAMMER_OF_LIGHT),
    ENIGMATIC_CODEX("Enigmatic Codex", BERSERKER, WOUNDING_STRIKE_BERSERKER, FLAME_BURST, ENERGY_SEER_SENTINEL, SANCTIFIED_BEACON, UNDYING_ARMY),
    CALAMITOUS_CODEX("Calamitous Codex", AVENGER, AVENGERS_STRIKE, SEISMIC_WAVE_BERSERKER, LIGHT_INFUSION_AVENGER, ARCANE_SHIELD, DEATHS_DEBT),
    VOLCANIC_CODEX("Volcanic Codex", PYROMANCER, FIREBALL, CHAIN_LIGHTNING, GROUND_SLAM_BERSERKER, CHAIN_HEAL, HEALING_RAIN),
    PROTECTIVE_CODEX("Protective Codex", SENTINEL, FORTIFYING_HEX, RAY_OF_LIGHT, ENERGY_SEER_CONJURER, ORBS_OF_LIFE, UNDYING_ARMY),
    FRACTAL_CODEX("Fractal Codex", VINDICATOR, RIGHTEOUS_STRIKE, RAY_OF_LIGHT, TIME_WARP_CRYOMANCER, SANCTIFIED_BEACON, DIVINE_BLESSING),
    INSPIRITED_CODEX("Inspirited Codex", SPIRITGUARD, FALLEN_SOULS, SPIRIT_LINK, TIME_WARP_CRYOMANCER, HOLY_RADIANCE_CRUSADER, HAMMER_OF_LIGHT),
    RATIONAL_CODEX("Rational Codex", DEFENDER, WOUNDING_STRIKE_DEFENDER, WATER_BREATH, VITALITY_LIQUOR, HOLY_RADIANCE_AVENGER, LAST_STAND),
    VALIANT_CODEX("Valiant Codex", CRUSADER, CRUSADERS_STRIKE, SPIRIT_LINK, ENERGY_SEER_CONJURER, LIGHTNING_ROD, INFERNO),
    VERGLAS_CODEX("Verglas Codex", CRYOMANCER, FROST_BOLT, RECKLESS_CHARGE, LIGHT_INFUSION_CRUSADER, HOLY_RADIANCE_PROTECTOR, ICE_BARRIER),
    HARMONIOUS_CODEX("Harmonious Codex", LUMINARY, MERCIFUL_HEX, CONSECRATE_PROTECTOR, WINDFURY_WEAPON, ARCANE_SHIELD, DEATHS_DEBT),
    AEON_CODEX("Aeon Codex", APOTHECARY, IMPALING_STRIKE, CONSECRATE_PROTECTOR, EARTHLIVING_WEAPON, HOLY_RADIANCE_PROTECTOR, ICE_BARRIER),
    ROOTED_CODEX("Rooted Codex", EARTHWARDEN, EARTHEN_SPIKE, SOOTHING_ELIXIR, LIGHT_INFUSION_PROTECTOR, LIGHTNING_ROD, DRAINING_MIASMA),
    MACABRE_CODEX("Macabre Codex", REVENANT, CRIPPLING_STRIKE, SOULFIRE_BEAM, TIME_WARP_AQUAMANCER, BLOOD_LUST, ORDER_OF_EVISCERATE),
    RADIANT_CODEX("Radiant Codex", PROTECTOR, PROTECTORS_STRIKE, CONSECRATE_PROTECTOR, EARTHLIVING_WEAPON, ARCANE_SHIELD, INFERNO),
    TEMPEST_CODEX("Tempest Codex", AQUAMANCER, WATER_BOLT, RECKLESS_CHARGE, LIGHT_INFUSION_PROTECTOR, PRISM_GUARD, HEALING_TOTEM),


    ;

    public static final PlayerCodex[] VALUES = values();

    @Nullable
    public static PlayerCodex getRandomCodex(Set<PlayerCodex> exclude) {
        List<PlayerCodex> list = new ArrayList<>(List.of(VALUES));
        list.removeAll(exclude);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    @Nonnull
    public static PlayerCodex getCodexForSpec(Specializations spec) {
        for (PlayerCodex codex : VALUES) {
            if (codex.spec == spec) {
                return codex;
            }
        }
        throw new IllegalArgumentException("No codex for spec " + spec);
    }

    public final String name;
    public final Specializations spec;
    public final Ability[] abilities;

    PlayerCodex(String name, Specializations spec, Ability... abilities) {
        this.name = name;
        this.spec = spec;
        this.abilities = abilities;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Specializations getSpec() {
        return spec;
    }

    @Override
    public Ability[] getAbilities() {
        return abilities;
    }

}
