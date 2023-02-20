package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityEnderman;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomEnderman extends EntityEnderman implements CustomEntity<CustomEnderman> {

    public CustomEnderman(World world) {
        super(world);
        resetAI(world);
        giveBaseAI();
    }

    public CustomEnderman(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomEnderman get() {
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
