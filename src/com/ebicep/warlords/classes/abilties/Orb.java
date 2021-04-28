package com.ebicep.warlords.classes.abilties;

import net.minecraft.server.v1_8_R3.EntityExperienceOrb;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class Orb extends EntityExperienceOrb {

    private ArmorStand armorStand;

    public Orb(World world, Location location) {
        super(world, location.getX(), location.getY(), location.getZ(), 1000);
    }

    @Override
    public void d(EntityHuman entityhuman) {

    }

    @Override
    public void t_() {

    }

    public Orb spawn(Location loc) {
        World w = ((CraftWorld) loc.getWorld()).getHandle();
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        w.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return this;
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public void setArmorStand(ArmorStand armorStand) {
        this.armorStand = armorStand;
    }
}