package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomEnderman extends EntityEnderman implements CustomEntity<CustomEnderman> {

    public CustomEnderman(World world) {
        super(world);
        this.goalSelector = new PathfinderGoalSelector(world != null && world.methodProfiler != null ? world.methodProfiler : null);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0, true));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 80.0F));
    }

    @Override
    public void spawn(Location location) {
        setPosition(location.getX(), location.getY(), location.getZ());
        getBukkitEntity().setCustomNameVisible(true);

        ((CraftWorld) location.getWorld()).getHandle().addEntity(this);
    }

    @Override
    public CustomEnderman get() {
        return this;
    }

}
