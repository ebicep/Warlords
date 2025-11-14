package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.berserker.WoundingStrikeBranchBerserker;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WoundingStrikeBerserker extends AbstractStrike<WoundingStrikeBerserker, WoundingStrikeBerserker.WoundingStrikeBerserkerStats> implements Damages<WoundingStrikeBerserker.DamageValues> {

    private final WoundingStrikeBerserkerStats stats = new WoundingStrikeBerserkerStats();
    private final DamageValues damageValues = new DamageValues();
    private FloatModifiable wounding = new FloatModifiable(40);
    private int woundingTickDuration = 60;

    public WoundingStrikeBerserker() {
        super(AbstractAbilityBuilder.create("woundingStrikeBerserker").pvp());
    }

    public WoundingStrikeBerserker(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.wounding = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("wounding"), float.class));
        this.woundingTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("woundingTickDuration"), int.class);
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "warrior.mortalstrike.impact", 2, 1);
        randomHitEffect(location, 7, 255, 0, 0);
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer) {
        float lustDamageBoost = wp.getCooldownManager().hasCooldown(BloodLust.BloodLustData.class) ? pveMasterUpgrade ? 2 : pveMasterUpgrade2 ? 1.25f : 1 : 1;
        nearPlayer.addInstance(InstanceBuilder.damage()
                                              .ability(this)
                                              .source(wp)
                                              .min(damageValues.strikeDamage.getMinValue() * lustDamageBoost)
                                              .max(damageValues.strikeDamage.getMaxValue() * lustDamageBoost)
                                              .crit(damageValues.strikeDamage)).ifPresent(finalEvent -> onFinalEvent(wp, nearPlayer, finalEvent));
        if (pveMasterUpgrade2) {
            additionalHit(2, wp, nearPlayer, warlordsEntity -> {
                        warlordsEntity.addInstance(InstanceBuilder.damage()
                                                                  .ability(this)
                                                                  .source(wp)
                                                                  .min(damageValues.strikeDamage.getMinValue() * lustDamageBoost)
                                                                  .max(damageValues.strikeDamage.getMaxValue() * lustDamageBoost)
                                                                  .crit(damageValues.strikeDamage)).ifPresent(finalEvent -> onFinalEvent(wp, finalEvent.getWarlordsEntity(), finalEvent));
                    }
            );
        }
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Strike the targeted enemy player, causing ")
                                               .damage(damageValues.strikeDamage)
                                               .text(" damage and ")
                                               .text("wounding", NamedTextColor.RED)
                                               .text(" them for ")
                                               .durationTicks(woundingTickDuration)
                                               .text(", making them receive ")
                                               .percent(wounding.getCalculatedValue(), NamedTextColor.RED)
                                               .text(" less healing.")
                                               .build();
    }

    @Override
    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        wounding.tick();
        super.runEveryTick(warlordsEntity);
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public WoundingStrikeBerserkerStats getAbilityStats() {
        return stats;
    }

    private void onFinalEvent(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer, WarlordsDamageHealingFinalEvent finalEvent) {
        if (finalEvent.isDead()) {
            return;
        }
        if (pveMasterUpgrade) {
            bleedOnHit(wp, nearPlayer);
            return;
        }
        WoundingCooldown.addWoundingCooldown(
                nearPlayer,
                name,
                wp,
                wounding.getCalculatedValue(),
                woundingTickDuration
        );
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new WoundingStrikeBranchBerserker(abilityTree, this);
    }

    private void bleedOnHit(WarlordsEntity giver, WarlordsEntity hit) {
        hit.getCooldownManager().removePreviousWounding();
        hit.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Bleed",
                "BLEED",
                WoundingStrikeBerserker.class,
                null,
                giver,
                CooldownTypes.LOW_LEVEL_DEBUFF,
                cooldownManager -> {},
                woundingTickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksLeft % 20 == 0) {
                       float healthDamage = hit.getMaxHealth() * 0.005f;
                       healthDamage = DamageCheck.clamp(healthDamage);
                       hit.addInstance(InstanceBuilder
                               .damage()
                               .cause("Bleed")
                               .source(giver)
                               .value(healthDamage)
                               .flags(InstanceFlags.DOT, InstanceFlags.IGNORE_SOURCE_DAMAGE_BOOST)
                       );
                    }
                })
        ) {
           @Override
           public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
               return currentHealValue * .2f;
           }
        });
    }

    public FloatModifiable getWounding() {
        return wounding;
    }

    public int getWoundingTickDuration() {
        return woundingTickDuration;
    }

    public void setWoundingTickDuration(int woundingTickDuration) {
        this.woundingTickDuration = woundingTickDuration;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable strikeDamage = new Value.RangedValueCritable(497, 632, 20, 175);

        private List<Value> values = List.of(strikeDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.strikeDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("strikeDamage"), Value.RangedValueCritable.class);
            this.values = List.of(strikeDamage);
        }

        public Value.RangedValueCritable getStrikeDamage() {
            return strikeDamage;
        }

    }

    public static class WoundingStrikeBerserkerStats extends AbstractStrikeStats<WoundingStrikeBerserker, WoundingStrikeBerserkerStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public WoundingStrikeBerserkerStats merge(WoundingStrikeBerserkerStats other, int multiplier) {
            WoundingStrikeBerserkerStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<WoundingStrikeBerserkerStats> getClazz() {
            return WoundingStrikeBerserkerStats.class;
        }

        @Override
        public WoundingStrikeBerserkerStats create() {
            return new WoundingStrikeBerserkerStats();
        }

    }

}
