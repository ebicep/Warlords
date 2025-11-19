package com.ebicep.warlords.player.ingame.instances.type;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

@SuppressWarnings("InstantiationOfUtilityClass")
public class Modifier<T> {

    // Damage Modifiers
    public static final Modifier<DamageBeforeVariableSetFromAttacker> DAMAGE_BEFORE_VARIABLE_SET_ATTACKER = new Modifier<>();
    public static final Modifier<DamageBeforeReductionFromAttacker> DAMAGE_BEFORE_ANY_REDUCTION_ATTACKER = new Modifier<>();
    public static final Modifier<DamageAddCritChanceFromAttacker> DAMAGE_CRIT_CHANCE_ATTACKER = new Modifier<>();
    public static final Modifier<DamageAddCritMultiplierFromAttacker> DAMAGE_CRIT_MULTIPLIER_ATTACKER = new Modifier<>();
    public static final Modifier<DamagePostCritCalculationFromAttacker> DAMAGE_POST_CRIT_CALCULATION_ATTACKER = new Modifier<>();
    public static final Modifier<DamageModifyBeforeInterveneFromSelf> DAMAGE_BEFORE_INTERVENE_SELF = new Modifier<>();
    public static final Modifier<DamageModifyBeforeInterveneFromAttacker> DAMAGE_BEFORE_INTERVENE_ATTACKER = new Modifier<>();
    public static final Modifier<DamageOnInterveneFromAttacker> DAMAGE_ON_INTERVENE_ATTACKER = new Modifier<>();
    public static final Modifier<DamageModifyAfterInterveneFromSelf> DAMAGE_AFTER_INTERVENE_SELF = new Modifier<>();
    public static final Modifier<DamageModifyAfterInterveneFromAttacker> DAMAGE_AFTER_INTERVENE_ATTACKER = new Modifier<>();
    public static final Modifier<DamageOnShieldFromSelf> DAMAGE_ON_SHIELD_SELF = new Modifier<>();
    public static final Modifier<DamageOnShieldFromAttacker> DAMAGE_ON_SHIELD_ATTACKER = new Modifier<>();
    public static final Modifier<DamageModifyAfterAllFromSelf> DAMAGE_AFTER_ALL_SELF = new Modifier<>();
    public static final Modifier<DamageOnDamageFromSelf> DAMAGE_ON_DAMAGE_SELF = new Modifier<>();
    public static final Modifier<DamageOnDamageFromAttacker> DAMAGE_ON_DAMAGE_ATTACKER = new Modifier<>();
    public static final Modifier<DamageOnDeathFromEnemies> DAMAGE_ON_DEATH_ENEMIES = new Modifier<>();
    public static final Modifier<DamageOnEndFromSelf> DAMAGE_ON_END_SELF = new Modifier<>();
    public static final Modifier<DamageOnEndFromAttacker> DAMAGE_ON_END_ATTACKER = new Modifier<>();
    // Healing Modifiers
    public static final Modifier<HealingBeforeVariableSetFromSelf> HEALING_BEFORE_VARIABLE_SET_SELF = new Modifier<>();
    public static final Modifier<HealingBeforeVariableSetFromAttacker> HEALING_BEFORE_VARIABLE_SET_ATTACKER = new Modifier<>();
    public static final Modifier<HealingModifyFromSelf> HEALING_MODIFY_SELF = new Modifier<>();
    public static final Modifier<HealingModifyFromAttacker> HEALING_MODIFY_ATTACKER = new Modifier<>();
    public static final Modifier<HealingOnHealFromSelf> HEALING_ON_HEAL_SELF = new Modifier<>();
    public static final Modifier<HealingOnHealFromAttacker> HEALING_ON_HEAL_ATTACKER = new Modifier<>();
    // Energy Modifiers
    public static final Modifier<EnergyGainPerTick> ENERGY_GAIN_PER_TICK = new Modifier<>();
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

        void apply(WarlordsDamageHealingEvent event, FloatModifiable currentDamageValue);

    }

    @FunctionalInterface
    public interface DamageModifyBeforeInterveneFromAttacker {

        void apply(WarlordsDamageHealingEvent event, FloatModifiable currentDamageValue);

    }

    @FunctionalInterface
    public interface DamageOnInterveneFromAttacker {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue);

    }

    @FunctionalInterface
    public interface DamageModifyAfterInterveneFromSelf {

        void apply(WarlordsDamageHealingEvent event, FloatModifiable currentDamageValue);

    }

    @FunctionalInterface
    public interface DamageModifyAfterInterveneFromAttacker {

        void apply(WarlordsDamageHealingEvent event, FloatModifiable currentDamageValue);

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

        void apply(WarlordsDamageHealingEvent event, FloatModifiable currentDamageValue, boolean isCrit);

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

        void apply(WarlordsDamageHealingEvent event, FloatModifiable currentHealValue);

    }

    @FunctionalInterface
    public interface HealingModifyFromAttacker {

        void apply(WarlordsDamageHealingEvent event, FloatModifiable currentHealValue);

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
