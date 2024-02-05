package com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable;

import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents one upgrade in a towers upgrade path
 */
public abstract class TowerUpgrade {

    protected String name;
    protected List<TowerUpgradeInstance> upgradeInstances = new ArrayList<>();
    protected FloatModifiable cost;
    protected boolean unlocked = false;

    public abstract void upgrade();

    public void tick() {
        upgradeInstances.forEach(TowerUpgradeInstance::tick);
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

        for (TowerUpgradeInstance upgradeInstance : upgradeInstances) {
            lore.add(upgradeInstance.getDescription());
        }

        return lore;
    }

}
