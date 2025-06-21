package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ContagiousFacade;
import com.ebicep.warlords.abilities.PoisonousHex;
import com.ebicep.warlords.abilities.SoulfireBeam;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

public class Contagion implements SpecBoostManager.SpecBoost<Contagion> {

    private int ephIncrease;
    private float facadeResistanceIncreasePercent;
    private float rangedAbilityRangeReductionBlocks;

    @Override
    public void init() {
        this.ephIncrease = getValue("ephIncrease", int.class);
        this.facadeResistanceIncreasePercent = getValue("facadeResistanceIncreasePercent", float.class);
        this.rangedAbilityRangeReductionBlocks = getValue("rangedAbilityRangeReductionBlocks", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "contagion";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(ephIncrease, facadeResistanceIncreasePercent, rangedAbilityRangeReductionBlocks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public Contagion get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getEnergyPerHit().addAdditiveModifier("Spec Boost", ephIncrease);
            warlordsPlayer.getAbilitiesMatching(ContagiousFacade.class).forEach(contagiousFacade -> {
                contagiousFacade.getDamageAbsorption().addAdditiveModifier("Spec Boost", facadeResistanceIncreasePercent);
                contagiousFacade.setReactivateAbility(false);
            });
            warlordsPlayer.getAbilitiesMatching(PoisonousHex.class).forEach(poisonousHex -> {
                poisonousHex.getMaxDistance().addAdditiveModifier("Spec Boost", -rangedAbilityRangeReductionBlocks);
            });
            warlordsPlayer.getAbilitiesMatching(SoulfireBeam.class).forEach(soulfireBeam -> {
                soulfireBeam.getMaxDistance().addAdditiveModifier("Spec Boost", -rangedAbilityRangeReductionBlocks);
            });
        }

    }

}
