package com.ebicep.warlords.player.ingame.instances;

import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.EnumSet;
import java.util.function.Consumer;

public enum InstanceFlags {

    STRIKE_IN_CONS,
    AVENGER_WRATH_STRIKE,
    LAST_STAND_FROM_SHIELD, // for last stand healing from absorbing from players with shield
    ROOTED,
    CAN_OVERHEAL_OTHERS,
    CAN_OVERHEAL_SELF,
    RECURSIVE,
    DOT, // damage over time
    REFLECTIVE_DAMAGE,
    CANT_KILL,
    NO_LUST_HEALING,
    APOTH_SELF_HEAL,
    DIRECT_HIT,
    DUPLICATE_AVENGER_STRIKE,
    NO_HEALING_ORBS,
    NO_HEALING_LEECH,
    FIRST_HIT,

    IGNORE_SELF_RES,
    NO_DISMOUNT,

    TRUE_HEALING, // does exact value healing regardless of modifications
    // Damage types - all effected by flag multiplier
    TRUE_DAMAGE, // does exact value damage regardless of shield/vene/reductions/dmg increase
    PIERCE, // ignores shield/vene, and victim dmg reductions
    IGNORE_DAMAGE_REDUCTION_ONLY, // ignores victim dmg reductions
    IGNORE_SOURCE_DAMAGE_BOOST, // ignores dmg increases from source aka this dmg cant be increased (e.g. order or evicerate)
    IGNORE_TARGET_DAMAGE_BOOST, // ignores dmg increases from target aka this dmg cant be increased (e.g. burn)
    IGNORE_CRIT_MODIFIERS,
    IGNORE_FLAG_MULTIPLIER, // ignores flag multiplier

    NO_MESSAGE, // doesnt send dmg/heal message to any player
    NO_HIT_SOUND,

    // tower defense
    TD_PHYSICAL,
    TD_MAGIC,

    // spec boost
    HAMMER_OF_JUDGEMENT_CONS,
    AURA_OF_RESTORATION_SOOTHING_ELIXIR,

    ;

    public static final EnumSet<InstanceFlags> TRUE_DAMAGE_IGNORE_CRIT = EnumSet.of(InstanceFlags.TRUE_DAMAGE, InstanceFlags.IGNORE_CRIT_MODIFIERS);

    public final Consumer<FloatModifiable.FloatModifier> ignorePositiveAdditive = floatModifier -> {
        if (floatModifier.getModifier() > 0) {
            floatModifier.addDisabledReason(name());
        }
    };

    public final Consumer<FloatModifiable.FloatModifier> ignorePositiveMultiplicative = floatModifier -> {
        if (floatModifier.getModifier() > 1) {
            floatModifier.addDisabledReason(name());
        }
    };

    public final Consumer<FloatModifiable.FloatModifier> ignoreNegativeAdditive = floatModifier -> {
        if (floatModifier.getModifier() < 0) {
            floatModifier.addDisabledReason(name());
        }
    };

    public final Consumer<FloatModifiable.FloatModifier> ignoreNegativeMultiplicative = floatModifier -> {
        if (floatModifier.getModifier() < 1) {
            floatModifier.addDisabledReason(name());
        }
    };

    public Consumer<FloatModifiable.FloatModifier> createDisabledReason() {
        return floatModifier -> floatModifier.addDisabledReason(name());
    }

}
