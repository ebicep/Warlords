package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomSlime extends EntitySlime implements CustomEntity<CustomSlime> {

    public CustomSlime(World world) {
        super(world);
        setSize(8);
    }

    //jump
    @Override
    protected void bF() {
        this.motY = 0.1; //motion y
        this.ai = true; //isAirBorne
    }

    @Override
    public void spawn(Location location) {
        setPosition(location.getX(), location.getY(), location.getZ());
        getBukkitEntity().setCustomNameVisible(true);

        ((CraftWorld) location.getWorld()).getHandle().addEntity(this);
    }

    @Override
    public CustomSlime get() {
        return this;
    }

}
