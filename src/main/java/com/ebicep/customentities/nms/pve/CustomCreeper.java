package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityCreeper;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomCreeper extends EntityCreeper implements CustomEntity<CustomCreeper> {

    public CustomCreeper(World world) {
        super(world);
    }

    @Override
    public void spawn(Location location) {
        setPosition(location.getX(), location.getY(), location.getZ());
        getBukkitEntity().setCustomNameVisible(true);

        ((CraftWorld) location.getWorld()).getHandle().addEntity(this);
    }

    @Override
    public CustomCreeper get() {
        return this;
    }

}
