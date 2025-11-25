package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.RecklessCharge;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;

import java.util.List;

public class RecklessAscent implements SpecBoostManager.SpecBoost<RecklessAscent> {

    private float radiusIncrease;
    private float travelDistanceIncrease;
    private float damageReductionPercent;
    private int damageReductionDurationTicks;
    private int verticalAscentDamage;
    private float bonusVerticalBlocks;

    @Override
    public void init() {
        this.radiusIncrease = getValue("radiusIncrease", float.class);
        this.travelDistanceIncrease = getValue("travelDistanceIncrease", float.class);
        this.damageReductionPercent = getValue("damageReductionPercent", float.class);
        this.damageReductionDurationTicks = getValue("damageReductionDurationTicks", int.class);
        this.verticalAscentDamage = getValue("verticalAscentDamage", int.class);
        this.bonusVerticalBlocks = getValue("bonusVerticalBlocks", float.class);
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
                recklessCharge.getHitBoxRadius().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", radiusIncrease);
                recklessCharge.setVerticalMovement(true);
            });
        }

        @EventHandler
        public void onWarlordsAbilityActivatePreEvent(WarlordsAbilityActivateEvent.Pre event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (!(event.getAbility() instanceof RecklessCharge recklessCharge)) {
                return;
            }
            Location location = warlordsEntity.getLocation();
            if (Math.abs(location.getPitch()) < 50) {
                return;
            }
            recklessCharge.setAdditionalBlocks(recklessCharge.getAdditionalBlocks() + bonusVerticalBlocks);
            warlordsEntity.addInstance(InstanceBuilder
                    .fall()
                    .source(warlordsEntity)
                    .value(verticalAscentDamage)
            );
        }

        @EventHandler
        public void onWarlordsAbilityActivatePostEvent(WarlordsAbilityActivateEvent.Post event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (event.getAbility() instanceof RecklessCharge recklessCharge) {
                if (Math.abs(warlordsEntity.getLocation().getPitch()) >= 50) {
                    recklessCharge.setAdditionalBlocks(recklessCharge.getAdditionalBlocks() - bonusVerticalBlocks);
                }
                warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                        getStringName(),
                        "ASC",
                        Boost.class,
                        null,
                        warlordsEntity,
                        CooldownTypes.SPEC_BOOST,
                        cooldownManager -> {},
                        damageReductionDurationTicks
                ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (e, currentDamageValue) -> {
                    currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE,
                            getStringName(),
                            AbstractAbility.convertToDivisionDecimal(damageReductionPercent)
                    );
                        }
                ));
            }
        }

    }

}
