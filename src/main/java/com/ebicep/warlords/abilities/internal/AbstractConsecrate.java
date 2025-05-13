package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractConsecrate extends AbstractAbility implements RedAbilityIcon, Duration, HitBox, AbilityStats<AbstractConsecrate, AbstractConsecrate.AbstractConsecrateStats> {

    protected FloatModifiable hitBox;
    protected int strikeDamageBoost;
    protected int tickDuration;
    private final AbstractConsecrateStats stats = new AbstractConsecrateStats();

    public AbstractConsecrate(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.strikeDamageBoost = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("strikeDamageBoost"), int.class);
        this.hitBox = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hitBox"), float.class));
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Consecrate the ground below your feet, declaring it sacred. Enemies standing on it will take ")
                .damage(getConsecrateDamage())
                .text(" damage every ")
                .durationSeconds(1)
                .text(" and take ")
                .percent(strikeDamageBoost, NamedTextColor.RED)
                .text(" increased damage from your paladin strikes. Has a radius of ")
                .blocks(hitBox.getCalculatedValue())
                .text(". Lasts ")
                .durationTicks(tickDuration)
                .text(".")
                .build();

    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Location location = wp.getLocation().clone();

        Utils.playGlobalSound(location, "paladin.consecrate.activation", 2, 1);
        float radius = hitBox.getCalculatedValue();
        CircleEffect circleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                location,
                radius,
                new CircumferenceEffect(Particle.HAPPY_VILLAGER, Particle.DUST),
                new DoubleLineEffect(Particle.EFFECT)
        );

        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                null,
                AbstractConsecrate.class,
                null,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                },
                false,
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if ((inPve && ticksElapsed % 4 == 0) || (!inPve && ticksElapsed % 2 == 0)) {
                        circleEffect.playEffects();
                    }
                    if (ticksElapsed % 20 == 0) {
                        PlayerFilter.entitiesAround(location, radius, 6, radius)
                                    .aliveEnemiesOf(wp)
                                    .forEach(enemy -> {
                                        stats.playersHit++;
                                        enemy.addInstance(InstanceBuilder
                                                .damage()
                                                .ability(this)
                                                .source(wp)
                                                .value(getConsecrateDamage())
                                                .flags(InstanceFlags.DOT)
                                        );
                                    });
                    }
                })
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (!event.getCause().equals(getStrikeName()) || event.getFlags().contains(InstanceFlags.STRIKE_IN_CONS)) {
                    return currentDamageValue;
                }
                boolean insideCons = location.distanceSquared(event.getWarlordsEntity().getLocation()) < radius * radius;
                if (!insideCons) {
                    return currentDamageValue;
                }
                event.getFlags().add(InstanceFlags.STRIKE_IN_CONS);
                addStrikesBoosted();
                return currentDamageValue * convertToMultiplicationDecimal(strikeDamageBoost);
            }
        });

        return true;
    }

    @Nonnull
    public abstract String getStrikeName();

    public void addStrikesBoosted() {
        stats.strikesBoosted++;
    }

    public abstract Value.RangedValueCritable getConsecrateDamage();

    @Override
    public FloatModifiable getHitBoxRadius() {
        return hitBox;
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
    public AbstractConsecrateStats getAbilityStats() {
        return stats;
    }

    public int getStrikeDamageBoost() {
        return strikeDamageBoost;
    }

    public void setStrikeDamageBoost(int strikeDamageBoost) {
        this.strikeDamageBoost = strikeDamageBoost;
    }

    public static class AbstractConsecrateStats extends AbstractAbilityStats<AbstractConsecrate, AbstractConsecrateStats> {

        @Field("strikes_boosted")
        private int strikesBoosted = 0;
        @Field("targets_hit")
        private int playersHit = 0;

        @Override
        public Class<AbstractConsecrateStats> getClazz() {
            return AbstractConsecrateStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Hit", playersHit));
            statsDisplay.add(new AbilityStatDisplay("Strikes Boosted", strikesBoosted));
            return statsDisplay;
        }

        @Override
        public AbstractConsecrateStats merge(AbstractConsecrateStats other, int multiplier) {
            AbstractConsecrateStats stats = super.merge(other, multiplier);
            stats.strikesBoosted = this.strikesBoosted + other.strikesBoosted * multiplier;
            stats.playersHit = this.playersHit + other.playersHit * multiplier;
            return stats;
        }

        @Override
        public AbstractConsecrateStats create() {
            return new AbstractConsecrateStats();
        }

        public void addPlayersHit() {
            playersHit++;
        }

    }

}
