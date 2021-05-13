package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class Projectile extends AbstractAbility {

    private int maxDistance;

    public Projectile(String name, int minDamageHeal, int maxDamageHeal, int cooldown, int energyCost, int critChance, int critMultiplier, String description, int maxDistance) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, description);
        this.maxDistance = maxDistance;
    }

    @Override
    public void onActivate(Player player) {
        Warlords.getPlayer(player).subtractEnergy(energyCost);

        CustomProjectile customProjectile = new CustomProjectile(player, player.getLocation(), player.getLocation(), player.getLocation().getDirection(), maxDistance, this);
        Warlords.getCustomProjectiles().add(customProjectile);

        // SOUNDS
        if (customProjectile.getBall().getName().contains("Fire")) {
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.playSound(player.getLocation(), "mage.fireball.activation", 1, 1);
            }
        } else if (customProjectile.getBall().getName().contains("Frost")) {
            //TODO - Why is this here?
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
            List<Entity> near = player.getNearbyEntities(7.0D, 3.5D, 7.0D);
            for (Entity entity : near) {
                if (entity instanceof Player) {
                    Player nearPlayer = (Player) entity;
                    if (warlordsPlayer.getFrostbolt() == 0) {
                        nearPlayer.setWalkSpeed(WarlordsPlayer.currentSpeed);
                        warlordsPlayer.setFrostbolt(2 * 20 - 10);
                    }
                }
            }
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.playSound(player.getLocation(), "mage.frostbolt.activation", 1, 1);
            }
        } else if (customProjectile.getBall().getName().contains("Water")) {
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.playSound(player.getLocation(), "mage.waterbolt.activation", 1, 1);
            }
        } else if (customProjectile.getBall().getName().contains("Flame")) {
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.playSound(player.getLocation(), "mage.fireball.activation", 1, 1);
            }
        }

    }

    public static class CustomProjectile {

        private Player shooter;
        private Location startingLocation;
        private Location currentLocation;
        private Vector direction;
        private int maxDistance;
        private Projectile projectile;

        public CustomProjectile(Player shooter, Location startingLocation, Location currentLocation, Vector direction, int maxDistance, Projectile projectile) {
            this.shooter = shooter;
            this.startingLocation = startingLocation;
            this.currentLocation = currentLocation;
            this.direction = direction;
            this.maxDistance = maxDistance;
            this.projectile = projectile;
        }

        public Player getShooter() {
            return shooter;
        }

        public void setShooter(Player shooter) {
            this.shooter = shooter;
        }

        public Location getStartingLocation() {
            return startingLocation;
        }

        public void setStartingLocation(Location startingLocation) {
            this.startingLocation = startingLocation;
        }

        public Location getCurrentLocation() {
            return currentLocation;
        }

        public void setCurrentLocation(Location currentLocation) {
            this.currentLocation = currentLocation;
        }

        public Vector getDirection() {
            return direction;
        }

        public void setDirection(Vector direction) {
            this.direction = direction;
        }

        public int getMaxDistance() {
            return maxDistance;
        }

        public void setMaxDistance(int maxDistance) {
            this.maxDistance = maxDistance;
        }

        public Projectile getBall() {
            return projectile;
        }

        public void setBall(Projectile projectile) {
            this.projectile = projectile;
        }
    }
}