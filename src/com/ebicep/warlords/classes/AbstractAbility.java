package com.ebicep.warlords.classes;

import com.ebicep.warlords.WarlordsPlayer;
import javax.annotation.Nonnull;
import org.bukkit.entity.Player;

public abstract class AbstractAbility {

    protected String name;
    protected int minDamageHeal;
    protected int maxDamageHeal;
    protected float currentCooldown;
    protected float cooldown;
    protected int energyCost;
    protected int critChance;
    protected int critMultiplier;
    protected String description;

    public AbstractAbility(String name, int minDamageHeal, int maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier, String description) {
        this.name = name;
        this.minDamageHeal = minDamageHeal;
        this.maxDamageHeal = maxDamageHeal;
        this.cooldown = cooldown;
        this.energyCost = energyCost;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        this.description = description;
    }

    public abstract void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player);

    public void boostSkill() {
        minDamageHeal *= 1.2;
        maxDamageHeal *= 1.2;
    }

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

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
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
