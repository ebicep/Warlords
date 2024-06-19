package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Overheal;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.spiritguard.RepentanceBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import com.google.common.util.concurrent.AtomicDouble;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Repentance extends AbstractAbility implements BlueAbilityIcon, Duration {

    private float pool = 0;
    private int tickDuration = 240;
    private int poolDecay = 60;
    private float damageConvertPercent = 10;
    private float energyConvertPercent = 3.5f;

    public Repentance() {
        super("Repentance", 0, 0, 31.32f, 20);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Taking damage empowers your damaging abilities and melee hits, restoring health and energy based on ")
                               .append(Component.text("10%", NamedTextColor.RED))
                               .append(Component.text(" + "))
                               .append(Component.text(format(damageConvertPercent) + "%", NamedTextColor.RED))
                               .append(Component.text(" of the damage you've recently took. Lasts "))
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
                        float energyGain = (float) energyGained.get() / 10 / 20;
                        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Remembrance",
                                "REME",
                                Repentance.class,
                                new Repentance(),
                                wp,
                                CooldownTypes.BUFF,
                                cooldownManager1 -> {

                                },
                                5 * 20
                        ) {
                            @Override
                            public float addEnergyGainPerTick(float energyGainPerTick) {
                                return energyGainPerTick + energyGain;
                            }
                        });
                    }
                },
                tickDuration
        ) {
            @Override
            public boolean distinct() {
                return true;
            }

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
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
                energyGained.addAndGet(attacker.addEnergy(attacker, "Repentance", healthToAdd * (energyConvertPercent / 100f)));
                pool *= .5f;
            }
        });

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new RepentanceBranch(abilityTree, this);
    }

    @Override
    public void runEverySecond(@Nullable WarlordsEntity warlordsEntity) {
        if (pool > 0) {
            float newPool = pool * .8f - poolDecay;
            pool = Math.max(newPool, 0);
        }
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

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
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
}