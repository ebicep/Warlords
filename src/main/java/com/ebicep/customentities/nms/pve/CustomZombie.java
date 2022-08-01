package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomZombie extends EntityZombie implements CustomEntity<CustomZombie> {

    //https://github.com/ZeroedInOnTech/1.8.8/blob/master/1.8.8/Build%20918/src/minecraft/net/minecraft/entity/monster/EntityZombie.java
    public CustomZombie(World world) {
        super(world);
        setBaby(false);

        resetAI(world);
        giveBaseAI();
    }

    public CustomZombie(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomZombie get() {
        return this;
    }

}
