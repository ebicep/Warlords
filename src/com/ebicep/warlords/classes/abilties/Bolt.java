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
    private LightningBolt lightningBolt;

    public Bolt(WarlordsPlayer shooter, ArmorStand armorStand, Location location, Vector direction, LightningBolt lightningBolt) {
        this.shooter = shooter;
        this.armorStand = armorStand;
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setHelmet(new ItemStack(Material.RED_MUSHROOM));
        armorStand.setHeadPose(new EulerAngle(direction.getY() * -1, 0, 0));
        this.location = location;
        this.direction = direction;
        this.lightningBolt = lightningBolt;
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

    public LightningBolt getLightningBolt() {
        return lightningBolt;
    }

    public void setLightningBolt(LightningBolt lightningBolt) {
        this.lightningBolt = lightningBolt;
    }
}
