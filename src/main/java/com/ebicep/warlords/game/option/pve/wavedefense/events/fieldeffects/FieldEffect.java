package com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.text.Component;

import java.util.Collections;
import java.util.List;

public interface FieldEffect {

    String getName();

    String getDescription();

    default List<Component> getSubDescription() {
        return Collections.emptyList();
    }

    default void onStart(Game game) {

    }

    default void onWarlordsEntityCreated(WarlordsEntity player) {

    }

    default void afterAllWarlordsEntitiesCreated(List<WarlordsEntity> players) {

    }

    default void run(Game game, int ticksElapsed) {

    }

}
