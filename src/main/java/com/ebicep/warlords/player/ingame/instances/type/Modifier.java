package com.ebicep.warlords.player.ingame.instances.type;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

@SuppressWarnings("InstantiationOfUtilityClass")
public class Modifier<T> {

    public static final Modifier<BeforeVariableSetFromAttacker> BEFORE_VARIABLE_SET_ATTACKER = new Modifier<>();
    public static final Modifier<BeforeReductionFromAttacker> BEFORE_ANY_REDUCTION_ATTACKER = new Modifier<>();
    public static final Modifier<AddCritChanceFromAttacker> CRIT_CHANCE_ATTACKER = new Modifier<>();
    public static final Modifier<AddCritMultiplierFromAttacker> CRIT_MULTIPLIER_ATTACKER = new Modifier<>();
    public static final Modifier<PostCritCalculationFromAttacker> POST_CRIT_CALCULATION_ATTACKER = new Modifier<>();
    public static final Modifier<ModifyDamageBeforeInterveneFromSelf> DAMAGE_BEFORE_INTERVENE_SELF = new Modifier<>();
    public static final Modifier<ModifyDamageBeforeInterveneFromAttacker> DAMAGE_BEFORE_INTERVENE_ATTACKER = new Modifier<>();
    public static final Modifier<OnInterveneFromAttacker> ON_INTERVENE_ATTACKER = new Modifier<>();
    public static final Modifier<ModifyDamageAfterInterveneFromSelf> DAMAGE_AFTER_INTERVENE_SELF = new Modifier<>();
    public static final Modifier<ModifyDamageAfterInterveneFromAttacker> DAMAGE_AFTER_INTERVENE_ATTACKER = new Modifier<>();
    public static final Modifier<OnShieldFromSelf> ON_SHIELD_SELF = new Modifier<>();
    public static final Modifier<OnShieldFromAttacker> ON_SHIELD_ATTACKER = new Modifier<>();
    public static final Modifier<ModifyDamageAfterAllFromSelf> DAMAGE_AFTER_ALL_SELF = new Modifier<>();
    public static final Modifier<OnDamageFromSelf> ON_DAMAGE_SELF = new Modifier<>();
    public static final Modifier<OnDamageFromAttacker> ON_DAMAGE_ATTACKER = new Modifier<>();
    public static final Modifier<OnDeathFromEnemies> ON_DEATH_ENEMIES = new Modifier<>();
    public static final Modifier<OnEndFromSelf> ON_END_SELF = new Modifier<>();
    public static final Modifier<OnEndFromAttacker> ON_END_ATTACKER = new Modifier<>();

    private Modifier() {

    }

    @FunctionalInterface
    public interface BeforeVariableSetFromAttacker {

        void apply(WarlordsDamageHealingEvent event);

    }

    @FunctionalInterface
    public interface BeforeReductionFromAttacker {

        void apply(WarlordsDamageHealingEvent event);

    }

    @FunctionalInterface
    public interface AddCritChanceFromAttacker {

        void apply(WarlordsDamageHealingEvent event, FloatModifiable currentCritChance);

    }

    @FunctionalInterface
    public interface AddCritMultiplierFromAttacker {

        void apply(WarlordsDamageHealingEvent event, FloatModifiable currentCritMultiplier);

    }

    @FunctionalInterface
    public interface PostCritCalculationFromAttacker {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, float critChance, float critMultiplier);

    }

    @FunctionalInterface
    public interface ModifyDamageBeforeInterveneFromSelf {

        void apply(WarlordsDamageHealingEvent event, FloatModifiable currentDamageValue);

    }

    @FunctionalInterface
    public interface ModifyDamageBeforeInterveneFromAttacker {

        void apply(WarlordsDamageHealingEvent event, FloatModifiable currentDamageValue);

    }

    @FunctionalInterface
    public interface OnInterveneFromAttacker {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue);

    }

    @FunctionalInterface
    public interface ModifyDamageAfterInterveneFromSelf {

        void apply(WarlordsDamageHealingEvent event, FloatModifiable currentDamageValue);

    }

    @FunctionalInterface
    public interface ModifyDamageAfterInterveneFromAttacker {

        void apply(WarlordsDamageHealingEvent event, FloatModifiable currentDamageValue);

    }

    @FunctionalInterface
    public interface OnShieldFromSelf {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit);

    }

    @FunctionalInterface
    public interface OnShieldFromAttacker {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit);

    }

    @FunctionalInterface
    public interface ModifyDamageAfterAllFromSelf {

        void apply(WarlordsDamageHealingEvent event, FloatModifiable currentDamageValue, boolean isCrit);

    }

    @FunctionalInterface
    public interface OnDamageFromSelf {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit);

    }

    @FunctionalInterface
    public interface OnDamageFromAttacker {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit);

    }

    @FunctionalInterface
    public interface OnDeathFromEnemies {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, boolean isKiller);

    }

    @FunctionalInterface
    public interface OnEndFromSelf {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit);

    }

    @FunctionalInterface
    public interface OnEndFromAttacker {

        void apply(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit);

    }


}
