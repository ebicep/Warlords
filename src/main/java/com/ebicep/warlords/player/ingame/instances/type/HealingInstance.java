package com.ebicep.warlords.player.ingame.instances.type;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;

public interface HealingInstance extends Instance {

    /**
     * Calls before variables are declared - based on self cooldowns
     */
    default void healingDoBeforeVariableSetFromSelf(WarlordsDamageHealingEvent event) {
    }

    /**
     * Calls before variables are declared - based on attackers cooldowns
     */
    default void healingDoBeforeVariableSetFromAttacker(WarlordsDamageHealingEvent event) {
    }

    /**
     * Done before any healing is done to players - based on self cooldowns
     */
    default float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
        return currentHealValue;
    }

    /**
     * Done before any healing is done to players - based on attackers cooldowns
     */
    default float modifyHealingFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
        return currentHealValue;
    }

    default void onHealFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
    }

    default void onHealFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
    }

}
