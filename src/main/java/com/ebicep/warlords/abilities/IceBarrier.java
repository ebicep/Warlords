package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.cryomancer.IceBarrierBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class IceBarrier extends AbstractAbility implements OrangeAbilityIcon, Duration, AbilityStats<IceBarrier, IceBarrier.IceBarrierStats> {

    private final IceBarrierStats stats = new IceBarrierStats();
    private int tickDuration = 120;
    private float damageReductionPercent = 50;
    private float slownessOnMeleeHit = 20;
    private int slowDuration = 2;

    public IceBarrier() {
        super(AbstractAbilityBuilder.create("iceBarrier").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.damageReductionPercent = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReductionPercent"), float.class);
        this.slownessOnMeleeHit = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("slownessOnMeleeHit"), int.class);
        this.slowDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("slowDuration"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "mage.icebarrier.activation", 2, 1);
        IceBarrierData data = new IceBarrierData(this);
        RegularCooldown<IceBarrierData> iceBarrierCooldown = new RegularCooldown<>(
                name,
                "ICE",
                IceBarrierData.class,
                data,
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
                        LocationBuilder locationBuilder = new LocationBuilder(wp.getLocation()).addY(-1).pitch(0).forward(3.5);
                        List<Location> verticalRectangle = LocationUtils.getVerticalRectangle(locationBuilder, 4, 5);
                        for (Location location : verticalRectangle) {
                            EffectUtils.displayParticle(Particle.BLOCK, location, 10, .1, .1, .1, 0, Material.BLUE_ICE.createBlockData());
                            PlayerFilter.entitiesAround(location, 1, 1, 1)
                                        .aliveEnemiesOf(wp)
                                        .filter(enemy -> !enemy.getCooldownManager().hasCooldownFromName("Ice Wall"))
                                        .forEach(enemy -> {
                                            enemy.addSpeedModifier(wp, "Ice Wall", -50, ticksLeft);
                                            enemy.getCooldownManager()
                                                 .addCooldown(new RegularCooldown<>("Ice Wall",
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
                                                         return currentDamageValue * 1.35f;
                                                     }
                                                 });
                                        });
                        }
                    } else {
                        Location particleLoc = wp.getLocation().add(0, 1.5, 0);
                        EffectUtils.displayParticle(Particle.CLOUD, particleLoc, 1, 0.2, 0.2, 0.2, 0.001);
                        EffectUtils.displayParticle(Particle.FIREWORK, particleLoc, 1, 0.3, 0.2, 0.3, 0.0001);
                        if (pveMasterUpgrade) {
                            Utils.playGlobalSound(particleLoc, Sound.BLOCK_GLASS_BREAK, 1, 1.35f);
                            EffectUtils.playHelixAnimation(particleLoc.add(0, -1.25, 0), 6, Particle.FIREWORK, 1, 8);
                            for (WarlordsEntity we : PlayerFilter.entitiesAround(wp, 6, 6, 6).aliveEnemiesOf(wp).closestFirst(wp)) {
                                we.setDamageResistance(we.getSpec().getDamageResistance() - 1);
                                if (we instanceof WarlordsNPC npc) {
                                    npc.setDamageResistance(npc.getSpec().getDamageResistance() - 1);
                                }
                                we.addSpeedModifier(wp, "Ice Barrier Slowness", -75, 20);
                            }
                        }
                    }
                    if (wp.isInPve()) {
                        for (WarlordsEntity we : PlayerFilter.entitiesAround(wp, 15, 15, 15).aliveEnemiesOf(wp).closestFirst(wp)) {
                            if (we instanceof WarlordsNPC warlordsNPC) {
                                warlordsNPC.getMob().setTarget(wp);
                            }
                        }
                    }
                })
        ) {

            @Override
            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (event.getCause().isEmpty() && !Objects.equals(event.getSource(), event.getWarlordsEntity())) {
                    event.getSource().addSpeedModifier(event.getWarlordsEntity(), "Ice Barrier", -slownessOnMeleeHit, slowDuration * 20);
                }
                return currentDamageValue;
            }

            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (pveMasterUpgrade2) {
                    return currentDamageValue;
                }
                return currentDamageValue * getDamageReduction();
            }

        };
        wp.getCooldownManager().addCooldown(iceBarrierCooldown);
        if (pveMasterUpgrade) {
            wp.addKnockbackModifier(wp, name, -30, iceBarrierCooldown);
        }
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Surround yourself with a layer of cold air, reducing damage taken by ")
                                               .percent(damageReductionPercent, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(", while active, taking melee damage reduces the attacker's movement speed by ")
                                               .percent(slownessOnMeleeHit, NamedTextColor.WHITE)
                                               .text(" for ")
                                               .durationSeconds(slowDuration)
                                               .text(" " + (inPve ? " and take aggro of nearby mobs" : "") + ". Lasts ")
                                               .durationTicks(tickDuration)
                                               .text(".")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new IceBarrierBranch(abilityTree, this);
    }

    public float getDamageReduction() {
        return (100 - damageReductionPercent) / 100f;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public IceBarrierStats getAbilityStats() {
        return stats;
    }

    public float getDamageReductionPercent() {
        return damageReductionPercent;
    }

    public void setDamageReductionPercent(float damageReductionPercent) {
        this.damageReductionPercent = damageReductionPercent;
    }

    public float getSlownessOnMeleeHit() {
        return slownessOnMeleeHit;
    }

    public void setSlownessOnMeleeHit(float slownessOnMeleeHit) {
        this.slownessOnMeleeHit = slownessOnMeleeHit;
    }

    public static class IceBarrierData {

        private IceBarrier iceBarrier;

        public IceBarrierData(IceBarrier iceBarrier) {
            this.iceBarrier = iceBarrier;
        }

        public IceBarrier getIceBarrier() {
            return iceBarrier;
        }

    }

    public static class IceBarrierStats extends AbstractAbilityStats<IceBarrier, IceBarrierStats> {

        @Override
        public Class<IceBarrierStats> getClazz() {
            return IceBarrierStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public IceBarrierStats merge(IceBarrierStats other, int multiplier) {
            IceBarrierStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public IceBarrierStats create() {
            return new IceBarrierStats();
        }

    }

}
