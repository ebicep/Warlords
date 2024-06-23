package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsEnergyUseEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EnergySeer extends AbstractAbility implements PurpleAbilityIcon, Duration, Heals<EnergySeer.HealingValues> {

    private final HealingValues healingValues = new HealingValues();
    private int tickDuration = 100;
    private int energyRestore = 180;
    private int epsDecrease = 20;
    private int speedBuff = 30;

    public EnergySeer() {
        super("Energy Seer", 26, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Gain ")
                               .append(Component.text(energyRestore, NamedTextColor.YELLOW))
                               .append(Component.text(" energy and heal "))
                               .append(Component.text(" for "))
                               .append(Heals.formatHealingPercent(healingValues.seerHealingMultiplier, aFloat -> aFloat * 100))
                               .append(Component.text(" of the energy expended for the next "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. When Energy Seer ends, lose "))
                               .append(Component.text(epsDecrease, NamedTextColor.YELLOW))
                               .append(Component.text(" energy per second and gain"))
                               .append(Component.text(speedBuff + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" speed for "))
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
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "arcanist.energyseer.activation", 2, 0.9f);
        for (int i = 0; i < 20; i++) {
            EffectUtils.displayParticle(Particle.SOUL_FIRE_FLAME, wp.getLocation(), 3, 0.3, 0.1, 0.3, 0.1);
        }

        wp.addEnergy(wp, name, energyRestore);

        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "SEER",
                EnergySeer.class,
                new EnergySeer(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    wp.addSpeedModifier(wp, name, speedBuff, tickDuration);
                    wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                            name,
                            "SEER",
                            EnergySeer.class,
                            new EnergySeer(),
                            wp,
                            CooldownTypes.ABILITY,
                            cooldownManager2 -> {

                            },
                            tickDuration,
                            Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                if (ticksElapsed % 4 == 0) {
                                    Location location = wp.getLocation();
                                    location.add(0, 1.2, 0);
                                    EffectUtils.displayParticle(
                                            Particle.SOUL,
                                            location,
                                            2,
                                            0.3,
                                            0,
                                            0.3,
                                            0.1
                                    );
                                }
                            })
                    ) {
                        @Override
                        public float addEnergyGainPerTick(float energyGainPerTick) {
                            return energyGainPerTick - epsDecrease / 20f;
                        }
                    });
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        Location location = wp.getLocation();
                        location.add(0, 1.2, 0);
                        EffectUtils.displayParticle(
                                Particle.SOUL_FIRE_FLAME,
                                location,
                                2,
                                0.3,
                                0,
                                0.3,
                                0.1
                        );
                    }
                })
        ) {

            @Override
            protected Listener getListener() {
                return new Listener() {
                    @EventHandler
                    public void onEnergyUsed(WarlordsEnergyUseEvent.Post event) {
                        float energyUsed = event.getEnergyUsed();
                        if (energyUsed <= 0) {
                            return;
                        }
                        if (!Objects.equals(event.getWarlordsEntity(), wp)) {
                            return;
                        }
                        wp.addInstance(InstanceBuilder
                                .healing()
                                .ability(EnergySeer.this)
                                .source(wp)
                                .value(energyUsed * healingValues.seerHealingMultiplier.getValue())
                        );
                    }
                };
            }
        });
        return true;
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
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.SetValue seerHealingMultiplier = new Value.SetValue(5);
        private final List<Value> values = List.of(seerHealingMultiplier);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
