package com.ebicep.warlords.player.ingame.instances.type;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import com.ebicep.warlords.util.warlords.modifiablevalues.MultiFloatModifiable;

@SuppressWarnings("InstantiationOfUtilityClass")
public class Modifier<T> {

    // Damage Modifiers

    /**
     * Called before the variable min/max has been set via constructor or other instances. (eg. Ignite)
     */
    public static final Modifier<DamageBeforeVariableSetFromAttacker> MODIFY_OUTGOING_DAMAGE_BEFORE_VARIABLE_SET = new Modifier<>();
    public static final Modifier<DamageBeforeReductionFromAttacker> DAMAGE_BEFORE_ANY_REDUCTION_ATTACKER = new Modifier<>();
    /**
     * Called as additive on crit chance of abilities, use this to increase crit chance. (eg. Inferno)
     */
    public static final Modifier<DamageAddCritChanceFromAttacker> MODIFY_OUTGOING_CRIT_CHANCE = new Modifier<>();
    /**
     * Called as additive on crit multiplier of abilities, use this to increase crit multiplier. (eg. Inferno)
     */
    public static final Modifier<DamageAddCritMultiplierFromAttacker> MODIFY_OUTGOING_CRIT_MULTIPLIER = new Modifier<>();
    /**
     * Called after crit chance/multiplier have been set and the value has been calculated, use this to modify crit multiplier. (eg. Sanctified Beacon)
     */
    public static final Modifier<DamagePostCritCalculationFromAttacker> MODIFY_OUTGOING_CRIT_MULTIPLIER_POST_CALC = new Modifier<>();
    /**
     * Called before intervene, use this to increase incoming damage. (eg. Last Stand, Vindicate)
     */
    public static final Modifier<DamageModifyBeforeInterveneFromSelf> INCOMING_DAMAGE_BEFORE_INTERVENE = new Modifier<>();
    /**
     * Called before intervene, use this to increase outgoing damage. (eg. Damage Powerup, Berserk)
     */
    public static final Modifier<DamageModifyBeforeInterveneFromAttacker> MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE = new Modifier<>();
    /**
     * Called after intervene, use this to decrease incoming damage. (eg. Last Stand, Vindicate)
     */
    public static final Modifier<DamageModifyAfterInterveneFromSelf> MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE = new Modifier<>();
    /**
     * Called after intervene, use this to increase/decrease outgoing damage. (eg. Damage Powerup, Berserk)
     */
    public static final Modifier<DamageModifyAfterInterveneFromAttacker> MODIFY_OUTGOING_DAMAGE_AFTER_INTERVENE = new Modifier<>();
    /**
     * Use this if you want to modify or call certain code when a shield is damaged. (eg. Arcane Shield)
     */
    public static final Modifier<DamageOnShieldFromSelf> ON_INCOMING_SHIELD_DAMAGE = new Modifier<>();
    /**
     * Use this if you want to modify or call certain code when a shield is damaged. (eg. Guardian Beam, Mystical Barrier)
     */
    public static final Modifier<DamageOnShieldFromAttacker> ON_OUTGOING_SHIELD_DAMAGE = new Modifier<>();
    /**
     * Modifies incoming damage after all other buffs or modifications have been applied. (eg. Boss damage checks)
     */
    public static final Modifier<DamageModifyAfterAllFromSelf> MODIFY_INCOMING_DAMAGE_AFTER_ALL_MODIFIERS = new Modifier<>();
    /**
     * When you receive damage or a damage instance, do the following. (eg. Receive energy whenever you get hit)
     */
    public static final Modifier<DamageOnDamageFromSelf> ON_INCOMING_DAMAGE = new Modifier<>();
    /**
     * When you deal damage or add a damage instance to another enemy or source. (eg. Receive energy whenever you hit an entity)
     */
    public static final Modifier<DamageOnDamageFromAttacker> ON_OUTGOING_DAMAGE = new Modifier<>();
    /**
     * Called when an enemy or entity dies from your sources. (eg. Order of Eviscerate)
     */
    public static final Modifier<DamageOnDeathFromEnemies> ON_ENEMY_DEATH = new Modifier<>();
    public static final Modifier<DamageOnEndFromSelf> DAMAGE_ON_END_SELF = new Modifier<>();
    public static final Modifier<DamageOnEndFromAttacker> DAMAGE_ON_END_ATTACKER = new Modifier<>();
    public static final Modifier<DamageOnInterveneFromAttacker> DAMAGE_ON_INTERVENE_ATTACKER = new Modifier<>();

    // Healing Modifiers

    public static final Modifier<HealingBeforeVariableSetFromSelf> HEALING_BEFORE_VARIABLE_SET_SELF = new Modifier<>();
    public static final Modifier<HealingBeforeVariableSetFromAttacker> HEALING_BEFORE_VARIABLE_SET_ATTACKER = new Modifier<>();

    /**
     * Use this to increase healing you receive. (eg. Divine Blessing)
     */
    public static final Modifier<HealingModifyFromSelf> MODIFY_INCOMING_HEALING = new Modifier<>();
    /**
     * Use this to increase your healing output. (eg. Energy Seer)
     */
    public static final Modifier<HealingModifyFromAttacker> MODIFY_OUTGOING_HEALING = new Modifier<>();
    /**
     * When you receive healing or a healing instance, do the following. (eg. Receive energy whenever you get hit)
     */
    public static final Modifier<HealingOnHealFromSelf> ON_INCOMING_HEALING = new Modifier<>();
    /**
     * When you heal or add a healing instance to another ally or source. (eg. Receive energy whenever you hit an entity)
     */
    public static final Modifier<HealingOnHealFromAttacker> ON_OUTGOING_HEALING = new Modifier<>();

    // Energy Modifiers

    /**
     * Increase energy gain per tick (eg. Avenger's Wrath)
     */
    public static final Modifier<EnergyGainPerTick> ENERGY_GAIN_PER_TICK = new Modifier<>();
    /**
     * Increase energy gain per hit (eg. Water Bolt Master 2)
     */
    public static final Modifier<EnergyGainPerHit> ENERGY_GAIN_PER_HIT = new Modifier<>();


    private Modifier() {

    }

    @FunctionalInterface
    public interface DamageBeforeVariableSetFromAttacker {

        void apply(WarlordsDamageHealingEvent event);

    }

    @FunctionalInterface
    public interface DamageBeforeReductionFromAttacker {

        void apply(WarlordsDamageHealingEvent event);

    }

    @FunctionalInterface
    public interface DamageAddCritChanceFromAttacker {

        void apply(WarlordsDamageHealingEvent event, FloatModifiable currentCritChance);

    }

    @FunctionalInterface
    public interface DamageAddCritMultiplierFromAttacker {

        void apply(WarlordsDamageHealingEvent event, FloatModifiable currentCritMultiplier);

    }

    @FunctionalInterface
    public interface DamagePostCritCalculationFromAttacker {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, float critChance, float critMultiplier);

    }

    @FunctionalInterface
    public interface DamageModifyBeforeInterveneFromSelf {

        void apply(WarlordsDamageHealingEvent event, MultiFloatModifiable currentDamageValue);

    }

    @FunctionalInterface
    public interface DamageModifyBeforeInterveneFromAttacker {

        void apply(WarlordsDamageHealingEvent event, MultiFloatModifiable currentDamageValue);

    }

    @FunctionalInterface
    public interface DamageOnInterveneFromAttacker {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue);

    }

    @FunctionalInterface
    public interface DamageModifyAfterInterveneFromSelf {

        void apply(WarlordsDamageHealingEvent event, MultiFloatModifiable currentDamageValue);

    }

    @FunctionalInterface
    public interface DamageModifyAfterInterveneFromAttacker {

        void apply(WarlordsDamageHealingEvent event, MultiFloatModifiable currentDamageValue);

    }

    @FunctionalInterface
    public interface DamageOnShieldFromSelf {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit);

    }

    @FunctionalInterface
    public interface DamageOnShieldFromAttacker {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit);

    }

    @FunctionalInterface
    public interface DamageModifyAfterAllFromSelf {

        void apply(WarlordsDamageHealingEvent event, MultiFloatModifiable currentDamageValue, boolean isCrit);

    }

    @FunctionalInterface
    public interface DamageOnDamageFromSelf {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit);

    }

    @FunctionalInterface
    public interface DamageOnDamageFromAttacker {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit);

    }

    @FunctionalInterface
    public interface DamageOnDeathFromEnemies {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, boolean isKiller);

    }

    @FunctionalInterface
    public interface DamageOnEndFromSelf {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit);

    }

    @FunctionalInterface
    public interface DamageOnEndFromAttacker {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit);

    }

    @FunctionalInterface
    public interface HealingBeforeVariableSetFromSelf {

        void apply(WarlordsDamageHealingEvent event);

    }

    @FunctionalInterface
    public interface HealingBeforeVariableSetFromAttacker {

        void apply(WarlordsDamageHealingEvent event);

    }

    @FunctionalInterface
    public interface HealingModifyFromSelf {

        void apply(WarlordsDamageHealingEvent event, MultiFloatModifiable currentHealValue);

    }

    @FunctionalInterface
    public interface HealingModifyFromAttacker {

        void apply(WarlordsDamageHealingEvent event, MultiFloatModifiable currentHealValue);

    }

    @FunctionalInterface
    public interface HealingOnHealFromSelf {

        void apply(WarlordsDamageHealingEvent event, float currentHealValue, boolean isCrit);

    }

    @FunctionalInterface
    public interface HealingOnHealFromAttacker {

        void apply(WarlordsDamageHealingEvent event, float currentHealValue, boolean isCrit);

    }

    @FunctionalInterface
    public interface EnergyGainPerTick {

        void apply(FloatModifiable energyGainPerTick);

    }

    @FunctionalInterface
    public interface EnergyGainPerHit {

        void apply(FloatModifiable energyPerHit);

    }


}
