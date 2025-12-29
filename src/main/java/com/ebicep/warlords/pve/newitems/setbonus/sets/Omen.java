package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Omen extends BaseSet {

    private int maxHealthBoost;

    @Override
    public void init() {
        super.init();
        this.maxHealthBoost = getValue("maxHealthBoost", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "omen";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(maxHealthBoost);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getHealth().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(), 1 + maxHealthBoost / 100f);
        }

    }

}