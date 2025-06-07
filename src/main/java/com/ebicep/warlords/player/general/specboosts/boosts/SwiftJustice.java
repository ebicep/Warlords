package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.RighteousStrike;
import com.ebicep.warlords.abilities.Vindicate;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownUtils;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
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

        private WarlordsPlayer warlordsEntity;

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
                    private boolean strikeUsed = false;

                    @Override
                    protected Listener getListener() {
                        return CooldownUtils.getDebuffImmunityListener(CooldownUtils.DebuffImmunity
                                .create(warlordsEntity)
                                .speedPredicate(CooldownUtils.DebuffImmunity.DEFAULT_SPEED)
                        );
                    }

                    @Override
                    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        WarlordsEntity victim = event.getWarlordsEntity();
                        if (event.getAbility() instanceof RighteousStrike && !strikeUsed) {
                            strikeUsed = true;
                            victim.getCooldownManager().subtractTicksOnRegularCooldowns(nextStrikeCooldownReductionTicks, CooldownTypes.ABILITY);
                            return currentDamageValue * AbstractAbility.convertToMultiplicationDecimal(nextStrikeDamageIncreasePercent);
                        }
                        return currentDamageValue;
                    }
                };
                vindicate.addSecondaryAbility(
                        3,
                        () -> {
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
