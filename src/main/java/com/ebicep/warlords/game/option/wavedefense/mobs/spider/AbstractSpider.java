package com.ebicep.warlords.game.option.wavedefense.mobs.spider;

import com.ebicep.customentities.nms.pve.CustomSpider;
import com.ebicep.warlords.game.option.wavedefense.mobs.AbstractMob;
import org.bukkit.Location;
import org.bukkit.inventory.EntityEquipment;

public abstract class AbstractSpider extends AbstractMob<CustomSpider> {

    public AbstractSpider(Location spawnLocation, String name, EntityEquipment ee, int maxHealth, float walkSpeed, int damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(new CustomSpider(spawnLocation.getWorld()), spawnLocation, name, ee, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }
}
