package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityCreeper;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomCreeper extends EntityCreeper implements CustomEntity<CustomCreeper> {

    public CustomCreeper(World world) {
        super(world);
    }

    public CustomCreeper(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomCreeper get() {
        return this;
    }

}
