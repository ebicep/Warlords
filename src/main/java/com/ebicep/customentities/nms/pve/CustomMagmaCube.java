package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityMagmaCube;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomMagmaCube extends EntityMagmaCube implements CustomEntity<CustomMagmaCube> {

    public CustomMagmaCube(World world) {
        super(world);
        setSize(6);
    }

    //jump
    @Override
    protected void bF() {
        this.motY = (0.05F + (float) this.getSize() * 0.05F); //motion y
        this.ai = true; //isAirBorne
    }

    @Override
    protected void bH() {

    }

    /*@Override
    public void onDeath(CustomMagmaCube customMagmaCube, Location deathLocation, WaveDefenseOption waveDefenseOption) {
        if (customMagmaCube.getSize() <= 1) return;
        //TODO Will finish this on saturday -Plikie
        for (int i = 0; i < 2; i++) {
            CustomMagmaCube babyMagmaCube = new CustomMagmaCube(((CraftWorld) deathLocation.getWorld()).getHandle());
            babyMagmaCube.setSize(customMagmaCube.getSize() - 1);
            babyMagmaCube.spawn(deathLocation);

            waveDefenseOption.spawn(babyMagmaCube);
        }
    }*/

    @Override
    public void spawn(Location location) {
        setPosition(location.getX(), location.getY(), location.getZ());
        getBukkitEntity().setCustomNameVisible(true);

        ((CraftWorld) location.getWorld()).getHandle().addEntity(this);
    }

    @Override
    public CustomMagmaCube get() {
        return this;
    }

}
