package com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;

import java.util.function.Consumer;

/**
 * Represents an upgrade instance of an upgrade, one upgrade can upgrade range/damage which are each one instance
 * This is to allow for easy access to the values of an upgrade if they need to be modified
 */
public abstract class TowerUpgradeInstance {

    protected final Consumer<TowerUpgradeInstance> onUpgrade;

    public TowerUpgradeInstance() {
        this.onUpgrade = towerUpgradeInstance -> {};
    }

    public TowerUpgradeInstance(Consumer<TowerUpgradeInstance> onUpgrade) {
        this.onUpgrade = onUpgrade;
    }

    public abstract Component getDescription();

    public void tick() {
    }

    public static abstract class Valued extends TowerUpgradeInstance {

        protected FloatModifiable value;

        public Valued(float value) {
            this(value, towerUpgradeInstance -> {});
        }

        public Valued(float value, Consumer<TowerUpgradeInstance> onUpgrade) {
            super(onUpgrade);
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

        public Damage(float value, Consumer<TowerUpgradeInstance> onUpgrade) {
            super(value, onUpgrade);
        }

        public Damage(float value, AbstractAbility ability) {
            super(value, towerUpgradeInstance -> {
//                ability.getMinDamageHeal().addAdditiveModifier("Upgrade", value);
//                ability.getMaxDamageHeal().addAdditiveModifier("Upgrade", value); TODO
            });
        }

        @Override
        public String getName() {
            return "Damage";
        }
    }

    public static class Healing extends Valued {

        public Healing(float value) {
            super(value);
        }

        public Healing(float value, Consumer<TowerUpgradeInstance> onUpgrade) {
            super(value, onUpgrade);
        }

        public Healing(float value, AbstractAbility ability) {
            super(value, towerUpgradeInstance -> {
//                ability.getMinDamageHeal().addAdditiveModifier("Upgrade", value);
//                ability.getMaxDamageHeal().addAdditiveModifier("Upgrade", value);
            });
        }

        @Override
        public String getName() {
            return "Healing";
        }
    }

    public static class Range extends Valued {

        public Range(float value) {
            super(value);
        }

        public Range(float value, Consumer<TowerUpgradeInstance> onUpgrade) {
            super(value, onUpgrade);
        }

        public <T extends AbstractAbility & HitBox> Range(float value, T ability) {
            super(value, towerUpgradeInstance -> {
                ability.getHitBoxRadius().addAdditiveModifier("Upgrade", value);
                ability.getHitBoxRadius().addAdditiveModifier("Upgrade", value);
            });
        }

        @Override
        public String getName() {
            return "Range";
        }
    }

}
