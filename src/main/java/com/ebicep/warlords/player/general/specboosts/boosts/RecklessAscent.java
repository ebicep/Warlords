package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.RecklessCharge;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;

import java.util.List;

import static com.ebicep.warlords.abilities.internal.AbstractAbility.convertToDivisionDecimal;

public class RecklessAscent implements SpecBoostManager.SpecBoost<RecklessAscent> {

    private float radiusIncrease;
    private float travelDistanceIncrease;
    private float damageReductionPercent;
    private int damageReductionDurationTicks;
    private int verticalAscentDamage;

    @Override
    public void init() {
        this.radiusIncrease = getValue("radiusIncrease", float.class);
        this.travelDistanceIncrease = getValue("travelDistanceIncrease", float.class);
        this.damageReductionPercent = getValue("damageReductionPercent", float.class);
        this.damageReductionDurationTicks = getValue("damageReductionDurationTicks", int.class);
        this.verticalAscentDamage = getValue("verticalAscentDamage", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "recklessAscent";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(radiusIncrease, travelDistanceIncrease, damageReductionPercent, damageReductionDurationTicks, verticalAscentDamage);
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
            });
        }

        @EventHandler
        public void onWarlordsAbilityActivatePreEvent(WarlordsAbilityActivateEvent.Pre event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (!(event.getAbility() instanceof RecklessCharge)) {
                return;
            }
            Location location = warlordsEntity.getLocation();
            if (Math.abs(location.getPitch()) < 50) {
                return;
            }
            if (warlordsEntity.getCurrentHealth() < verticalAscentDamage) {
                event.setCancelled(true);
                return;
            }
            warlordsEntity.addInstance(InstanceBuilder
                    .fall()
                    .cause(getStringName())
                    .source(warlordsEntity)
                    .value(verticalAscentDamage)
            );
        }

        @EventHandler
        public void onWarlordsAbilityActivatePostEvent(WarlordsAbilityActivateEvent.Post event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (event.getAbility() instanceof RecklessCharge) {
                warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                        getStringName(),
                        "ASC",
                        Boost.class,
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
