package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.LinkedCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.defender.WoundingStrikeBranchDefender;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WoundingStrikeDefender extends AbstractStrike<WoundingStrikeDefender, WoundingStrikeDefender.WoundingStrikeDefenderStats> implements Damages<WoundingStrikeDefender.DamageValues> {

    private final WoundingStrikeDefenderStats stats = new WoundingStrikeDefenderStats();
    private final DamageValues damageValues = new DamageValues();
    private int wounding = 25;
    private int woundingDurationInTicks = 60;

    public WoundingStrikeDefender() {
        super(AbstractAbilityBuilder.create("woundingStrikeDefender").pvp());
    }

    @Override
    protected void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.wounding = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("wounding"), int.class);
        this.woundingDurationInTicks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("woundingDurationInTicks"), int.class);
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
                        warlordsEntity.addInstance(InstanceBuilder.damage()
                                                                  .ability(this)
                                                                  .source(wp)
                                                                  .value(damageValues.strikeDamage)
                                                                  .flag(InstanceFlags.PIERCE,
                                                                          warlordsEntity instanceof WarlordsNPC warlordsNPC && !(warlordsNPC.getMob() instanceof BossLike)
                                                                  )).ifPresent(finalEvent -> onFinalEvent(wp, finalEvent.getWarlordsEntity(), finalEvent));
                    }
            );
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
        if (!(nearPlayer.getCooldownManager().hasCooldownFromName("Wounding Strike"))) {
            nearPlayer.sendMessage(Component.text("You are ", NamedTextColor.GRAY)
                                            .append(Component.text("wounded", NamedTextColor.RED))
                                            .append(Component.text(".", NamedTextColor.GRAY)));
        }
        if (!nearPlayer.getCooldownManager().hasCooldown(WoundingStrikeBerserker.class)) {
            nearPlayer.getCooldownManager().removeCooldownByName("Wounding Strike", true);
            nearPlayer.getCooldownManager()
                      .addCooldown(new RegularCooldown<>(name, "WND", WoundingStrikeDefender.class, new WoundingStrikeDefender(), wp, CooldownTypes.DEBUFF, cooldownManager -> {
                      }, cooldownManager -> {
                          if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterNameActionBar("WND").stream().count() == 1) {
                              nearPlayer.sendMessage(Component.text("You are no longer ", NamedTextColor.GRAY)
                                                              .append(Component.text("wounded", NamedTextColor.RED))
                                                              .append(Component.text(".", NamedTextColor.GRAY)));
                          }
                      }, woundingDurationInTicks
                      ) {

                          @Override
                          public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                              return currentHealValue * (100 - wounding) / 100f;
                          }

                          @Override
                          public PlayerNameData addSuffixFromOther() {
                              return new PlayerNameData(Component.text("WND", NamedTextColor.RED),
                                      we -> we == wp || (we.isTeammate(nearPlayer) && we.getSpecClass().specType == SpecType.HEALER)
                              );
                          }
                      });
        }
    }

    private void damageReductionOnCrit(WarlordsEntity we, WarlordsEntity nearPlayer) {
        Set<WarlordsEntity> teammates = PlayerFilter.entitiesAround(nearPlayer, 10, 10, 10).aliveTeammatesOfExcludingSelf(we).stream().collect(Collectors.toSet());
        LinkedCooldown<?> linkedCooldown = new LinkedCooldown<>(name + " Resistance",
                "STRIKE RES",
                WoundingStrikeDefender.class,
                new WoundingStrikeDefender(),
                we,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                cooldownManager -> {
                },
                5 * 20,
                Collections.emptyList(),
                teammates
        ) {

            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * 0.7f;
            }
        };
        we.getCooldownManager().removeCooldownByName(name + " Resistance");
        we.getCooldownManager().addCooldown(linkedCooldown);
        for (WarlordsEntity teammate : teammates) {
            teammate.getCooldownManager().removeCooldownByName(name + " Resistance");
            teammate.getCooldownManager().addCooldown(linkedCooldown);
        }
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public WoundingStrikeDefenderStats getAbilityStats() {
        return stats;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Strike the targeted enemy player, causing")
                                               .damage(damageValues.strikeDamage)
                                               .text(" damage and ")
                                               .text("wounding", NamedTextColor.RED)
                                               .text(" them for ")
                                               .durationTicks(woundingDurationInTicks)
                                               .text(", making them receive ")
                                               .percent(wounding, NamedTextColor.RED)
                                               .text(" less healing.")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new WoundingStrikeBranchDefender(abilityTree, this);
    }

    public int getWounding() {
        return wounding;
    }

    public void setWounding(int wounding) {
        this.wounding = wounding;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable strikeDamage = new Value.RangedValueCritable(416, 557, 20, 200);

        private final List<Value> values = List.of(strikeDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.strikeDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("strikeDamage"), Value.RangedValueCritable.class);
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
