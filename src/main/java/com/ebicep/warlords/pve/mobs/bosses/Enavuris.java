package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.customentities.nms.pve.CustomBat;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractProjectile;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.abilities.PvEAbility;
import com.ebicep.warlords.pve.mobs.abilities.SpawnMobAbility;
import com.ebicep.warlords.pve.mobs.flags.Unsilencable;
import com.ebicep.warlords.pve.mobs.flags.Unstunnable;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import io.papermc.paper.entity.TeleportFlag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class Enavuris extends AbstractMob implements BossMob, Unsilencable, Unstunnable {

    public static final LocationUtils.LocationXYZ[] CAGE_LOCATIONS = {
            new LocationUtils.LocationXYZ(137, 16, 62),
            new LocationUtils.LocationXYZ(114, 16, 45),
            new LocationUtils.LocationXYZ(87, 16, 45),
            new LocationUtils.LocationXYZ(103, 16, 69),
            new LocationUtils.LocationXYZ(91, 16, 81),
            new LocationUtils.LocationXYZ(128, 16, 85),
    };

    private static void attachPrisonWalls(Block block, BlockFace... blockFace) {
        if (block.getBlockData() instanceof MultipleFacing multipleFacing) {
            for (BlockFace face : blockFace) {
                multipleFacing.setFace(face, true);
            }
            block.setBlockData(multipleFacing);
        }
    }

    private final Map<LocationUtils.LocationBlockHolder, Material> cageBlocks = new HashMap<>();
    @Nullable
    private CustomBat leashHolder = null;

    public Enavuris(Location spawnLocation) {
        super(spawnLocation,
                "Enavuris",
                95000,
                0.475f, // TODO
                20,
                745,
                985
        );
    }

    public Enavuris(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new EnderStones(),
                new Imprisonment(),
                new SpawnMobAbility(12, Mob.ENAVURITE) {
                    @Override
                    public int getSpawnAmount() {
                        int playerCount = pveOption.playerCount();
                        if (playerCount <= 3) {
                            return 3;
                        }
                        if (playerCount <= 5) {
                            return 6;
                        }
                        return 8;
                    }
                },
                new SpawnMobAbility(18, Mob.VANISHING_ENAVURITE) {
                    @Override
                    public int getSpawnAmount() {
                        int playerCount = pveOption.playerCount();
                        if (playerCount <= 3) {
                            return 3;
                        }
                        if (playerCount <= 5) {
                            return 6;
                        }
                        return 8;
                    }
                }
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ENAVURIS;
    }

    @Override
    public Component getDescription() {
        return Component.text("jowiudnajkksjbciu", NamedTextColor.DARK_PURPLE);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.LIGHT_PURPLE;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        for (LocationUtils.LocationXYZ cageLocation : CAGE_LOCATIONS) {
            createPrison(cageBlocks, new Location(warlordsNPC.getWorld(), cageLocation.x(), cageLocation.y(), cageLocation.z()));
        }
        Map<LocationUtils.LocationBlockHolder, Material> previousBlocks = option.getGame().getPreviousBlocks();
        cageBlocks.forEach(previousBlocks::putIfAbsent);

        createLeashHolder();

        int playerCount = pveOption.playerCount();
        int spawnAmount = 7;
        if (playerCount <= 3) {
            spawnAmount = 3;
        } else if (playerCount <= 5) {
            spawnAmount = 5;
        }
        for (int i = 0; i < spawnAmount; i++) {
            option.spawnNewMob(Mob.ENAVURITE.createMob(option.getRandomSpawnLocation(warlordsNPC)));
            option.spawnNewMob(Mob.VANISHING_ENAVURITE.createMob(option.getRandomSpawnLocation(warlordsNPC)));
        }

    }

    public static void createPrison(Map<LocationUtils.LocationBlockHolder, Material> previousBlocks, Location location) {
        LocationBuilder locationBuilder = new LocationBuilder(location)
                .pitch(0)
                .yaw(0)
                .left(1)
                .forward(1)
                .addY(-1);
        World world = location.getWorld();
        // floor/roof
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Block floorBlock = world.getBlockAt(locationBuilder);
                Block roofBlock = world.getBlockAt(locationBuilder.clone().add(0, 3, 0));
                previousBlocks.putIfAbsent(new LocationUtils.LocationBlockHolder(floorBlock.getLocation()), floorBlock.getType());
                previousBlocks.putIfAbsent(new LocationUtils.LocationBlockHolder(roofBlock.getLocation()), roofBlock.getType());
                floorBlock.setType(Material.OBSIDIAN);
                roofBlock.setType(Material.OBSIDIAN);
                locationBuilder.right(1);
            }
            locationBuilder.backward(1);
            locationBuilder.left(3);
        }
        locationBuilder.forward(1);

        // walls - start bottom left
        locationBuilder.addY(1);
        createPrisonWall(previousBlocks, locationBuilder).forEach(block -> attachPrisonWalls(block, BlockFace.SOUTH, BlockFace.WEST));

        locationBuilder.forward(1);
        createPrisonWall(previousBlocks, locationBuilder).forEach(block -> attachPrisonWalls(block, BlockFace.SOUTH, BlockFace.NORTH));

        locationBuilder.forward(1);
        createPrisonWall(previousBlocks, locationBuilder).forEach(block -> attachPrisonWalls(block, BlockFace.WEST, BlockFace.NORTH));

        locationBuilder.right(1);
        createPrisonWall(previousBlocks, locationBuilder).forEach(block -> attachPrisonWalls(block, BlockFace.WEST, BlockFace.EAST));

        locationBuilder.right(1);
        createPrisonWall(previousBlocks, locationBuilder).forEach(block -> attachPrisonWalls(block, BlockFace.NORTH, BlockFace.EAST));

        locationBuilder.backward(1);
        createPrisonWall(previousBlocks, locationBuilder).forEach(block -> attachPrisonWalls(block, BlockFace.NORTH, BlockFace.SOUTH));

        locationBuilder.backward(1);
        createPrisonWall(previousBlocks, locationBuilder).forEach(block -> attachPrisonWalls(block, BlockFace.EAST, BlockFace.SOUTH));

        locationBuilder.left(1);
        createPrisonWall(previousBlocks, locationBuilder).forEach(block -> attachPrisonWalls(block, BlockFace.EAST, BlockFace.WEST));
    }

    private void createLeashHolder() {
        leashHolder = new CustomBat(warlordsNPC.getLocation().add(0, 0, 0));
        leashHolder.setResting(true);
        ((CraftWorld) warlordsNPC.getWorld()).getHandle().addFreshEntity(leashHolder, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    private static List<Block> createPrisonWall(Map<LocationUtils.LocationBlockHolder, Material> previousBlocks, Location location) {
        List<Block> blocks = new ArrayList<>();
        World world = location.getWorld();
        location = location.clone();
        for (int i = 0; i < 2; i++) {
            Block block = world.getBlockAt(location);
            previousBlocks.putIfAbsent(new LocationUtils.LocationBlockHolder(block.getLocation()), block.getType());
            block.setType(Material.IRON_BARS);
            blocks.add(block);
            location.add(0, 1, 0);
        }
        return blocks;
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (leashHolder == null || !leashHolder.valid) {
            createLeashHolder();
        }

        Location location = warlordsNPC.getLocation().add(0, 1, 0);
        leashHolder.teleportTo(location.getX(), location.getY(), location.getZ());
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
    }

    @Override
    public void setTarget(WarlordsEntity target) {
        super.setTarget(target);
        onTargetSwap(target.getEntity());
    }

    @Override
    public void setTarget(LivingEntity target) {
        super.setTarget(target);
        onTargetSwap(target);
    }

    @Override
    public void cleanup(PveOption pveOption) {
        super.cleanup(pveOption);
        if (leashHolder != null) {
            leashHolder.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
        }
        cageBlocks.forEach((locationBlockHolder, material) -> locationBlockHolder.getBlock().setType(material));
    }

    private void onTargetSwap(Entity target) {
        if (!Objects.equals(getTarget(), target) && target instanceof Player player) {
            player.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_AMBIENT, 2, .6f);
        }
    }

    public @Nullable CustomBat getLeashHolder() {
        return leashHolder;
    }

    public static class EnderStones extends AbstractProjectile implements PvEAbility {

        private final int radius = 3;
        private PveOption pveOption;

        public EnderStones() {
            super(
                    "Ender Stones",
                    500,
                    600,
                    10,
                    50,
                    20,
                    180,
                    2,
                    50,
                    false
            );
        }

        @Override
        protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {

        }

        @Override
        protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
            WarlordsEntity shooter = projectile.getShooter();
            Location startingLocation = projectile.getStartingLocation();
            Location currentLocation = projectile.getCurrentLocation();

//            Utils.playGlobalSound(currentLocation, "mage.fireball.impact", 2, 1); TODO

            int playersHit = 0;

            for (WarlordsEntity nearEntity : PlayerFilter
                    .entitiesAround(currentLocation, radius, radius, radius)
                    .aliveEnemiesOf(shooter)
                    .excluding(projectile.getHit())
            ) {
                getProjectiles(projectile).forEach(p -> p.getHit().add(nearEntity));
                playersHit++;

                nearEntity.addInstance(InstanceBuilder
                        .damage()
                        .ability(this)
                        .source(shooter)
                        .value(damageValues.enderStonesDamage)
                );

                if (Objects.equals(hit, nearEntity)) {
                    new CooldownFilter<>(nearEntity, RegularCooldown.class)
                            .filterCooldownName(name + " Silence")
                            .findAny()
                            .ifPresentOrElse(
                                    cd -> cd.setTicksLeft(cd.getTicksLeft() + 30),
                                    () -> nearEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                                            name + " Silence",
                                            "SILENCE",
                                            EnderStones.class,
                                            null,
                                            shooter,
                                            CooldownTypes.DEBUFF,
                                            cooldownManager -> {
                                            },
                                            30
                                    ) {
                                        @Override
                                        protected Listener getListener() {
                                            return new Listener() {
                                                @EventHandler
                                                public void onAbilityActivate(WarlordsAbilityActivateEvent.Pre event) {
                                                    if (!Objects.equals(event.getWarlordsEntity(), nearEntity) || event.getSlot() != 0) {
                                                        return;
                                                    }
                                                    event.setCancelled(true);
                                                    Player player = event.getPlayer();
                                                    player.sendMessage(Component.text("You have been silenced!", NamedTextColor.RED));
                                                    player.playSound(player.getLocation(), "notreadyalert", 1, 1);
                                                }
                                            };
                                        }
                                    })
                            );
                }
                nearEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                        name + " Cripple",
                        "CRIP",
                        EnderStones.class,
                        null,
                        shooter,
                        CooldownTypes.DEBUFF,
                        cooldownManager -> {
                        },
                        3 * 20
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * .75f;
                    }
                });
            }


            return playersHit;
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity shooter) {
            new GameRunnable(shooter.getGame()) {
                int fired = 0;

                @Override
                public void run() {
                    fire(shooter, shooter.getEyeLocation());
                    if (++fired >= 3) {
                        cancel();
                    }
                }
            }.runTaskTimer(0, 10);
            return true;
        }

        @Override
        protected void onSpawn(@Nonnull InternalProjectile projectile) {
            super.onSpawn(projectile);
            Location spawn = projectile.getCurrentLocation().clone().add(0, -.5, 0);
            DragonFireball dragonFireball = projectile.getWorld().spawn(spawn, DragonFireball.class);
            ArmorStand armorStand = Utils.spawnArmorStand(spawn.clone(), stand -> {
                stand.setMarker(true);
                stand.addPassenger(dragonFireball);
            });
            projectile.addTask(new InternalProjectileTask() {
                @Override
                public void run(InternalProjectile projectile) {
                    armorStand.teleport(projectile.getCurrentLocation().clone().add(0, -.5, 0),
                            PlayerTeleportEvent.TeleportCause.PLUGIN,
                            TeleportFlag.EntityState.RETAIN_PASSENGERS
                    );
                }

                @Override
                public void onDestroy(InternalProjectile projectile) {
                    armorStand.teleport(projectile.getCurrentLocation().clone().add(0, -.5, 0),
                            PlayerTeleportEvent.TeleportCause.PLUGIN,
                            TeleportFlag.EntityState.RETAIN_PASSENGERS
                    );
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            armorStand.remove();
                            dragonFireball.remove();
                        }
                    }.runTaskLater(Warlords.getInstance(), 3); // without delay, appears to remove before hitting player/ground

                }
            });
        }

        @Nullable
        @Override
        protected String getActivationSound() {
            return Sound.ENTITY_ENDER_DRAGON_SHOOT.getKey().getKey();
        }

        @Override
        protected float getSoundVolume() {
            return 1;
        }

        @Override
        protected float getSoundPitch() {
            return 1;
        }

        @org.jetbrains.annotations.Nullable
        @Override
        public PveOption getPveOption() {
            return pveOption;
        }

        @Override
        public void setPveOption(PveOption pveOption) {
            this.pveOption = pveOption;
        }

        private final DamageValues damageValues = new DamageValues();

        public DamageValues getDamageValues() {
            return damageValues;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.RangedValue enderStonesDamage = new Value.RangedValue(500, 600);
            private final List<Value> values = List.of(enderStonesDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }

    public static class Imprisonment extends AbstractPveAbility {

        private static final int IMPRISONMENT_TICKS = 10 * 20;
        private final List<AbstractMob> cursedPsions = new ArrayList<>();
        private WarlordsEntity caster;
        @Nullable
        private WarlordsEntity imprisonedPlayer;
        private int imprisonTicks = 0;
        private List<BlockDisplay> blockDisplays = new ArrayList<>();

        public Imprisonment() {
            super("Imprisonment", 20, 50, true);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            caster = wp;
            imprisonTicks = 0;
            imprisonedPlayer = PlayerFilter.playingGame(wp.getGame())
                                           .aliveEnemiesOf(wp)
                                           .sorted(Comparator.comparing(w -> -w.getMaxEnergy()))
                                           .findFirstOrNull();
            if (imprisonedPlayer == null) {
                return false;
            }
            LocationUtils.LocationXYZ randomCageLocation = CAGE_LOCATIONS[ThreadLocalRandom.current().nextInt(CAGE_LOCATIONS.length)];
            Location cageLocation = new Location(wp.getWorld(), randomCageLocation.x() + .5, randomCageLocation.y() + .1, randomCageLocation.z() + .5);
            Location floorLocation = LocationUtils.getGroundLocation(cageLocation.clone().add(0, -2, 0));
            int playerCount = pveOption.playerCount();
            int spawnAmount = 3;
            if (playerCount <= 3) {
                spawnAmount = 1;
            } else if (playerCount <= 5) {
                spawnAmount = 2;
            }
            for (int i = 0; i < spawnAmount; i++) {
                AbstractMob psion = Mob.CURSED_PSION.createMob(floorLocation);
                pveOption.spawnNewMob(psion);
                cursedPsions.add(psion);
            }
            imprisonedPlayer.teleport(cageLocation);
            imprisonedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 10 * 20, 1, false, false, false));

            BlockDisplay blockDisplay = wp.getWorld().spawn(new LocationBuilder(cageLocation).left(3).addY(2), BlockDisplay.class, display -> {
                display.setBlock(Material.END_PORTAL.createBlockData());
            });

            blockDisplays.add(blockDisplay);

            return true;
        }

        @Override
        public void runEveryTick(@org.jetbrains.annotations.Nullable WarlordsEntity warlordsEntity) {
            super.runEveryTick(warlordsEntity);
            if (imprisonedPlayer == null) {
                return;
            }
            if (cursedPsions.stream().allMatch(mob -> mob.getWarlordsNPC().isDead())) {
                Utils.playGlobalSound(imprisonedPlayer.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 2, .5f);
                reset(false);
                return;
            }
            if (imprisonTicks >= IMPRISONMENT_TICKS) {
                Utils.playGlobalSound(imprisonedPlayer.getLocation(), Sound.BLOCK_ANVIL_PLACE, 2, .1f);
                imprisonedPlayer.die(warlordsEntity);
                reset(true);
            } else if (imprisonTicks > IMPRISONMENT_TICKS - 40 && imprisonTicks % 3 == 0) {
                Utils.playGlobalSound(imprisonedPlayer.getLocation(), Instrument.PIANO, new Note(24));
            } else if (imprisonTicks % 20 == 0) {
                Utils.playGlobalSound(imprisonedPlayer.getLocation(), Instrument.PIANO, new Note(imprisonTicks / 10 + 4));
            }
            imprisonTicks++;
        }

        private void reset(boolean imprisonedDead) {
            imprisonedPlayer = null;
            cursedPsions.clear();
            imprisonTicks = 0;

            PlayerFilter.playingGame(caster.getGame())
                        .aliveEnemiesOf(caster)
                        .sorted(Comparator.comparing(w -> ThreadLocalRandom.current().nextInt()))
                        .limit(imprisonedDead ? 2 : 1)
                        .forEach(swappedTarget -> giveVows(caster, swappedTarget));
        }

        private void giveVows(@Nonnull WarlordsEntity wp, WarlordsEntity target) {
            if (target == null) {
                return;
            }
            Location targetLocation = target.getEntity().getLocation();
            target.teleport(wp.getEntity().getLocation());
            wp.teleport(targetLocation);

            AtomicReference<Debuff> currentDebuff = new AtomicReference<>(null);
            target.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "Vows of the End",
                    "VOWS",
                    Imprisonment.class,
                    null,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {},
                    15 * 20,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 100 == 0) {
                            Debuff randomDebuff = Debuff.getRandomDebuff();
                            currentDebuff.set(randomDebuff);
                            if (randomDebuff == null) {
                                return;
                            }
                            cooldown.setName("Vows of the End - " + randomDebuff.name());
                            switch (randomDebuff) {
                                case SLOW -> target.addSpeedModifier(wp, name, -25, 5 * 20);
                                case DARKNESS -> target.addPotionEffect(new PotionEffect(
                                        PotionEffectType.DARKNESS,
                                        5 * 20,
                                        1, false,
                                        false,
                                        false
                                ));
                            }
                        }
                    })
            ) {
                @Override
                public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                    if (currentDebuff.get() == Debuff.WOUND) {
                        return currentHealValue * .75f;
                    }
                    return currentHealValue;
                }

                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    if (currentDebuff.get() == Debuff.CRIPPLE) {
                        return currentDamageValue * .75f;
                    }
                    return currentDamageValue;
                }

                @Override
                public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                    if (currentDebuff.get() != Debuff.LEECH) {
                        return;
                    }
                    float healingMultiplier = .15f;
                    float healValue = currentDamageValue * healingMultiplier;
                    event.getSource().addInstance(InstanceBuilder
                            .healing()
                            .cause("Leech")
                            .source(wp)
                            .value(healValue)
                    );
                }

                @Override
                protected Listener getListener() {
                    return new Listener() {
                        @EventHandler
                        public void onAbilityActivate(WarlordsAbilityActivateEvent.Pre event) {
                            if (!Objects.equals(event.getWarlordsEntity(), target) || event.getSlot() != 0 || currentDebuff.get() != Debuff.SILENCE) {
                                return;
                            }
                            event.setCancelled(true);
                            Player player = event.getPlayer();
                            player.sendMessage(Component.text("You have been silenced!", NamedTextColor.RED));
                            player.playSound(player.getLocation(), "notreadyalert", 1, 1);
                        }
                    };
                }
            });
        }

        private enum Debuff {
            SLOW, WOUND, CRIPPLE, DARKNESS, SILENCE, LEECH,
            ;
            public static final Debuff[] VALUES = values();

            public static Debuff getRandomDebuff() {
                return VALUES[ThreadLocalRandom.current().nextInt(VALUES.length)];
            }
        }
    }


}
