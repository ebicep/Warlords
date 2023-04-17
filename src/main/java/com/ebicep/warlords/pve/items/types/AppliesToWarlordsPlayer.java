package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

public interface AppliesToWarlordsPlayer {

    void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption);


}
