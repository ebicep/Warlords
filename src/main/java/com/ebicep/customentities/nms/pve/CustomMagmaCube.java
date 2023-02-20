package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityMagmaCube;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomMagmaCube extends EntityMagmaCube implements CustomEntity<CustomMagmaCube> {

    private final int flameHitbox = 6;

    public CustomMagmaCube(World world) {
        super(world);
        setSize(7);
    }

    public CustomMagmaCube(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    //jump
    @Override
    protected void bF() {
        this.motY = (0.07F + (float) this.getSize() * 0.07F); //motion y
        this.ai = true; //isAirBorne
    }

    @Override
    protected void bH() {

    }

    @Override
    public CustomMagmaCube get() {
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
