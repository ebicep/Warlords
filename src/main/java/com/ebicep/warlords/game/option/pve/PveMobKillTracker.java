package com.ebicep.warlords.game.option.pve;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;

public final class PveMobKillTracker {

    private PveMobKillTracker() {
    }

    public static void recordKill(WarlordsEntity killer, WarlordsEntity dead, AbstractMob mob) {
        if (!(killer instanceof WarlordsPlayer) || mob == null || dead == null) {
            return;
        }
        killer.getMinuteStats().addMobKill(mob.getName());
        dead.getHitBy().forEach((assisted, value) -> assisted.getMinuteStats().addMobAssist(mob.getName()));
    }
}
