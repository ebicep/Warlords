package com.ebicep.warlords.player.ingame.cooldowns.instances;

public enum InstanceFlags {

    STRIKE_IN_CONS,
    AVENGER_WRATH_STRIKE,
    IGNORE_SELF_RES,
    NO_DISMOUNT,

    // Damage types - all effected by flag multiplier //TODO filter out reduction/increase
    TRUE_DAMAGE, // does exact value damage regardless of shield/vene/reductions/dmg increase
    PIERCE_DAMAGE // only ignores shield/vene, not reductions/dmg increase

}
