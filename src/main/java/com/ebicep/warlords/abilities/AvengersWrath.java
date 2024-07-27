package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbilityDescriptionBuilder;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsStrikeEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.avenger.AvengersWrathBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class AvengersWrath extends AbstractAbility implements OrangeAbilityIcon, Duration {

    public int extraPlayersStruck = 0;
    public int playersStruckDuringWrath = 0;
    public int playersKilledDuringWrath = 0;

    private int tickDuration = 240;
    private float energyPerSecond = 20;
    private int maxTargets = 2;
    private int hitRadius = 5;

    public AvengersWrath() {
        super("Avenger's Wrath", 53, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Burst with incredible holy power, causing your Avenger's Strikes to hit up to ")
                .text(maxTargets, NamedTextColor.BLUE)
                .text(" additional enemies that are within ")
                .blocks(hitRadius)
                .text(" of your target. Your energy per second is increased by ")
                .energy(energyPerSecond)
                .text(" for the duration of the effect. Lasts ")
                .durationTicks(tickDuration)
                .text(".")
                .build();
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Extra Players Struck", "" + extraPlayersStruck));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "paladin.avengerswrath.activation", 2, 1);

        AvengersWrath tempAvengersWrath = new AvengersWrath();
        wp.getCooldownManager().removeCooldown(AvengersWrath.class, false);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "WRATH",
                AvengersWrath.class,
                tempAvengersWrath,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        EffectUtils.displayParticle(
                                Particle.SPELL,
                                wp.getLocation().add(0, 1.2, 0),
                                6,
                                0.3F,
                                0.1F,
                                0.3F,
                                0.2F
                        );
                    }
                })
        ) {
            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (!event.getCause().equals("Avenger's Strike") || event.getFlags().contains(InstanceFlags.AVENGER_WRATH_STRIKE)) {
                    return;
                }
                WarlordsEntity warlordsEntity = event.getWarlordsEntity();
                tempAvengersWrath.addPlayersStruckDuringWrath();
                EnumSet<InstanceFlags> flags = EnumSet.of(InstanceFlags.AVENGER_WRATH_STRIKE);
                if (event.getFlags().contains(InstanceFlags.STRIKE_IN_CONS)) {
                    flags.add(InstanceFlags.STRIKE_IN_CONS);
                }
                if (pveMasterUpgrade2) {
                    warlordsEntity.addInstance(InstanceBuilder
                            .damage()
                            .cause("Avenger's Strike")
                            .source(wp)
                            .value(event)
                            .flags(flags)
                    );
                    tempAvengersWrath.addPlayersStruckDuringWrath();
                }
                for (WarlordsEntity wrathTarget : PlayerFilter
                        .entitiesAround(warlordsEntity, hitRadius, hitRadius, hitRadius)
                        .aliveEnemiesOf(wp)
                        .closestFirst(warlordsEntity)
                        .excluding(warlordsEntity)
                        .limit(maxTargets)
                ) {
                    addExtraPlayersStruck();
                    tempAvengersWrath.addPlayersStruckDuringWrath();

                    wrathTarget.addInstance(InstanceBuilder
                            .damage()
                            .cause("Avenger's Strike")
                            .source(wp)
                            .value(event)
                            .flags(flags)
                    );

                    Bukkit.getPluginManager().callEvent(new WarlordsStrikeEvent(wp, AvengersWrath.this, wrathTarget));
                    wrathTarget.subtractEnergy(name, 10, true);
                }
            }

            @Override
            public void onDeathFromEnemies(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, boolean isKiller) {
                if (isKiller) {
                    tempAvengersWrath.addPlayersKilledDuringWrath();
                }
            }

            @Override
            public float addEnergyGainPerTick(float energyGainPerTick) {
                return energyGainPerTick + energyPerSecond / 20f;
            }
        });

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new AvengersWrathBranch(abilityTree, this);
    }

    public void addPlayersStruckDuringWrath() {
        playersStruckDuringWrath++;
    }

    public void addExtraPlayersStruck() {
        extraPlayersStruck++;
    }

    public void addPlayersKilledDuringWrath() {
        playersKilledDuringWrath++;
    }

    public int getPlayersStruckDuringWrath() {
        return playersStruckDuringWrath;
    }

    public int getPlayersKilledDuringWrath() {
        return playersKilledDuringWrath;
    }

    public float getEnergyPerSecond() {
        return energyPerSecond;
    }

    public void setEnergyPerSecond(float energyPerSecond) {
        this.energyPerSecond = energyPerSecond;
    }


    public int getMaxTargets() {
        return maxTargets;
    }

    public void setMaxTargets(int maxTargets) {
        this.maxTargets = maxTargets;
    }

    public int getHitRadius() {
        return hitRadius;
    }

    public void setHitRadius(int hitRadius) {
        this.hitRadius = hitRadius;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }
}
