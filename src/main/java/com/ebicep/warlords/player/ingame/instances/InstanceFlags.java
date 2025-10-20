package com.ebicep.warlords.player.ingame.instances;

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
    IGNORE_FERVENT_TITLE,
    DUPLICATE_AVENGER_STRIKE,
    NO_HEALING_ORBS,

    IGNORE_SELF_RES,
    NO_DISMOUNT,

    TRUE_HEALING, // does exact value healing regardless of modifications
    // Damage types - all effected by flag multiplier
    TRUE_DAMAGE, // does exact value damage regardless of shield/vene/reductions/dmg increase
    PIERCE, // ignores shield/vene, and victim dmg reductions
    IGNORE_DAMAGE_REDUCTION_ONLY, // ignores victim dmg reductions
    IGNORE_DAMAGE_BOOST, // ignores victim dmg increases aka this dmg cant be increased
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

}
