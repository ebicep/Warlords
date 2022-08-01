package com.ebicep.warlords.game.option.wavedefense.mobs.pigzombie;

import com.ebicep.customentities.nms.pve.CustomPigZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.AbstractMob;
import org.bukkit.Location;
import org.bukkit.inventory.EntityEquipment;

public abstract class AbstractPigZombie extends AbstractMob<CustomPigZombie> {

    public AbstractPigZombie(Location spawnLocation, String name, EntityEquipment ee, int maxHealth, float walkSpeed, int damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(new CustomPigZombie(spawnLocation.getWorld()), spawnLocation, name, ee, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }
}
