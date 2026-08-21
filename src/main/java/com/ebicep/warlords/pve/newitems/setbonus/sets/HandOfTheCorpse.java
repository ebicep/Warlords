package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class HandOfTheCorpse extends BaseSet {

    private int energyPerHit;

    @Override
    public void init() {
        super.init();
        this.energyPerHit = getValue("energyPerHit", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "handOfTheCorpse";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(energyPerHit);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getEnergyPerHit().addModifier(FloatModifiable.ModifierType.ADDITIVE, getName(), energyPerHit);
        }

    }

}