package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.player.general.SkillBoosts;

import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractWeapon {

    protected int meleeDamage;
    protected int critChance;
    protected int critMultiplier;
    protected int healthBonus;
    protected SkillBoosts skillBoost;

    public AbstractWeapon() {
        generateStats();
    }

    public abstract void generateStats();

    protected int generateRandomValueBetween(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

}
