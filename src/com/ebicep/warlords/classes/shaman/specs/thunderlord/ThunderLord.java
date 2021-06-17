package com.ebicep.warlords.classes.shaman.specs.thunderlord;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;
import org.bukkit.entity.Player;

public class ThunderLord extends AbstractShaman {
    public ThunderLord(Player player) {
        super(player, 5200, 305, 0,
                new LightningBolt(),
                new Chain("Chain Lightning", -294, -575, 10, 40, 20, 175),
                new Windfury(),
                new LightningRod(),
                new Totem.TotemThunderlord());
    }
}
