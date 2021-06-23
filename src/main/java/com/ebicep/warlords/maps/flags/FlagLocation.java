/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebicep.warlords.maps.flags;

import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author fernando
 */
public interface FlagLocation {

    @Nonnull
    Location getLocation();

    @Nullable
    FlagLocation update(@Nonnull FlagInfo info);

}
