package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractTimeWarp;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.pyromancer.TimeWarpBranchPyromancer;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.sounds.SoundSource;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class TimeWarpPyromancer extends AbstractTimeWarp {

    public TimeWarpPyromancer() {
        super();
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.activation", 3, 1);

        Location warpLocation = wp.getLocation();
        List<Location> warpTrail = new ArrayList<>();
        int startingBlocksTravelled = wp.getBlocksTravelled();

        // pveMasterUpgrade2
        List<WarlordsEntity> linkedPlayers = new ArrayList<>();
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

                    wp.addInstance(InstanceBuilder
                            .healing()
                            .ability(this)
                            .source(wp)
                            .value(wp.getMaxHealth() * (warpHealPercentage / 100f))
                    );

                    wp.getEntity().teleport(warpLocation);
                    warpTrail.clear();

                    if (pveMasterUpgrade2) {
                        float cooldownReduction = 0;
                        for (WarlordsEntity linkedPlayer : linkedPlayers) {
                            if (linkedPlayer.isDead()) {
                                cooldownReduction += .75f;
                                continue;
                            }
                            float healthDamage = linkedPlayer.getMaxBaseHealth() * .05f;
                            if (linkedPlayer instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof BossLike) {
                                healthDamage = DamageCheck.clamp(healthDamage);
                            }
                            linkedPlayer.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Accursed Leap")
                                    .source(wp)
                                    .value(healthDamage)
                            );
                        }
                        subtractCurrentCooldown(cooldownReduction);
                    }
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

                        if (pveMasterUpgrade2) {
                            PlayerFilter.entitiesAround(wp, 3, 3, 3)
                                        .aliveEnemiesOf(wp)
                                        .excluding(linkedPlayers)
                                        .forEach(warlordsEntity -> {
                                            linkedPlayers.add(warlordsEntity);
                                            wp.playSound(
                                                    warlordsEntity.getLocation().add(0, 1, 0),
                                                    Instrument.PIANO,
                                                    new Note(0, Note.Tone.G, true),
                                                    SoundSource.MASTER
                                            );
                                        });
                        }
                    }
                    if (pveMasterUpgrade2 && ticksElapsed % 8 == 0) {
                        double rad = 0.7d;
                        for (int i = 0; i < linkedPlayers.size(); i++) {
                            WarlordsEntity linked = linkedPlayers.get(i);
                            // play circle particle effect after linked then chain particle effect from linked to linkedAfter
                            // chain will be the closest possible to linkedAfter
                            LocationBuilder linkedLocation = new LocationBuilder(linked.getLocation())
                                    .addY(1);
                            for (int j = 0; j < 12; j++) {
                                double x = rad * cos(j);
                                double z = rad * sin(j);
                                Location location = linkedLocation
                                        .clone()
                                        .add(x, 0, z);
                                EffectUtils.displayParticle(
                                        Particle.SPELL_WITCH,
                                        location,
                                        1
                                );
                            }
                            if (i < linkedPlayers.size() - 1) {
                                WarlordsEntity linkedNext = linkedPlayers.get(i + 1);
                                LocationBuilder linkedNextLocation = new LocationBuilder(linkedNext.getLocation())
                                        .addY(1);
                                EffectUtils.playParticleLinkAnimation(
                                        linkedNextLocation.faceTowards(linkedLocation)
                                                          .forward(rad),
                                        linkedLocation.faceTowards(linkedNextLocation)
                                                      .forward(rad),
                                        Particle.SPELL_WITCH,
                                        0
                                );
                            }
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
        return new TimeWarpBranchPyromancer(abilityTree, this);
    }

}
