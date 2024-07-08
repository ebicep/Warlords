package com.ebicep.warlords.player.ingame.instances.type;

import net.kyori.adventure.text.Component;

import javax.annotation.Nullable;

public interface DebugInstance {

    @Nullable
    default Component getDebugMessage() {
        return null;
    }

}
