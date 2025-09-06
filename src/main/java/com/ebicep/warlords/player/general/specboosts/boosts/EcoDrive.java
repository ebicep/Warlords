package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.HolyRadianceProtector;
import com.ebicep.warlords.abilities.LightInfusionProtector;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;

import java.util.List;

public class EcoDrive implements SpecBoostManager.SpecBoost<EcoDrive> {

    private int lightInfusionCooldownReductionTicks;
    private int holyRadianceEnergyCost;
    private float singleAllyHealBonusPercent;

    @Override
    public void init() {
        this.lightInfusionCooldownReductionTicks = getValue("lightInfusionCooldownReductionTicks", int.class);
        this.holyRadianceEnergyCost = getValue("holyRadianceEnergyCost", int.class);
        this.singleAllyHealBonusPercent = getValue("singleAllyHealBonusPercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "ecoDrive";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(lightInfusionCooldownReductionTicks, holyRadianceEnergyCost, singleAllyHealBonusPercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public EcoDrive get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {


        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(LightInfusionProtector.class).forEach(lightInfusion -> {
                lightInfusion.getCooldown().addAdditiveModifier("Spec Boost", -lightInfusionCooldownReductionTicks / 20f);
            });
            warlordsPlayer.getAbilitiesMatching(HolyRadianceProtector.class).forEach(holyRadiance -> {
                holyRadiance.getEnergyCost().addAdditiveModifier("Spec Boost", -holyRadianceEnergyCost);
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
            ).addModifier(Modifier.HEALING_MODIFY_ATTACKER, (event, currentHealValue) -> {
                        if (event.getSource().equals(warlordsPlayer) && event.getCause().equals("Protector's Strike")) {
                            List<CustomInstanceFlags> customFlags = event.getCustomFlags();
                            for (CustomInstanceFlags customFlag : customFlags) {
                                if (customFlag instanceof CustomInstanceFlags.PlayersEffectedInstanceFlag(List<WarlordsEntity> healedPlayers) && healedPlayers.size() == 1) {
                                    currentHealValue.addMultiplicativeModifierMult(getStringName(), 1 + singleAllyHealBonusPercent / 100);
                                }
                            }
                        }
                    }
            ));
        }

    }

}
