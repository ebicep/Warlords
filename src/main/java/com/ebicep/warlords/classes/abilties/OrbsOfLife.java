package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import net.minecraft.server.v1_8_R3.EntityExperienceOrb;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrbsOfLife extends AbstractAbility {

    public static final double SPAWN_RADIUS = 1.75;
    private List<Orb> spawnedOrbs = new ArrayList<>();

    public OrbsOfLife() {
        super("Orbs of Life", 252, 420, 19.57f, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Striking and hitting enemies with\n" +
                "§7abilities causes them to drop an orb of\n" +
                "§7life that lasts §68 §7seconds, restoring\n" +
                "§a" + maxDamageHeal + " §7health to the ally that pick it up.\n" +
                "§7Other nearby allies recover §a" + minDamageHeal + " §7health.\n" +
                "§7Lasts §613.2 §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.getCooldownManager().addCooldown(OrbsOfLife.this.getClass(), new OrbsOfLife(), "ORBS", 13, wp, CooldownTypes.ABILITY);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.revenant.orbsoflife", 2, 1);
        }
    }

    public Location generateSpawnLocation(Location location) {
        Location spawnLocation;
        int counter = 0;
        do {
            counter++;
            //generate random  position in circle
            Random rand = new Random();
            double angle = rand.nextDouble() * 360;
            double x = SPAWN_RADIUS * Math.cos(angle) + (rand.nextDouble() - .5);
            double z = SPAWN_RADIUS * Math.sin(angle) + (rand.nextDouble() - .5);
            spawnLocation = location.clone().add(x, 0, z);
        } while (counter < 50 && (orbsInsideBlock(spawnLocation) || nearLocation(spawnLocation)));
        return spawnLocation;
    }

    public boolean orbsInsideBlock(Location location) {
        if (location.getWorld().getBlockAt(location).getType() != Material.AIR) {
            for (int i = 1; i < 3; i++) {
                if (location.getWorld().getBlockAt(location.clone().add(0, i, 0)).getType() == Material.AIR &&
                        location.getWorld().getBlockAt(location.clone().add(0, i + 1.75, 0)).getType() == Material.AIR
                ) {
                    location.add(0, i, 0);
                    return false;
                }
            }
            return true;
        } else if (location.getWorld().getBlockAt(location.clone().add(0, -3, 0)).getType() == Material.AIR ||
                location.getWorld().getBlockAt(location.clone().add(0, -2, 0)).getType() == Material.AIR ||
                location.getWorld().getBlockAt(location.clone().add(0, -1, 0)).getType() == Material.AIR
        ) {
            for (int i = 3; i > 0; i--) {
                if (location.getWorld().getBlockAt(location.clone().add(0, -i, 0)).getType() == Material.AIR) {
                    location.add(0, -i, 0);
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean nearLocation(Location location) {
        for (Orb orb : spawnedOrbs) {
            double distance = orb.getArmorStand().getLocation().distanceSquared(location);
            if (distance < 1)
                return true;
        }
        return false;
    }

    public List<Orb> getSpawnedOrbs() {
        return spawnedOrbs;
    }

    public static class Orb extends EntityExperienceOrb {

        private ArmorStand armorStand;
        private final WarlordsPlayer owner;

        public Orb(World world, Location location, WarlordsPlayer owner) {
            super(world, location.getX(), location.getY(), location.getZ(), 1000);
            this.owner = owner;
        }

        // Makes it so they cannot be picked up
        @Override
        public void d(EntityHuman entityHuman) {

        }

        public Orb spawn(Location loc) {
            World w = ((CraftWorld) loc.getWorld()).getHandle();
            this.setPosition(loc.getX(), loc.getY(), loc.getZ());
            w.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
            return this;
        }

        public void remove() {
            armorStand.remove();
            getBukkitEntity().remove();
            owner.getCooldownManager().getCooldown(OrbsOfLife.class).forEach(cd -> {
                ((OrbsOfLife) cd.getCooldownObject()).getSpawnedOrbs().remove(this);
            });
        }

        public ArmorStand getArmorStand() {
            return armorStand;
        }

        public void setArmorStand(ArmorStand armorStand) {
            this.armorStand = armorStand;
        }

        public WarlordsPlayer getOwner() {
            return owner;
        }
    }
}
