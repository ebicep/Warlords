package com.ebicep.warlords.player.cooldowns;

import com.ebicep.warlords.events.WarlordsDamageHealingEvent;

public interface DamageHealingInstance {

    /**
     * if true, only the methods of the first cooldown is applied, the rest are skipped,
     * checks based on class and name
     */
    default boolean distinct() {
        return false;
    }

    /**
     * boolean if damageheal instance is healing
     */
    default boolean isHealing() {
        return false;
    }

    //HEALING

    /**
     * Done before any healing is done to players
     */
    default float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
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

    /**
     * Called after when the player takes shield damage - based on self cooldowns
     */
    default void onShield(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
    }

    /**
     * Called after all damage modifications - based on self cooldowns
     */
    default void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
    }

    /**
     * Called after all damage modifications - based on attackers cooldowns
     */
    default void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
    }

    /**
     * Called at the end of damage instance - based on self cooldowns
     */
    default void onEndFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
    }

    /**
     * Called at the end of damage instance - based on attackers cooldowns
     */
    default void onEndFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
    }
}
