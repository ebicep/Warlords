package com.ebicep.warlords.classes.shaman.specs.thunderlord;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.LightningRod;
import com.ebicep.warlords.classes.abilties.Strike;
import com.ebicep.warlords.classes.abilties.Windfury;
import com.ebicep.warlords.classes.abilties.temp;
import com.ebicep.warlords.classes.shaman.AbstractShaman;
import org.bukkit.entity.Player;

public class ThunderLord extends AbstractShaman {
    public ThunderLord(Player player) {
        super(player, 5200, 305, 0,
                new Strike("Avenger's Strike", -427, -577, 0, 90, 25, 185, "avenger strike description"),
                new temp(),
                new Windfury(),
                new LightningRod(),
                new temp());
    }
}
