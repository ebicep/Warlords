package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.AbstractTimeWarp;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.player.CryoPod;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.cryomancer.TimeWarpBranchCryomancer;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TimeWarpCryomancer extends AbstractTimeWarp {

    public TimeWarpCryomancer() {
        super(AbstractAbilityBuilder.create("timeWarpCryomancer").pvp());
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.activation", 3, 1);
        Location warpLocation = wp.getLocation();
        List<Location> warpTrail = new ArrayList<>();
        Game game = wp.getGame();
        PveOption pveOption = game.getOption(PveOption.class).stream().findFirst().orElse(null);
        CryoPod cryoPod;
        TimeWarpPyromancerData data = new TimeWarpPyromancerData(
                warpLocation,
                this,
                () -> wp.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(wp.getMaxHealth() * (warpHealPercentage / 100f)))
        );
        if (pveMasterUpgrade && pveOption != null) {
            cryoPod = new CryoPod(warpLocation, wp.getName()) {

                @Override
                public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
                    if (wp.isDead()) {
                        return;
                    }
                    PlayerFilter.entitiesAround(warpLocation, 5, 5, 5).aliveEnemiesOf(wp).forEach(warlordsEntity -> {
                        if (warlordsEntity instanceof WarlordsNPC) {
                            warlordsEntity.addSpeedModifier(wp, "Frostbite Leap", -80, 60);
                        }
                    });
                    if (wp.isDead()) {
                        return;
                    }
                    PlayerFilterGeneric.playingGameWarlordsNPCs(game).filter(n -> Objects.equals(n.getMob().getTarget(), npc.getEntity())).forEach(n -> {
                        n.getMob().setTarget(wp);
                    });
                }
            };
            pveOption.spawnNewMob(cryoPod, Team.BLUE);
        } else {
            cryoPod = null;
            if (pveMasterUpgrade2) {
                PlayerFilter.entitiesAround(wp, 30, 30, 30).aliveEnemiesOf(wp).forEach(enemy -> {
                    int duration = 100;
                    enemy.addSpeedModifier(wp, "Freezing Cold", -80, duration);
                    enemy.getCooldownManager().removeCooldownByName("Freezing Cold");
                    enemy.getCooldownManager()
                         .addCooldown(new RegularCooldown<>("Freezing Cold",
                                 "COLD",
                                 TimeWarpPyromancerData.class,
                                 data,
                                 wp,
                                 CooldownTypes.ABILITY,
                                 cooldownManager -> {
                                 },
                                 duration
                         ) {

                             @Override
                             public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                 return currentDamageValue * 1.15f;
                             }
                         });
                });
                EffectUtils.displayParticle(Particle.SNOWFLAKE, wp.getLocation().add(0, .2, 0), 1700, 15, 0, 15, 0);
            }
        }
        RegularCooldown<TimeWarpPyromancerData> timeWarpCooldown = new RegularCooldown<>(name,
                "TIME",
                TimeWarpPyromancerData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (wp.isDead() || game.getState() instanceof EndState) {
                        return;
                    }
                    getAbilityStats().addTimesSuccessful();
                    Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.teleport", 1, 1);
                    data.getWarpHeal().run();
                    wp.getEntity().teleport(warpLocation);
                    if (pveMasterUpgrade) {
                        wp.getCooldownManager()
                          .addCooldown(new RegularCooldown<>("Frostbite Leap", "WARP RES", TimeWarpPyromancerData.class, data, wp, CooldownTypes.ABILITY, cooldownManager2 -> {
                          }, cooldownManager2 -> {
                          }, 5 * 20, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                          })
                          ) {

                              @Override
                              public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                  return currentDamageValue * .2f;
                              }
                          });
                    }
                },
                cooldownManager -> {
                    warpTrail.clear();
                    if (pveOption != null && cryoPod != null && pveOption.getMobs().contains(cryoPod)) {
                        cryoPod.getWarlordsNPC().die(cryoPod.getWarlordsNPC(), WarlordsDeathEvent.DeathInfoBuilder.create().setForced(true));
                    }
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 6 == 0) {
                        for (Location location : warpTrail) {
                            EffectUtils.displayParticle(Particle.WITCH, location, 1, 0.01, 0, 0.01, 0.001);
                        }
                        warpTrail.add(wp.getLocation());
                        EffectUtils.displayParticle(Particle.CLOUD, warpLocation, 1, 0.1, 0, 0.1, 0.001);
                        int points = 6;
                        double radius = 0.5d;
                        for (int e = 0; e < points; e++) {
                            double angle = 2 * Math.PI * e / points;
                            Location point = warpLocation.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
                            EffectUtils.displayParticle(Particle.CLOUD, point, 1, 0.1, 0, 0.1, 0.001);
                        }
                        if (pveMasterUpgrade && cryoPod != null && cryoPod.getWarlordsNPC().isAlive()) {
                            EffectUtils.playCylinderAnimation(warpLocation, .7, Particle.CLOUD, 1);
                            points = 24;
                            radius = .85;
                            for (int e = 0; e < points; e++) {
                                double angle = 2 * Math.PI * e / points;
                                Location point = warpLocation.clone().add(radius * Math.sin(angle), 2.1, radius * Math.cos(angle));
                                EffectUtils.displayParticle(
                                        Particle.DUST,
                                        point,
                                        1,
                                        0,
                                        0,
                                        0,
                                        0,
                                        new Particle.DustOptions(Color.fromRGB(0, 100, 100), 2)
                                );
                            }
                        }
                    }
                })
        ) {
        };
        wp.getCooldownManager().addCooldown(timeWarpCooldown);
        if (pveMasterUpgrade) {
            addSecondaryAbility(1, () -> {
                        timeWarpCooldown.setTicksLeft(1);
                    }, false, secondaryAbility -> !wp.getCooldownManager().hasCooldown(timeWarpCooldown)
            );
        }
        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new TimeWarpBranchCryomancer(abilityTree, this);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
    }

    public static class TimeWarpPyromancerData {

        private final Location warpLocation;
        private final TimeWarpCryomancer staticAbility;
        private Runnable warpHeal;

        public TimeWarpPyromancerData(Location warpLocation, TimeWarpCryomancer staticAbility, Runnable warpHeal) {
            this.warpLocation = warpLocation;
            this.staticAbility = staticAbility;
            this.warpHeal = warpHeal;
        }

        public Location getWarpLocation() {
            return warpLocation;
        }

        public TimeWarpCryomancer getStaticAbility() {
            return staticAbility;
        }

        public Runnable getWarpHeal() {
            return warpHeal;
        }

        public void setWarpHeal(Runnable warpHeal) {
            this.warpHeal = warpHeal;
        }

    }

}
