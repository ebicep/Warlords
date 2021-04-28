package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.WarlordsPlayer;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class Bolt {

    private WarlordsPlayer shooter;
    private ArmorStand armorStand;
    private Location location;
    private Vector direction;
    private String name;
    private int minDmg;
    private int maxDmg;
    private int critChance;
    private int critMultiplier;

    public Bolt(WarlordsPlayer shooter, ArmorStand armorStand, Location location, Vector direction, String name, int minDmg, int maxDmg, int critChance, int critMultiplier) {
        this.shooter = shooter;
        this.armorStand = armorStand;
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setHelmet(new ItemStack(Material.RED_MUSHROOM));
        armorStand.setHeadPose(new EulerAngle(direction.getY() * -1, 0, 0));
        this.location = location;
        this.direction = direction;
        this.name = name;
        this.minDmg = minDmg;
        this.maxDmg = maxDmg;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
    }

    public WarlordsPlayer getShooter() {
        return shooter;
    }

    public void setShooter(WarlordsPlayer shooter) {
        this.shooter = shooter;
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public void setArmorStand(ArmorStand armorStand) {
        this.armorStand = armorStand;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Vector getTeleportDirection() {
        return direction;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinDmg() {
        return minDmg;
    }

    public void setMinDmg(int minDmg) {
        this.minDmg = minDmg;
    }

    public int getMaxDmg() {
        return maxDmg;
    }

    public void setMaxDmg(int maxDmg) {
        this.maxDmg = maxDmg;
    }

    public int getCritChance() {
        return critChance;
    }

    public void setCritChance(int critChance) {
        this.critChance = critChance;
    }

    public int getCritMultiplier() {
        return critMultiplier;
    }

    public void setCritMultiplier(int critMultiplier) {
        this.critMultiplier = critMultiplier;
    }

    //    @Override
//    public void g(float f, float f1) {
//        if (!hasGravity()) { // hasGravity() actually means hasNoGravity(), probably a mistake in deobfuscating.
//            super.g(f, f1);
//        } else {
//            move(5, motY, motZ); // Give them some velocity anyways ;3
//        }
//    }
//    @Override
//    public void m() { // This method is called each tick. This also slowly multiplies each velocity by 0.98, so I just reset those values.
//        if (hasGravity()) {
//            double motX = this.motX, motY = this.motY, motZ = this.motZ;
//            super.m();
//            this.motX = motX;
//            this.motY = motY;
//            this.motZ = motZ;
//        } else super.m();
//    }
}
