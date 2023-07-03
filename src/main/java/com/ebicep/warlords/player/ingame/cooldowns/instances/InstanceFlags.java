package com.ebicep.warlords.player.ingame.cooldowns.instances;

public enum InstanceFlags {

    STRIKE_IN_CONS,
    AVENGER_WRATH_STRIKE,
    LAST_STAND_FROM_SHIELD, // for last stand healing from absorbing from players with shield
    IGNORE_SELF_RES,
    NO_DISMOUNT,

    // Damage types - all effected by flag multiplier
    TRUE_DAMAGE, // does exact value damage regardless of shield/vene/reductions/dmg increase
    PIERCE_DAMAGE // ignores shield/vene, and victim dmg reductions

}
