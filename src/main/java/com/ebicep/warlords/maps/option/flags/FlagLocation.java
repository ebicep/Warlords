/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebicep.warlords.maps.option.flags;

import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface FlagLocation {

    @Nonnull
    Location getLocation();

    @Nullable
    FlagLocation update(@Nonnull FlagInfo info);
	
}
