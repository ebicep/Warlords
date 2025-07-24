package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Parry extends AbstractAbility implements AbilityStats<Parry, Parry.ParryStats>, RedAbilityIcon {

    private final ParryStats stats = new ParryStats();
    private int blockTickDuration;
    private int knockbackTickDuration;
    private float damageReduction;
    private int damageReductionTickDuration;

    public Parry() {
        super(AbstractAbilityBuilder.create("parry").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.blockTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("blockTickDuration"), int.class);
        this.knockbackTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("knockbackTickDuration"), int.class);
        this.damageReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReduction"), float.class);
        this.damageReductionTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReductionTickDuration"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 2, 0.5f);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "BLOCK",
                Parry.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {

                },
                cooldownManager -> {
                },
                blockTickDuration
        ) {

            @Override
            protected Listener getListener() {
                return new Listener() {

                    boolean blocked = false;

                    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
                    public void onDamageHeal(WarlordsDamageHealingEvent event) {
                        if (blocked) {
                            return;
                        }
                        if (!Objects.equals(event.getWarlordsEntity(), wp)) {
                            return;
                        }
                        if (event.isHealingInstance()) {
                            return;
                        }
                        if (event.getCause().isEmpty()) {
                            return;
                        }
                        if (event.getFlags().contains(InstanceFlags.DOT)) {
                            return;
                        }
                        Utils.playGlobalSound(wp.getLocation(), Sound.ITEM_SHIELD_BLOCK, 2, .7f);
                        WarlordsEntity source = event.getSource();
                        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" You ", NamedTextColor.GRAY))
                                                                      .append(Component.text("Parried", NamedTextColor.YELLOW))
                                                                      .append(Component.text(" " + source.getName() + "'s " + event.getCause() + "!", NamedTextColor.GRAY)));
                        source.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" Your " + event.getCause() + " was ", NamedTextColor.GRAY))
                                                                          .append(Component.text("Parried", NamedTextColor.YELLOW))
                                                                          .append(Component.text(" by " + wp.getName() + "!", NamedTextColor.GRAY)));

                        blocked = true;
                        stats.timesBlocked++;
                        event.setCancelled(true);
                        setTicksLeft(0);
                        new CooldownFilter<>(wp, RegularCooldown.class)
                                .filterCooldownClass(ParryDamageReduction.class)
                                .findAny()
                                .ifPresentOrElse(parryDamageReductionCooldown -> {
                                            if (parryDamageReductionCooldown.getCooldownObject() instanceof ParryDamageReduction parryDamageReduction) {
                                                parryDamageReduction.instances.add(damageReductionTickDuration);
                                                parryDamageReductionCooldown.setTicksLeft(damageReductionTickDuration);
                                            }
                                        }, () -> {
                                            wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                                    "Parry Damage Reduction",
                                                    "REDUC",
                                                    ParryDamageReduction.class,
                                                    new ParryDamageReduction(damageReductionTickDuration),
                                                    wp,
                                                    CooldownTypes.ABILITY,
                                                    cooldownManager -> {

                                                    },
                                                    damageReductionTickDuration,
                                                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                                        List<Integer> instances = cooldown.getCooldownObject().instances;
                                                        instances.replaceAll(integer -> integer - 1);
                                                        instances.removeIf(integer -> integer <= 0);
                                                    })
                                            ) {
                                                @Override
                                                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                                    return currentDamageValue * convertToDivisionDecimal(cooldownObject.instances.size() * damageReduction);
                                                }

                                                @Nonnull
                                                @Override
                                                public Component getDebugMessage() {
                                                    List<Integer> instances = cooldownObject.instances;
                                                    return Component.text(NumberFormat.formatOptionalHundredths(instances.size()) + "=" +
                                                                    instances.stream().map(Object::toString).collect(Collectors.joining(",")),
                                                            NamedTextColor.YELLOW
                                                    );
                                                }
                                            });
                                        }
                                );
                    }
                };
            }
        });
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "PARRY",
                Parry.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {

                },
                cooldownManager -> {
                },
                knockbackTickDuration
        ) {
            boolean parried = false;

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (!parried && event.getAbility() instanceof AbstractStrike<?, ?>) {
                    parried = true;
                    stats.timesKnockbacked++;
                    WarlordsEntity victim = event.getWarlordsEntity();
                    Vector v = wp.getLocation().toVector().subtract(victim.getLocation().toVector()).normalize().multiply(-1.5).setY(0.35);
                    victim.setVelocity(name, v, false);
                    setTicksLeft(0);
                }
            }
        });
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("For ")
                .durationTicks(blockTickDuration)
                .text(", you block the damage of the first skill that hits you and gain ")
                .damageReduction(damageReduction)
                .text(" damage reduction for ")
                .durationTicks(damageReductionTickDuration)
                .text(". For ")
                .durationTicks(knockbackTickDuration)
                .text(", you deal massive knockback with your next Crusader's Strike.")
                .build();
    }

    @Override
    public ParryStats getAbilityStats() {
        return stats;
    }

    public static class ParryDamageReduction {

        private final List<Integer> instances = new ArrayList<>();

        public ParryDamageReduction(int instanceDuration) {
            this.instances.add(instanceDuration);
        }

    }

    public static class ParryStats extends AbstractAbilityStats<Parry, ParryStats> {

        @Field("times_blocks")
        private int timesBlocked = 0;
        @Field("times_knockbacked")
        private int timesKnockbacked = 0;

        @Override
        public Class<ParryStats> getClazz() {
            return ParryStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Times Blocked", timesBlocked));
            statsDisplay.add(new AbilityStatDisplay("Times Knockbacked", timesKnockbacked));
            return statsDisplay;
        }

        @Override
        public ParryStats merge(ParryStats other, int multiplier) {
            ParryStats stats = super.merge(other, multiplier);
            stats.timesBlocked = this.timesBlocked + other.timesBlocked * multiplier;
            stats.timesKnockbacked = this.timesKnockbacked + other.timesKnockbacked * multiplier;
            return stats;
        }

        @Override
        public ParryStats create() {
            return new ParryStats();
        }

    }

}
