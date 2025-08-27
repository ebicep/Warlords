package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFlag;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownUtils;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.*;

public class BullRush extends AbstractAbility implements PurpleAbilityIcon, HitBox, Duration, AbilityStats<BullRush, BullRush.BullRushStats>, Heals<BullRush.HealingValues>, Damages<BullRush.DamageValues> {

    private final BullRushStats stats = new BullRushStats();
    private final DamageValues damageValues = new DamageValues();
    private final HealingValues healingValues = new HealingValues();
    private int speedBoost;
    private int tickDuration;
    private FloatModifiable radius = new FloatModifiable(13);
    private float knockbackMagnitude;
    private float knockbackMagnitudeFlag;
    private float knockbackY;

    public BullRush() {
        super(AbstractAbilityBuilder.create("bullRush").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.speedBoost = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speedBoost"), int.class);
        this.radius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), float.class));
        this.knockbackMagnitude = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("knockbackMagnitude"), float.class);
        this.knockbackMagnitudeFlag = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("knockbackMagnitudeFlag"), float.class);
        this.knockbackY = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("knockbackY"), float.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 1f);
        wp.setFlagPickCooldown(1);
        List<FloatModifiable.FloatModifier> modifiers = new ArrayList<>();

        wp.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(healingValues.bullRushHealing));
        wp.unstun();
        Set<WarlordsEntity> hit = new HashSet<>();
        float radius = this.radius.getCalculatedValue();
        RegularCooldown<BullRush> cd = new RegularCooldown<>(
                name,
                "BULL",
                BullRush.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        float magnitude = wp.hasFlag() ? -knockbackMagnitude : -knockbackMagnitudeFlag;
                        PlayerFilter.entitiesAround(wp, radius, radius, radius)
                                    .aliveEnemiesOf(wp)
                                    .excluding(hit)
                                    .forEach(warlordsEntity -> {
                                        hit.add(warlordsEntity);
                                        warlordsEntity.addInstance(InstanceBuilder
                                                .damage()
                                                .ability(this)
                                                .source(wp)
                                                .value(damageValues.bullRushDamage)
                                        ).ifPresent(finalEvent -> {
                                            if (!warlordsEntity.hasFlag()) {
                                                Vector v = wp.getCurrentVector().normalize().multiply(magnitude).setY(knockbackY);
                                                new GameRunnable(wp.getGame()) {
                                                    @Override
                                                    public void run() {
                                                        warlordsEntity.setVelocity(name, v, false);
                                                    }
                                                }.runTaskLater(1);
                                            }
                                        });
                                    });
                    }
                })
        ) {
            @Override
            protected Listener getListener() {
                return CooldownUtils.getDebuffImmunityListener(CooldownUtils.DebuffImmunity
                        .create(wp)
                        .stunPredicate()
                );
            }
        };
        cd.getFlags().add(CooldownFlag.CANNOT_BE_REDUCED_VIND);
        wp.getCooldownManager().addCooldown(cd);
        wp.addSpeedModifier(wp, name, speedBoost, cd);
        wp.addKnockbackModifier(wp, name, -100, cd);
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Heal for ")
                .heal(healingValues.bullRushHealing)
                .text(" and gain knockback and stun immunity and ")
                .percent(speedBoost, NamedTextColor.WHITE)
                .text(" speed for ")
                .durationTicks(tickDuration)
                .text(". Enemies within ")
                .blocks(radius)
                .text(" will take ")
                .damage(damageValues.bullRushDamage)
                .text(" damage and will be knocked opposite the direction you are travelling.")
                .build();
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
    public BullRushStats getAbilityStats() {
        return stats;
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValue bullRushDamage = new Value.RangedValue(506, 730);
        private List<Value> values = List.of(bullRushDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.bullRushDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameDamage("bullRushDamage"),
                    Value.RangedValue.class
            );
            this.values = List.of(bullRushDamage);
        }

        public Value.RangedValue getBullRushDamage() {
            return bullRushDamage;
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.SetValue bullRushHealing = new Value.SetValue(500);

        private List<Value> values = List.of(bullRushHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.bullRushHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("bullRushHealing"), Value.SetValue.class);
            this.values = List.of(bullRushHealing);
        }

        public Value.SetValue getBullRushHealing() {
            return bullRushHealing;
        }

    }

    public static class BullRushStats extends AbstractAbilityStats<BullRush, BullRushStats> {

        @Override
        public Class<BullRushStats> getClazz() {
            return BullRushStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public BullRushStats merge(BullRushStats other, int multiplier) {
            BullRushStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public BullRushStats create() {
            return new BullRushStats();
        }

    }

}
