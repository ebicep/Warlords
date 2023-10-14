package com.ebicep.warlords.game.option.pvp.siege;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface SiegeState {

    void start(@Nonnull Game game);

    /**
     * @param ticksElapsed the amount of ticks that have elapsed since the state started
     * @return whether the state should end
     */
    boolean tick(int ticksElapsed);

    /**
     *
     */
    void end();

    /**
     * @return the next state to transition to
     */
    SiegeState getNextState();

    Component getSidebarComponent(int ticksElapsed);

    /**
     * -1 if no time limit
     *
     * @return the max amount of seconds this state can last
     */
    int maxSeconds();

    default void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {

    }

}
