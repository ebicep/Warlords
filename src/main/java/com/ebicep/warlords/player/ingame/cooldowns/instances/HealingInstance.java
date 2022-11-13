package com.ebicep.warlords.player.ingame.cooldowns.instances;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;

public interface HealingInstance extends Instance {

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
