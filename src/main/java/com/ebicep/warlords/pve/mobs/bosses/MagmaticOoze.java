package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.internal.ProjectileAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.magmacube.AbstractMagmaCube;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class MagmaticOoze extends AbstractMagmaCube implements BossMob {

    private static final Material DAMAGE_BLOCK = Material.MAGMA_BLOCK;
    private final Map<Location, Material> previousBlocks;
    private int splitNumber;

    public MagmaticOoze(Location spawnLocation) {
        this(spawnLocation, 0, new HashMap<>());
    }

    public MagmaticOoze(Location spawnLocation, int splitNumber, Map<Location, Material> previousBlocks) {
        super(10 - (splitNumber * 2),
                spawnLocation,
                "Magmatic Ooze",
                MobTier.ILLUSION,
                null,
                100_000 / (splitNumber + 1),
                1f,
                40,
                100,
                200,
                new FlamingSlam(1000 - (splitNumber * 100), 1500 - (splitNumber * 100)),
                new HeatAura(100 - (splitNumber * 10), 10 - splitNumber),
                new FieryProjectile(600 - (splitNumber * 10), 700 - (splitNumber * 10)),
                new Split(splitNumber, loc -> new MagmaticOoze(loc, splitNumber + 1, previousBlocks)),
                new MoltenFissure(previousBlocks)
        );
        this.splitNumber = splitNumber;
        this.previousBlocks = previousBlocks;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        if (splitNumber == 0) {
            Game game = option.getGame();
            new GameRunnable(game) {

                @Override
                public void run() {
                    PlayerFilter.playingGame(getGame())
                                .aliveEnemiesOf(warlordsNPC)
                                .forEach(warlordsEntity -> {
                                    Material type = LocationUtils.getGroundLocation(warlordsEntity.getLocation()).subtract(0, .5, 0).getBlock().getType();
                                    if (type == DAMAGE_BLOCK) {
                                        warlordsEntity.addDamageInstance(
                                                warlordsEntity,
                                                "Magma",
                                                150,
                                                200,
                                                0,
                                                100
                                        );
                                    }
                                });
                }
            }.runTaskTimer(20, 20);
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        new GameRunnable(option.getGame()) {

            @Override
            public void run() {
                if (option.getMobs().stream().noneMatch(abstractMob -> abstractMob instanceof MagmaticOoze)) {
                    // all dead
                    // restore blocks
                    previousBlocks.forEach((location, material) -> {
                        Block block = location.getBlock();
                        block.setType(material);
                    });
                }
            }
        }.runTaskLater(2);
    }

    private static class FlamingSlam extends AbstractPveAbility {

        private final int hitbox = 6;

        public FlamingSlam(float minDamageHeal, float maxDamageHeal) {
            super("Flaming Slam", minDamageHeal, maxDamageHeal, 12, 50, 15, 200);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            if (!wp.getEntity().isOnGround()) {
                return false;
            }
            // launch entity in air towards enemy player
            // on impact, fiery shockwave + lava?

            Game game = wp.getGame();

            //launch straight into air then down diagonally towards enemy player
            wp.setVelocity(name, new Vector(0, 2, 0), true);
            new GameRunnable(game) {

                boolean launchedTowardsPlayer = false;

                @Override
                public void run() {
                    // check if y velocity starts going down
                    Vector currentVector = wp.getEntity().getVelocity();
                    // TODO maybe tp look towards random ppl then launch towards them
                    if (currentVector.getY() <= 0 && !launchedTowardsPlayer) {
                        // diagonally towards enemy player
                        PlayerFilter.playingGame(game)
                                    .aliveEnemiesOf(wp)
                                    .findAny()
                                    .ifPresent(enemy -> {
                                        Vector vectorTowardsEnemy = new LocationBuilder(wp.getLocation()).getVectorTowards(enemy.getLocation());
                                        wp.setVelocity(name, vectorTowardsEnemy.multiply(2), true);
                                    });
                        launchedTowardsPlayer = true;
                    }
                    if (launchedTowardsPlayer) {
                        // check if hit ground
                        boolean onGround = wp.getEntity().isOnGround();
                        if (onGround) {
                            // shockwave
                            shockwave(wp);
                            cancel();
                        }
                    }
                }

            }.runTaskTimer(20, 2);
            return true;
        }

        private void shockwave(WarlordsEntity wp) {
            // flying blocks
            for (int i = 0; i < 20; i++) {
                Utils.addFallingBlock(wp.getLocation(), new Vector(
                        ThreadLocalRandom.current().nextDouble(.05, .1),
                        ThreadLocalRandom.current().nextDouble(.1, .5),
                        ThreadLocalRandom.current().nextDouble(.05, .1)
                ));
            }
            // damage
            PlayerFilter.entitiesAround(wp, hitbox, hitbox, hitbox)
                        .aliveEnemiesOf(wp)
                        .forEach(enemy -> {
                            enemy.addDamageInstance(
                                    wp,
                                    name,
                                    minDamageHeal,
                                    maxDamageHeal,
                                    critChance,
                                    critMultiplier
                            );
                        });
            // lava?
        }
    }

    private static class HeatAura extends AbstractPveAbility {

        private final int hitbox;

        public HeatAura(float startDamage, int hitbox) {
            super("Heat Aura", startDamage, startDamage, 2, 50, 25, 200);
            this.hitbox = hitbox;
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            // increase heat / damage on every use
            this.multiplyMinMax(1.05f);
            PlayerFilter.entitiesAround(wp, hitbox, hitbox, hitbox)
                        .aliveEnemiesOf(wp)
                        .forEach(enemy -> {
                            enemy.addDamageInstance(
                                    wp,
                                    name,
                                    minDamageHeal,
                                    maxDamageHeal,
                                    critChance,
                                    critMultiplier
                            );
                        });
            return true;
        }

    }

    private static class FieryProjectile extends AbstractPveAbility implements ProjectileAbility {

        private final double speed = 0.160;
        private final double gravity = -0.005;
        private final double hitbox = 7;
        private final double kbVelocity = 1.2;

        public FieryProjectile(float minDamageHeal, float maxDamageHeal) {
            super("Fiery Projectile", minDamageHeal, maxDamageHeal, 5, 50, 10, 200);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            Location location = wp.getLocation();

            Vector speed = wp.getLocation().getDirection().normalize().multiply(this.speed).setY(.01);

            if (wp instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() != null) {
                AbstractMob<?> npcMob = warlordsNPC.getMob();
                LivingEntity target = npcMob.getTarget();
                if (target != null) {
                    double distance = location.distance(target.getBukkitLivingEntity().getLocation());
                    speed.setY(distance * .002);
                }
            }

            Utils.spawnThrowableProjectile(
                    wp.getGame(),
                    Utils.spawnArmorStand(location, armorStand -> {
                        armorStand.getEquipment().setHelmet(new ItemStack(Material.FIRE_CHARGE));
                    }),
                    speed,
                    gravity,
                    this.speed,
                    (newLoc, integer) -> wp.getLocation().getWorld().spawnParticle(
                            Particle.FLAME,
                            newLoc.clone().add(0, -1, 0),
                            6,
                            0.3F,
                            0.3F,
                            0.3F,
                            0.1F,
                            null,
                            true
                    ),
                    newLoc -> PlayerFilter
                            .entitiesAroundRectangle(newLoc, 1, 2, 1)
                            .aliveTeammatesOfExcludingSelf(wp)
                            .findFirstOrNull(),
                    (newLoc, directHit) -> {
                        new GameRunnable(wp.getGame()) {
                            @Override
                            public void run() {
                                for (WarlordsEntity p : PlayerFilter
                                        .entitiesAround(newLoc, hitbox, hitbox, hitbox)
                                        .aliveEnemiesOf(wp)
                                ) {
                                    Vector v;
                                    if (p == directHit) {
                                        v = new LocationBuilder(location).getVectorTowards(p.getLocation()).multiply(kbVelocity).setY(1.5);
                                    } else {
                                        v = new LocationBuilder(p.getLocation()).getVectorTowards(newLoc).multiply(-kbVelocity).setY(1.5);
                                    }
                                    p.setVelocity(name, v, false, false);
                                    p.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                                }

                                newLoc.setPitch(-12);
                                Location impactLocation = newLoc.clone().subtract(speed);
                                Utils.spawnFallingBlocks(impactLocation, 1.4, 20, -.5, ThreadLocalRandom.current().nextDouble(1, 1.2));
                                Utils.spawnFallingBlocks(impactLocation, 1.1, 9, -.4, ThreadLocalRandom.current().nextDouble(.8, 1));
                                Utils.spawnFallingBlocks(impactLocation, .7, 6, -.3, ThreadLocalRandom.current().nextDouble(.6, .8));
                                Utils.spawnFallingBlocks(impactLocation, .4, 3, -.2, ThreadLocalRandom.current().nextDouble(.4, .6));
                            }
                        }.runTaskLater(1);
                    }
            );
            return true;
        }
    }

    private static class Split extends AbstractPveAbility {
        private final int maxSplit = 4;
        private final int split;

        private final Function<Location, AbstractMob<?>> splitSpawnFunction;

        public Split(int split, Function<Location, AbstractMob<?>> splitSpawnFunction) {
            super("Split", 20, 50);
            this.split = split;
            this.splitSpawnFunction = splitSpawnFunction;
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            if (split >= maxSplit) {
                return true;
            }
            // 25% chance to split
            if (ThreadLocalRandom.current().nextInt(4) == 0) {
                pveOption.spawnNewMob(splitSpawnFunction.apply(wp.getLocation()));
            }
            return true;
        }

    }

    private static class MoltenFissure extends AbstractPveAbility {

        private final Map<Location, Material> previousBlocks;

        public MoltenFissure(Map<Location, Material> previousBlocks) {
            super("Molten Fissure", 30, 50);
            this.previousBlocks = previousBlocks;
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            if (!wp.getEntity().isOnGround()) {
                return false;
            }
            Location groundLocation = LocationUtils.getGroundLocation(wp.getLocation());
            EffectUtils.displayParticle(
                    Particle.BLOCK_CRACK,
                    groundLocation,
                    300,
                    5,
                    0,
                    5,
                    0,
                    Material.DIRT.createBlockData()
            );
            Game game = wp.getGame();
            new GameRunnable(game) {
                @Override
                public void run() {
                    // initial ground break, 2x2 up to 4x4
                    int breakSize = ThreadLocalRandom.current().nextInt(2, 5);
                    List<Location> initialBreak = new ArrayList<>();
                    LocationBuilder randomFacingStartLocation = new LocationBuilder(groundLocation.add(0, -.25, 0).toCenterLocation())
                            .pitch(0)
                            .yaw(ThreadLocalRandom.current().nextInt(360));
                    LocationBuilder locationBuilder = randomFacingStartLocation.clone()
                                                                               .backward(breakSize / 2f)
                                                                               .left(breakSize / 2f);
                    // iterate in square starting at bottom left
                    for (int i = 0; i < breakSize * breakSize; i++) {
                        for (int j = 2; j <= 4; j++) {
                            EffectUtils.displayParticle(
                                    Particle.LAVA,
                                    locationBuilder.clone().add(0, j, 0),
                                    2,
                                    .25,
                                    .1,
                                    .25,
                                    0
                            );
                        }
                        initialBreak.add(locationBuilder.clone());
                        locationBuilder.right(1);
                        if (i % breakSize == breakSize - 1) {
                            locationBuilder.left(breakSize);
                            locationBuilder.forward(1);
                        }
                    }
                    for (Location location : initialBreak) {
                        Block block = location.getBlock();
                        previousBlocks.putIfAbsent(location, block.getType());
                        game.getPreviousBlocks().putIfAbsent(location, block.getType());
                        block.setType(DAMAGE_BLOCK); //TODO
                    }

                    // fissures
                    int max = 15;
                    List<List<Location>> fissures = new ArrayList<>();
                    fissures.add(getFissureLocations(randomFacingStartLocation.clone().forward(1), max));
                    fissures.add(getFissureLocations(randomFacingStartLocation.clone().backward(1).lookBackwards(), max));
                    fissures.add(getFissureLocations(randomFacingStartLocation.clone().left(1).lookLeft(), max));
                    fissures.add(getFissureLocations(randomFacingStartLocation.clone().right(1).lookRight(), max));
                    new GameRunnable(game) {
                        int spread = 0;

                        @Override
                        public void run() {
                            for (List<Location> fissure : fissures) {
                                Location location = fissure.get(spread);
                                EffectUtils.displayParticle(
                                        Particle.LAVA,
                                        location.clone().add(0, 1, 0),
                                        3,
                                        .25,
                                        .25,
                                        .25,
                                        0
                                );
                                Block block = location.getBlock();
                                previousBlocks.putIfAbsent(location, block.getType());
                                game.getPreviousBlocks().putIfAbsent(location, block.getType());
                                block.setType(DAMAGE_BLOCK);
                            }
                            spread++;
                            if (spread >= max) {
                                cancel();
                            }
                        }
                    }.runTaskTimer(20, 3);
                }
            }.runTaskLater(30);
            return true;
        }

        public List<Location> getFissureLocations(Location start, int max) {
            // begin at start location then go forward 2 times + random 0-2
            // randomly go left or right 1 block
            // go forward start 1 block forward
            List<Location> locations = new ArrayList<>();
            //TODO normalize yaw, diagonal wont work?
            LocationBuilder location = new LocationBuilder(start)
                    .pitch(0);
            while (locations.size() < max) {
                // check if block is solid, if not then check up to 10 blocks up/down depending if underground or above, if not then return
                validateLocation(location);

                for (int i = 0; i < 2 + ThreadLocalRandom.current().nextInt(3); i++) {
                    location.forward(1);
                    locations.add(location.clone());
                }
                if (ThreadLocalRandom.current().nextBoolean()) {
                    location.left(1);
                } else {
                    location.right(1);
                }
                locations.add(location.clone());
            }
            return locations;
        }

        public static void validateLocation(LocationBuilder location) {
            // check if block is solid, if not then check up to 10 blocks up/down depending if underground or above, if not then return
            boolean isAir = location.getBlock().getType() == Material.AIR;
            if (isAir) {
                if (location.clone().addY(-1).getBlock().getType() != Material.AIR) {
                    location.addY(-1);
                } else {
                    // check down
                    for (int i = 0; i < 10; i++) {
                        location.addY(-1);
                        if (location.getBlock().getType() != Material.AIR) {
                            return;
                        }
                    }
                }
            } else if (location.clone().addY(1).getBlock().getType() != Material.AIR) {
                // check up
                for (int i = 0; i < 10; i++) {
                    location.addY(1);
                    if (location.getBlock().getType() == Material.AIR) {
                        return;
                    }
                }
            }
        }
    }

}
