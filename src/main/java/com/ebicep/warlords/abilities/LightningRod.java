package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
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

    private final int knockbackRadius = 5;
    private final HealingValues healingValues = new HealingValues();
    private final LightningRodStats stats = new LightningRodStats();
    private int energyRestore = 160;

    public LightningRod() {
        this(31.5f, 0);
    }

    public LightningRod(float cooldown, float startCooldown) {
        super("Lightning Rod", cooldown, 0, startCooldown);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Call down an energizing bolt of lightning upon yourself, restoring ")
                .percent(healingValues.healthRestore.getValue(), NamedTextColor.GREEN)
                .text(" health and ")
                .energy(energyRestore)
                .text(" and knocking all nearby enemies in a ")
                .blocks(knockbackRadius)
                .text(" radius back.")
                .build();

    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        List<WarlordsEntity> hit = kbHealEnergy(wp);

        if (pveMasterUpgrade) {
            damageIncreaseOnUse(wp);
            new GameRunnable(wp.getGame()) {
                int bonusActivations = 0;

                @Override
                public void run() {
                    if (bonusActivations++ < 2) {
                        kbHealEnergy(wp);
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 40);
        } else if (pveMasterUpgrade2) {
            giveCallOfThunderEffect(wp, hit);
        }

        // pulsedamage
        List<CapacitorTotem.CapacitorTotemData> totems = AbstractTotem.getTotemsDownAndClose(wp, wp.getEntity(), CapacitorTotem.CapacitorTotemData.class);
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

    private List<WarlordsEntity> kbHealEnergy(@Nonnull WarlordsEntity wp) {
        wp.addEnergy(wp, name, energyRestore);
        Utils.playGlobalSound(wp.getLocation(), "shaman.lightningrod.activation", 2, 1);
        new FallingBlockWaveEffect(wp.getLocation(), knockbackRadius, 1, Material.ORANGE_TULIP).play();
        wp.getWorld().spigot().strikeLightningEffect(wp.getLocation(), true);
        wp.addInstance(InstanceBuilder
                .healing()
                .ability(this)
                .source(wp)
                .value(wp.getMaxHealth() * (healingValues.healthRestore.getMultiplicativePercent()))
        );

        List<WarlordsEntity> hit = PlayerFilter
                .entitiesAround(wp, knockbackRadius, knockbackRadius, knockbackRadius)
                .aliveEnemiesOf(wp)
                .toList();
        for (WarlordsEntity enemy : hit) {
            if (pveMasterUpgrade2) {
                if (enemy instanceof WarlordsNPC warlordsNPC) {
                    warlordsNPC.setStunTicks(60);
                }
            } else {
                final Location loc = enemy.getLocation();
                final Vector v = wp.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.5).setY(0.35);
                enemy.setVelocity(name, v, false);
            }
        }
        return hit;
    }

    private void damageIncreaseOnUse(WarlordsEntity we) {
        we.addSpeedModifier(we, "Rod Speed", 20, 12 * 20, "BASE");
        we.getCooldownManager().removeCooldown(LightningRod.class, false);
        we.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "ROD DMG",
                LightningRod.class,
                new LightningRod(),
                we,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                12 * 20
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * 1.2f;
            }
        });
    }

    private void giveCallOfThunderEffect(WarlordsEntity from, List<WarlordsEntity> hit) {
        LightningRod tempRod = new LightningRod();
        for (WarlordsEntity warlordsEntity : hit) {
            ChainLightning.giveShockedEffect(
                    from,
                    warlordsEntity,
                    ChainLightning.class,
                    new ChainLightning()
            );
        }
        from.getCooldownManager().removeCooldownByName("Call of Thunder Buff");
        List<FloatModifiable.FloatModifier> modifiers;
        if (pveMasterUpgrade2) {
            modifiers = from.getAbilitiesMatching(ChainLightning.class)
                            .stream()
                            .map(ability -> ability.getCooldown().addAdditiveModifier("Call of Thunder Buff", -25))
                            .toList();
        } else {
            modifiers = Collections.emptyList();
        }
        from.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Call of Thunder Buff",
                "THUN",
                LightningRod.class,
                tempRod,
                from,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                cooldownManager -> {
                    modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                },
                8 * 20
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

    public int getEnergyRestore() {
        return energyRestore;
    }

    public void setEnergyRestore(int energyRestore) {
        this.energyRestore = energyRestore;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public LightningRodStats getAbilityStats() {
        return stats;
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.SetValue healthRestore = new Value.SetValue(30);
        private final List<Value> values = List.of(healthRestore);

        public Value.SetValue getHealthRestore() {
            return healthRestore;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public static class LightningRodStats extends AbstractAbilityStats<LightningRod, LightningRodStats> {

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
        public Class<LightningRodStats> getClazz() {
            return LightningRodStats.class;
        }

        @Override
        public LightningRodStats create() {
            return new LightningRodStats();
        }
    }
}