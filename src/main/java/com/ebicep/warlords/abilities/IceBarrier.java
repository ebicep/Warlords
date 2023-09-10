package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.cryomancer.IceBarrierBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IceBarrier extends AbstractAbility implements OrangeAbilityIcon, Duration {

    private int tickDuration = 120;
    private float damageReductionPercent = 50;
    private int slownessOnMeleeHit = 20;

    public IceBarrier() {
        super("Ice Barrier", 0, 0, 46.98f, 0);
    }

    public IceBarrier(float damageReductionPercent) {
        super("Ice Barrier", 0, 0, 46.98f, 0);
        this.damageReductionPercent = damageReductionPercent;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Surround yourself with a layer of cold air, reducing damage taken by ")
                               .append(Component.text(format(damageReductionPercent) + "%", NamedTextColor.RED))
                               .append(Component.text(", while active, taking melee damage reduces the attacker's movement speed by "))
                               .append(Component.text(slownessOnMeleeHit + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" for "))
                               .append(Component.text("2", NamedTextColor.GOLD))
                               .append(Component.text(" seconds " + (inPve ? " and take agro of nearby mobs" : "") + ". Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        Utils.playGlobalSound(player.getLocation(), "mage.icebarrier.activation", 2, 1);

        Location castLocation = wp.getLocation();
        List<Location> verticalRectangle;
        if (pveMasterUpgrade2) {
            verticalRectangle = LocationUtils.getVerticalRectangle(castLocation.add(0, -1, 0), 4, 5);
        } else {
            verticalRectangle = new ArrayList<>();
        }

        IceBarrier tempIceBarrier = new IceBarrier(damageReductionPercent);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "ICE",
                IceBarrier.class,
                tempIceBarrier,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 5 != 0) {
                        return;
                    }
                    if (pveMasterUpgrade2) {
                        for (Location location : verticalRectangle) {
                            EffectUtils.displayParticle(
                                    Particle.CLOUD,
                                    location,
                                    5,
                                    .25,
                                    .25,
                                    .25,
                                    0
                            );
                            PlayerFilter.entitiesAround(location, 1, 1, 1)
                                        .aliveEnemiesOf(wp)
                                        .filter(enemy -> !enemy.getCooldownManager().hasCooldownFromName("Ice Wall"))
                                        .forEach(enemy -> {
                                            enemy.addSpeedModifier(wp, "Ice Wall", -50, ticksLeft);
                                            enemy.getCooldownManager().addCooldown(new RegularCooldown<>(
                                                    "Ice Wall",
                                                    "WALL",
                                                    IceBarrier.class,
                                                    new IceBarrier(),
                                                    wp,
                                                    CooldownTypes.ABILITY,
                                                    cooldownManager -> {

                                                    },
                                                    ticksLeft
                                            ) {
                                                @Override
                                                public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                                    return currentDamageValue * 1.15f;
                                                }
                                            });
                                        });
                        }
                    } else {
                        Location particleLoc = wp.getLocation().add(0, 1.5, 0);

                        particleLoc.getWorld().spawnParticle(Particle.CLOUD, particleLoc, 1, 0.2, 0.2, 0.2, 0.001, null, true);
                        particleLoc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, particleLoc, 1, 0.3, 0.2, 0.3, 0.0001, null, true);

                        if (pveMasterUpgrade) {
                            Utils.playGlobalSound(particleLoc, Sound.BLOCK_GLASS_BREAK, 1, 1.35f);
                            EffectUtils.playHelixAnimation(
                                    particleLoc.add(0, -1.25, 0),
                                    6,
                                    Particle.FIREWORKS_SPARK,
                                    1,
                                    8
                            );

                            for (WarlordsEntity we : PlayerFilter
                                    .entitiesAround(wp, 6, 6, 6)
                                    .aliveEnemiesOf(wp)
                                    .closestFirst(wp)
                            ) {
                                we.setDamageResistance(we.getSpec().getDamageResistance() - 1);
                                if (we instanceof WarlordsNPC npc) {
                                    npc.setDamageResistancePrefix(npc.getDamageResistancePrefix() - 1);
                                }
                                we.addSpeedModifier(wp, "Ice Barrier Slowness", -75, 20);
                            }
                        }

                        for (WarlordsEntity we : PlayerFilter
                                .entitiesAround(wp, 15, 15, 15)
                                .aliveEnemiesOf(wp)
                                .closestFirst(wp)
                        ) {
                            if (we instanceof WarlordsNPC) {
                                ((WarlordsNPC) we).getMob().setTarget(wp);
                            }
                        }
                    }
                })
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (pveMasterUpgrade2) {
                    return currentDamageValue;
                }
                float newDamageValue = currentDamageValue * getDamageReduction();
                event.getWarlordsEntity().addAbsorbed(Math.abs(currentDamageValue - newDamageValue));
                return newDamageValue;
            }

            @Override
            public void multiplyKB(Vector currentVector) {
                if (pveMasterUpgrade) {
                    currentVector.multiply(0.7);
                }
            }
        });

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new IceBarrierBranch(abilityTree, this);
    }

    public float getDamageReduction() {
        return (100 - damageReductionPercent) / 100f;
    }

    public float getDamageReductionPercent() {
        return damageReductionPercent;
    }

    public void setDamageReductionPercent(float damageReductionPercent) {
        this.damageReductionPercent = damageReductionPercent;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }


    public int getSlownessOnMeleeHit() {
        return slownessOnMeleeHit;
    }

    public void setSlownessOnMeleeHit(int slownessOnMeleeHit) {
        this.slownessOnMeleeHit = slownessOnMeleeHit;
    }
}
