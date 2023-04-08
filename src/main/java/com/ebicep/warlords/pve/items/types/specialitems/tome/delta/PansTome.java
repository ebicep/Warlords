package com.ebicep.warlords.pve.items.types.specialitems.tome.delta;

import com.ebicep.warlords.abilties.ChainHeal;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.AbstractChainBase;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

public class PansTome extends SpecialDeltaTome {

    @Override
    public String getName() {
        return "Pan's Tome";
    }

    @Override
    public String getBonus() {
        return "Chain abilities guarantee to hit their max amount of targets.";
    }

    @Override
    public String getDescription() {
        return "Born to be wild.";
    }

    @Override
    public Classes getClasses() {
        return Classes.SHAMAN;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
            if (!(ability instanceof AbstractChainBase)) {
                continue;
            }
            AbstractChainBase chain = (AbstractChainBase) ability;
            chain.setBounceRange(100);
            int additionalBounces = chain.getAdditionalBounces();
            if (ability instanceof ChainHeal) {
                chain.setAdditionalBounces(additionalBounces + 1);
            } else {
                chain.setAdditionalBounces(additionalBounces + 2);
            }
        }

    }
}
