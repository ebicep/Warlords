package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ContagiousFacade;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Contagion implements SpecBoostManager.SpecBoost<Contagion> {

    private int ephIncrease;
    private float facadeResistanceIncreasePercent;
    private int facadePhexStacksIncrease;

    @Override
    public void init() {
        this.ephIncrease = getValue("ephIncrease", int.class);
        this.facadeResistanceIncreasePercent = getValue("facadeResistanceIncreasePercent", float.class);
        this.facadePhexStacksIncrease = getValue("facadePhexStacksIncrease", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "contagion";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(ephIncrease, facadeResistanceIncreasePercent, facadePhexStacksIncrease);
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
            warlordsPlayer.getEnergyPerHit().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", ephIncrease);
            warlordsPlayer.getAbilitiesMatching(ContagiousFacade.class).forEach(contagiousFacade -> {
                contagiousFacade.getDamageAbsorption().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", facadeResistanceIncreasePercent);
                contagiousFacade.setStacksGranted(contagiousFacade.getStacksGranted() + facadePhexStacksIncrease);
                contagiousFacade.setReactivateAbility(false);
            });
        }

    }

}
