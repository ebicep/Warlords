package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Haze extends AbstractAbility implements OrangeAbilityIcon, Damages<Haze.DamageValues>, AbilityStats<Haze, Haze.VanishStats>, OrderOfEviscerateLike {

    private final VanishStats stats = new VanishStats();
    private final DamageValues damageValues = new DamageValues();

    private int tickDuration = 100;
    private float incomingDamageReduction = 30;
    private float incomingHealingReduction = 30;
    private float hazeRadius = 5;
    private int vulnerableTickDuration = 160;
    private float vulnerableDamageBonus = 20;

    public Haze() {
        super(AbstractAbilityBuilder.create("haze").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.incomingDamageReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("incomingDamageReduction"), float.class);
        this.incomingHealingReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("incomingHealingReduction"), float.class);
        this.hazeRadius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hazeRadius"), float.class);
        this.vulnerableTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("vulnerableTickDuration"), int.class);
        this.vulnerableDamageBonus = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("vulnerableDamageBonus"), float.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 2, 0.7f);

        HazeData data = new HazeData(!FlagHolder.isPlayerHolderFlag(wp));
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "HAZE",
                HazeData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    PlayerFilter.entitiesAround(wp, hazeRadius, hazeRadius, hazeRadius)
                                .aliveEnemiesOf(wp)
                                .forEach(enemy -> {
                                    enemy.getCooldownManager().addCooldown(new RegularCooldown<>(
                                            "Vulnerable",
                                            "VULN",
                                            HazeData.class,
                                            null,
                                            wp,
                                            CooldownTypes.HIGH_LEVEL_DEBUFF,
                                            cooldownManager2 -> {
                                            },
                                            vulnerableTickDuration
                                    ) {
                                        @Override
                                        public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                            return currentDamageValue * convertToMultiplicationDecimal(vulnerableDamageBonus);
                                        }
                                    });
                                });
                },
                cooldownManager -> {
                    OrderOfEviscerate.removeCloak(wp, true);
                    wp.removePotionEffect(PotionEffectType.INVISIBILITY);
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    Location currentLocation = wp.getLocation();

                    if (ticksElapsed % 3 == 0) {
                        EffectUtils.displayParticle(Particle.CAMPFIRE_COSY_SMOKE, currentLocation.clone().add(0, 1.3, 0), 10, hazeRadius * 0.8, 1, hazeRadius * 0.8, .0);
                    }

                    if (ticksElapsed % 20 == 0) {
                        PlayerFilter.entitiesAround(currentLocation, hazeRadius, hazeRadius, hazeRadius)
                                    .aliveEnemiesOf(wp)
                                    .forEach(enemy -> {
                                        enemy.addInstance(InstanceBuilder
                                                .damage()
                                                .ability(this)
                                                .source(wp)
                                                .value(damageValues.hazeDamage)
                                        );
                                    });
                    }
                })
        ) {

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (event.getAbility() instanceof ShadowStep || event.getAbility() instanceof Haze) {
                    return;
                }
                setTicksLeft(0);
            }

            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (!data.vanished) {
                    return currentDamageValue;
                }
                return currentDamageValue * convertToDivisionDecimal(incomingDamageReduction);
            }

            @Override
            public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                if (!data.vanished) {
                    return currentHealValue;
                }
                if (!event.getSource().equals(wp)) {
                    return currentHealValue * convertToDivisionDecimal(incomingHealingReduction);
                }
                return currentHealValue;
            }
        });

        if (!FlagHolder.isPlayerHolderFlag(wp)) {
            OrderOfEviscerate.giveCloak(wp, tickDuration);
        }
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Cloak yourself for ")
                .durationTicks(tickDuration)
                .text(", reducing incoming damage by ")
                .percent(incomingDamageReduction, NamedTextColor.RED)
                .text(", and incoming healing by ")
                .percent(incomingHealingReduction, NamedTextColor.RED)
                .text(".")
                .emptyLine()
                .text("During this period, you will create a haze of smoke around you, dealing ")
                .damage(damageValues.hazeDamage)
                .text(" damage per second to all enemies within ")
                .blocks(hazeRadius)
                .text(". All attacks except Shadow Step end the skill.")
                .emptyLine()
                .text("When Haze ends, all nearby enemies will be marked as Vulnerable. Vulnerable enemies take ")
                .percent(vulnerableDamageBonus, NamedTextColor.RED)
                .text(" more damage for ")
                .durationTicks(vulnerableTickDuration)
                .text(".")
                .build();
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public VanishStats getAbilityStats() {
        return stats;
    }

    public static class HazeData {

        private boolean vanished;

        public HazeData(boolean vanished) {
            this.vanished = vanished;
        }

    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.SetValue hazeDamage = new Value.SetValue(200);

        private List<Value> values = List.of(hazeDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.hazeDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameDamage("hazeDamage"),
                    Value.SetValue.class
            );
            this.values = List.of(hazeDamage);
        }

        public Value.SetValue getHazeDamage() {
            return hazeDamage;
        }

    }

    public static class VanishStats extends AbstractAbilityStats<Haze, VanishStats> {

        @Override
        public Class<VanishStats> getClazz() {
            return VanishStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public VanishStats merge(VanishStats other, int multiplier) {
            VanishStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public VanishStats create() {
            return new VanishStats();
        }

    }

}