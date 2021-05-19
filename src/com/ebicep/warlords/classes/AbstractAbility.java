package com.ebicep.warlords.classes;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAbility {

    protected String name;
    protected int minDamageHeal;
    protected int maxDamageHeal;
    protected float currentCooldown;
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

    public abstract void onActivate(Player player);

    public String getName() {
        return name;
    }

    public int getMinDamageHeal() {
        return minDamageHeal;
    }

    public void setMinDamageHeal(int minDamageHeal) {
        this.minDamageHeal = minDamageHeal;
    }

    public int getMaxDamageHeal() {
        return maxDamageHeal;
    }

    public void setMaxDamageHeal(int maxDamageHeal) {
        this.maxDamageHeal = maxDamageHeal;
    }

    public float getCurrentCooldown() {
        return currentCooldown;
    }

    public void setCurrentCooldown(float currentCooldown) {
        this.currentCooldown = currentCooldown;
    }

    public void subtractCooldown(float cooldown) {
        if (currentCooldown != 0) {
            if (currentCooldown - cooldown < 0) {
                currentCooldown = 0;
            } else {
                currentCooldown -= cooldown;
            }
        }
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
