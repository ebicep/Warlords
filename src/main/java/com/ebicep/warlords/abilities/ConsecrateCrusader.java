package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractConsecrate;
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
import com.ebicep.warlords.pve.upgrades.paladin.crusader.ConsecrateBranchCrusader;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsecrateCrusader extends AbstractConsecrate {

    private final DamageValues damageValues = new DamageValues();

    public ConsecrateCrusader() {
        super(144, 194.4f, 50, 20, 175, 15, 4, 5);
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
                new CircumferenceEffect(Particle.REDSTONE, new Particle.DustOptions(Color.fromRGB(255, 215, 0), 1))
                        .particlesPerCircumference(.25),
                new DoubleLineEffect(Particle.REDSTONE, new Particle.DustOptions(Color.fromRGB(255, 255, 100), 1))
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
                    Location updatedLocation = wp.getLocation();
                    circleEffect.setCenter(updatedLocation);
                    if (ticksElapsed % 5 == 0) {
                        circleEffect.playEffects();
                    }
                    if (ticksElapsed % 30 == 0) {
                        AtomicInteger energyGiven = new AtomicInteger();
                        PlayerFilter.entitiesAround(updatedLocation, radius, 6, radius)
                                    .aliveEnemiesOf(wp)
                                    .forEach(enemy -> {
                                        playersHit++;
                                        enemy.addInstance(InstanceBuilder
                                                .damage()
                                                .ability(this)
                                                .value(damageValues.consecrateDamage)
                                        );
                                        if (energyGiven.get() < 105) {
                                            energyGiven.addAndGet(15);
                                            wp.addEnergy(wp, "Sanctifying Ring", 15);
                                        }
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
        return "Crusader's Strike";
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new ConsecrateBranchCrusader(abilityTree, this);
    }

    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable consecrateDamage = new Value.RangedValueCritable(144, 194.4f, 20, 175);
        private final List<Value> values = List.of(consecrateDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
