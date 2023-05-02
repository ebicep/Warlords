package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.abilties.GroundSlam;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega.GlassKnuckles;

import java.util.Set;

public class PendragonGauntlets extends SpecialDeltaGauntlet implements AppliesToWarlordsPlayer {
    public PendragonGauntlets(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public PendragonGauntlets() {

    }

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
    }

    @Override
    public String getName() {
        return "Pendragon Gauntlets";
    }

    @Override
    public String getBonus() {
        return "Increases Ground Slam's critical chance by 25%.";
    }

    @Override
    public String getDescription() {
        return "For the worthy.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
            if (ability instanceof GroundSlam) {
                ability.setCritChance(ability.getCritChance() + 25);
            }
        }
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new GlassKnuckles(statPool);
    }
}
