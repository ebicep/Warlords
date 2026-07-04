package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.LinkedCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HealingLink extends AbstractAbility implements PurpleAbilityIcon, Duration, Heals<HealingLink.HealingValues>, AbilityStats<HealingLink, HealingLink.HealingLinkStats> {

    private final HealingLinkStats stats = new HealingLinkStats();
    private final HealingValues healingValues = new HealingValues();
    private float radius = 10;
    private float damagePercentTaken = 30;
    private int tickDuration = 120;
    private int maxTargets = 1;

    public HealingLink() {
        super(AbstractAbilityBuilder.create("healingLink").pvp());
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
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.radius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), float.class);
        this.damagePercentTaken = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damagePercentTaken"), float.class);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.maxTargets = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxTargets"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Set<WarlordsEntity> targets = PlayerFilter.entitiesAround(wp, radius, radius, radius)
                                                  .aliveTeammatesOfExcludingSelf(wp)
                                                  .requireLineOfSightIntervene(wp, false)
                                                  .lookingAtFirst(wp)
                                                  .limit(maxTargets)
                                                  .stream()
                                                  .collect(Collectors.toSet());
        if (targets.isEmpty()) {
            return false;
        }
        LinkedCooldown<HealingLink> linkCooldown = new LinkedCooldown<>(name,
                "LINK",
                HealingLink.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 20 == 0) {
                        for (WarlordsEntity linked : cooldown.getLinkedEntities()) {
                            linked.addInstance(InstanceBuilder.healing()
                                                              .ability(this)
                                                              .source(wp)
                                                              .value(healingValues.linkHealing)
                            ).ifPresent(finalEvent -> {
                                wp.addInstance(InstanceBuilder.melee()
                                                              .source(wp)
                                                              .value(finalEvent.getValue() * damagePercentTaken / 100)
                                                              .showAsCrit(finalEvent.isCrit())
                                );
                            });
                        }
                    }
                }),
                targets
        );
        wp.getCooldownManager().removeCooldown(HealingLink.class, false);
        wp.getCooldownManager().addCooldown(linkCooldown);
        targets.forEach(target -> {
            target.getCooldownManager().removeCooldown(HealingLink.class, false);
            target.getCooldownManager().addCooldown(linkCooldown);
            Utils.playGlobalSound(wp.getLocation(), "warrior.revenant.orbsoflife", 1, 2f);
            EffectUtils.playParticleLinkAnimation(wp.getLocation(), target.getLocation(), Particle.HAPPY_VILLAGER);
        });
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Link yourself to an ally, healing them for ")
                                               .heal(healingValues.linkHealing)
                                               .text(" health every second for ")
                                               .durationTicks(tickDuration)
                                               .text(". You receive damage equal to ")
                                               .percent(damagePercentTaken, NamedTextColor.RED)
                                               .text(" of the amount healed.")
                                               .build();
    }

    @Override
    public HealingLinkStats getAbilityStats() {
        return stats;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.SetValue linkHealing = new Value.SetValue(350);

        private List<Value> values = List.of(linkHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.linkHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("linkHealing"),
                    Value.SetValue.class
            );
            this.values = List.of(linkHealing);
        }

    }


    public static class HealingLinkStats extends AbstractAbilityStats<HealingLink, HealingLinkStats> {


        @Override
        public Class<HealingLinkStats> getClazz() {
            return HealingLinkStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public HealingLinkStats merge(HealingLinkStats other, int multiplier) {
            HealingLinkStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public HealingLinkStats create() {
            return new HealingLinkStats();
        }

    }


}
