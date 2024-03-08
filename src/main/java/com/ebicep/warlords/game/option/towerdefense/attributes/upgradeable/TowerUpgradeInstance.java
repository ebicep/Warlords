package com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable;

import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;

/**
 * Represents an upgrade instance of an upgrade, one upgrade can upgrade range/damage which are each one instance
 * This is to allow for easy access to the values of an upgrade if they need to be modified
 */
public abstract class TowerUpgradeInstance {

    public abstract Component getDescription();

    public void tick() {
    }

    public static abstract class Valued extends TowerUpgradeInstance {

        protected FloatModifiable value;

        public Valued(float value) {
            super();
            this.value = new FloatModifiable(value);
        }

        public FloatModifiable getFloatModifiableValue() {
            return value;
        }

        public float getValue() {
            return value.getCalculatedValue();
        }

        public abstract String getName();

        @Override
        public Component getDescription() {
            return Component.text("+" + NumberFormat.formatOptionalTenths(value.getCalculatedValue()) + " " + getName());
        }
    }

    public static class Damage extends Valued {

        public Damage(float value) {
            super(value);
        }

        @Override
        public String getName() {
            return "Damage";
        }
    }

    public static class Range extends Valued {

        public Range(float value) {
            super(value);
        }

        @Override
        public String getName() {
            return "Range";
        }
    }

}
