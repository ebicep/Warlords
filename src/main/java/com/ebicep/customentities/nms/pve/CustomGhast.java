package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityGhast;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomGhast extends EntityGhast implements CustomEntity<CustomGhast> {

    public CustomGhast(World world) {
        super(world);
    }

    @Override
    public void spawn(Location location) {
        setPosition(location.getX(), location.getY(), location.getZ());
        getBukkitEntity().setCustomNameVisible(true);

        ((CraftWorld) location.getWorld()).getHandle().addEntity(this);
    }

    @Override
    public CustomGhast get() {
        return this;
    }

}
