package com.ebicep.warlords.classes.shaman.earthwarden;

import com.ebicep.warlords.classes.abilties.Boulder;
import com.ebicep.warlords.classes.abilties.EarthenSpike;
import com.ebicep.warlords.classes.abilties.temp;
import com.ebicep.warlords.classes.shaman.AbstractShaman;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Earthwarden extends AbstractShaman {

    public Earthwarden(Player player) {
        super(new EarthenSpike(), new Boulder(), new temp(), new temp(), new temp(), 6200, 305,player);
    }

}