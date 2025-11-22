package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifier;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifierBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.thunderlord.WindfuryBranch;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class WindfuryWeapon extends AbstractAbility implements PurpleAbilityIcon, Duration, AbilityStats<WindfuryWeapon, WindfuryWeapon.WindfuryWeaponStats> {

    private final WindfuryWeaponStats stats = new WindfuryWeaponStats();
    private int tickDuration = 160;
    private float procChance = 35;
    private int maxHits = 2;
    private float weaponDamage = 135;
    private int guaranteedHits = 1;

    public WindfuryWeapon() {
        super(AbstractAbilityBuilder.create("windfuryWeapon").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.procChance = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("procChance"), float.class);
        this.maxHits = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxHits"), int.class);
        this.weaponDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("weaponDamage"), float.class);
        this.guaranteedHits = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("guaranteedHits"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "shaman.windfuryweapon.activation", 2, 1);
        wp.getCooldownManager().removeCooldown(WindfuryWeapon.class, false);
        MotionModifier shreddingFurySpeed = new MotionModifierBuilder().setFrom(wp).setName("Shredding Fury").setModifier(0).setDuration(Integer.MAX_VALUE).build();
        wp.addSpeedModifier(shreddingFurySpeed);
        AtomicInteger procs = new AtomicInteger(0);
        final int[] guaranteedHitsLeft = {guaranteedHits};
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "FURY",
                WindfuryWeapon.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    shreddingFurySpeed.setTicksLeft(0);
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        EffectUtils.displayParticle(Particle.CRIT, wp.getLocation().add(0, 1.2, 0), 3, 0.2, 0, 0.2, 0.1);
                    }
                })
        ).addModifier(Modifier.DAMAGE_ON_END_ATTACKER, (event, currentDamageValue, isCrit) -> {
                    if (!event.getCause().isEmpty() || event.getFlags().contains(InstanceFlags.RECURSIVE)) {
                        return;
                    }
                    WarlordsEntity victim = event.getWarlordsEntity();
                    WarlordsEntity attacker = event.getSource();
                    double windfuryActivate = ThreadLocalRandom.current().nextDouble(100);
                    if (guaranteedHitsLeft[0] > 0) {
                        guaranteedHitsLeft[0]--;
                        windfuryActivate = 0;
                    }
                    if (!(windfuryActivate < procChance)) {
                        return;
                    }
                    procs.incrementAndGet();
                    stats.timesProcd++;
                    new GameRunnable(victim.getGame()) {

                        final float minDamage = wp instanceof WarlordsPlayer warlordsPlayer && warlordsPlayer.getWeapon() != null ?
                                                warlordsPlayer.getWeapon().getMeleeDamageMin() :
                                                132;

                        final float maxDamage = wp instanceof WarlordsPlayer warlordsPlayer && warlordsPlayer.getWeapon() != null ?
                                                warlordsPlayer.getWeapon().getMeleeDamageMax() :
                                                179;

                        int counter = 0;

                        @Override
                        public void run() {
                            Utils.playGlobalSound(victim.getLocation(), "shaman.windfuryweapon.impact", 2, 1);
                            victim.addInstance(InstanceBuilder.damage()
                                                              .ability(WindfuryWeapon.this)
                                                              .source(attacker)
                                                              .min(minDamage * (weaponDamage / 100f))
                                                              .max(maxDamage * (weaponDamage / 100f))
                                                              .critChance(25)
                                                              .critMultiplier(200));
                            if (pveMasterUpgrade) {
                                victim.setDamageResistance(victim.getSpec().getDamageResistance() - 2);
                                if (victim instanceof WarlordsNPC npc) {
                                    npc.setDamageResistance(npc.getSpec().getDamageResistance() - 2);
                                }
                            }
                            counter++;
                            if (counter == maxHits) {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(3, 3);
                    if (pveMasterUpgrade2 && procs.get() <= 10) {
                        shreddingFurySpeed.setModifier(shreddingFurySpeed.getModifier() + 2.5f);
                    }
                }
        ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                    if (pveMasterUpgrade2) {
                        currentDamageValue.addMultiplicativeModifierMult(name, (100 - Math.min(15, procs.get() * 2.5f)) / 100f);
                    }
                }
        ));
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Imbue your weapon with the power of the wind, causing each of your melee attacks to have a ")
                                               .percent(procChance, NamedTextColor.BLUE)
                                               .text(" chance to hit ")
                                               .text(maxHits, NamedTextColor.BLUE)
                                               .text(" additional times for ")
                                               .percent(weaponDamage, NamedTextColor.RED)
                                               .text(" weapon damage. Lasts ")
                                               .durationTicks(tickDuration)
                                               .text(".")
                                               .emptyLine()
                                               .text("The first ")
                                               .text(guaranteedHits, NamedTextColor.BLUE)
                                               .text(" hits is guaranteed to activate Windfury.")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new WindfuryBranch(abilityTree, this);
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
    public WindfuryWeaponStats getAbilityStats() {
        return stats;
    }

    public int getGuaranteedHits() {
        return guaranteedHits;
    }

    public void setGuaranteedHits(int guaranteedHits) {
        this.guaranteedHits = guaranteedHits;
    }

    public float getProcChance() {
        return procChance;
    }

    public void setProcChance(float procChance) {
        this.procChance = procChance;
    }

    public int getMaxHits() {
        return maxHits;
    }

    public void setMaxHits(int maxHits) {
        this.maxHits = maxHits;
    }

    public float getWeaponDamage() {
        return weaponDamage;
    }

    public void setWeaponDamage(float weaponDamage) {
        this.weaponDamage = weaponDamage;
    }

    public static class WindfuryWeaponStats extends AbstractAbilityStats<WindfuryWeapon, WindfuryWeaponStats> {

        @Field("times_procd")
        private int timesProcd = 0;

        @Override
        public Class<WindfuryWeaponStats> getClazz() {
            return WindfuryWeaponStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Times Proc'd", timesProcd));
            return statsDisplay;
        }

        @Override
        public WindfuryWeaponStats merge(WindfuryWeaponStats other, int multiplier) {
            WindfuryWeaponStats stats = super.merge(other, multiplier);
            stats.timesProcd = this.timesProcd + other.timesProcd * multiplier;
            return stats;
        }

        @Override
        public WindfuryWeaponStats create() {
            return new WindfuryWeaponStats();
        }

    }

}
