package com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.util.java.RomanNumber;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents one upgrade in a towers upgrade path
 */
public class TowerUpgrade {

    protected String name;
    protected FloatModifiable cost;
    protected Consumer<TowerUpgrade> onUpgrade;
    protected List<TowerUpgradeCategory> upgradeCategories;
    protected boolean unlocked = false;

    public TowerUpgrade(TowerUpgradeBuilder towerUpgradeBuilder) {
        this.name = towerUpgradeBuilder.name;
        this.cost = new FloatModifiable(towerUpgradeBuilder.cost);
        this.onUpgrade = towerUpgradeBuilder.onUpgrade;
        this.upgradeCategories = towerUpgradeBuilder.upgradeCategories;
    }

    public TowerUpgrade(String name, TowerUpgradeInstance... upgradeInstances) {
        this.name = name;
        this.cost = new FloatModifiable(0);
        this.upgradeCategories = new ArrayList<>();
//        this.upgradeCategories.addAll(Arrays.asList(upgradeInstances));
    }

    public void upgrade() {
        unlocked = true;
        onUpgrade.accept(this);
        onUpgrade();
    }

    protected void onUpgrade() {

    }

    public void tick() {
        upgradeCategories
                .stream()
                .flatMap(towerUpgradeCategory -> towerUpgradeCategory.upgradeInstances().stream())
                .forEach(TowerUpgradeInstance::tick);
    }

    public String getName() {
        return name;
    }

    public FloatModifiable getFloatModifiableCost() {
        return cost;
    }

    public float getCost() {
        return cost.getCalculatedValue();
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public List<Component> getDescription() {
        List<Component> lore = new ArrayList<>();

        for (int i = 0; i < upgradeCategories.size(); i++) {
            TowerUpgradeCategory upgradeCategory = upgradeCategories.get(i);
            lore.add(upgradeCategory.name());
            for (TowerUpgradeInstance upgradeInstance : upgradeCategory.upgradeInstances()) {
                lore.add(Component.text("  ").append(upgradeInstance.getDescription()));
            }
            if (i != upgradeCategories.size() - 1) {
                lore.add(Component.empty());
            }
        }

        return lore;
    }

    public static class TowerUpgradeBuilder {

        public static TowerUpgradeBuilder create(String name, float cost) {
            return new TowerUpgradeBuilder(name, cost);
        }

        public static TowerUpgradeBuilder create(int level, float cost) {
            return new TowerUpgradeBuilder(level, cost);
        }

        private final String name;
        private final float cost;
        private final List<TowerUpgradeCategory> upgradeCategories = new ArrayList<>();
        private Consumer<TowerUpgrade> onUpgrade = upgrade -> {};

        public TowerUpgradeBuilder(String name, float cost) {
            this.name = name;
            this.cost = cost;
        }

        public TowerUpgradeBuilder(int level, float cost) {
            this.name = "Level " + RomanNumber.toRoman(level);
            this.cost = cost;
        }

        public TowerUpgrade build() {
            return new TowerUpgrade(this);
        }

        public TowerUpgradeBuilder setOnUpgrade(Consumer<TowerUpgrade> onUpgrade) {
            this.onUpgrade = onUpgrade;
            return this;
        }

        public TowerUpgradeBuilder addUpgradeCategory(String name, TowerUpgradeInstance... upgradeInstances) {
            upgradeCategories.add(new TowerUpgradeCategory(name, upgradeInstances));
            return this;
        }

        public TowerUpgradeBuilder addUpgradeCategory(TowerUpgradeInstance... upgradeInstances) {
            upgradeCategories.add(new TowerUpgradeCategory(Component.text("General", NamedTextColor.GREEN), upgradeInstances));
            return this;
        }

        public TowerUpgradeBuilder addUpgradeCategory(AbstractAbility ability, TowerUpgradeInstance... upgradeInstances) {
            upgradeCategories.add(new TowerUpgradeCategory(ability.getName(), upgradeInstances));
            return this;
        }

        public TowerUpgradeBuilder addUpgradeCategory(TowerUpgradeCategory.TowerUpgradeCategoryBuilder<?> towerUpgradeCategoryBuilder) {
            upgradeCategories.add(towerUpgradeCategoryBuilder.build());
            return this;
        }

    }

}
