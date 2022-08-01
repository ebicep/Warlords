package com.ebicep.warlords.game.option.wavedefense.mobs.slime;

import com.ebicep.customentities.nms.pve.CustomSlime;
import com.ebicep.warlords.game.option.wavedefense.mobs.AbstractMob;
import org.bukkit.Location;
import org.bukkit.inventory.EntityEquipment;

public abstract class AbstractSlime extends AbstractMob<CustomSlime> {
    public AbstractSlime(Location spawnLocation, String name, EntityEquipment ee, int maxHealth, float walkSpeed, int damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(new CustomSlime(spawnLocation.getWorld()), spawnLocation, name, ee, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }
}
