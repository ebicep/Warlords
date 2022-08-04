package com.ebicep.warlords.player.ingame.cooldowns.instances;

import com.ebicep.warlords.events.WarlordsDamageHealingEvent;

public interface HealingInstance extends Instance {

    /**
     * Done before any healing is done to players
     */
    default float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
        return currentHealValue;
    }

}
