package com.ebicep.warlords.player.cooldowns;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.GameRunnable;

import javax.annotation.Nonnull;

public abstract class CooldownRunnable extends GameRunnable {

    public int ticksLeft;

    public CooldownRunnable(@Nonnull Game game) {
        super(game);
    }

    public CooldownRunnable(@Nonnull Game game, boolean runInPauseMode) {
        super(game, runInPauseMode);
    }

    public CooldownRunnable(WarlordsPlayer warlordsPlayer) {
        super(warlordsPlayer.getGame());
    }

}
