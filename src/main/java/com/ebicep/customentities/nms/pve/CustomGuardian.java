package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityGuardian;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomGuardian extends EntityGuardian implements CustomEntity<CustomGuardian> {

    public CustomGuardian(World world) {
        super(world);
    }


    public CustomGuardian(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomGuardian get() {
        return this;
    }

}
