package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityWitch;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomWitch extends EntityWitch implements CustomEntity<CustomWitch> {

    public CustomWitch(World world) {
        super(world);
        resetAI(world);
        aiWander(1);
        aiLookAtPlayer();
    }

    public CustomWitch(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomWitch get() {
        return this;
    }

    private boolean stunned;

    @Override
    public void collide(Entity entity) {
        if (stunned) {
            return;
        }
        super.collide(entity);
    }

    @Override
    public void setStunned(boolean stunned) {
        this.stunned = stunned;
    }
}
