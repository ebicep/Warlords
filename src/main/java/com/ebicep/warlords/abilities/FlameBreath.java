package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FlameBreath extends AbstractAbility implements RedAbilityIcon, Damages<FlameBreath.DamageValues>, AbilityStats<FlameBreath, FlameBreath.FlameBreathStats> {

    private final DamageValues damageValues = new DamageValues();
    private final FlameBreathStats stats = new FlameBreathStats();
    private int maxAnimationTime = 12;
    private int maxAnimationEffects = 4;
    private float hitbox = 10;
    private double velocity = 1.1;

    public FlameBreath() {
        super(AbstractAbilityBuilder.create("flameBreath").pvp());
    }

    public FlameBreath(AbstractAbilityBuilder builder) {
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
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "mage.fireball.activation", 2, .6f);
        Location playerLoc = new LocationBuilder(wp.getLocation()).pitch(0).add(0, 1.7, 0);
        EffectUtils.playSpiralAnimation(
                wp,
                playerLoc,
                maxAnimationEffects,
                maxAnimationTime,
                (center, animationTimer) -> {
                },
                Particle.DRIPPING_LAVA,
                Particle.FLAME
        );
        Location playerEyeLoc = new LocationBuilder(wp.getLocation()).pitch(0).backward(1);
        Vector viewDirection = playerLoc.getDirection();
        for (WarlordsEntity breathTarget : PlayerFilter.entitiesAroundRectangle(wp, hitbox - 2.5, hitbox, hitbox - 2.5).aliveEnemiesOf(wp)) {
            Vector direction = breathTarget.getLocation().subtract(playerEyeLoc).toVector().normalize();
            if (!(viewDirection.dot(direction) > .68)) {
                continue;
            }
            stats.playersHit++;
            if (breathTarget.hasFlag()) {
                stats.carrierHit++;
            }
            if (breathTarget.getCooldownManager().hasCooldownExtends(AbstractTimeWarp.class) && FlagHolder.playerNearFlag(breathTarget)) {
                stats.warpsKnockbacked++;
            }
            breathTarget.addInstance(InstanceBuilder
                    .damage()
                    .cause("Flame Breath")
                    .source(wp)
                    .value(damageValues.flameBreathDamage)
            ).ifPresent(finalEvent -> {
                Location loc = breathTarget.getLocation();
                Vector v = wp.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-velocity).setY(0.2);
                new GameRunnable(wp.getGame()) {
                    @Override
                    public void run() {
                        breathTarget.setVelocity(name, v, false);
                    }
                }.runTaskLater(1);

            });
        }
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Breathe flames in a cone in front of you, knocking back enemies, dealing ")
                                               .damage(damageValues.flameBreathDamage)
                                               .text(" damage to enemies hit.")
                                               .build();
    }

    @Override
    public FlameBreathStats getAbilityStats() {
        return stats;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable flameBreathDamage = new Value.RangedValueCritable(557, 753, 25, 185);

        private List<Value> values = List.of(flameBreathDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.flameBreathDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameDamage("flameBreathDamage"),
                    Value.RangedValueCritable.class
            );
            this.values = List.of(flameBreathDamage);
        }

        public Value.RangedValueCritable getFlameBreathDamage() {
            return flameBreathDamage;
        }

    }

    public static class FlameBreathStats extends AbstractAbilityStats<FlameBreath, FlameBreathStats> {

        @Field("targets_hit")
        private int playersHit = 0;
        @Field("carrier_hit")
        private int carrierHit = 0;
        @Field("warps_knockbacked")
        private int warpsKnockbacked = 0;

        @Override
        public Class<FlameBreathStats> getClazz() {
            return FlameBreathStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Hit", playersHit));
            statsDisplay.add(new AbilityStatDisplay("Carriers Hit", carrierHit));
            statsDisplay.add(new AbilityStatDisplay("Warps Knockbacked", warpsKnockbacked));
            return statsDisplay;
        }

        @Override
        public FlameBreathStats merge(FlameBreathStats other, int multiplier) {
            FlameBreathStats stats = super.merge(other, multiplier);
            stats.playersHit = this.playersHit + other.playersHit * multiplier;
            stats.carrierHit = this.carrierHit + other.carrierHit * multiplier;
            stats.warpsKnockbacked = this.warpsKnockbacked + other.warpsKnockbacked * multiplier;
            return stats;
        }

        @Override
        public FlameBreathStats create() {
            return new FlameBreathStats();
        }

    }

}
