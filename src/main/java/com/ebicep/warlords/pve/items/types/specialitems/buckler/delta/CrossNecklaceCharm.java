package com.ebicep.warlords.pve.items.types.specialitems.buckler.delta;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractConsecrate;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.omega.BreastplateBuckler;

import java.util.Set;

public class CrossNecklaceCharm extends SpecialDeltaBuckler implements CraftsInto {

    public CrossNecklaceCharm() {
    }

    public CrossNecklaceCharm(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getName() {
        return "Cross Necklace Chakram";
    }

    @Override
    public String getBonus() {
        return "Consecrate lasts 3 more seconds and increase your strike damage by an additional 5%.";
    }

    @Override
    public String getDescription() {
        return "Exorcism on the go!";
    }


    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
            if (ability instanceof AbstractConsecrate consecrate) {
                consecrate.setTickDuration(consecrate.getTickDuration() + 60);
                consecrate.setStrikeDamageBoost(consecrate.getStrikeDamageBoost() + 5);
            }
        }
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new BreastplateBuckler(statPool);
    }
}
