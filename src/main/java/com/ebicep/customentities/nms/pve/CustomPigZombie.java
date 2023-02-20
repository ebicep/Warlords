package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPigZombie;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomPigZombie extends EntityPigZombie implements CustomEntity<CustomPigZombie> {

    public CustomPigZombie(World world) {
        super(world);
        resetAI(world);
        giveBaseAI();
    }

    public CustomPigZombie(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomPigZombie get() {
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
