package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractTimeWarp;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.aquamancer.TimeWarpBranchAquamancer;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeWarpAquamancer extends AbstractTimeWarp {

    public TimeWarpAquamancer() {
        super();
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.activation", 3, 1);

        Location warpLocation = wp.getLocation();
        List<Location> warpTrail = new ArrayList<>();
        List<ArmorStand> altarsBlocks = new ArrayList<>();
        LocationBuilder baseLocation;
        if (pveMasterUpgrade) {
            baseLocation = new LocationBuilder(warpLocation)
                    .pitch(0)
                    .yaw(0)
                    .addY(-1.4);
            List<Location> spawnLocations = getAltarLocations(baseLocation.clone()
                                                                          .left(.6f * 2)
                                                                          .forward(.6f));
            for (Location spawnLocation : spawnLocations) {
                ArmorStand altar = Utils.spawnArmorStand(spawnLocation, armorStand -> {
                    armorStand.setMarker(true);
                    armorStand.getEquipment().setHelmet(new ItemStack(Material.PRISMARINE_BRICKS));
                });
                altarsBlocks.add(altar);
            }
            altarsBlocks.addAll(getAltarPillar(new LocationBuilder(baseLocation)
                    .addY(-.8)
                    .left(2)
                    .forward(2))
            );
            altarsBlocks.addAll(getAltarPillar(new LocationBuilder(baseLocation)
                    .addY(-.8)
                    .right(2)
                    .forward(2))
            );
            altarsBlocks.addAll(getAltarPillar(new LocationBuilder(baseLocation)
                    .addY(-.8)
                    .left(2)
                    .backward(2))
            );
            altarsBlocks.addAll(getAltarPillar(new LocationBuilder(baseLocation)
                    .addY(-.8)
                    .right(2)
                    .backward(2))
            );
        } else {
            baseLocation = null;
        }
        RegularCooldown<TimeWarpAquamancer> timeWarpCooldown = new RegularCooldown<>(
                name,
                "TIME",
                TimeWarpAquamancer.class,
                new TimeWarpAquamancer(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (wp.isDead() || wp.getGame().getState() instanceof EndState) {
                        return;
                    }

                    timesSuccessful++;
                    Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.teleport", 1, 1);

                    wp.addInstance(InstanceBuilder
                            .healing()
                            .ability(this)
                            .source(wp)
                            .value(wp.getMaxHealth() * (warpHealPercentage / 100f))
                    );

                    wp.getEntity().teleport(warpLocation);
                    warpTrail.clear();

                    if (pveMasterUpgrade2) {
                        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Cyclone",
                                "CYC",
                                TimeWarpAquamancer.class,
                                new TimeWarpAquamancer(),
                                wp,
                                CooldownTypes.ABILITY,
                                cooldownManager1 -> {
                                },
                                5 * 20
                        ) {
                            @Override
                            public float modifyHealingFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
                                return currentHealValue * 1.15f;
                            }
                        });
                    }
                },
                cooldownManager -> {
                    altarsBlocks.forEach(Entity::remove);
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (pveMasterUpgrade2) {
                        if (ticksElapsed % 2 == 0) {
                            PlayerFilter.entitiesAround(wp, 3, 3, 3)
                                        .aliveEnemiesOf(wp)
                                        .forEach(enemy -> {
                                            Utils.addKnockback(name, wp.getLocation(), enemy, -1.2, 0.2);
                                        });
                        }
                        if (ticksElapsed % 8 == 0) {
                            EffectUtils.playSpiralAnimation(
                                    true,
                                    wp,
                                    new LocationBuilder(wp.getLocation()).pitch(0),
                                    4,
                                    16,
                                    (matrix4d, integer) -> {},
                                    List.of(
                                            new Pair<>(Particle.BLOCK_DUST, Material.MOSSY_COBBLESTONE.createBlockData()),
                                            new Pair<>(Particle.BLOCK_DUST, Material.BLUE_CONCRETE.createBlockData())
                                    ),
                                    Particle.DRIP_WATER, Particle.ENCHANTMENT_TABLE
                            );
                        }
                        if (ticksElapsed % 20 == 0) {
                            Utils.playGlobalSound(wp.getLocation(), "mage.waterbreath.activation", 2, .5f);
                        }
                    }
                    if (ticksElapsed % 4 == 0) {
                        for (Location location : warpTrail) {
                            location.getWorld().spawnParticle(
                                    Particle.SPELL_WITCH,
                                    location,
                                    1,
                                    0.01,
                                    0,
                                    0.01,
                                    0.001,
                                    null,
                                    true
                            );
                        }

                        warpTrail.add(wp.getLocation());
                        warpLocation.getWorld().spawnParticle(
                                Particle.SPELL_WITCH,
                                warpLocation,
                                4,
                                0.1,
                                0,
                                0.1,
                                0.001,
                                null,
                                true
                        );

                        int points = 6;
                        double radius = 0.5d;
                        for (int e = 0; e < points; e++) {
                            double angle = 2 * Math.PI * e / points;
                            Location point = warpLocation.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
                            point.getWorld().spawnParticle(
                                    Particle.CLOUD,
                                    point,
                                    1,
                                    0.1,
                                    0,
                                    0.1,
                                    0.001,
                                    null,
                                    true
                            );

                        }
                    }
                    if (pveMasterUpgrade && baseLocation != null) {
                        if (ticksElapsed % 4 == 0) {
                            PlayerFilter.entitiesAround(baseLocation, 15, 14, 15)
                                        .aliveTeammatesOf(wp)
                                        .forEach(warlordsEntity -> {
                                            warlordsEntity.getSpeed().removeSlownessModifiers();
                                            warlordsEntity.getCooldownManager().removeDebuffCooldowns();
                                        });
                        }
                        if (ticksElapsed % 8 == 0 && ticksLeft >= 40) {
                            baseLocation.getWorld().spawnParticle(
                                    Particle.DRIP_WATER,
                                    baseLocation.clone().add(0, 4, 0),
                                    5,
                                    1,
                                    0,
                                    1,
                                    0.1,
                                    null,
                                    true
                            );
                        }
                    }
                })
        );
        wp.getCooldownManager().addCooldown(timeWarpCooldown);

        if (pveMasterUpgrade) {
            addSecondaryAbility(
                    1,
                    () -> timeWarpCooldown.setTicksLeft(1),
                    false,
                    secondaryAbility -> !wp.getCooldownManager().hasCooldown(timeWarpCooldown)
            );
        }
        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new TimeWarpBranchAquamancer(abilityTree, this);
    }

    private List<Location> getAltarLocations(Location topLeft) {
        topLeft = topLeft.clone();
        List<Location> spawnLocations = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                spawnLocations.add(new LocationBuilder(topLeft).right(j * .6f));
            }
            topLeft = new LocationBuilder(topLeft).backward(.6f);
        }
        return spawnLocations;
    }

    private List<ArmorStand> getAltarPillar(Location baseLocation) {
        baseLocation = baseLocation.clone();
        List<ArmorStand> pillars = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Location pillarLocation = baseLocation.add(0, .6, 0);
            ArmorStand pillar = Utils.spawnArmorStand(pillarLocation, armorStand -> {
                armorStand.setMarker(true);
                armorStand.getEquipment().setHelmet(new ItemStack(Material.DARK_PRISMARINE));
            });
            pillars.add(pillar);
        }
        ArmorStand light = Utils.spawnArmorStand(baseLocation.add(0, .6, 0), armorStand -> {
            armorStand.setMarker(true);
            armorStand.getEquipment().setHelmet(new ItemStack(Material.SEA_LANTERN));
        });
        pillars.add(light);
        return pillars;
    }

}
