package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomSlime extends EntitySlime implements CustomEntity {

    public CustomSlime(World world) {
        super(world);
        setSize(8);
    }

    @Override
    protected void bF() {
        this.motY = 0.1;
        this.ai = true;
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
