package com.ebicep.warlords.classes;

public class Weapon {

    private int energyCost;
    private int critChance;
    private int critMultiplier;
    private String description;

    public Weapon(int energyCost, int critChance, int critMultiplier, String description) {
        this.energyCost = energyCost;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        this.description = description;
    }
}
