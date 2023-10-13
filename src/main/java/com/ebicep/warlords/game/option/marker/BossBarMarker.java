package com.ebicep.warlords.game.option.marker;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.bossbar.BossBar;

public interface BossBarMarker extends GameMarker {

    int getPriority(WarlordsEntity player);

    BossBar getBossBar();

}
