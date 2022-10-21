package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityIronGolem;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomIronGolem extends EntityIronGolem implements CustomEntity<CustomIronGolem> {

    public CustomIronGolem(World world) {
        super(world);
        resetAI(world);
        giveBaseAI(1.0, 0.6, 20);
    }

    public CustomIronGolem(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }


    @Override
    public CustomIronGolem get() {
        return this;
    }

    /**
     * On collide with entity, overrided so any entity that collides will this will not be targeted
     *
     * @param entity The entity to check against
     */
    @Override
    protected void s(Entity entity) {

    }

}
