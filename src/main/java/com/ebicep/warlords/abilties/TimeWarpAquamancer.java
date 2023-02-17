package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeWarpAquamancer extends AbstractAbility {

    public int timesSuccessful = 0;

    private int duration = 5;
    private int warpHealPercentage = 30;

    public TimeWarpAquamancer() {
        super("Time Warp", 0, 0, 28.19f, 30);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Activate to place a time rune on the ground. After §6" + duration +
                " §7seconds, you will warp back to that location and restore §a" + warpHealPercentage + "% §7of your health";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Successful", "" + timesSuccessful));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "mage.timewarp.activation", 3, 1);

        Location warpLocation = wp.getLocation();
        List<Location> warpTrail = new ArrayList<>();
        int startingBlocksTravelled = wp.getBlocksTravelled();
        RegularCooldown<TimeWarp> timeWarpCooldown = new RegularCooldown<>(
                name,
                "TIME",
                TimeWarp.class,
                new TimeWarp(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (wp.isDead() || wp.getGame().getState() instanceof EndState) {
                        return;
                    }

                    timesSuccessful++;
                    Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.teleport", 1, 1);

                    wp.addHealingInstance(
                            wp,
                            name,
                            wp.getMaxHealth() * (warpHealPercentage / 100f),
                            wp.getMaxHealth() * (warpHealPercentage / 100f),
                            0,
                            100,
                            false,
                            false
                    );

                    wp.getEntity().teleport(warpLocation);
                    warpTrail.clear();

                    if (pveUpgrade) {
                        List<ArmorStand> altarsBlocks = new ArrayList<>();
                        LocationBuilder baseLocation = new LocationBuilder(warpLocation)
                                .pitch(0)
                                .yaw(0)
                                .addY(-1.4);
                        List<Location> spawnLocations = getAltarLocations(baseLocation.clone()
                                                                                      .left(.6f * 2)
                                                                                      .forward(.6f));
                        for (Location spawnLocation : spawnLocations) {
                            ArmorStand altar = wp.getWorld().spawn(spawnLocation, ArmorStand.class);
                            altar.setVisible(false);
                            altar.setGravity(false);
                            altar.setMarker(true);
                            altar.setHelmet(new ItemStack(Material.PRISMARINE, 1, (short) 1));
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

                        int blocksTravelled = wp.getBlocksTravelled() - startingBlocksTravelled;
                        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Monsoon Leap Altar",
                                "ALTAR",
                                TimeWarp.class,
                                new TimeWarp(),
                                wp,
                                CooldownTypes.ABILITY,
                                cooldownManager1 -> {
                                },
                                cooldownManager1 -> {
                                    altarsBlocks.forEach(Entity::remove);
                                },
                                (int) Math.min(10, blocksTravelled / 5f) * 20,
                                // 50 blocks = 10s, 40 blocks = 8s, 30 blocks = 6s, 20 blocks = 4s, 10 blocks = 2s
                                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                    if (ticksElapsed % 4 == 0) {
                                        PlayerFilter.entitiesAround(baseLocation, 2.5, 3, 2.5)
                                                    .aliveTeammatesOf(wp)
                                                    .forEach(warlordsEntity -> {
                                                        warlordsEntity.getSpeed().removeSlownessModifiers();
                                                        warlordsEntity.getCooldownManager().removeDebuffCooldowns();
                                                    });
                                    }
                                    if (ticksElapsed % 8 == 0 && ticksLeft >= 40) {
                                        ParticleEffect.DRIP_WATER.display(1, 0, 1, 0.1F, 5, baseLocation.clone().add(0, 4, 0), 500);

                                    }
                                })
                        ));
                    }
                },
                duration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        for (Location location : warpTrail) {
                            ParticleEffect.SPELL_WITCH.display(0.01f, 0, 0.01f, 0.001f, 1, location, 500);
                        }

                        warpTrail.add(wp.getLocation());
                        ParticleEffect.SPELL_WITCH.display(0.1f, 0, 0.1f, 0.001f, 4, warpLocation, 500);

                        int points = 6;
                        double radius = 0.5d;
                        for (int e = 0; e < points; e++) {
                            double angle = 2 * Math.PI * e / points;
                            Location point = warpLocation.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
                            ParticleEffect.CLOUD.display(0.1F, 0, 0.1F, 0.001F, 1, point, 500);
                        }
                    }
                })
        );
        wp.getCooldownManager().addCooldown(timeWarpCooldown);

        if (pveUpgrade) {
            addSecondaryAbility(
                    () -> timeWarpCooldown.setTicksLeft(1),
                    false,
                    secondaryAbility -> !wp.getCooldownManager().hasCooldown(timeWarpCooldown)
            );
        }
        return true;
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
            ArmorStand pillar = baseLocation.getWorld().spawn(pillarLocation, ArmorStand.class);
            pillar.setVisible(false);
            pillar.setGravity(false);
            pillar.setMarker(true);
            pillar.setHelmet(new ItemStack(Material.PRISMARINE, 1, (short) 2));
            pillars.add(pillar);
        }
        ArmorStand light = baseLocation.getWorld().spawn(baseLocation.add(0, .6, 0), ArmorStand.class);
        light.setVisible(false);
        light.setGravity(false);
        light.setMarker(true);
        light.setHelmet(new ItemStack(Material.SEA_LANTERN, 1, (short) 2));
        pillars.add(light);
        return pillars;
    }

    public int getTimesSuccessful() {
        return timesSuccessful;
    }

    public int getWarpHealPercentage() {
        return warpHealPercentage;
    }

    public void setWarpHealPercentage(int warpHealPercentage) {
        this.warpHealPercentage = warpHealPercentage;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
