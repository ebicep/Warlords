package com.ebicep.warlords.classes.shaman.specs.earthwarden;

import com.ebicep.warlords.classes.abilties.Boulder;
import com.ebicep.warlords.classes.abilties.EarthenSpike;
import com.ebicep.warlords.classes.abilties.TotemEarthwarden;
import com.ebicep.warlords.classes.abilties.temp;
import com.ebicep.warlords.classes.shaman.AbstractShaman;
import org.bukkit.entity.Player;

public class Earthwarden extends AbstractShaman {

    public Earthwarden(Player player) {
        super(player, 6200, 305, 10,
                new EarthenSpike(),
                new Boulder(),
                new temp(),
                new temp(),
                new TotemEarthwarden());
    }

}