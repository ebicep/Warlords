package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.aquamancer.WaterBreathBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WaterBreath extends AbstractAbility implements RedAbilityIcon, CanReduceCooldowns, Heals<WaterBreath.HealingValues>, AbilityStats<WaterBreath, WaterBreath.WaterBreathStats> {

    private final WaterBreathStats stats = new WaterBreathStats();
    private final HealingValues healingValues = new HealingValues();
    private int maxAnimationTime = 12;
    private int maxAnimationEffects = 4;
    private float hitbox = 10;
    private double velocity = 1.1;

    public WaterBreath() {
        super(AbstractAbilityBuilder.create("waterBreath").pvp());
    }

    public WaterBreath(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.maxAnimationTime = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxAnimationTime"), int.class);
        this.maxAnimationEffects = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxAnimationEffects"), int.class);
        this.hitbox = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hitbox"), float.class);
        this.velocity = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("velocity"), float.class);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Breathe water in a cone in front of you, knocking back enemies, cleansing all ")
                                               .text("de-buffs", NamedTextColor.DARK_RED)
                                               .text(" and restoring ")
                                               .heal(healingValues.breathHealing)
                                               .text(" health to yourself and all allies hit.")
                                               .emptyLine()
                                               .text("Water Breath can overheal allies for up to ")
                                               .percent(10, NamedTextColor.GREEN)
                                               .text(" of their max health as bonus health for ")
                                               .durationSeconds(Overheal.OVERHEAL_DURATION)
                                               .text(".")
                                               .build();
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "mage.waterbreath.activation", 2, 1);
        EffectUtils.displayParticle(Particle.HEART, wp.getLocation().add(0, 0.7, 0), 2, 0.6, 0.6, 0.6, 1);
        Location playerLoc = new LocationBuilder(wp.getLocation()).pitch(0).add(0, 1.7, 0);
        EffectUtils.playSpiralAnimation(
                wp,
                playerLoc,
                maxAnimationEffects,
                maxAnimationTime,
                (center, animationTimer) -> {
                },
                Particle.DRIPPING_WATER,
                Particle.ENCHANT,
                Particle.HAPPY_VILLAGER
        );
        if (pveMasterUpgrade2) {
            giveBreathHitBuff(wp, wp);
        }
        int previousDebuffsRemoved = stats.debuffsRemoved;
        stats.debuffsRemoved += wp.getCooldownManager().removeDebuffCooldowns();
        wp.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(healingValues.breathHealing));
        Location playerEyeLoc = new LocationBuilder(wp.getLocation()).pitch(0).backward(1);
        Vector viewDirection = playerLoc.getDirection();
        for (WarlordsEntity breathTarget : PlayerFilter.entitiesAroundRectangle(playerLoc, hitbox - 2.5, hitbox, hitbox - 2.5).excluding(wp).isAlive()) {
            Vector direction = breathTarget.getLocation().subtract(playerEyeLoc).toVector().normalize();
            if (!(viewDirection.dot(direction) > .68)) {
                continue;
            }
            CooldownManager breathTargetCooldownManager = breathTarget.getCooldownManager();
            if (wp.isTeammate(breathTarget)) {
                stats.targetsHealed++;
                stats.debuffsRemoved += breathTargetCooldownManager.removeDebuffCooldowns();
                breathTarget.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(healingValues.breathHealing).flags(InstanceFlags.CAN_OVERHEAL_OTHERS));
                Overheal.giveOverHeal(wp, breathTarget);
                if (pveMasterUpgrade) {
                    regenOnHit(wp, breathTarget);
                }
                if (pveMasterUpgrade2) {
                    giveBreathHitBuff(wp, breathTarget);
                }
            } else {
                final Location loc = breathTarget.getLocation();
                final Vector v = wp.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-velocity).setY(0.2);
                breathTarget.setVelocity(name, v, false);
            }
        }
        int totalDebuffsRemoved = stats.debuffsRemoved - previousDebuffsRemoved;
        if (totalDebuffsRemoved >= 7) {
            ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.CLEANSING_RITUAL);
        }
        return true;
    }

    private void regenOnHit(WarlordsEntity giver, WarlordsEntity hit) {
        boolean hasPreviousCooldown = hit.getCooldownManager().hasCooldown(WaterBreath.class);
        hit.getCooldownManager().removeCooldown(WaterBreath.class, false);
        hit.getCooldownManager().addRegularCooldown(
                name,
                "BREATH RGN",
                WaterBreath.class,
                new WaterBreath(),
                giver,
                CooldownTypes.ABILITY,
                cooldownManager -> {},
                5 * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksLeft % 20 == 0) {
                        float healing = hit.getMaxHealth() * 0.02f;
                        hit.addInstance(InstanceBuilder
                                .healing()
                                .ability(this)
                                .source(giver)
                                .value(healing)
                                .flags(InstanceFlags.CAN_OVERHEAL_OTHERS)
                        );
                    }
                })
        );
        if (!hasPreviousCooldown) {
            hit.getSpec().decreaseAllCooldownTimersBy(1.5f);
        }
    }

    private static void giveBreathHitBuff(@Nonnull WarlordsEntity wp, WarlordsEntity breathTarget) {
        CooldownManager breathTargetCooldownManager = breathTarget.getCooldownManager();
        breathTargetCooldownManager.removeCooldownByName("Bubble Blessing");
        AbstractCooldown<WaterBreath> cooldown = new RegularCooldown<>(
                "Bubble Blessing",
                "BUBBLE",
                WaterBreath.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                4 * 20,
                Collections.singletonList((cooldown1, ticksLeft, ticksElapsed) -> {
                    if (ticksLeft % 5 == 0) {
                        EffectUtils.displayParticle(Particle.BUBBLE, breathTarget.getLocation().add(0, 1.25, 0), 5, 0.4f, 0.4f, 0.4f, 0);
                    }
                })
        ).addModifier(
                Modifier.ON_OUTGOING_DAMAGE,
                (event, currentDamageValue, isCrit) -> {
                    if (event.getCause().equals("Bubble Blessing")) {
                        return;
                    }
                    if (event.getFlags().contains(InstanceFlags.DOT)) {
                        return;
                    }
                    if (ThreadLocalRandom.current().nextDouble() < .35) {
                        WarlordsEntity enemy = event.getWarlordsEntity();
                        enemy.addInstance(InstanceBuilder
                                .damage()
                                .cause("Bubble Blessing")
                                .min(372)
                                .max(441)
                                .source(breathTarget)
                        );
                        Utils.playGlobalSound(enemy.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, 1, 1);
                    }
                }
        );
        breathTarget.addKnockbackModifier(wp, "Water Breath Master", -100, cooldown);
        breathTargetCooldownManager.addCooldown(cooldown);
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new WaterBreathBranch(abilityTree, this);
    }

    @Override
    public boolean canReduceCooldowns() {
        return pveMasterUpgrade;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public WaterBreathStats getAbilityStats() {
        return stats;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public int getMaxAnimationTime() {
        return maxAnimationTime;
    }

    public void setMaxAnimationTime(int maxAnimationTime) {
        this.maxAnimationTime = maxAnimationTime;
    }

    public float getHitbox() {
        return hitbox;
    }

    public void setHitbox(float hitbox) {
        this.hitbox = hitbox;
    }

    public void setMaxAnimationEffects(int maxAnimationEffects) {
        this.maxAnimationEffects = maxAnimationEffects;
    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.RangedValueCritable breathHealing = new Value.RangedValueCritable(536, 743, 25, 175);

        private List<Value> values = List.of(breathHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.breathHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("breathHealing"),
                    Value.RangedValueCritable.class
            );
            this.values = List.of(breathHealing);
        }

        public Value.RangedValueCritable getBreathHealing() {
            return breathHealing;
        }

    }

    public static class WaterBreathStats extends AbstractAbilityStats<WaterBreath, WaterBreathStats> {

        @Field("targets_healed")
        private int targetsHealed = 0;

        @Field("debuffs_removed")
        private int debuffsRemoved = 0;

        @Override
        public Class<WaterBreathStats> getClazz() {
            return WaterBreathStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Healed", targetsHealed));
            statsDisplay.add(new AbilityStatDisplay("Debuffs Removed", debuffsRemoved));
            return statsDisplay;
        }

        @Override
        public WaterBreathStats merge(WaterBreathStats other, int multiplier) {
            WaterBreathStats stats = super.merge(other, multiplier);
            stats.targetsHealed = this.targetsHealed + other.targetsHealed * multiplier;
            stats.debuffsRemoved = this.debuffsRemoved + other.debuffsRemoved * multiplier;
            return stats;
        }

        @Override
        public WaterBreathStats create() {
            return new WaterBreathStats();
        }

    }

}
