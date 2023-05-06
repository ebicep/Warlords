package com.ebicep.warlords.abilties;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrbsOfLife extends AbstractAbility implements Duration {

    public static final double SPAWN_RADIUS = 1.15;
    public static float ORB_HEALING = 225;

    public int orbsProduced = 0;

    private final List<Orb> spawnedOrbs = new ArrayList<>();
    private final int floatingOrbRadius = 20;
    private int tickDuration = 280;
    private int orbTickMultiplier = 1;

    public OrbsOfLife() {
        super("Orbs of Life", ORB_HEALING, ORB_HEALING, 19.57f, 20);
    }

    public OrbsOfLife(float minDamage, float maxDamage) {
        super("Orbs of Life", minDamage, maxDamage, 19.57f, 20);
    }


    @Override
    public void updateDescription(Player player) {
        description = Component.text("Spawn ")
                               .append(Component.text("2 ", NamedTextColor.YELLOW))
                               .append(Component.text("initial orbs on cast."))
                               .append(Component.text("\n\nStriking and hitting enemies with abilities causes them to drop an orb of life that lasts "))
                               .append(Component.text("8", NamedTextColor.GOLD))
                               .append(Component.text(" seconds, restoring "))
                               .append(Component.text(format(maxDamageHeal) + " ", NamedTextColor.GREEN))
                               .append(Component.text("health to the ally that picks it up. Other nearby allies recover "))
                               .append(Component.text(format(minDamageHeal) + " ", NamedTextColor.GREEN))
                               .append(Component.text("health. After 1.5 seconds the healing will increase by "))
                               .append(Component.text("40%", NamedTextColor.GREEN))
                               .append(Component.text(" over 6.5 seconds. Lasts "))
                               .append(Component.text(format(tickDuration / 20f) + " ", NamedTextColor.GOLD))
                               .append(Component.text("seconds."))
                               .append(Component.text("\n\nRecast to make the orbs levitate towards you or the nearest ally in a "))
                               .append(Component.text(floatingOrbRadius + " ", NamedTextColor.YELLOW))
                               .append(Component.text("block radius."));

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Orbs Produced", "" + orbsProduced));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "warrior.revenant.orbsoflife", 2, 1);

        OrbsOfLife tempOrbsOfLight = new OrbsOfLife(minDamageHeal, maxDamageHeal);
        PersistentCooldown<OrbsOfLife> orbsOfLifeCooldown = new PersistentCooldown<>(
                name,
                "ORBS",
                OrbsOfLife.class,
                tempOrbsOfLight,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    List<Orb> orbs = new ArrayList<>(tempOrbsOfLight.getSpawnedOrbs());
                    orbs.forEach(Orb::remove);
                },
                false,
                tickDuration,
                orbsOfLife -> orbsOfLife.getSpawnedOrbs().isEmpty()
        ) {
            @Override
            public void onInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                spawnOrbs(wp, event.getWarlordsEntity(), event.getAbility(), this);
                if (event.getAbility().equals("Crippling Strike")) {
                    spawnOrbs(wp, event.getWarlordsEntity(), event.getAbility(), this);
                }
            }

            @Override
            public void onShieldFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                spawnOrbs(wp, event.getWarlordsEntity(), event.getAbility(), this);
                if (event.getAbility().equals("Crippling Strike")) {
                    spawnOrbs(wp, event.getWarlordsEntity(), event.getAbility(), this);
                }
            }

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                spawnOrbs(wp, event.getWarlordsEntity(), event.getAbility(), this);
                if (event.getAbility().equals("Crippling Strike")) {
                    spawnOrbs(wp, event.getWarlordsEntity(), event.getAbility(), this);
                }
            }
        };
        wp.getCooldownManager().addCooldown(orbsOfLifeCooldown);

        spawnOrbs(wp, wp, "Orbs Of Life", orbsOfLifeCooldown);
        spawnOrbs(wp, wp, "Orbs Of Life", orbsOfLifeCooldown);
        if (pveUpgrade) {
            spawnOrbs(wp, wp, "Orbs Of Life", orbsOfLifeCooldown);
        }

        addSecondaryAbility(() -> {
                    if (wp.isAlive()) {
                        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.08f, 0.7f);
                        wp.getWorld().spawnParticle(
                                Particle.ENCHANTMENT_TABLE,
                                wp.getLocation().add(0, 1.5, 0),
                                10,
                                0.8,
                                0,
                                0.8,
                                0.2,
                                null,
                                true
                        );

                        //setting target player to move towards (includes self)
                        if (wp.isInPve()) {
                            tempOrbsOfLight.getSpawnedOrbs().forEach(orb -> orb.setPlayerToMoveTowards(PlayerFilter
                                    .entitiesAround(orb.armorStand.getLocation(), floatingOrbRadius, floatingOrbRadius, floatingOrbRadius)
                                    .aliveTeammatesOf(wp)
                                    .leastAliveFirst()
                                    .findFirstOrNull()
                            ));
                        } else {
                            tempOrbsOfLight.getSpawnedOrbs().forEach(orb -> orb.setPlayerToMoveTowards(PlayerFilter
                                    .entitiesAround(orb.armorStand.getLocation(), floatingOrbRadius, floatingOrbRadius, floatingOrbRadius)
                                    .aliveTeammatesOf(wp)
                                    .closestFirst(orb.getArmorStand().getLocation())
                                    .findFirstOrNull()
                            ));
                        }
                        //moving orb
                        new GameRunnable(wp.getGame()) {
                            @Override
                            public void run() {
                                tempOrbsOfLight.getSpawnedOrbs().stream().filter(orb -> orb.getPlayerToMoveTowards() != null).forEach(targetOrb -> {
                                    WarlordsEntity target = targetOrb.getPlayerToMoveTowards();
                                    ArmorStand orbArmorStand = targetOrb.getArmorStand();
                                    Location orbLocation = orbArmorStand.getLocation();
                                    @NotNull List<Entity> orb = orbArmorStand.getPassengers();
                                    //must eject passenger then reassign it before teleporting bc ???
                                    orbArmorStand.eject();
                                    orbArmorStand.teleport(
                                            new LocationBuilder(orbLocation.clone())
                                                    .add(target.getLocation().toVector().subtract(orbLocation.toVector()).normalize().multiply(1))
                                    );
                                    orb.forEach(orbArmorStand::addPassenger);
                                    orbArmorStand.getWorld().spawnParticle(
                                            Particle.SPELL_WITCH,
                                            orbArmorStand.getLocation().add(0, 1.65, 0),
                                            1,
                                            0,
                                            0,
                                            0,
                                            0,
                                            null,
                                            true
                                    );
                                });

                                if (tempOrbsOfLight.getSpawnedOrbs().stream().noneMatch(orb -> orb.getPlayerToMoveTowards() != null)) {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(0, 1);

                        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                .append(Component.text(" Your current ", NamedTextColor.GRAY))
                                .append(Component.text(name, NamedTextColor.GREEN))
                                .append(Component.text(" will now levitate towards you or a teammate!", NamedTextColor.GRAY))
                        );
                    }
                },
                true,
                secondaryAbility -> wp.isDead() || !wp.getCooldownManager().hasCooldown(orbsOfLifeCooldown) || orbsOfLifeCooldown.isHidden()
        );

        return true;
    }

    public List<Orb> getSpawnedOrbs() {
        return spawnedOrbs;
    }

    public void spawnOrbs(WarlordsEntity owner, WarlordsEntity victim, String ability, PersistentCooldown<OrbsOfLife> cooldown) {
        if (ability.isEmpty() || ability.equals("Intervene")) {
            return;
        }
        if (cooldown.isHidden()) {
            return;
        }
        owner.doOnStaticAbility(OrbsOfLife.class, orbsOfLife -> orbsOfLife.addOrbProduced(1));

        OrbsOfLife orbsOfLife = cooldown.getCooldownObject();
        Location location = victim.getLocation();
        Location spawnLocation = orbsOfLife.generateSpawnLocation(location);

        OrbsOfLife.Orb orb = new OrbsOfLife.Orb(((CraftWorld) location.getWorld()).getHandle(), spawnLocation, cooldown, cooldown.getFrom(), orbTickMultiplier);
        orbsOfLife.getSpawnedOrbs().add(orb);

        orbsOfLife.addOrbProduced(1);
        if (cooldown.getCooldownObject().getOrbsProduced() >= 50) {
            ChallengeAchievements.checkForAchievement(owner, ChallengeAchievements.ORBIFICATION);
        }
    }

    public void addOrbProduced(int amount) {
        this.orbsProduced += amount;
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

    public int getOrbsProduced() {
        return orbsProduced;
    }

    public boolean orbsInsideBlock(Location location) {
        return location.getBlock().getType() != Material.AIR;
    }

    public boolean nearLocation(Location location) {
        for (Orb orb : spawnedOrbs) {
            double distance = orb.getArmorStand().getLocation().distanceSquared(location);
            if (distance < 1) {
                return true;
            }
        }
        return false;
    }

    public void setOrbTickMultiplier(int orbTickMultiplier) {
        this.orbTickMultiplier = orbTickMultiplier;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public static class Orb extends ExperienceOrb {

        private final ArmorStand armorStand;
        private final PersistentCooldown<OrbsOfLife> cooldown;
        private final int tickMultiplier;
        private int ticksLived = 0;
        private WarlordsEntity playerToMoveTowards = null;

        public Orb(ServerLevel world, Location location, PersistentCooldown<OrbsOfLife> cooldown, WarlordsEntity owner, int tickMultiplier) {
            super(world, location.getX(), location.getY() + 2, location.getZ(), 2500, org.bukkit.entity.ExperienceOrb.SpawnReason.CUSTOM, null);
            this.cooldown = cooldown;
            ArmorStand orbStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, 1.5, 0), EntityType.ARMOR_STAND);
            orbStand.setVisible(false);
            orbStand.setGravity(true);
            orbStand.addPassenger(spawn(location).getBukkitEntity());
            ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
            for (WarlordsEntity warlordsEntity : PlayerFilter.playingGame(owner.getGame()).enemiesOf(owner)) {
                if (warlordsEntity.getEntity() instanceof Player player) {
                    PacketUtils.removeEntityForPlayer(player, getId());
                }
            }
            this.armorStand = orbStand;
            this.tickMultiplier = tickMultiplier;
            new GameRunnable(owner.getGame()) {

                @Override
                public void run() {
                    if (!armorStand.isValid()) {
                        this.cancel();
                    } else {
                        ticksLived += tickMultiplier;
                    }
                }
            }.runTaskTimer(30, 0);
        }

        public Orb spawn(Location loc) {
            ServerLevel w = ((CraftWorld) loc.getWorld()).getHandle();
            moveTo(loc.getX(), loc.getY(), loc.getZ());
            w.addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
            return this;
        }

        // Makes it so they cannot be picked up
        @Override
        public void playerTouch(@Nonnull net.minecraft.world.entity.player.Player player) {
            super.playerTouch(player);
        }

        public void remove() {
            armorStand.remove();
            getBukkitEntity().remove();
            cooldown.getCooldownObject().getSpawnedOrbs().remove(this);
        }

        public ArmorStand getArmorStand() {
            return armorStand;
        }

        public PersistentCooldown<OrbsOfLife> getCooldown() {
            return cooldown;
        }

        public WarlordsEntity getOwner() {
            return cooldown.getFrom();
        }

        public int getTicksLived() {
            return ticksLived;
        }

        public int getTicksToLive() {
            return 160 * tickMultiplier;
        }

        public WarlordsEntity getPlayerToMoveTowards() {
            return playerToMoveTowards;
        }

        public void setPlayerToMoveTowards(WarlordsEntity playerToMoveTowards) {
            this.playerToMoveTowards = playerToMoveTowards;
        }
    }
}
