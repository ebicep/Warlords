package com.ebicep.warlords.pve.items.types.specialitems.tome.delta;

import com.ebicep.warlords.abilities.ChainHeal;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractChain;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.tome.omega.GuideForTheRiverStyx;

import java.util.Set;

public class PansTome extends SpecialDeltaTome implements CraftsInto {

    public PansTome(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public PansTome() {

    }

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
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
            if (!(ability instanceof AbstractChain chain)) {
                continue;
            }
            chain.setBounceRange(100);
            int additionalBounces = chain.getAdditionalBounces();
            if (ability instanceof ChainHeal) {
                chain.setAdditionalBounces(additionalBounces + 1);
            } else {
                chain.setAdditionalBounces(additionalBounces + 2);
            }
        }

    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new GuideForTheRiverStyx(statPool);
    }
}
