package com.ebicep.customentities.nms;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Horse;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

public class CustomHorse extends Horse {

    public CustomHorse(Location location) {
        super(EntityType.HORSE, ((CraftWorld) location.getWorld()).getHandle());
        setPos(location.getX(), location.getY(), location.getZ());
    }

    @Override
    protected void registerGoals() {

    }

    @Override
    protected boolean canPerformRearing() {
        return false;
    }

}
