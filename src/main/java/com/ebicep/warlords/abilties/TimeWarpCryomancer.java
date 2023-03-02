package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TimeWarpCryomancer extends AbstractAbility {

    public int timesSuccessful = 0;

    private int duration = 5;
    private int warpHealPercentage = 30;

    public TimeWarpCryomancer() {
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


        AtomicReference<ArmorStand> cryoPod = new AtomicReference<>(null);
        if (pveUpgrade) {
            cryoPod.set(wp.getWorld().spawn(warpLocation, ArmorStand.class));
            cryoPod.get().setVisible(false);
            cryoPod.get().setGravity(false);

            PlayerFilter.entitiesAround(warpLocation, 15, 15, 15)
                        .aliveEnemiesOf(wp)
                        .forEach(warlordsEntity -> {
                            if (warlordsEntity instanceof WarlordsNPC) {
                                ((WarlordsNPC) warlordsEntity).getMob().setTarget(cryoPod.get());
                            }
                        });
        }

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

                    if (pveUpgrade && cryoPod.get() != null) {
                        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Frostbite Leap",
                                "WARP RES",
                                TimeWarpCryomancer.class,
                                null,
                                wp,
                                CooldownTypes.ABILITY,
                                cooldownManager2 -> {
                                },
                                cooldownManager2 -> {
                                },
                                5 * 20,
                                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                })
                        ) {
                            @Override
                            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                return currentDamageValue * .2f;
                            }
                        });
                        PlayerFilter.entitiesAround(warpLocation, 5, 5, 5)
                                    .aliveEnemiesOf(wp)
                                    .forEach(warlordsEntity -> {
                                        if (warlordsEntity instanceof WarlordsNPC) {
                                            warlordsEntity.addSpeedModifier(wp, "Frostbite Leap", -80, 60);
                                        }
                                    });
                    }
                },
                cooldownManager -> {
                    if (pveUpgrade && cryoPod.get() != null) {
                        cryoPod.get().remove();
                    }
                },
                duration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
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

                        if (pveUpgrade && cryoPod.get() != null) {
                            EffectUtils.playCylinderAnimation(warpLocation, .7, Particle.CLOUD, 1);
                            points = 24;
                            radius = .85;
                            for (int e = 0; e < points; e++) {
                                double angle = 2 * Math.PI * e / points;
                                Location point = warpLocation.clone().add(radius * Math.sin(angle), 2.1, radius * Math.cos(angle));
                                point.getWorld().spawnParticle(
                                        Particle.REDSTONE,
                                        point,
                                        1,
                                        0,
                                        0,
                                        0,
                                        0,
                                        new Particle.DustOptions(Color.fromRGB(0, 100, 100), 2),
                                        true
                                );
                            }
//                            PlayerFilter.entitiesAround(warpLocation, 10, 10, 10)
//                                        .aliveEnemiesOf(wp)
//                                        .forEach(warlordsEntity -> {
//                                            if (warlordsEntity instanceof WarlordsNPC) {
//                                                ((WarlordsNPC) warlordsEntity).getMob().setTarget(cryoPod.get());
//                                            }
//                                        });
                        }
                    }
                })
        ) {

        };
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
