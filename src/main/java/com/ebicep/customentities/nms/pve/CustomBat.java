package com.ebicep.customentities.nms.pve;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;

public class CustomBat extends Bat {

    public CustomBat(Location location) {
        super(EntityType.BAT, ((CraftWorld) location.getWorld()).getHandle());
        setPos(location.getX(), location.getY(), location.getZ());
        setInvisible(true);
        setNoAi(true);
        setInvulnerable(true);
    }

    @Override
    public void playAmbientSound() {
    }

    @Override
    public void tick() {
    }

    @Override
    public void setResting(boolean roosting) {
        super.setResting(roosting);
        setInvisible(true);
        setNoAi(true);
        setInvulnerable(true);
    }
}
