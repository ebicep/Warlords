package com.ebicep.warlords.pve.gameevents.libraryarchives;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.player.general.Specializations;

public enum PlayerCodex implements Codex {

//    MENACING_CODEX("Menacing Codex", CONJURER, SEISMIC_WAVE_BERSERKER, ENERGY_SEER_SENTINEL, HOLY_RADIANCE_AVENGER, VINDICATE),
//    ELUSIVE_CODEX("Elusive Codex", ASSASSIN, FREEZING_BREATH, VITALITY_LIQUOR, CHAIN_HEAL, VINDICATE),
//    THUNDEROUS_CODEX("Thunderous Codex", THUNDERLORD, SEISMIC_WAVE, WINDFURY_WEAPON, PRISM_GUARD, HAMMER_OF_LIGHT),
//    ENIGMATIC_CODEX("Enigmatic Codex", BERSERKER, FLAME_BURST, ENERGY_SEER_SENTINEL, SANCTIFIED_BEACON, UNDYING_ARMY),
//    CALAMITOUS_CODEX("Calamitous Codex", AVENGER, SEISMIC_WAVE, LIGHT_INFUSION, ARCANE_SHIELD, DEATHS_DEBT),
//    VOLCANIC_CODEX("Volcanic Codex", PYROMANCER, CHAIN_LIGHTNING, GROUND_SLAM, CHAIN_HEAL, HEALING_RAIN),
//    PROTECTIVE_CODEX("Protective Codex", SENTINEL, RAY_OF_LIGHT, ENERGY_SEER_CONJURER, ORBS_OF_LIFE, UNDYING_ARMY),
//    FRACTAL_CODEX("Fractal Codex", VINDICATOR, RAY_OF_LIGHT, TIME_WARP, SANCTIFIED_BEACON, DIVINE_BLESSING),
//    INSPIRITED_CODEX("Inspirited Codex", SPIRITGUARD, SPIRIT_LINK, TIME_WARP, HOLY_RADIANCE_CRUSADER, HAMMER_OF_LIGHT),
//    RATIONAL_CODEX("Rational Codex", DEFENDER, WATER_BREATH, VITALITY_LIQUOR, HOLY_RADIANCE_AVENGER, LAST_STAND),
//    VALIANT_CODEX("Valiant Codex", CRUSADER, SPIRIT_LINK, ENERGY_SEER_CONJURER, LIGHTNING_ROD, INFERNO),
//    VERGLAS_CODEX("Verglas Codex", CRYOMANCER, RECKLESS_CHARGE, LIGHT_INFUSION, HOLY_RADIANCE_PROTECTOR, ICE_BARRIER),
//    HARMONIOUS_CODEX("Harmonious Codex", LUMINARY, CONSECRATE, WINDFURY_WEAPON, ARCANE_SHIELD, DEATHS_DEBT),
//    AEON_CODEX("Aeon Codex", APOTHECARY, CONSECRATE, EARTHLIVING_WEAPON, HOLY_RADIANCE_PROTECTOR, ICE_BARRIER),
//    ROOTED_CODEX("Rooted Codex", EARTHWARDEN, SOOTHING_ELIXIR, LIGHT_INFUSION, LIGHTNING_ROD, DRAINING_MIASMA),
//    MACABRE_CODEX("Macabre Codex", REVENANT, SOULFIRE_BEAM, TIME_WARP, BLOOD_LUST, ORDER_OF_EVISCERATE),
//    RADIANT_CODEX("Radiant Codex", PROTECTOR, CONSECRATE, EARTHLIVING_WEAPON, ARCANE_SHIELD, INFERNO),
//    TEMPEST_CODEX("Tempest Codex", AQUAMANCER, RECKLESS_CHARGE, LIGHT_INFUSION, PRISM_GUARD, HEALING_TOTEM),

    ;

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
