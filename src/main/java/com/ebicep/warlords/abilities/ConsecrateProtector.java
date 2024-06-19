package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractConsecrate;
import com.ebicep.warlords.abilities.internal.CanReduceCooldowns;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.protector.ConsecrateBranchProtector;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsecrateProtector extends AbstractConsecrate implements CanReduceCooldowns {

    public ConsecrateProtector() {
        super(96, 130, 10, 20, 175, 15, 4, 5);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        if (!pveMasterUpgrade2) {
            return super.onActivate(wp);
        }


        Location location = wp.getLocation().clone();

        Utils.playGlobalSound(location, "paladin.consecrate.activation", 2, 1);
        float radius = hitBox.getCalculatedValue();
        CircleEffect circleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                location,
                radius,
                new CircumferenceEffect(Particle.REDSTONE, new Particle.DustOptions(Color.fromRGB(10, 120, 100), 1))
                        .particlesPerCircumference(.25),
                new DoubleLineEffect(Particle.REDSTONE, new Particle.DustOptions(Color.fromRGB(170, 225, 175), 1))
        );

        AtomicInteger timesReduced = new AtomicInteger();
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
                    Location updatedLocation = wp.getLocation();
                    circleEffect.setCenter(updatedLocation);
                    if (ticksElapsed % 5 == 0) {
                        circleEffect.playEffects();
                    }
                    if (ticksElapsed % 30 == 0) {
                        PlayerFilter.entitiesAround(updatedLocation, radius, 6, radius)
                                    .aliveEnemiesOf(wp)
                                    .forEach(enemy -> {
                                        playersHit++;
                                        enemy.addInstance(InstanceBuilder
                                                .damage()
                                                .ability(this)
                                                .value(damageValues.consecrateDamage)
                                        ).ifPresent(finalEvent -> {
                                            if (timesReduced.get() < 15) {
                                                timesReduced.getAndIncrement();
                                                wp.getAbilitiesMatching(HolyRadianceProtector.class).forEach(holy -> holy.subtractCurrentCooldown(.2f));
                                            }
                                        });
                                    });
                    }
                })
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (event.getFlags().contains(InstanceFlags.STRIKE_IN_CONS)) {
                    return currentDamageValue;
                }
                event.getFlags().add(InstanceFlags.STRIKE_IN_CONS);
                addStrikesBoosted();
                return currentDamageValue * convertToMultiplicationDecimal(strikeDamageBoost);
            }
        });
        return true;
    }

    @Override
    protected void damageEnemy(WarlordsEntity wp, WarlordsEntity enemy) {
        enemy.addInstance(InstanceBuilder
                .damage()
                .ability(this)
                .source(wp)
                .value(damageValues.consecrateDamage)
        );
    }

    @Nonnull
    @Override
    public String getStrikeName() {
        return "Protector's Strike";
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ConsecrateBranchProtector(abilityTree, this);
    }

    @Override
    public boolean canReduceCooldowns() {
        return pveMasterUpgrade2;
    }

    private final DamageValues damageValues = new DamageValues();

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable consecrateDamage = new Value.RangedValueCritable(96, 130, 20, 175);
        private final List<Value> values = List.of(consecrateDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
