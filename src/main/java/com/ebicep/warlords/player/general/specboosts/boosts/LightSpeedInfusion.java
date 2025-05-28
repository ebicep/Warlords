package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.HolyRadianceProtector;
import com.ebicep.warlords.abilities.LightInfusionProtector;
import com.ebicep.warlords.abilities.ProtectorsStrike;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;

import java.util.List;

public class LightSpeedInfusion implements SpecBoostManager.SpecBoost<LightSpeedInfusion> {

    private int holyRadianceEnergyCost;
    private int lightInfusionCooldownReductionTicks;
    private float singleAllyHealBonusPercent;

    @Override
    public void init() {
        this.holyRadianceEnergyCost = getValue("holyRadianceEnergyCost", int.class);
        this.lightInfusionCooldownReductionTicks = getValue("lightInfusionCooldownReductionTicks", int.class);
        this.singleAllyHealBonusPercent = getValue("singleAllyHealBonusPercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "lightSpeedInfusion";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(holyRadianceEnergyCost, lightInfusionCooldownReductionTicks, singleAllyHealBonusPercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public LightSpeedInfusion get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {


        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(HolyRadianceProtector.class).forEach(holyRadiance -> {
                holyRadiance.getEnergyCost().addOverridingModifier("Spec Boost", holyRadianceEnergyCost);
            });
            warlordsPlayer.getAbilitiesMatching(LightInfusionProtector.class).forEach(lightInfusion -> {
                lightInfusion.getCooldown().addAdditiveModifier("Spec Boost", -lightInfusionCooldownReductionTicks / 20f);
            });
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getStringName(),
                    null,
                    Boost.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {
                    },
                    false
            ) {

                @Override
                public float modifyHealingFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
                    if (event.getSource().equals(warlordsPlayer) && event.getCause().equals("Protector's Strike")) {
                        List<CustomInstanceFlags> customFlags = event.getCustomFlags();
                        for (CustomInstanceFlags customFlag : customFlags) {
                            if (customFlag instanceof ProtectorsStrike.HealedPlayersInstanceFlag(List<WarlordsEntity> healedPlayers) && healedPlayers.size() == 1) {
                                return currentHealValue * (1 + singleAllyHealBonusPercent / 100);
                            }
                        }
                    }
                    return currentHealValue;
                }

            });
        }

    }

}
