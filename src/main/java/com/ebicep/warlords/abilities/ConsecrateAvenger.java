package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.AbstractConsecrate;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifier;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifierBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.avenger.ConsecrateBranchAvenger;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class ConsecrateAvenger extends AbstractConsecrate implements Damages<ConsecrateAvenger.DamageValues> {

    private static final int SANCTIFY_PULSE_TICKS = 10;
    private static final float SANCTIFY_RADIUS_PER_PULSE = 0.2f;
    private static final float SANCTIFY_MAX_RADIUS = 20f;
    private static final float SANCTIFY_CRIT_PER_PULSE = 2f;
    private static final float SANCTIFY_SPEED_PER_ENEMY = 1.5f;
    private static final float SANCTIFY_STRIKE_PER_ENEMY = 3f;
    private static final float SANCTIFY_BUFF_CAP = 30f;

    private final DamageValues damageValues = new DamageValues();
    private int sanctifyCastId;

    public ConsecrateAvenger() {
        super(AbstractAbilityBuilder.create("consecrateAvenger").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        if (pveMasterUpgrade) {
            return activateSanctify(wp);
        }
        if (!pveMasterUpgrade2) {
            return super.onActivateInternal(wp);
        }
        Location location = wp.getLocation().clone();
        Utils.playGlobalSound(location, "paladin.consecrate.activation", 2, 1);
        float radius = hitBox.getCalculatedValue();
        CircleEffect circleEffect = new CircleEffect(wp.getGame(),
                wp.getTeam(),
                location,
                radius,
                new CircumferenceEffect(Particle.DUST, new Particle.DustOptions(Color.fromRGB(210, 50, 50), 1)).particlesPerCircumference(.25),
                new DoubleLineEffect(Particle.DUST, new Particle.DustOptions(Color.fromRGB(255, 160, 160), 1))
        );
        HashSet<WarlordsEntity> hit = new HashSet<>();
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(name, null, AbstractConsecrate.class, null, wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, cooldownManager -> {
        }, false, tickDuration, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
            Location updatedLocation = wp.getLocation();
            circleEffect.setCenter(updatedLocation);
            if (ticksElapsed % 5 == 0) {
                circleEffect.playEffects();
            }
            if (ticksElapsed % 30 == 0) {
                PlayerFilter.entitiesAround(updatedLocation, radius, 6, radius).aliveEnemiesOf(wp).forEach(enemy -> {
                    hit.add(enemy);
                    getAbilityStats().addPlayersHit();
                    enemy.addInstance(InstanceBuilder.damage().ability(this).source(wp).value(damageValues.consecrateDamage));
                });
            }
        })
        ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                    if (event.getFlags().contains(InstanceFlags.STRIKE_IN_CONS)) {
                        return;
                    }
                    if (!hit.contains(event.getWarlordsEntity())) {
                        return;
                    }
                    event.getFlags().add(InstanceFlags.STRIKE_IN_CONS);
                    addStrikesBoosted();
            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, convertToMultiplicationDecimal(strikeDamageBoost));
                }
        ));
        return true;
    }

    private boolean activateSanctify(@Nonnull WarlordsEntity wp) {
        Location location = wp.getLocation().clone();
        Utils.playGlobalSound(location, "paladin.consecrate.activation", 2, 1);
        float[] radius = {hitBox.getCalculatedValue()};
        float[] strikeBonus = {0};
        String modifierName = "Sanctify " + ++sanctifyCastId;
        CircleEffect circleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                location.clone(),
                radius[0],
                new CircumferenceEffect(Particle.DUST, new Particle.DustOptions(Color.fromRGB(210, 50, 50), 1)).particlesPerCircumference(.25),
                new DoubleLineEffect(Particle.DUST, new Particle.DustOptions(Color.fromRGB(255, 160, 160), 1))
        );
        Value.RangedValueCritable damage = getConsecrateDamage();
        FloatModifiable.FloatModifier critChance = damage.critChance().addModifier(FloatModifiable.ModifierType.ADDITIVE, modifierName, 0);
        FloatModifiable.FloatModifier critMultiplier = damage.critMultiplier().addModifier(FloatModifiable.ModifierType.ADDITIVE, modifierName, 0);
        MotionModifier speed = new MotionModifierBuilder()
                .setFrom(wp)
                .setName(modifierName)
                .setModifier(0)
                .setDuration(Integer.MAX_VALUE)
                .build();
        wp.addSpeedModifier(speed);
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
                    damage.critChance().removeModifier(modifierName);
                    damage.critMultiplier().removeModifier(modifierName);
                    speed.setTicksLeft(0);
                },
                false,
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 5 == 0) {
                        circleEffect.playEffects();
                    }
                    if (ticksElapsed % SANCTIFY_PULSE_TICKS != 0) {
                        return;
                    }
                    PlayerFilter.entitiesAroundInGame(wp.getGame(), location, radius[0], 6, radius[0])
                                .aliveEnemiesOf(wp)
                                .forEach(enemy -> enemy.addInstance(InstanceBuilder
                                        .damage()
                                        .ability(this)
                                        .source(wp)
                                        .value(damage)
                                        .flags(InstanceFlags.DOT)
                                ).ifPresent(event -> getAbilityStats().addPlayersHit()));
                    radius[0] = Math.min(SANCTIFY_MAX_RADIUS, radius[0] + SANCTIFY_RADIUS_PER_PULSE);
                    circleEffect.setRadius(radius[0]);
                    critChance.setModifier(critChance.getModifier() + SANCTIFY_CRIT_PER_PULSE);
                    critMultiplier.setModifier(critMultiplier.getModifier() + SANCTIFY_CRIT_PER_PULSE);
                    int enemies = (int) PlayerFilter.entitiesAroundInGame(wp.getGame(), location, radius[0], 6, radius[0])
                                                    .aliveEnemiesOf(wp)
                                                    .stream()
                                                    .count();
                    speed.setModifier(Math.min(SANCTIFY_BUFF_CAP, enemies * SANCTIFY_SPEED_PER_ENEMY));
                    strikeBonus[0] = Math.min(SANCTIFY_BUFF_CAP, enemies * SANCTIFY_STRIKE_PER_ENEMY);
                })
        ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
            if (!event.getCause().equals(getStrikeName())) {
                return;
            }
            if (strikeBonus[0] > 0) {
                currentDamageValue.addModifier(
                        FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                        modifierName,
                        convertToMultiplicationDecimal(strikeBonus[0])
                );
            }
            if (event.getFlags().contains(InstanceFlags.STRIKE_IN_CONS)) {
                return;
            }
            boolean insideCons = location.distanceSquared(event.getWarlordsEntity().getLocation()) < radius[0] * radius[0];
            if (!insideCons) {
                return;
            }
            event.getFlags().add(InstanceFlags.STRIKE_IN_CONS);
            addStrikesBoosted();
            currentDamageValue.addModifier(
                    FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                    name,
                    convertToMultiplicationDecimal(strikeDamageBoost)
            );
        }));
        return true;
    }

    @Override
    public Value.RangedValueCritable getConsecrateDamage() {
        return damageValues.consecrateDamage;
    }

    @Nonnull
    @Override
    public String getStrikeName() {
        return "Avenger's Strike";
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ConsecrateBranchAvenger(abilityTree, this);
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable consecrateDamage = new Value.RangedValueCritable(198, 267, 20, 175);

        private List<Value> values = List.of(consecrateDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.consecrateDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameDamage("consecrateDamage"),
                    Value.RangedValueCritable.class
            );
            this.values = List.of(consecrateDamage);
        }

        public Value.RangedValueCritable getConsecrateDamage() {
            return consecrateDamage;
        }

    }

}
