package com.ebicep.warlords.classes;

import org.bukkit.event.player.PlayerInteractEvent;

public abstract class AbstractAbility {

    protected String name;
    protected int cooldown;
    protected int energyCost;
    protected int critChance;
    protected int critMultiplier;
    protected String description;

    public AbstractAbility(String name, int cooldown, int energyCost, int critChance, int critMultiplier, String description) {
        this.name = name;
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
