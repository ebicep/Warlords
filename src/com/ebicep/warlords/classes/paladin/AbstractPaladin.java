package com.ebicep.warlords.classes.paladin;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.PlayerClass;
import org.bukkit.entity.Player;

public abstract class AbstractPaladin extends PlayerClass {

    protected Player player;
    protected int health;
    protected int totalEnergy;
    protected int energyPerSec;
    protected int energyOnHit;
    protected int damageResistance;

    public AbstractPaladin(AbstractAbility weapon, AbstractAbility red, AbstractAbility purple, AbstractAbility blue, AbstractAbility orange, int health, int totalEnergy, Player player) {
        super(weapon, red, purple, blue, orange, player);
        this.health = health;
        this.totalEnergy = totalEnergy;
        this.energyPerSec = 20;
        this.energyOnHit = 20;
        this.damageResistance = 0;
    }

    public AbstractPaladin(AbstractAbility weapon, AbstractAbility red, AbstractAbility purple, AbstractAbility blue, AbstractAbility orange, int health, int totalEnergy, int damageResistance, Player player) {
        super(weapon, red, purple, blue, orange, player);
        this.health = health;
        this.totalEnergy = totalEnergy;
        this.energyPerSec = 20;
        this.energyOnHit = 20;
        this.damageResistance = damageResistance;
    }
}
