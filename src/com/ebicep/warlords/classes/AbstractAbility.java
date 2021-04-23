package com.ebicep.warlords.classes;

import org.bukkit.event.player.PlayerInteractEvent;

public abstract class AbstractAbility {

    protected String name;
    protected int minDamageHeal;
    protected int maxDamageHeal;
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

    public int getCooldown() {
        return cooldown;
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
