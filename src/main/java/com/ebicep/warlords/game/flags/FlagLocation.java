/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebicep.warlords.game.flags;

import com.ebicep.warlords.events.game.WarlordsFlagUpdatedEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface FlagLocation {

    @Nonnull
    Location getLocation();

    @Nullable
    FlagLocation update(@Nonnull FlagInfo info);

    @Nonnull
    List<TextComponent> getDebugInformation();

    default void onFlagUpdateEventOld(WarlordsFlagUpdatedEvent event) {

    }

    default void onFlagUpdateEventNew(WarlordsFlagUpdatedEvent event) {

    }

}
