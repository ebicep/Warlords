package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FallenSouls extends AbstractAbility {

    public FallenSouls() {
        super("Fallen Souls", -197, -254, 0, 55, 20, 180,
                "§7Summon a wave of fallen souls, dealing\n" +
                        "§c%dynamic.value% §7- §c%dynamic.value% §7damage to all enemies they\n" +
                        "§7pass through. Each target hit reduces the\n" +
                        "§7cooldown of Spirit Link by §62 §7seconds.");
    }

    @Override
    public void onActivate(Player player) {
        Location location = player.getLocation().add(player.getLocation().getDirection().multiply(-1));
        ArmorStand fallenSoulLeft = player.getWorld().spawn(location.subtract(0, .5, 0), ArmorStand.class);
        Location locationLeft = player.getLocation().add(player.getLocation().getDirection().multiply(-1));
        locationLeft.setYaw(location.getYaw() - 15);
        location.add(0, .5, 0);
        ArmorStand fallenSoulMiddle = player.getWorld().spawn(location.subtract(0, .5, 0), ArmorStand.class);
        Location locationMiddle = player.getLocation().add(player.getLocation().getDirection().multiply(-1));
        locationMiddle.setYaw(location.getYaw() - 0);
        location.add(0, .5, 0);
        ArmorStand fallenSoulRight = player.getWorld().spawn(location.subtract(0, .5, 0), ArmorStand.class);
        Location locationRight = player.getLocation().add(player.getLocation().getDirection().multiply(-1));
        locationRight.setYaw(location.getYaw() + 15);
        location.add(0, .5, 0);
        Warlords.getFallenSouls().add(new FallenSoul(Warlords.getPlayer(player), fallenSoulLeft, fallenSoulMiddle, fallenSoulRight, player.getLocation(), player.getLocation(), player.getLocation(), locationLeft.getDirection(), locationMiddle.getDirection(), locationRight.getDirection(), this));

        Warlords.getPlayer(player).subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.lightningbolt.impact", 1, 1.5f);
        }
    }

    public static class FallenSoul {

        private WarlordsPlayer shooter;
        private ArmorStand fallenSoulLeft;
        private ArmorStand fallenSoulMiddle;
        private ArmorStand fallenSoulRight;
        private Location locationLeft;
        private Location locationMiddle;
        private Location locationRight;
        private Vector directionLeft;
        private Vector directionMiddle;
        private Vector directionRight;
        private boolean leftRemoved;
        private boolean middleRemoved;
        private boolean rightRemoved;
        private FallenSouls fallenSouls;
        private List<WarlordsPlayer> playersHit;

        public FallenSoul(WarlordsPlayer shooter, ArmorStand fallenSoulLeft, ArmorStand fallenSoulMiddle, ArmorStand fallenSoulRight, Location locationLeft, Location locationMiddle, Location locationRight, Vector directionLeft, Vector directionMiddle, Vector directionRight, FallenSouls fallenSouls) {
            this.shooter = shooter;
            this.fallenSoulLeft = fallenSoulLeft;
            fallenSoulLeft.setHelmet(new ItemStack(Material.ACACIA_FENCE_GATE));
            fallenSoulLeft.setGravity(false);
            fallenSoulLeft.setVisible(false);
            fallenSoulLeft.setHeadPose(new EulerAngle(directionLeft.getY() * -1, 0, 0));
            this.fallenSoulMiddle = fallenSoulMiddle;
            fallenSoulMiddle.setHelmet(new ItemStack(Material.ACACIA_FENCE_GATE));
            fallenSoulMiddle.setGravity(false);
            fallenSoulMiddle.setVisible(false);
            fallenSoulMiddle.setHeadPose(new EulerAngle(directionMiddle.getY() * -1, 0, 0));
            this.fallenSoulRight = fallenSoulRight;
            fallenSoulRight.setHelmet(new ItemStack(Material.ACACIA_FENCE_GATE));
            fallenSoulRight.setGravity(false);
            fallenSoulRight.setVisible(false);
            fallenSoulRight.setHeadPose(new EulerAngle(directionRight.getY() * -1, 0, 0));
            this.locationLeft = locationLeft;
            this.locationMiddle = locationMiddle;
            this.locationRight = locationRight;
            this.directionLeft = directionLeft;
            this.directionMiddle = directionMiddle;
            this.directionRight = directionRight;
            this.fallenSouls = fallenSouls;
            leftRemoved = false;
            middleRemoved = false;
            rightRemoved = false;
            playersHit = new ArrayList<>();
            playersHit.add(shooter);
        }

        public WarlordsPlayer getShooter() {
            return shooter;
        }

        public void setShooter(WarlordsPlayer shooter) {
            this.shooter = shooter;
        }

        public ArmorStand getFallenSoulLeft() {
            return fallenSoulLeft;
        }

        public void setFallenSoulLeft(ArmorStand fallenSoulLeft) {
            this.fallenSoulLeft = fallenSoulLeft;
        }

        public ArmorStand getFallenSoulMiddle() {
            return fallenSoulMiddle;
        }

        public void setFallenSoulMiddle(ArmorStand fallenSoulMiddle) {
            this.fallenSoulMiddle = fallenSoulMiddle;
        }

        public ArmorStand getFallenSoulRight() {
            return fallenSoulRight;
        }

        public void setFallenSoulRight(ArmorStand fallenSoulRight) {
            this.fallenSoulRight = fallenSoulRight;
        }

        public Location getLocationLeft() {
            return locationLeft;
        }

        public void setLocationLeft(Location locationLeft) {
            this.locationLeft = locationLeft;
        }

        public Location getLocationMiddle() {
            return locationMiddle;
        }

        public void setLocationMiddle(Location locationMiddle) {
            this.locationMiddle = locationMiddle;
        }

        public Location getLocationRight() {
            return locationRight;
        }

        public void setLocationRight(Location locationRight) {
            this.locationRight = locationRight;
        }

        public Vector getDirectionLeft() {
            return directionLeft;
        }

        public void setDirectionLeft(Vector directionLeft) {
            this.directionLeft = directionLeft;
        }

        public Vector getDirectionMiddle() {
            return directionMiddle;
        }

        public void setDirectionMiddle(Vector directionMiddle) {
            this.directionMiddle = directionMiddle;
        }

        public Vector getDirectionRight() {
            return directionRight;
        }

        public void setDirectionRight(Vector directionRight) {
            this.directionRight = directionRight;
        }

        public FallenSouls getFallenSouls() {
            return fallenSouls;
        }

        public boolean isLeftRemoved() {
            return leftRemoved;
        }

        public void setLeftRemoved(boolean leftRemoved) {
            this.leftRemoved = leftRemoved;
        }

        public boolean isMiddleRemoved() {
            return middleRemoved;
        }

        public void setMiddleRemoved(boolean middleRemoved) {
            this.middleRemoved = middleRemoved;
        }

        public boolean isRightRemoved() {
            return rightRemoved;
        }

        public void setRightRemoved(boolean rightRemoved) {
            this.rightRemoved = rightRemoved;
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
}
