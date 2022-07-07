package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityMagmaCube;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomMagmaCube extends EntityMagmaCube implements CustomEntity {

    public CustomMagmaCube(World world) {
        super(world);
        setSize(6);
    }

    @Override
    protected void bF() {
        this.motY = (0.05F + (float)this.getSize() * 0.05F);
        this.ai = true;
    }

    @Override
    protected void bH() {

    }

    @Override
    public void spawn(Location location) {
        setPosition(location.getX(), location.getY(), location.getZ());
        getBukkitEntity().setCustomNameVisible(true);

        ((CraftWorld) location.getWorld()).getHandle().addEntity(this);
    }

    @Override
    public EntityInsentient get() {
        return this;
    }

}
