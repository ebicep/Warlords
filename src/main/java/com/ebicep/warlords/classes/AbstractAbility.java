package com.ebicep.warlords.classes;

import org.bukkit.entity.Player;

public abstract class AbstractAbility {

    protected String name;
    protected float minDamageHeal;
    protected float maxDamageHeal;
    protected float currentCooldown;
    protected float cooldown;
    protected int energyCost;
    protected int critChance;
    protected int critMultiplier;
    protected String description;
    protected boolean boosted;

    public AbstractAbility(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        this.name = name;
        this.minDamageHeal = minDamageHeal;
        this.maxDamageHeal = maxDamageHeal;
        this.cooldown = cooldown;
        this.energyCost = energyCost;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        boosted = false;
    }

    public abstract void updateDescription(Player player);

    public abstract void onActivate(Player player);

    public void boostSkill() {
        boosted = true;
        this.minDamageHeal *= 1.2;
        this.maxDamageHeal *= 1.2;
    }

    // purely for fun now, perhaps may actually use this later
    public void boostOrange() {
        boosted = true;
        this.minDamageHeal *= 1.4;
        this.maxDamageHeal *= 1.4;
    }

    public String getName() {
        return name;
    }

    public float getMinDamageHeal() {
        return minDamageHeal;
    }

    public void setMinDamageHeal(float minDamageHeal) {
        this.minDamageHeal = minDamageHeal;
    }

    public float getMaxDamageHeal() {
        return maxDamageHeal;
    }

    public void setMaxDamageHeal(float maxDamageHeal) {
        this.maxDamageHeal = maxDamageHeal;
    }

    public int getCurrentCooldownItem() {
        return (int) Math.round(currentCooldown + .5);
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

    /*
flameburst/chain: 9.4
breath: 6.3
water breath: 12.53
time warp: 28.19
arcane shield/bloodlust/rod/repentance: 31.32
barrier/inferno/berserk: 46.98
healing rain/wrath: 52.85
ground slam: 9.32
defender slam: 7.34
vene: 14.09
last stand: 56.38
wave: 11.74
orbs/holy radiance: 19.57
army/presence/hammer/healing totem/sg totem: 70.47
reckless charge: 9.98
infusion/earthliving/windfury: 15.66
prot radiance: 9.87
boulder: 7.05
chain heal: 7.99
consecrate: 7.83
capac totem: 62.64
spirit link: 8.61
souldbind: 21.92
    */
}
