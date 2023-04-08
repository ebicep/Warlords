package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.abilties.GroundSlam;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega.GlassKnuckles;

public class PendragonGauntlets extends SpecialDeltaGauntlet implements AppliesToWarlordsPlayer {
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
        return "Increases Ground Slam's critical chance to 100%.";
    }

    @Override
    public String getDescription() {
        return "For the worthy.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
            if (ability instanceof GroundSlam) {
                ability.setCritChance(100);
            }
        }
    }

    @Override
    public AbstractItem getCraftsInto() {
        return new GlassKnuckles();
    }
}
