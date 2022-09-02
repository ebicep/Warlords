package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityGhast;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomGhast extends EntityGhast implements CustomEntity<CustomGhast> {

    public CustomGhast(World world) {
        super(world);
    }

    public CustomGhast(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomGhast get() {
        return this;
    }

}