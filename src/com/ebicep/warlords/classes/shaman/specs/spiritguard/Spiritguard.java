package com.ebicep.warlords.classes.shaman.specs.spiritguard;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;
import org.bukkit.entity.Player;

public class Spiritguard extends AbstractShaman {
    public Spiritguard(Player player) {
        super(player, 5530, 305, 10,
                new FallenSouls(),
                new Chain("Spirit Link", -236, -446, 9, 40, 20, 175, "spirit link description"),
                new temp(),
                new Repentance(),
                new temp());
    }
}
