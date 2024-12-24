package com.ebicep.holograms;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class HologramText extends Hologram {

    public HologramText(String name, Location location, Function<Player, HologramData> playerDataFunction) {
        super(name, location, playerDataFunction);
    }

}
