package com.ebicep.warlords.pve.items.types.specialitems.buckler.delta;

import com.ebicep.warlords.abilties.Consecrate;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

public class CrossNecklaceCharm extends SpecialDeltaBuckler {

    @Override
    public String getName() {
        return "Cross Necklace Chakram";
    }

    @Override
    public String getBonus() {
        return "Consecrate lasts 2 more seconds.";
    }

    @Override
    public String getDescription() {
        return "Exorcism on the go!";
    }

    @Override
    public Classes getClasses() {
        return Classes.PALADIN;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
            if (ability instanceof Consecrate) {
                Consecrate consecrate = (Consecrate) ability;
                consecrate.setTickDuration(consecrate.getTickDuration() + 40);
            }
        }
    }

}
