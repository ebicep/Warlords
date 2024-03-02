package com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable;

import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;

/**
 * Represents an upgrade instance of an upgrade, one upgrade can upgrade range/damage which are each one instance
 * This is to allow for easy access to the values of an upgrade if they need to be modified
 */
public abstract class TowerUpgradeInstance {

    protected FloatModifiable value;

    public TowerUpgradeInstance(float value) {
        this.value = new FloatModifiable(value);
    }

    public abstract Component getDescription();

    public void tick() {
        value.tick();
    }

    public FloatModifiable getFloatModifiableValue() {
        return value;
    }

    public float getValue() {
        return value.getCalculatedValue();
    }

    public static class DamageUpgradeInstance extends TowerUpgradeInstance {

        public DamageUpgradeInstance(float value) {
            super(value);
        }

        @Override
        public Component getDescription() {
            return Component.text("+" + NumberFormat.formatOptionalTenths(value.getCalculatedValue()) + " Damage");
        }
    }

}
