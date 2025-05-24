package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.RecklessCharge;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import org.bukkit.event.EventHandler;

import java.util.List;

import static com.ebicep.warlords.abilities.internal.AbstractAbility.convertToDivisionDecimal;

public class RecklessAscent implements SpecBoostManager.SpecBoost<RecklessAscent> {

    private float radiusIncrease;
    private float travelDistanceIncrease;
    private float damageReductionPercent;
    private int damageReductionDurationTicks;

    @Override
    public void init() {
        this.radiusIncrease = getValue("radiusIncrease", float.class);
        this.travelDistanceIncrease = getValue("travelDistanceIncrease", float.class);
        this.damageReductionPercent = getValue("damageReductionPercent", float.class);
        this.damageReductionDurationTicks = getValue("damageReductionDurationTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "recklessAscent";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(radiusIncrease, travelDistanceIncrease, damageReductionPercent, damageReductionDurationTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public RecklessAscent get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(RecklessCharge.class).forEach(recklessCharge -> {
                recklessCharge.setAdditionalBlocks(recklessCharge.getAdditionalBlocks() + travelDistanceIncrease);
                recklessCharge.getHitBoxRadius().addAdditiveModifier("Spec Boost", radiusIncrease);
                recklessCharge.setVerticalMovement(true);
//                recklessCharge.setMaxChargeDuration(recklessCharge.getMaxChargeDuration() + 1);
            });
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(RecklessCharge.class).forEach(recklessCharge -> {
                recklessCharge.setAdditionalBlocks(recklessCharge.getAdditionalBlocks() - travelDistanceIncrease);
                recklessCharge.getHitBoxRadius().removeModifier("Spec Boost");
                recklessCharge.setVerticalMovement(false);
//                recklessCharge.setMaxChargeDuration(recklessCharge.getMaxChargeDuration() - 1);
            });
        }

        @EventHandler
        public void onWarlordsAbilityActivate(WarlordsAbilityActivateEvent.Post event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (event.getAbility() instanceof RecklessCharge) {
                warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                        getStringName(),
                        "ASC",
                        RecklessAscent.Boost.class,
                        null,
                        warlordsEntity,
                        CooldownTypes.SPEC_BOOST,
                        cooldownManager -> {},
                        damageReductionDurationTicks
                ) {
                    @Override
                    public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * convertToDivisionDecimal(damageReductionPercent);
                    }
                });
            }
        }

    }

}
