package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.EnergySeerConjurer;
import com.ebicep.warlords.abilities.EnergySeerLuminary;
import com.ebicep.warlords.abilities.EnergySeerSentinel;
import com.ebicep.warlords.abilities.FortifyingHex;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsEnergyUseEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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

public abstract class AbstractEnergySeer<T extends AbstractEnergySeer.EnergySeerData> extends AbstractAbility implements PurpleAbilityIcon, Duration, Heals<AbstractEnergySeer.HealingValues> {

    private final HealingValues healingValues = new HealingValues();
    private int tickDuration = 100;
    private int postEffectTickDuration = 100;
    private int energyRestore = 130;
    private int epsDecrease = 10;
    private int speedBuff = 30;

    public AbstractEnergySeer() {
        super("Energy Seer", 26, 0);
    }

    @Override
    public void updateDescription(Player player) {
        if (inPve) {
            description = getBonus().append(Component.text(", gain "))
                                    .append(Component.text(energyRestore, NamedTextColor.YELLOW))
                                    .append(Component.text(" energy, and heal "))
                                    .append(Component.text(" for "))
                                    .append(Heals.formatHealingPercent(healingValues.seerHealingMultiplier, aFloat -> aFloat * 100))
                                    .append(Component.text(" of the energy expended for the next "))
                                    .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                                    .append(Component.text(" seconds. When Energy Seer ends, lose "))
                                    .append(Component.text(epsDecrease, NamedTextColor.YELLOW))
                                    .append(Component.text(" energy per second and gain"))
                                    .append(Component.text(speedBuff + "%", NamedTextColor.YELLOW))
                                    .append(Component.text(" speed for "))
                                    .append(Component.text(format(postEffectTickDuration / 20f), NamedTextColor.GOLD))
                                    .append(Component.text(" seconds."));
        } else {
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
                                   .append(Component.text(format(postEffectTickDuration / 20f), NamedTextColor.GOLD))
                                   .append(Component.text(" seconds."));
        }
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

        T data = getDataObject();
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "SEER",
                getDataClass(),
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    onEnd(wp, data);
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
            public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                if (inPve && AbstractEnergySeer.this instanceof EnergySeerLuminary energySeerLuminary) {
                    return currentHealValue * convertToMultiplicationDecimal(energySeerLuminary.getHealingIncrease());

                }
                return currentHealValue;
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (inPve && AbstractEnergySeer.this instanceof EnergySeerConjurer energySeerConjurer) {
                    return currentDamageValue * convertToMultiplicationDecimal(energySeerConjurer.getDamageIncrease());
                }
                return currentDamageValue;
            }

            @Override
            protected Listener getListener() {
                return new Listener() {
                    @EventHandler
                    public void onEnergyUsed(WarlordsEnergyUseEvent.Post event) {
                        float energyUsed = event.getEnergyUsed();
                        if (energyUsed <= 0) {
                            return;
                        }
                        AbstractEnergySeer.this.onEnergyUsed(wp, event, data);
                        if (!Objects.equals(event.getWarlordsEntity(), wp)) {
                            return;
                        }
                        data.setEnergyUsed(data.getEnergyUsed() + energyUsed);
                        wp.addInstance(InstanceBuilder
                                .healing()
                                .ability(AbstractEnergySeer.this)
                                .source(wp)
                                .value(energyUsed * healingValues.seerHealingMultiplier.getValue())
                        );
                    }

                    @EventHandler
                    public void onCooldownAdd(WarlordsAddCooldownEvent event) {
                        if (!inPve) {
                            return;
                        }
                        if (!(AbstractEnergySeer.this instanceof EnergySeerSentinel energySeerSentinel)) {
                            return;
                        }
                        AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                        if (!Objects.equals(cooldown.getFrom(), wp)) {
                            return;
                        }
                        if (cooldown.getCooldownObject() instanceof FortifyingHex fortifyingHex) {
                            fortifyingHex.getDamageReduction().addAdditiveModifier(name, energySeerSentinel.getDamageResistance(), getTicksLeft());
                        }
                    }
                };
            }
        });
        return true;
    }

    public abstract T getDataObject();

    public abstract Class<T> getDataClass();

    protected void onEnd(WarlordsEntity wp, T data) {
        wp.addSpeedModifier(wp, name, speedBuff, tickDuration);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "SEER",
                AbstractEnergySeer.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager2 -> {

                },
                postEffectTickDuration,
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
    }

    public abstract TextComponent getBonus();

    protected void onEnergyUsed(WarlordsEntity wp, WarlordsEnergyUseEvent.Post event, T cooldownObjet) {
    }

    public int getEnergyRestore() {
        return energyRestore;
    }

    public void setEnergyRestore(int energyRestore) {
        this.energyRestore = energyRestore;
    }

    public int getEpsDecrease() {
        return epsDecrease;
    }

    public void setEpsDecrease(int epsDecrease) {
        this.epsDecrease = epsDecrease;
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

        protected final Value.SetValue seerHealingMultiplier = new Value.SetValue(6);
        private final List<Value> values = List.of(seerHealingMultiplier);

        public Value.SetValue getSeerHealingMultiplier() {
            return seerHealingMultiplier;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public static class EnergySeerData {

        private float energyUsed = 0;

        public float getEnergyUsed() {
            return energyUsed;
        }

        public void setEnergyUsed(float energyUsed) {
            this.energyUsed = energyUsed;
        }

    }

}
