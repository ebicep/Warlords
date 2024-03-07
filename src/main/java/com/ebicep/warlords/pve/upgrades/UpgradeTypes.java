package com.ebicep.warlords.pve.upgrades;

import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import javax.annotation.Nullable;

public class UpgradeTypes {

    public static final NamedUpgradeType ENERGY_COST = new NamedUpgradeType() {
        @Override
        public String getName() {
            return "Spark";
        }

        @Override
        public String getDescription0(String value) {
            return "-" + value + " Energy Cost";
        }

        @Override
        public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
            modifier.setModifier(-value);
        }
    };
    public static final NamedUpgradeType COOLDOWN_REDUCTION = new NamedUpgradeType() {
        @Override
        public String getName() {
            return "Zeal";
        }

        @Override
        public String getDescription0(String value) {
            return "+" + value + "% Cooldown Reduction";
        }

        @Nullable
        @Override
        public String getDescription(double value) {
            return NamedUpgradeType.super.getDescription(value * 100);
        }

        @Override
        public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
            modifier.setModifier(-value);
        }
    };
    public static final NamedUpgradeType HITBOX = new UpgradeTypes.NamedUpgradeType() {

        @Override
        public String getName() {
            return "Scope";
        }

        @Override
        public String getDescription0(String value) {
            return "+" + value + " Block Radius";
        }

        @Override
        public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
            modifier.setModifier(value);
        }
    };
    public static final NamedUpgradeType SPLASH = new UpgradeTypes.NamedUpgradeType() {

        @Override
        public String getName() {
            return "Area";
        }

        @Override
        public String getDescription0(String value) {
            return "+" + value + " Splash Radius";
        }

        @Override
        public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
            modifier.setModifier(value);
        }
    };
    public static final NamedUpgradeType DAMAGE = new UpgradeTypes.NamedUpgradeType() {

        @Override
        public String getName() {
            return "Impair";
        }

        @Override
        public String getDescription0(String value) {
            return "+" + value + "% Damage";
        }

        @Override
        public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
            modifier.setModifier(value / 100);
        }
    };
    public static final NamedUpgradeType HEALING = new UpgradeTypes.NamedUpgradeType() {

        @Override
        public String getName() {
            return "Alleviate";
        }

        @Override
        public String getDescription0(String value) {
            return "+" + value + "% Healing";
        }

        @Override
        public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
            modifier.setModifier(value / 100);
        }
    };

    public interface UpgradeType {

        @Nullable
        default String getDescription(double value) {
            return getDescription0(NumberFormat.formatOptionalHundredths(value));
        }

        @Nullable
        String getDescription0(String value);

        /**
         * Only applies to the effect, not cost or description
         *
         * @return true if the value should be scaled by the level of the upgrade
         */
        default boolean autoScaleEffect() {
            return true;
        }

        default boolean autoScaleDescription() {
            return true;
        }

        default void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {

        }

        default void run(float value) {

        }
    }

    public interface NamedUpgradeType extends UpgradeType {
        String getName();
    }


    public interface DamageUpgradeType extends NamedUpgradeType {

        @Override
        default String getDescription0(String value) {
            return "+" + value + "% Damage";
        }

        @Override
        default String getName() {
            return "Impair";
        }
    }

    public interface HealingUpgradeType extends NamedUpgradeType {

        @Override
        default String getDescription0(String value) {
            return "+" + value + "% Healing";
        }

        @Override
        default String getName() {
            return "Alleviate";
        }
    }

    public interface DurationUpgradeType extends NamedUpgradeType {

        @Override
        default String getName() {
            return "Chronos";
        }

        @Override
        default String getDescription0(String value) {
            return "+" + value + "s Duration";
        }

        @Nullable
        @Override
        default String getDescription(double value) {
            return NamedUpgradeType.super.getDescription(value / 20);
        }

        @Override
        default boolean autoScaleEffect() {
            return false;
        }
    }

    public interface ShieldUpgradeType extends NamedUpgradeType {

        @Override
        default String getName() {
            return "Fortify";
        }

    }

    public interface EnergyUpgradeType extends NamedUpgradeType {

        @Override
        default String getName() {
            return "Energize";
        }

    }

    public interface SpeedUpgradeType extends NamedUpgradeType {

        @Override
        default String getName() {
            return "Agility";
        }

    }

    public interface LuckUpgradeType extends NamedUpgradeType {

        @Override
        default String getName() {
            return "Fortune";
        }

    }

}
