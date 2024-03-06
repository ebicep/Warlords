package com.ebicep.warlords.player.ingame.cooldowns.instances;

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

    IGNORE_SELF_RES,
    NO_DISMOUNT,

    // Damage types - all effected by flag multiplier
    TRUE_DAMAGE, // does exact value damage regardless of shield/vene/reductions/dmg increase
    PIERCE, // ignores shield/vene, and victim dmg reductions
    IGNORE_DAMAGE_REDUCTION_ONLY, // ignores victim dmg reductions
    IGNORE_DAMAGE_BOOST, // ignores victim dmg increases aka this dmg cant be increased

    NO_MESSAGE, // doesnt send dmg/heal message to any player
    NO_HIT_SOUND,

    // tower defense
    TD_PHYSICAL,
    TD_MAGIC,

}
