package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.HolyRadianceProtector;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.event.EventHandler;

import java.util.List;

public class DivineEffulgence implements SpecBoostManager.SpecBoost<DivineEffulgence> {

    private float holyRadianceHealingIncreasePercent;
    private float holyRadianceTravelSpeedPercentIncrease;
    private float rangedDamageReductionPercent;
    private int rangedDamageReductionDurationTicks;

    @Override
    public void init() {
        this.holyRadianceHealingIncreasePercent = getValue("holyRadianceHealingIncreasePercent", float.class);
        this.holyRadianceTravelSpeedPercentIncrease = getValue("holyRadianceTravelSpeedPercentIncrease", float.class);
        this.rangedDamageReductionPercent = getValue("rangedDamageReductionPercent", float.class);
        this.rangedDamageReductionDurationTicks = getValue("rangedDamageReductionDurationTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "divineEffulgence";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(holyRadianceHealingIncreasePercent, holyRadianceTravelSpeedPercentIncrease, rangedDamageReductionPercent, rangedDamageReductionDurationTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public DivineEffulgence get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(HolyRadianceProtector.class).forEach(holyRadiance -> {
                holyRadiance.getHealValues().getRadianceHealing().forEachValue(floatModifiable ->
                        floatModifiable.addMultiplicativeModifierAdd("Spec Boost", holyRadianceHealingIncreasePercent / 100)
                );
                holyRadiance.getSpeed().addMultiplicativeModifierAdd("Spec Boost", holyRadianceTravelSpeedPercentIncrease / 100);
            });
        }

        @EventHandler
        public void onWarlordsDamageHealing(WarlordsDamageHealingFinalEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (event.isDamageInstance()) {
                return;
            }
            if (!event.getCause().equals("Holy Radiance")) {
                return;
            }
            event.getWarlordsEntity().getCooldownManager().addCooldown(new RegularCooldown<>(
                    getStringName(),
                    "EFF",
                    Boost.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {},
                    rangedDamageReductionDurationTicks
            ) {

                @Override
                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    if (Utils.isProjectile(event.getCause())) {
                        return currentDamageValue * AbstractAbility.convertToDivisionDecimal(rangedDamageReductionPercent);
                    }
                    return currentDamageValue;
                }
            });
        }

    }

}
