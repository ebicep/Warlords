package com.ebicep.warlords.classes;

import org.bukkit.event.player.PlayerInteractEvent;

public abstract class AbstractAbility {

    protected String name;
    protected int minDamageHeal;
    protected int maxDamageHeal;
    protected int currentCooldown;
    protected int cooldown;
    protected int energyCost;
    protected int critChance;
    protected int critMultiplier;
    protected String description;

    public AbstractAbility(String name, int minDamageHeal, int maxDamageHeal, int cooldown, int energyCost, int critChance, int critMultiplier, String description) {
        this.name = name;
        this.minDamageHeal = minDamageHeal;
        this.maxDamageHeal = maxDamageHeal;
        this.cooldown = cooldown;
        this.energyCost = energyCost;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        this.description = description;
    }

    public abstract void onActivate(PlayerInteractEvent e);

    public String getName() {
        return name;
    }

    public int getMinDamageHeal() {
        return minDamageHeal;
    }

    public int getMaxDamageHeal() {
        return maxDamageHeal;
    }

    public int getCurrentCooldown() {
        return currentCooldown;
    }

    public void setCurrentCooldown(int currentCooldown) {
        this.currentCooldown = currentCooldown;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public int getCritChance() {
        return critChance;
    }

    public int getCritMultiplier() {
        return critMultiplier;
    }

    public String getDescription() {
        return description;
    }
}
