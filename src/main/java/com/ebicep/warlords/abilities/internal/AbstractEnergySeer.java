package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.EnergySeerConjurer;
import com.ebicep.warlords.abilities.EnergySeerLuminary;
import com.ebicep.warlords.abilities.EnergySeerSentinel;
import com.ebicep.warlords.abilities.FortifyingHex;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsEnergyUseEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class AbstractEnergySeer<T extends AbstractEnergySeer.EnergySeerData> extends AbstractAbility implements PurpleAbilityIcon, Duration, Heals<AbstractEnergySeer.HealingValues>, AbilityStats<AbstractEnergySeer, AbstractEnergySeer.AbstractEnergySeerStats> {

    protected int tickDuration = 100;
    protected int postEffectTickDuration = 100;
    protected int energyRestore = 130;
    protected int epsDecrease = 10;
    protected int speedBuff = 30;
    private final HealingValues healingValues = new HealingValues();
    private final AbstractEnergySeerStats stats = new AbstractEnergySeerStats();

    public AbstractEnergySeer(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.postEffectTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("postEffectTickDuration"), int.class);
        this.energyRestore = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("energyRestore"), int.class);
        this.epsDecrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("epsDecrease"), int.class);
        this.speedBuff = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speedBuff"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "arcanist.energyseer.activation", 2, 0.9f);
        for (int i = 0; i < 20; i++) {
            EffectUtils.displayParticle(Particle.SOUL_FIRE_FLAME, wp.getLocation(), 3, 0.3, 0.1, 0.3, 0.1);
        }

        wp.addEnergy(wp, name, energyRestore);

        T data = getDataObject();
        RegularCooldown<T> cd = new RegularCooldown<>(
                name,
                "SEER",
                getDataClass(),
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    onEnd(wp, data);
                },
                cooldownManager -> {
                    onEndForce(wp, data);
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
                        stats.timesHealed++;
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
                        if (cooldown.getCooldownObject() instanceof FortifyingHex.FortifyingHexData fortifyingHex) {
                            fortifyingHex.setDamageReduction(fortifyingHex.getDamageReduction() + energySeerSentinel.getDamageResistance());
                        }
                    }
                };
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (inPve && AbstractEnergySeer.this instanceof EnergySeerConjurer energySeerConjurer) {
                    return currentDamageValue * convertToMultiplicationDecimal(energySeerConjurer.getDamageIncrease());
                }
                return currentDamageValue;
            }
        };
        if (inPve && AbstractEnergySeer.this instanceof EnergySeerLuminary energySeerLuminary) {
            cd.addModifier(Modifier.HEALING_MODIFY_SELF, (event, currentHealValue) -> {
                        currentHealValue.addMultiplicativeModifierMult(name, convertToMultiplicationDecimal(energySeerLuminary.getHealingIncrease()));
                    }
            );
        }
        wp.getCooldownManager().addCooldown(cd);
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        AbilityDescriptionBuilder builder = AbilityDescriptionBuilder.create("");
        if (inPve) {
            builder.append(getBonus()).text(", gain ");
        } else {
            builder.text("Gain ");
        }
        description = builder
                .energy(energyRestore)
                .text(" and heal for ")
                .heal(healingValues.seerHealingMultiplier, aFloat -> aFloat * 100)
                .text(" of the energy expended for the next ")
                .durationTicks(tickDuration)
                .text(". When Energy Seer ends, lose ")
                .energy(epsDecrease)
                .text(" per second and gain")
                .percent(speedBuff, NamedTextColor.YELLOW)
                .text(" speed for ")
                .durationTicks(postEffectTickDuration)
                .text(".")
                .build();
    }

    public abstract TextComponent getBonus();

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
        ).addModifier(Modifier.ENERGY_GAIN_PER_TICK, energyGainPerTick -> energyGainPerTick.addAdditiveModifier(name, -epsDecrease / 20f)));
    }

    protected void onEndForce(WarlordsEntity wp, T data) {

    }

    protected void onEnergyUsed(WarlordsEntity wp, WarlordsEnergyUseEvent.Post event, T cooldownObjet) {
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

    @Override
    public AbstractEnergySeerStats getAbilityStats() {
        return stats;
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

    public static class HealingValues implements Value.ValueHolder {

        private Value.SetValue seerHealingMultiplier = new Value.SetValue(5);
        private List<Value> values = List.of(seerHealingMultiplier);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.seerHealingMultiplier = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("seerHealingMultiplier"),
                    Value.SetValue.class
            );
            this.values = List.of(seerHealingMultiplier);
        }

        public Value.SetValue getSeerHealingMultiplier() {
            return seerHealingMultiplier;
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

    public static class AbstractEnergySeerStats extends AbstractAbilityStats<AbstractEnergySeer, AbstractEnergySeerStats> {

        @Field("times_healed")
        private int timesHealed = 0;

        @Override
        public Class<AbstractEnergySeerStats> getClazz() {
            return AbstractEnergySeerStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Times Healed", timesHealed));
            return statsDisplay;
        }

        @Override
        public AbstractEnergySeerStats merge(AbstractEnergySeerStats other, int multiplier) {
            AbstractEnergySeerStats stats = super.merge(other, multiplier);
            stats.timesHealed = this.timesHealed + other.timesHealed * multiplier;
            return stats;
        }

        @Override
        public AbstractEnergySeerStats create() {
            return new AbstractEnergySeerStats();
        }

    }

}
