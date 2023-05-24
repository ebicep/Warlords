package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractTimeWarpBase;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.mobs.player.CryoPod;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeWarpCryomancer extends AbstractTimeWarpBase {

    public TimeWarpCryomancer() {
        super();
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "mage.timewarp.activation", 3, 1);

        Location warpLocation = wp.getLocation();
        List<Location> warpTrail = new ArrayList<>();


        PveOption pveOption = wp.getGame()
                                .getOptions()
                                .stream()
                                .filter(PveOption.class::isInstance)
                                .map(PveOption.class::cast)
                                .findFirst()
                                .orElse(null);
        CryoPod cryoPod;
        if (pveUpgrade && pveOption != null) {
            cryoPod = new CryoPod(warpLocation, wp.getName()) {
                @Override
                public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
                    wp.getCooldownManager().addCooldown(new RegularCooldown<TimeWarpCryomancer>(
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
            };
            pveOption.spawnNewMob(cryoPod, Team.BLUE);
        } else {
            cryoPod = null;
        }

        RegularCooldown<TimeWarpCryomancer> timeWarpCooldown = new RegularCooldown<>(
                name,
                "TIME",
                TimeWarpCryomancer.class,
                new TimeWarpCryomancer(),
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
                cooldownManager -> {
                    if (pveOption != null && cryoPod != null && pveOption.getMobs().contains(cryoPod)) {
                        cryoPod.getWarlordsNPC().die(cryoPod.getWarlordsNPC());
                    }
                },
                tickDuration,
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

                        if (pveUpgrade && cryoPod != null && cryoPod.getWarlordsNPC().isAlive()) {
                            EffectUtils.playCylinderAnimation(warpLocation, .7, ParticleEffect.CLOUD, 1);
                            points = 24;
                            radius = .85;
                            for (int e = 0; e < points; e++) {
                                double angle = 2 * Math.PI * e / points;
                                Location point = warpLocation.clone().add(radius * Math.sin(angle), 2.1, radius * Math.cos(angle));
                                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(0, 100, 100), point, 500);
                                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(0, 100, 100), point, 500);
                            }
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

}
