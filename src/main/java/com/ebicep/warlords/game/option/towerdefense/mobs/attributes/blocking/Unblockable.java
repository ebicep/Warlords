package com.ebicep.warlords.game.option.towerdefense.mobs.attributes.blocking;

import com.ebicep.warlords.player.ingame.WarlordsNPC;

public class Unblockable implements TDBlockingMode {

    public static final TDBlockingMode DEFAULT = new Unblockable();

    @Override
    public boolean filterDefender(WarlordsNPC warlordsNPC) {
        return false;
    }

    @Override
    public boolean filterAttacker(WarlordsNPC warlordsNPC) {
        return false;
    }

}
