package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.thunderlord.LightningRodBranch;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LightningRod extends AbstractAbility implements BlueAbilityIcon, Heals<LightningRod.HealingValues>, AbilityStats<LightningRod, LightningRod.LightningRodStats> {

    private final LightningRodStats stats = new LightningRodStats();
    private final HealingValues healingValues = new HealingValues();
    private int knockbackRadius = 5;
    private int energyRestore = 160;
    private float horizontalTotemProcRange;
    private float verticalTotemProcRange;
    private float magnitude;
    private float y;

    public LightningRod() {
        this(AbstractAbilityBuilder.create("lightningRod").pvp());
    }

    public LightningRod(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.knockbackRadius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("knockbackRadius"), int.class);
        this.energyRestore = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("energyRestore"), int.class);
        this.horizontalTotemProcRange = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("horizontalTotemProcRange"), float.class);
        this.verticalTotemProcRange = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("verticalTotemProcRange"), float.class);
        this.magnitude = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("magnitude"), float.class);
        this.y = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("y"), float.class);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Call down an energizing bolt of lightning upon yourself, restoring ")
                                               .percent(healingValues.healthRestore.getValue(), NamedTextColor.GREEN)
                                               .text(" health and ")
                                               .energy(energyRestore)
                                               .text(" and knocking all nearby enemies in a ")
                                               .blocks(knockbackRadius)
                                               .text(" radius back.")
                                               .build();
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        List<WarlordsEntity> hit = kbHealEnergy(wp, true);
        if (pveMasterUpgrade) {
            damageIncreaseOnUse(wp);
            new GameRunnable(wp.getGame()) {

                int bonusActivations = 0;

                @Override
                public void run() {
                    if (wp.isDead()) {
                        this.cancel();
                    }

                    if (bonusActivations++ < 3) {
                        kbHealEnergy(wp, false);
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(40, 40);
        } else if (pveMasterUpgrade2) {
            giveCallOfThunderEffect(wp, hit);
        }
        // pulsedamage
        List<CapacitorTotem.CapacitorTotemData> totems = AbstractTotem.getTotemsDownAndClose(
                wp,
                CapacitorTotem.CapacitorTotemData.class,
                horizontalTotemProcRange,
                verticalTotemProcRange
        );
        totems.forEach(data -> {
            ArmorStand totem = data.getArmorStand();
            Utils.playGlobalSound(totem.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
            wp.playSound(wp.getLocation(), "shaman.chainlightning.impact", 2, 1);
            data.proc();
            if (data.getTotem().isPveMasterUpgrade()) {
                data.setRadius(data.getRadius() + 0.5);
            }
        });
        return true;
    }

    public float getHorizontalTotemProcRange() {
        return horizontalTotemProcRange;
    }

    public void setHorizontalTotemProcRange(float horizontalTotemProcRange) {
        this.horizontalTotemProcRange = horizontalTotemProcRange;
    }

    public float getVerticalTotemProcRange() {
        return verticalTotemProcRange;
    }

    public void setVerticalTotemProcRange(float verticalTotemProcRange) {
        this.verticalTotemProcRange = verticalTotemProcRange;
    }

    private List<WarlordsEntity> kbHealEnergy(@Nonnull WarlordsEntity wp, boolean shouldHeal) {
        wp.addEnergy(wp, name, energyRestore);
        Utils.playGlobalSound(wp.getLocation(), "shaman.lightningrod.activation", 2, 1);
        FallingBlockWaveEffect.create(wp.getLocation(), knockbackRadius, 6, Material.ORANGE_TULIP);
        EffectUtils.strikeLightning(wp.getLocation(), true);
        if (shouldHeal) {
            wp.addInstance(InstanceBuilder.healing()
                    .ability(this)
                    .source(wp)
                    .value(wp.getMaxHealth() * (healingValues.healthRestore.getMultiplicativePercent()))
            );
        }
        List<WarlordsEntity> hit = PlayerFilter.entitiesAround(wp, knockbackRadius, knockbackRadius, knockbackRadius).aliveEnemiesOf(wp).toList();
        for (WarlordsEntity enemy : hit) {
            if (pveMasterUpgrade2) {
                enemy.setStunTicks(60);
            } else {
                final Location loc = enemy.getLocation();
                final Vector v = wp.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-magnitude).setY(y);
                enemy.setVelocity(name, v, false);
            }
        }
        return hit;
    }

    private void damageIncreaseOnUse(WarlordsEntity we) {
        we.addSpeedModifier(we, "Rod Speed", 20, 12 * 20);
        we.getCooldownManager().removeCooldown(LightningRod.class, false);
        we.getCooldownManager().addCooldown(new RegularCooldown<>(name, "ROD DMG", LightningRod.class, new LightningRod(), we, CooldownTypes.ABILITY, cooldownManager -> {
        }, 12 * 20
        ) {

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * 1.2f;
            }
        });
    }

    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
    }

    public void setY(float y) {
        this.y = y;
    }

    private void giveCallOfThunderEffect(WarlordsEntity from, List<WarlordsEntity> hit) {
        for (WarlordsEntity warlordsEntity : hit) {
            ChainLightning.giveShockedEffect(from, warlordsEntity, ChainLightning.class, new ChainLightning());
        }
        from.getCooldownManager().removeCooldownByName("Call of Thunder Buff");
        List<FloatModifiable.FloatModifier> modifiers;
        if (pveMasterUpgrade2) {
            modifiers = from.getAbilitiesMatching(ChainLightning.class).stream().map(ability -> ability.getEnergyCost().addAdditiveModifier("Call of Thunder Buff", -25)).toList();
        } else {
            modifiers = Collections.emptyList();
        }
        from.getCooldownManager().addCooldown(new RegularCooldown<>("Call of Thunder Buff", "THUN", LightningRod.class, null, from, CooldownTypes.BUFF, cooldownManager -> {
        }, cooldownManager -> {
            modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
        }, 10 * 20
        ) {

            @Override
            public float addEnergyGainPerTick(float energyGainPerTick) {
                return energyGainPerTick + 15 / 20f;
            }
        });
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new LightningRodBranch(abilityTree, this);
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public LightningRodStats getAbilityStats() {
        return stats;
    }

    public int getEnergyRestore() {
        return energyRestore;
    }

    public void setEnergyRestore(int energyRestore) {
        this.energyRestore = energyRestore;
    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.SetValue healthRestore = new Value.SetValue(30);

        private List<Value> values = List.of(healthRestore);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.healthRestore = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("healthRestore"), Value.SetValue.class);
            this.values = List.of(healthRestore);
        }

        public Value.SetValue getHealthRestore() {
            return healthRestore;
        }

    }

    public static class LightningRodStats extends AbstractAbilityStats<LightningRod, LightningRodStats> {

        @Override
        public Class<LightningRodStats> getClazz() {
            return LightningRodStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public LightningRodStats merge(LightningRodStats other, int multiplier) {
            LightningRodStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public LightningRodStats create() {
            return new LightningRodStats();
        }

    }

}
