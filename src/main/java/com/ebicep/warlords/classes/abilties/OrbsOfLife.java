package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.LocationBuilder;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import net.minecraft.server.v1_8_R3.EntityExperienceOrb;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrbsOfLife extends AbstractAbility {

    public static final double SPAWN_RADIUS = 1.75;
    private List<Orb> spawnedOrbs = new ArrayList<>();

    private final int duration = 14;
    private final int floatingOrbRadius = 20;

    public OrbsOfLife() {
        super("Orbs of Life", 240, 400, 21.57f, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Spawn §e2 §7initial orbs on cast." +
                "\n\n" +
                "§7Striking and hitting enemies with abilities\n" +
                "§7causes them to drop an orb of life that lasts §68\n" +
                "§7§7seconds, restoring §a" + maxDamageHeal + " §7health to the ally that\n" +
                "§7picks it up. Other nearby allies recover §a" + minDamageHeal + "\n" +
                "§7health. Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "§7You may SNEAK once per Orbs of Life cast to make\n" +
                "§7the orbs levitate towards the nearest ally in a §e" + floatingOrbRadius + "\n" +
                "§7block radius, healing them for §a" + maxDamageHeal + " §7health.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        OrbsOfLife tempOrbsOfLight = new OrbsOfLife();
        wp.getCooldownManager().addCooldown(name, OrbsOfLife.this.getClass(), tempOrbsOfLight, "ORBS", duration, wp, CooldownTypes.ABILITY);

        tempOrbsOfLight.getSpawnedOrbs().add(new Orb(((CraftWorld) player.getLocation().getWorld()).getHandle(), generateSpawnLocation(player.getLocation()), wp));
        tempOrbsOfLight.getSpawnedOrbs().add(new Orb(((CraftWorld) player.getLocation().getWorld()).getHandle(), generateSpawnLocation(player.getLocation()), wp));

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.revenant.orbsoflife", 2, 1);
        }

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;
                if (wp.isAlive() && player.isSneaking()) {
                    //setting target player to move towards (includes self)
                    tempOrbsOfLight.getSpawnedOrbs().forEach(orb -> orb.setPlayerToMoveTowards(PlayerFilter
                            .entitiesAround(orb.armorStand.getLocation(), floatingOrbRadius, floatingOrbRadius, floatingOrbRadius)
                            .aliveTeammatesOf(wp)
                            .closestFirst(orb.getArmorStand().getLocation())
                            .findFirstOrNull()
                    ));
                    //moving orb
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            tempOrbsOfLight.getSpawnedOrbs().stream().filter(orb -> orb.getPlayerToMoveTowards() != null).forEach(targetOrb -> {
                                WarlordsPlayer target = targetOrb.getPlayerToMoveTowards();
                                ArmorStand orbArmorStand = targetOrb.getArmorStand();
                                Location orbLocation = orbArmorStand.getLocation();
                                Entity orb = orbArmorStand.getPassenger();
                                //must eject passenger then reassign it before teleporting bc ???
                                orbArmorStand.eject();
                                orbArmorStand.teleport(
                                        new LocationBuilder(orbLocation.clone())
                                                .add(target.getLocation().toVector().subtract(orbLocation.toVector()).normalize().multiply(.95))
                                                .get()
                                );
                                orbArmorStand.setPassenger(orb);
                                ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0, 1, orbArmorStand.getLocation().add(0, 1.65, 0), 500);
                            });
                            if (tempOrbsOfLight.getSpawnedOrbs().stream().noneMatch(orb -> orb.getPlayerToMoveTowards() != null)) {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(Warlords.getInstance(), 0, 1);

                    player.sendMessage(ChatColor.GREEN + "Your current orbs will now levitate towards you or a teammate!");
                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(player.getLocation(), Sound.LEVEL_UP, 2, 0.7f);
                    }
                    Location particleLoc = player.getLocation();
                    particleLoc.add(0, 1.5, 0);
                    ParticleEffect.ENCHANTMENT_TABLE.display(0.8f, 0, 0.8f, 0.2f, 10, particleLoc, 500);

                    this.cancel();
                }
                if (counter >= 20 * duration || wp.isDeath()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 0);
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
        private int ticksLived = 0;
        private WarlordsPlayer playerToMoveTowards = null;

        public Orb(World world, Location location, WarlordsPlayer owner) {
            super(world, location.getX(), location.getY(), location.getZ(), 2500);
            this.owner = owner;
            ArmorStand orbStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
            orbStand.setVisible(false);
            orbStand.setGravity(false);
            orbStand.setPassenger(spawn(location).getBukkitEntity());
            this.armorStand = orbStand;
            new BukkitRunnable() {

                @Override
                public void run() {
                    if (!armorStand.isValid()) {
                        this.cancel();
                    } else {
                        ticksLived++;
                    }
                }
            }.runTaskTimer(Warlords.getInstance(), 30, 0);
        }

        @Override
        public String toString() {
            return "Orb{" +
                    "owner=" + owner +
                    ", ticksLived=" + ticksLived +
                    ", playerToMoveTowards=" + playerToMoveTowards +
                    '}';
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

        public WarlordsPlayer getOwner() {
            return owner;
        }

        public int getTicksLived() {
            return ticksLived;
        }

        public WarlordsPlayer getPlayerToMoveTowards() {
            return playerToMoveTowards;
        }

        public void setPlayerToMoveTowards(WarlordsPlayer playerToMoveTowards) {
            this.playerToMoveTowards = playerToMoveTowards;
        }
    }
}
