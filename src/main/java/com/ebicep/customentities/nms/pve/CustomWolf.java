package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomWolf extends EntityWolf implements CustomEntity<CustomWolf> {

    public CustomWolf(World world) {
        super(world);
        resetAI(world);
        giveBaseAI();
    }

    public CustomWolf(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

//    @Override
//    public boolean a(EntityHuman entityhuman) {
//        System.out.println("aaaaaaaaaaaaaaaaaa");
//        return false;
//    }
//
//    @Override
//    public boolean d(ItemStack itemstack) {
//        System.out.println("dddddddddddddddddd");
//        return false;
//    }
//
//    @Override
//    public boolean a(ItemStack itemstack) {
//        System.out.println("31212312312");
//        return false;
//    }

    @Override
    public CustomWolf get() {
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
