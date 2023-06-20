package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractTimeWarp;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeWarpPyromancer extends AbstractTimeWarp {

    public TimeWarpPyromancer() {
        super();
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "mage.timewarp.activation", 3, 1);

        Location warpLocation = wp.getLocation();
        List<Location> warpTrail = new ArrayList<>();
        int startingBlocksTravelled = wp.getBlocksTravelled();
        RegularCooldown<TimeWarpPyromancer> timeWarpCooldown = new RegularCooldown<>(
                name,
                "TIME",
                TimeWarpPyromancer.class,
                new TimeWarpPyromancer(),
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
                },
                tickDuration,
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
                    }
                })
        ) {
            @Override
            public float addCritChanceFromAttacker(WarlordsDamageHealingEvent event, float currentCritChance) {
                if (pveMasterUpgrade) {
                    return currentCritChance + (wp.getBlocksTravelled() - startingBlocksTravelled);
                }
                return currentCritChance;
            }

            @Override
            public float addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
                if (pveMasterUpgrade) {
                    return currentCritMultiplier + (wp.getBlocksTravelled() - startingBlocksTravelled);
                }
                return currentCritMultiplier;
            }
        };
        wp.getCooldownManager().addCooldown(timeWarpCooldown);

        if (pveMasterUpgrade) {
            addSecondaryAbility(
                    () -> timeWarpCooldown.setTicksLeft(1),
                    false,
                    secondaryAbility -> !wp.getCooldownManager().hasCooldown(timeWarpCooldown)
            );
        }
        return true;
    }

}
