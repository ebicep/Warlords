package com.ebicep.warlords.player.ingame.instances.type;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;

public interface DamageInstance extends Instance {

    /**
     * Calls before variables are declared - based on self cooldowns
     */
    default void damageDoBeforeVariableSetFromSelf(WarlordsDamageHealingEvent event) {
    }

    /**
     * Calls before variables are declared - based on attackers cooldowns
     */
    default void damageDoBeforeVariableSetFromAttacker(WarlordsDamageHealingEvent event) {
    }

    /**
     * Calls before any reduction - based on self cooldowns
     */
    default void doBeforeReductionFromSelf(WarlordsDamageHealingEvent event) {

    }

    /**
     * Calls before any reduction - based on attackers cooldowns
     */
    default void doBeforeReductionFromAttacker(WarlordsDamageHealingEvent event) {

    }

    /**
     * If attacker has abilities that increase their crit chance (inferno), numbers should be in the tens place
     */
    default float addCritChanceFromAttacker(WarlordsDamageHealingEvent event, float currentCritChance) {
        return currentCritChance;
    }

    default float setCritChanceFromAttacker(WarlordsDamageHealingEvent event, float currentCritChance) {
        return currentCritChance;
    }

    /**
     * If attacker has abilities that increase their crit multiplier (inferno), numbers should be in the tens place
     */
    default float addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
        return currentCritMultiplier;
    }

    default float setCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
        return currentCritMultiplier;
    }

    default void onPostCritCalculationFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, float critChance, float critMultiplier) {
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
     * Called after when the player takes damage while intervened - based on attackers cooldowns
     */
    default void onInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
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
    default void onShieldFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
    }

    /**
     * Called after when the player takes shield damage - based on attackers cooldowns
     */
    default void onShieldFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
    }

    /**
     * Called just before the player takes damage - based on self cooldowns
     */
    default float modifyDamageAfterAllFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
        return currentDamageValue;
    }

    /**
     * Called after all damage modifications and after the damage has been applied - based on self cooldowns
     */
    default void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
    }

    /**
     * Called after all damage modifications and after the damage has been applied - based on attackers cooldowns
     */
    default void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
    }

    /**
     * Called if the player dies - based on all enemies cooldowns
     */
    default void onDeathFromEnemies(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, boolean isKiller) {

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
