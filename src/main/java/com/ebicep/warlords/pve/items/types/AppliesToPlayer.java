package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;

public interface AppliesToPlayer {

    void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer);

    String getEffect();

    String getEffectDescription();


}
