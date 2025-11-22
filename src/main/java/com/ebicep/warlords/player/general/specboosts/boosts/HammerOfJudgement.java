package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ConsecrateProtector;
import com.ebicep.warlords.abilities.HammerOfLight;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.ebicep.warlords.abilities.internal.AbstractAbility.convertToMultiplicationDecimal;

public class HammerOfJudgement implements SpecBoostManager.SpecBoost<HammerOfJudgement> {

    private int hammerOfLightCooldownIncreaseTicks;
    private float hammerThrowRadius;
    private Value.RangedValue hammerThrowDamage;
    private int consecrateDurationTicks;

    @Override
    public void init() {
        this.hammerOfLightCooldownIncreaseTicks = getValue("hammerOfLightCooldownIncreaseTicks", int.class);
        this.hammerThrowRadius = getValue("hammerThrowRadius", float.class);
        this.hammerThrowDamage = getValue("hammerThrowDamage", Value.RangedValue.class);
        this.consecrateDurationTicks = getValue("consecrateDurationTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "hammerOfJudgement";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(hammerOfLightCooldownIncreaseTicks, hammerThrowRadius, hammerThrowDamage, consecrateDurationTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public HammerOfJudgement get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private final String cooldownName = getStringName() + " Consecrated";
        private WarlordsEntity warlordsEntity;
        private ConsecrateProtector consecrateProtector;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            this.consecrateProtector = warlordsPlayer
                    .getAbilitiesMatching(ConsecrateProtector.class)
                    .stream()
                    .findFirst()
                    .orElseGet(() -> {
                        ConsecrateProtector cons = new ConsecrateProtector();
                        cons.init(cons.getBuilder());
                        return cons;
                    });
            warlordsPlayer.getAbilitiesMatching(HammerOfLight.class).forEach(hammerOfLight -> {
                hammerOfLight.getCooldown().addAdditiveModifier("Spec Boost", hammerOfLightCooldownIncreaseTicks / 20f);
            });
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(HammerOfLight.class).forEach(hammerOfLight -> {
                hammerOfLight.getCooldown().removeModifier("Spec Boost");
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown)) {
                return;
            }
            if (!(cooldown.getCooldownObject() instanceof HammerOfLight.HammerOfLightData data) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            Location hammerLocation = data.getLocation();
            PlayerFilter.entitiesAround(hammerLocation, hammerThrowRadius, hammerThrowRadius, hammerThrowRadius)
                        .aliveEnemiesOf(warlordsEntity)
                        .forEach(enemy -> {
                                    enemy.addInstance(InstanceBuilder
                                            .damage()
                                            .cause(getStringName())
                                            .source(warlordsEntity)
                                            .value(hammerThrowDamage)
                                            .flags(InstanceFlags.IGNORE_FLAG_MULTIPLIER)
                                    );
                                    enemy.getCooldownManager().addCooldown(new RegularCooldown<>(
                                            cooldownName,
                                            null,
                                            Boost.class,
                                            null,
                                            warlordsEntity,
                                            CooldownTypes.SPEC_BOOST,
                                            cooldownManager -> {},
                                            consecrateDurationTicks,
                                            Collections.singletonList((cd, ticksLeft, ticksElapsed) -> {
                                                if (ticksElapsed % 20 == 0) {
                                                    enemy.addInstance(InstanceBuilder
                                                            .damage()
                                                            .ability(consecrateProtector)
                                                            .source(warlordsEntity)
                                                            .value(consecrateProtector.getConsecrateDamage())
                                                            .flags(InstanceFlags.DOT, InstanceFlags.HAMMER_OF_JUDGEMENT_CONS)
                                                    );
                                                }
                                            })
                                    ).addModifier(Modifier.INCOMING_DAMAGE_BEFORE_INTERVENE, (e, currentDamageValue) -> {
                                                if (!e.getCause().equals(consecrateProtector.getStrikeName()) ||
                                                        e.getFlags().contains(InstanceFlags.STRIKE_IN_CONS) ||
                                                        !e.getSource().equals(warlordsEntity)
                                                ) {
                                                    return;
                                                }
                                                e.getFlags().add(InstanceFlags.STRIKE_IN_CONS);
                                                consecrateProtector.addStrikesBoosted();
                                                currentDamageValue.addMultiplicativeModifierMult(cooldownName, convertToMultiplicationDecimal(consecrateProtector.getStrikeDamageBoost()));
                                            }
                                    ));
                                }
                        );
        }

        @EventHandler(ignoreCancelled = true)
        public void onDamageHeal(WarlordsDamageHealingEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!Objects.equals(event.getAbility(), consecrateProtector)) {
                return;
            }
            boolean hasConsecratedCooldown = event.getWarlordsEntity().getCooldownManager().hasCooldownFromName(cooldownName);
            if (!hasConsecratedCooldown) {
                return;
            }
            if (!event.getFlags().contains(InstanceFlags.HAMMER_OF_JUDGEMENT_CONS)) {
                event.setCancelled(true);
            }
        }

    }

}
