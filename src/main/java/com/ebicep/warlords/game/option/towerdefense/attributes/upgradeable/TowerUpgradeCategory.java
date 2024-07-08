package com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public record TowerUpgradeCategory(Component name, List<TowerUpgradeInstance> upgradeInstances) {

    public TowerUpgradeCategory(String name, List<TowerUpgradeInstance> upgradeInstances) {
        this(Component.text(name, NamedTextColor.GREEN), upgradeInstances);
    }

    public TowerUpgradeCategory(Component name, TowerUpgradeInstance... upgradeInstances) {
        this(name, List.of(upgradeInstances));
    }

    public TowerUpgradeCategory(String name, TowerUpgradeInstance... upgradeInstances) {
        this(Component.text(name, NamedTextColor.GREEN), List.of(upgradeInstances));
    }

    public static class TowerUpgradeCategoryBuilder<T extends AbstractAbility> {

        public static <T extends AbstractAbility> TowerUpgradeCategoryBuilder<T> create(T ability) {
            return new TowerUpgradeCategoryBuilder<>(ability);
        }

        private final T ability;
        private final List<TowerUpgradeInstance> upgradeInstances = new ArrayList<>();

        public TowerUpgradeCategoryBuilder(T ability) {
            this.ability = ability;
        }

        public T getAbility() {
            return ability;
        }

        public List<TowerUpgradeInstance> getUpgradeInstances() {
            return upgradeInstances;
        }

        public TowerUpgradeCategory build() {
            return new TowerUpgradeCategory(ability.getName(), upgradeInstances);
        }

        public TowerUpgradeCategoryBuilder<T> damage(float value) {
            upgradeInstances.add(new TowerUpgradeInstance.Damage(value, ability));
            return this;
        }

        public TowerUpgradeCategoryBuilder<T> healing(float value) {
            upgradeInstances.add(new TowerUpgradeInstance.Healing(value, ability));
            return this;
        }

        public TowerUpgradeCategoryBuilder<T> range(float value) {
            if (ability instanceof HitBox) {
                upgradeInstances.add(new TowerUpgradeInstance.Range(value, (AbstractAbility & HitBox) ability));
            } else {
                ChatUtils.MessageType.TOWER_DEFENSE.sendErrorMessage("Can't add range upgrade to " + ability.getName());
            }
            return this;
        }

        public TowerUpgradeCategoryBuilder<T> value(String name, float value, Function<T, FloatModifiable> modifiableFunction) {
            upgradeInstances.add(new TowerUpgradeInstance.Valued(value, towerUpgradeInstance -> {
                modifiableFunction.apply(ability).addAdditiveModifier("Upgrade", value);
            }) {
                @Override
                public String getName() {
                    return name;
                }
            });
            return this;
        }

    }

}
