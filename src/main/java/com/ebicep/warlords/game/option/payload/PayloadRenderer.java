package com.ebicep.warlords.game.option.payload;

import com.ebicep.warlords.game.Game;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public interface PayloadRenderer {

    void init(@Nonnull Game game);

    void move(Location newCenter);

    void playEffects(int ticksElapsed, Location center, double radius);

    void cleanup();

}
