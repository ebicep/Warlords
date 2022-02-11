package com.ebicep.warlords.player.cooldowns;

import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;

public interface DamageHealingInstance {

    default boolean isHealing() {
        return false;
    }

    //HEALING

    default float doBeforeHeal(WarlordsDamageHealingEvent event, float currentHealValue) {
        return currentHealValue;
    }

    //DAMAGE


    /**
     * If attacker has abilities that increase their crit chance (inferno)
     */
    default int addCritChanceFromAttacker(WarlordsDamageHealingEvent event, int currentCritChance) {
        return currentCritChance;
    }

    /**
     * If attacker has abilities that increase their crit multiplier (inferno)
     */
    default int addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, int currentCritMultiplier) {
        return currentCritMultiplier;
    }

    /**
     * If self has abilities that increase/decrease their damage taken (berserk) - before intervene
     */
    default float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
        return currentDamageValue;
    }

    /**
     * If attacker has abilities that increase/decrease damage done (berserk) - before intervene
     */
    default float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
        return currentDamageValue;
    }

    /**
     * If self has abilities that increase/decrease their damage taken (berserk) - after intervene
     */
    default float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
        return currentDamageValue;
    }

    /**
     * If attacker has abilities that increase/decrease damage done (berserk) - after intervene
     */
    default float modifyDamageAfterInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
        return currentDamageValue;
    }

}
