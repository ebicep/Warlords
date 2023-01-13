package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomSlime extends EntitySlime implements CustomEntity<CustomSlime> {

    public CustomSlime(World world) {
        super(world);
        setSize(5);
    }

    public CustomSlime(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    //jump
    @Override
    protected void bF() {
        this.motY = 0.1; //motion y
        this.ai = true; //isAirBorne
    }

    @Override
    public CustomSlime get() {
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
