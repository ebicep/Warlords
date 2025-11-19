package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.AbstractTimeWarp;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
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
import java.util.Optional;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class TimeWarpPyromancer extends AbstractTimeWarp {

    public TimeWarpPyromancer() {
        super(AbstractAbilityBuilder.create("timeWarpPyromancer").pvp());
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.activation", 3, 1);
        List<Location> warpTrail = new ArrayList<>();
        int startingBlocksTravelled = wp.getBlocksTravelled();
        // pveMasterUpgrade2
        List<WarlordsEntity> linkedPlayers = new ArrayList<>();
        TimeWarpPyromancerData data = new TimeWarpPyromancerData(
                wp.getLocation(),
                () -> wp.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(wp.getMaxHealth() * (warpHealPercentage / 100f)))
        );
        RegularCooldown<TimeWarpPyromancerData> timeWarpCooldown = new RegularCooldown<>(name,
                "TIME",
                TimeWarpPyromancerData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (wp.isDead() || wp.getGame().getState() instanceof EndState) {
                        return;
                    }
                    getAbilityStats().addTimesSuccessful();
                    Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.teleport", 1, 1);
                    data.getWarpHeal().run();
                    wp.getEntity().teleport(data.warpLocation);
                    if (pveMasterUpgrade2) {
                        float cooldownReduction = 0;
                        for (WarlordsEntity enemy : PlayerFilter.entitiesAround(wp, 12, 12, 12).aliveEnemiesOf(wp).toList()) {
                            float healthDamage = enemy.getMaxBaseHealth() * .075f;
                            healthDamage = DamageCheck.clamp(healthDamage);
                            Optional<WarlordsDamageHealingFinalEvent> finalEventOptional = enemy.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Accursed Leap")
                                    .source(wp)
                                    .value(healthDamage)
                                    .flags(InstanceFlags.IGNORE_SOURCE_DAMAGE_BOOST)
                            );
                            if (finalEventOptional.isPresent()) {
                                if (finalEventOptional.get().isDead()) {
                                    cooldownReduction += 2f;
                                }
                            }
                        }
                        subtractCurrentCooldown(cooldownReduction);
                    }
                    if (pveMasterUpgrade) {
                        giveDamageBoost(wp, startingBlocksTravelled);
                    }
                },
                cooldownManager -> {
                    warpTrail.clear();
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 6 == 0) {
                        for (Location location : warpTrail) {
                            EffectUtils.displayParticle(Particle.WITCH, location, 1, 0.01, 0, 0.01, 0.001);
                        }
                        warpTrail.add(wp.getLocation());
                        EffectUtils.displayParticle(Particle.WITCH, data.warpLocation, 4, 0.1, 0, 0.1, 0.001);
                        int points = 6;
                        double radius = 0.5d;
                        for (int e = 0; e < points; e++) {
                            double angle = 2 * Math.PI * e / points;
                            Location point = data.warpLocation.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
                            EffectUtils.displayParticle(Particle.CLOUD, point, 1, 0.1, 0, 0.1, 0.001);
                        }
                        if (pveMasterUpgrade2) {
                            PlayerFilter.entitiesAround(wp, 3, 3, 3).aliveEnemiesOf(wp).excluding(linkedPlayers).forEach(warlordsEntity -> {
                                linkedPlayers.add(warlordsEntity);
                                wp.playSound(warlordsEntity.getLocation().add(0, 1, 0), Instrument.PIANO, new Note(0, Note.Tone.G, true), SoundSource.MASTER);
                            });
                        }
                    }
                    if (pveMasterUpgrade2 && ticksElapsed % 8 == 0) {
                        double rad = 0.7d;
                        for (int i = 0; i < linkedPlayers.size(); i++) {
                            WarlordsEntity linked = linkedPlayers.get(i);
                            // play circle particle effect after linked then chain particle effect from linked to linkedAfter
                            // chain will be the closest possible to linkedAfter
                            LocationBuilder linkedLocation = new LocationBuilder(linked.getLocation()).addY(1);
                            for (int j = 0; j < 12; j++) {
                                double x = rad * cos(j);
                                double z = rad * sin(j);
                                Location location = linkedLocation.clone().add(x, 0, z);
                                EffectUtils.displayParticle(Particle.WITCH, location, 1);
                            }
                            if (i < linkedPlayers.size() - 1) {
                                WarlordsEntity linkedNext = linkedPlayers.get(i + 1);
                                LocationBuilder linkedNextLocation = new LocationBuilder(linkedNext.getLocation()).addY(1);
                                EffectUtils.playParticleLinkAnimation(linkedNextLocation.faceTowards(linkedLocation).forward(rad),
                                        linkedLocation.faceTowards(linkedNextLocation).forward(rad),
                                        Particle.WITCH,
                                        0
                                );
                            }
                        }
                    }
                })
        );
        wp.getCooldownManager().addCooldown(timeWarpCooldown);
        if (pveMasterUpgrade) {
            addSecondaryAbility(1, () -> timeWarpCooldown.setTicksLeft(1), false, secondaryAbility -> !wp.getCooldownManager().hasCooldown(timeWarpCooldown));
        }
        return true;
    }

    private void giveDamageBoost(WarlordsEntity we, int startingBlocksTravelled) {
        RegularCooldown<TimeWarpPyromancer> damageBoost = new RegularCooldown<>(
                name,
                "WARP DMG",
                TimeWarpPyromancer.class,
                null,
                we,
                CooldownTypes.BUFF,
                cooldownManager -> {

                },
                8 * 20
        );
        damageBoost.addModifier(Modifier.DAMAGE_BEFORE_INTERVENE_ATTACKER, (event, currentDamageValue) -> {
                    if (pveMasterUpgrade) {
                        currentDamageValue.addMultiplicativeModifierMult(name, convertToMultiplicationDecimal(0.75f * (we.getBlocksTravelled() - startingBlocksTravelled)));
                    }
                }
        );
        we.getCooldownManager().addCooldown(damageBoost);
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new TimeWarpBranchPyromancer(abilityTree, this);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
    }

    public static class TimeWarpPyromancerData {

        private final Location warpLocation;
        private Runnable warpHeal;

        public TimeWarpPyromancerData(Location warpLocation, Runnable warpHeal) {
            this.warpLocation = warpLocation;
            this.warpHeal = warpHeal;
        }

        public Location getWarpLocation() {
            return warpLocation;
        }

        public Runnable getWarpHeal() {
            return warpHeal;
        }

        public void setWarpHeal(Runnable warpHeal) {
            this.warpHeal = warpHeal;
        }

    }

}
