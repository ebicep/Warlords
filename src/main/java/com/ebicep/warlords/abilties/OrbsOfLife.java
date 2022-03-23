package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.server.v1_8_R3.EntityExperienceOrb;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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

    public static final double SPAWN_RADIUS = 1.15;
    public static float ORB_HEALING = 225;

    private final List<Orb> spawnedOrbs = new ArrayList<>();
    private final int duration = 14;
    private final int floatingOrbRadius = 20;

    public OrbsOfLife() {
        super("Orbs of Life", ORB_HEALING, ORB_HEALING, 19.57f, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Spawn §e2 §7initial orbs on cast." +
                "\n\n" +
                "§7Striking and hitting enemies with abilities\n" +
                "§7causes them to drop an orb of life that lasts §68\n" +
                "§7§7seconds, restoring §a" + format(maxDamageHeal) + " §7health to the ally that\n" +
                "§7picks it up. Other nearby allies recover §a" + format(minDamageHeal) + "\n" +
                "§7health. After 1.5 seconds the healing will increase\n" +
                "§7by §a40% §7over 6.5 seconds. Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "§7You may SNEAK to make the orbs levitate\n" +
                "§7towards you or the nearest ally in\n" +
                "§7a §e" + floatingOrbRadius + " §7block radius.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        OrbsOfLife tempOrbsOfLight = new OrbsOfLife();
        PersistentCooldown<OrbsOfLife> orbsOfLifeCooldown = new PersistentCooldown<OrbsOfLife>(
                name,
                "ORBS",
                OrbsOfLife.class,
                tempOrbsOfLight,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                duration * 20,
                orbsOfLife -> orbsOfLife.getSpawnedOrbs().isEmpty()
        ) {
            @Override
            public void onInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                spawnOrbs(event.getPlayer(), event.getAbility(), this);
                if (event.getAbility().equals("Crippling Strike")) {
                    spawnOrbs(event.getPlayer(), event.getAbility(), this);
                }
            }

            @Override
            public void onShieldFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                spawnOrbs(event.getPlayer(), event.getAbility(), this);
                if (event.getAbility().equals("Crippling Strike")) {
                    spawnOrbs(event.getPlayer(), event.getAbility(), this);
                }
            }

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                spawnOrbs(event.getPlayer(), event.getAbility(), this);
                if (event.getAbility().equals("Crippling Strike")) {
                    spawnOrbs(event.getPlayer(), event.getAbility(), this);
                }
            }
        };
        wp.getCooldownManager().addCooldown(orbsOfLifeCooldown);

        tempOrbsOfLight.getSpawnedOrbs().add(new Orb(((CraftWorld) player.getLocation().getWorld()).getHandle(), generateSpawnLocation(player.getLocation()), wp));
        tempOrbsOfLight.getSpawnedOrbs().add(new Orb(((CraftWorld) player.getLocation().getWorld()).getHandle(), generateSpawnLocation(player.getLocation()), wp));

        Utils.playGlobalSound(player.getLocation(), "warrior.revenant.orbsoflife", 2, 1);

        addSecondaryAbility(() -> {
                    if (wp.isAlive()) {
                        //setting target player to move towards (includes self)
                        tempOrbsOfLight.getSpawnedOrbs().forEach(orb -> orb.setPlayerToMoveTowards(PlayerFilter
                                .entitiesAround(orb.armorStand.getLocation(), floatingOrbRadius, floatingOrbRadius, floatingOrbRadius)
                                .aliveTeammatesOf(wp)
                                .closestFirst(orb.getArmorStand().getLocation())
                                .findFirstOrNull()
                        ));
                        //moving orb
                        new GameRunnable(wp.getGame()) {
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
                                                    .add(target.getLocation().toVector().subtract(orbLocation.toVector()).normalize().multiply(1))
                                                    .get()
                                    );
                                    orbArmorStand.setPassenger(orb);
                                    ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0, 1, orbArmorStand.getLocation().add(0, 1.65, 0), 500);
                                });
                                if (tempOrbsOfLight.getSpawnedOrbs().stream().noneMatch(orb -> orb.getPlayerToMoveTowards() != null)) {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(0, 1);

                        wp.sendMessage(WarlordsPlayer.GIVE_ARROW_GREEN + ChatColor.GRAY + " Your current " + ChatColor.GREEN + name + ChatColor.GRAY + " will now levitate towards you or a teammate!");
                        Utils.playGlobalSound(wp.getLocation(), Sound.LEVEL_UP, 0.08f, 0.7f);
                        ParticleEffect.ENCHANTMENT_TABLE.display(0.8f, 0, 0.8f, 0.2f, 10, wp.getLocation().add(0, 1.5, 0), 500);

                    }
                },
                true,
                secondaryAbility -> wp.isDead() || !wp.getCooldownManager().hasCooldown(orbsOfLifeCooldown) || orbsOfLifeCooldown.isHidden()
        );

        return true;
    }

    public void spawnOrbs(WarlordsPlayer victim, String ability, PersistentCooldown<OrbsOfLife> cooldown) {
        if (ability.isEmpty() || ability.equals("Intervene")) return;
        if (cooldown.isHidden()) return;
        OrbsOfLife orbsOfLife = cooldown.getCooldownObject();
        Location location = victim.getLocation();
        Location spawnLocation = orbsOfLife.generateSpawnLocation(location);

        OrbsOfLife.Orb orb = new OrbsOfLife.Orb(((CraftWorld) location.getWorld()).getHandle(), spawnLocation, cooldown.getFrom());
        orbsOfLife.getSpawnedOrbs().add(orb);
    }

    public Location generateSpawnLocation(Location location) {
        Location spawnLocation;
        int counter = 0;
        Random rand = new Random();
        do {
            counter++;
            //generate random  position in circle
            double angle = rand.nextDouble() * 360;
            double x = SPAWN_RADIUS * Math.cos(angle) + (rand.nextDouble() - .5);
            double z = SPAWN_RADIUS * Math.sin(angle) + (rand.nextDouble() - .5);
            spawnLocation = location.clone().add(x, 0, z);
        } while (counter < 50 && (orbsInsideBlock(spawnLocation) || nearLocation(spawnLocation)));
        return spawnLocation;
    }

    public boolean orbsInsideBlock(Location location) {
        return location.getBlock().getType() != Material.AIR;
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

        private final ArmorStand armorStand;
        private final WarlordsPlayer owner;
        private int ticksLived = 0;
        private WarlordsPlayer playerToMoveTowards = null;

        public Orb(World world, Location location, WarlordsPlayer owner) {
            super(world, location.getX(), location.getY() + 2, location.getZ(), 2500);
            this.owner = owner;
            ArmorStand orbStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, 1.5, 0), EntityType.ARMOR_STAND);
            orbStand.setVisible(false);
            orbStand.setGravity(true);
            orbStand.setPassenger(spawn(location).getBukkitEntity());
            for (WarlordsPlayer player : PlayerFilter.playingGame(owner.getGame()).enemiesOf(owner)) {
                if (player.getEntity() instanceof Player) {
                    ((CraftPlayer) player.getEntity()).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(getId()));
                }
            }
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
            new CooldownFilter<>(owner, PersistentCooldown.class)
                    .filterCooldownClassAndMapToObjectsOfClass(OrbsOfLife.class)
                    .forEachOrdered(orbsOfLife -> orbsOfLife.getSpawnedOrbs().remove(this));
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
