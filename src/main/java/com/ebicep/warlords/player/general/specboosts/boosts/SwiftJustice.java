package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.RighteousStrike;
import com.ebicep.warlords.abilities.Vindicate;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.*;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class SwiftJustice implements SpecBoostManager.SpecBoost<SwiftJustice> {

    private float vindicateCooldownReductionSeconds;
    private float recastSpeedIncreasePercent;
    private int recastDurationTicks; // 3 seconds * 20 ticks/sec
    private float nextStrikeDamageIncreasePercent;
    private int nextStrikeCooldownReductionTicks;

    @Override
    public void init() {
        this.vindicateCooldownReductionSeconds = getValue("vindicateCooldownReductionSeconds", float.class);
        this.recastSpeedIncreasePercent = getValue("recastSpeedIncreasePercent", float.class);
        this.recastDurationTicks = getValue("recastDurationTicks", int.class);
        this.nextStrikeDamageIncreasePercent = getValue("nextStrikeDamageIncreasePercent", float.class);
        this.nextStrikeCooldownReductionTicks = getValue("nextStrikeCooldownReductionTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "swiftJustice";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(vindicateCooldownReductionSeconds, recastSpeedIncreasePercent, recastDurationTicks, nextStrikeDamageIncreasePercent, nextStrikeCooldownReductionTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public SwiftJustice get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(Vindicate.class).forEach(vindicate -> {
                vindicate.getCooldown().addAdditiveModifier("Spec Boost", -vindicateCooldownReductionSeconds);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!cooldown.getName().equals("Vindicate Resistance") || !(cooldown.getCooldownClass().equals(Vindicate.class)) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            warlordsEntity.getAbilitiesMatching(Vindicate.class).forEach(vindicate -> {
                vindicate.addSecondaryAbility(
                        5,
                        () -> {
                            final boolean[] strikeUsed = {false};
                            RegularCooldown<Boost> cd = new RegularCooldown<>(
                                    getStringName(),
                                    "JUSTICE",
                                    Boost.class,
                                    null,
                                    warlordsEntity,
                                    CooldownTypes.SPEC_BOOST,
                                    cooldownManager -> {},
                                    recastDurationTicks
                            ) {


                                @Override
                                protected Listener getListener() {
                                    return CooldownUtils.getDebuffImmunityListener(CooldownUtils.DebuffImmunity
                                            .create(warlordsEntity)
                                            .speedPredicate(CooldownUtils.DebuffImmunity.DEFAULT_SPEED)
                                    );
                                }
                            };
                            cd.addModifier(Modifier.DAMAGE_BEFORE_INTERVENE_ATTACKER, (e, currentDamageValue) -> {
                                        WarlordsEntity victim = e.getWarlordsEntity();
                                        if (e.getAbility() instanceof RighteousStrike && !strikeUsed[0]) {
                                            strikeUsed[0] = true;
                                            victim.getCooldownManager().subtractTicksOnRegularCooldowns(nextStrikeCooldownReductionTicks, CooldownTypes.ABILITY);
                                            new CooldownFilter<>(victim, RegularCooldown.class)
                                                    .filter(regularCooldown -> regularCooldown.getCooldownType() == CooldownTypes.ABILITY)
                                                    .filter(regularCooldown -> !regularCooldown.getFlags().contains(CooldownFlag.CANNOT_BE_REDUCED) &&
                                                            !regularCooldown.getFlags().contains(CooldownFlag.CANNOT_BE_REDUCED_VIND))
                                                    .forEach(regularCooldown -> regularCooldown.subtractTime(nextStrikeCooldownReductionTicks));
                                            currentDamageValue.addMultiplicativeModifierMult(getStringName(),
                                                    AbstractAbility.convertToMultiplicationDecimal(nextStrikeDamageIncreasePercent)
                                            );
                                        }
                                    }
                            );
                            warlordsEntity.getSpeed().removeNegativeModifiers();
                            warlordsEntity.getCooldownManager().addCooldown(cd);
                            warlordsEntity.addSpeedModifier(warlordsEntity, getStringName(), recastSpeedIncreasePercent, cd);
                            warlordsEntity.addKnockbackModifier(warlordsEntity, getStringName(), -100, cd);
                        },
                        false,
                        secondaryAbility -> warlordsEntity.isDead() || !warlordsEntity.getCooldownManager().hasCooldown(cooldown)
                );
            });
        }

    }

}
