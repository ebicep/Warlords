package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.LinkedCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.defender.WoundingStrikeBranchDefender;
import com.ebicep.warlords.util.warlords.PlayerFilter;
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
import java.util.Set;
import java.util.stream.Collectors;

public class WoundingStrikeDefender extends AbstractStrike<WoundingStrikeDefender, WoundingStrikeDefender.WoundingStrikeDefenderStats> implements Damages<WoundingStrikeDefender.DamageValues> {

    private final WoundingStrikeDefenderStats stats = new WoundingStrikeDefenderStats();
    private final DamageValues damageValues = new DamageValues();
    private FloatModifiable wounding = new FloatModifiable(25);
    private int woundingTickDuration = 60;

    public WoundingStrikeDefender() {
        super(AbstractAbilityBuilder.create("woundingStrikeDefender").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.wounding = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("wounding"), float.class));
        this.woundingTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("woundingDurationInTicks"), int.class);
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "warrior.mortalstrike.impact", 2, 1);
        randomHitEffect(location, 7, 255, 0, 0);
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer) {
        nearPlayer.addInstance(InstanceBuilder.damage()
                                              .ability(this)
                                              .source(wp)
                                              .value(damageValues.strikeDamage)
                                              .flag(InstanceFlags.PIERCE, nearPlayer instanceof WarlordsNPC warlordsNPC && !(warlordsNPC.getMob() instanceof BossLike)))
                  .ifPresent(event -> onFinalEvent(wp, nearPlayer, event));
        if (pveMasterUpgrade2) {
            additionalHit(2, wp, nearPlayer, warlordsEntity -> {
                        warlordsEntity.addInstance(InstanceBuilder
                                .damage()
                                .ability(this)
                                .source(wp)
                                .value(damageValues.strikeDamage));
                    }
            );
            if (nearPlayer instanceof WarlordsNPC) {
                ((WarlordsNPC) nearPlayer).getMob().setTarget(wp);
            }
            nearPlayer.getCooldownManager().removeCooldown(WoundingStrikeDefender.class, false);
            nearPlayer.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    null,
                    WoundingStrikeDefender.class,
                    null,
                    wp,
                    CooldownTypes.HIGH_LEVEL_DEBUFF,
                    cooldownManager -> {

                    },
                    4 * 20
            ).addModifier(Modifier.DAMAGE_BEFORE_INTERVENE_ATTACKER, (event, currentDamageValue) -> {
                        currentDamageValue.addMultiplicativeModifierMult(name, 0.85f);
                    }
            ));
            new CooldownFilter<>(wp, RegularCooldown.class)
                    .filter(cd -> cd.getCooldownClass().equals(LastStand.LastStandData.class))
                    .forEach(cd -> cd.setTicksLeft(cd.getTicksLeft() + 10));
        }
        return true;
    }

    private void onFinalEvent(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer, WarlordsDamageHealingFinalEvent event) {
        if (event.isDead()) {
            return;
        }
        if (event.isCrit() && pveMasterUpgrade) {
            damageReductionOnCrit(wp, nearPlayer);
        }
        WoundingCooldown.addWoundingCooldown(
                nearPlayer,
                name,
                wp,
                wounding.getCalculatedValue(),
                woundingTickDuration
        );
    }

    private void damageReductionOnCrit(WarlordsEntity we, WarlordsEntity nearPlayer) {
        Set<WarlordsEntity> teammates = PlayerFilter.entitiesAround(nearPlayer, 10, 10, 10).aliveTeammatesOfExcludingSelf(we).stream().collect(Collectors.toSet());
        LinkedCooldown<?> linkedCooldown = new LinkedCooldown<>(name + " Resistance",
                "STRIKE RES",
                WoundingStrikeDefender.class,
                null,
                we,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                cooldownManager -> {
                },
                5 * 20,
                Collections.emptyList(),
                teammates
        );
        linkedCooldown.addModifier(Modifier.DAMAGE_AFTER_INTERVENE_SELF, (event, currentDamageValue) -> {
                    currentDamageValue.addMultiplicativeModifierMult(name, .7f);
                }
        );
        we.getCooldownManager().removeCooldownByName(name + " Resistance");
        we.getCooldownManager().addCooldown(linkedCooldown);
        for (WarlordsEntity teammate : teammates) {
            teammate.getCooldownManager().removeCooldownByName(name + " Resistance");
            teammate.getCooldownManager().addCooldown(linkedCooldown);
        }
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Strike the targeted enemy player, causing")
                                               .damage(damageValues.strikeDamage)
                                               .text(" damage and ")
                                               .text("wounding", NamedTextColor.RED)
                                               .text(" them for ")
                                               .durationTicks(woundingTickDuration)
                                               .text(", making them receive ")
                                               .percent(wounding, NamedTextColor.RED)
                                               .text(" less healing.")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new WoundingStrikeBranchDefender(abilityTree, this);
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
    public WoundingStrikeDefenderStats getAbilityStats() {
        return stats;
    }

    public FloatModifiable getWounding() {
        return wounding;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable strikeDamage = new Value.RangedValueCritable(416, 557, 20, 200);

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

    public static class WoundingStrikeDefenderStats extends AbstractStrikeStats<WoundingStrikeDefender, WoundingStrikeDefenderStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public WoundingStrikeDefenderStats merge(WoundingStrikeDefenderStats other, int multiplier) {
            WoundingStrikeDefenderStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<WoundingStrikeDefenderStats> getClazz() {
            return WoundingStrikeDefenderStats.class;
        }

        @Override
        public WoundingStrikeDefenderStats create() {
            return new WoundingStrikeDefenderStats();
        }

    }

}
