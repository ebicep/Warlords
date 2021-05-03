package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FallenSoul {

    private WarlordsPlayer shooter;
    private ArmorStand fallenSoul;
    private Location location;
    private Vector direction;
    private FallenSouls fallenSouls;
    private List<WarlordsPlayer> playersHit;

    public FallenSoul(WarlordsPlayer shooter, ArmorStand fallenSoul, Location location, Vector direction, FallenSouls fallenSouls) {
        this.shooter = shooter;
        this.fallenSoul = fallenSoul;
        fallenSoul.setHelmet(new ItemStack(Material.ACACIA_FENCE_GATE));
        fallenSoul.setGravity(false);
        fallenSoul.setVisible(false);
        fallenSoul.setHeadPose(new EulerAngle(direction.getY() * -1, 0, 0));
        this.location = location;
        this.direction = direction;
        this.fallenSouls = fallenSouls;
        playersHit = new ArrayList<>();
        playersHit.add(shooter);
    }

    public WarlordsPlayer getShooter() {
        return shooter;
    }

    public void setShooter(WarlordsPlayer shooter) {
        this.shooter = shooter;
    }

    public ArmorStand getFallenSoul() {
        return fallenSoul;
    }

    public void setFallenSoul(ArmorStand fallenSoul) {
        this.fallenSoul = fallenSoul;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Vector getDirection() {
        return direction;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public FallenSouls getFallenSouls() {
        return fallenSouls;
    }

    public void setFallenSouls(FallenSouls fallenSouls) {
        this.fallenSouls = fallenSouls;
    }

    public List<WarlordsPlayer> getPlayersHit() {
        return playersHit;
    }

    public void setPlayersHit(List<WarlordsPlayer> playersHit) {
        this.playersHit = playersHit;
    }
}
