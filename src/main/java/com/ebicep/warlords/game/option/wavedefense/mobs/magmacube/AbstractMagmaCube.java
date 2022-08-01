package com.ebicep.warlords.game.option.wavedefense.mobs.magmacube;

import com.ebicep.customentities.nms.pve.CustomMagmaCube;
import com.ebicep.warlords.game.option.wavedefense.mobs.AbstractMob;
import org.bukkit.Location;
import org.bukkit.inventory.EntityEquipment;

public abstract class AbstractMagmaCube extends AbstractMob<CustomMagmaCube> {

    public AbstractMagmaCube(Location spawnLocation, String name, EntityEquipment ee, int maxHealth, float walkSpeed, int damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(new CustomMagmaCube(spawnLocation.getWorld()), spawnLocation, name, ee, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }
}
