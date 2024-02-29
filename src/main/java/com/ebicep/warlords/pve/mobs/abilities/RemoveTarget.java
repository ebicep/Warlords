package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;

import javax.annotation.Nonnull;

public class RemoveTarget extends AbstractPveAbility {

    public RemoveTarget(float cooldown) {
        super(
                "Remove Target",
                cooldown,
                100
        );
    }

    @Override
    public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
        if (wp instanceof WarlordsNPC warlordsNPC) {
            warlordsNPC.getMob().removeTarget();
        }
        return true;
    }
}
