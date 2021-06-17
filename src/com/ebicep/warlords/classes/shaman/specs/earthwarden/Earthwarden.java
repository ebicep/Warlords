package com.ebicep.warlords.classes.shaman.specs.earthwarden;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;
import org.bukkit.entity.Player;

public class Earthwarden extends AbstractShaman {

    public Earthwarden(Player player) {
        super(player, 5530, 305, 10,
                new EarthenSpike(),
                new Boulder(),
                new Earthliving(),
                new Chain("Chain Heal", 454, 613, 8, 40, 20, 175),
                new Totem.TotemEarthwarden());
    }

}