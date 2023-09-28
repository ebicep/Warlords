package com.ebicep.warlords.player.ingame.cooldowns.instances;

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
    default float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
        return currentHealValue;
    }

    /**
     * Done before any healing is done to players - based on attackers cooldowns
     */
    default float doBeforeHealFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
        return currentHealValue;
    }

}
