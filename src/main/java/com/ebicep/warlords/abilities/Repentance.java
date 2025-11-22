package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.spiritguard.RepentanceBranch;
import com.ebicep.warlords.util.warlords.Utils;
import com.google.common.util.concurrent.AtomicDouble;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Repentance extends AbstractAbility implements BlueAbilityIcon, Duration, AbilityStats<Repentance, Repentance.RepentanceStats> {

    private final RepentanceStats stats = new RepentanceStats();
    private float pool = 0;
    private int tickDuration = 240;
    private int poolDecay = 60;
    private float damageConvertPercent = 10;
    private float energyConvertPercent = 3.5f;

    public Repentance() {
        super(AbstractAbilityBuilder.create("repentance").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.pool = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("pool"), float.class);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.poolDecay = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("poolDecay"), int.class);
        this.damageConvertPercent = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageConvertPercent"), float.class);
        this.energyConvertPercent = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("energyConvertPercent"), float.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "paladin.barrieroflight.impact", 2, 1.35f);
        EffectUtils.playCylinderAnimation(wp.getLocation(), 1, 255, 255, 255);
        pool += 2000;
        AtomicDouble energyGained = new AtomicDouble();
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "REPE",
                Repentance.class,
                new Repentance(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (pveMasterUpgrade2) {
                        //TODO message
                        float energyGain = (float) energyGained.get() / 3.3f / 20;
                        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Remembrance",
                                "REME",
                                Repentance.class,
                                new Repentance(),
                                wp,
                                CooldownTypes.BUFF,
                                cooldownManager1 -> {

                                },
                                8 * 20
                        ).addModifier(Modifier.ENERGY_GAIN_PER_TICK, energyGainPerTick -> energyGainPerTick.addAdditiveModifier("Remembrance", energyGain)));
                    }
                },
                tickDuration
        ) {
            @Override
            public boolean distinct() {
                return true;
            }

        }.addModifier(Modifier.ON_OUTGOING_DAMAGE, (event, currentDamageValue, isCrit) -> {
                    WarlordsEntity attacker = event.getSource();
                    int healthToAdd = (int) (pool * (damageConvertPercent / 100f)) + 10;
                    attacker.addInstance(InstanceBuilder
                            .healing()
                            .ability(Repentance.this)
                            .source(attacker)
                            .value(Math.min(500, healthToAdd))
                            .flag(InstanceFlags.CAN_OVERHEAL_SELF, pveMasterUpgrade2)
                    );
                    if (pveMasterUpgrade2) {
                        Overheal.giveOverHeal(wp, wp);
                    }
                    energyGained.addAndGet(attacker.addEnergy(attacker, name, healthToAdd * (energyConvertPercent / 100f)));
                    pool *= .5f;
                }
        ));
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Taking damage empowers your damaging abilities and melee hits, restoring health and energy based on ")
                                               .percent(10, NamedTextColor.RED)
                                               .text(" + ")
                                               .percent(damageConvertPercent, NamedTextColor.RED)
                                               .text(" of the damage you've recently took. Lasts ")
                                               .durationTicks(tickDuration)
                                               .text(".")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new RepentanceBranch(abilityTree, this);
    }

    @Override
    public void runEverySecond(@Nullable WarlordsEntity warlordsEntity) {
        if (pool > 0) {
            float newPool = pool * (pveMasterUpgrade ? .4f : .8f) - poolDecay;
            pool = Math.max(newPool, 0);
        }
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
    public RepentanceStats getAbilityStats() {
        return stats;
    }

    public float getDamageConvertPercent() {
        return damageConvertPercent;
    }

    public void setDamageConvertPercent(float damageConvertPercent) {
        this.damageConvertPercent = damageConvertPercent;
    }

    public void addToPool(float amount) {
        this.pool = Math.min(3000, pool + amount);
    }

    public int getPoolDecay() {
        return poolDecay;
    }

    public void setPoolDecay(int poolDecay) {
        this.poolDecay = poolDecay;
    }

    public float getEnergyConvertPercent() {
        return energyConvertPercent;
    }

    public void setEnergyConvertPercent(float energyConvertPercent) {
        this.energyConvertPercent = energyConvertPercent;
    }

    public static class RepentanceStats extends AbstractAbilityStats<Repentance, RepentanceStats> {

        @Override
        public Class<RepentanceStats> getClazz() {
            return RepentanceStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public RepentanceStats merge(RepentanceStats other, int multiplier) {
            RepentanceStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public RepentanceStats create() {
            return new RepentanceStats();
        }

    }

}
