package com.ebicep.warlords.classes.shaman.specs.spiritguard;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;
import org.bukkit.entity.Player;

public class Spiritguard extends AbstractShaman {
    public Spiritguard(Player player) {
        super(player, 10000, 305, 10,
                new FallenSouls(),
                new Chain("Spirit Link", -236.25f, -446.25f, 8.61f, 40, 20, 175),
                new Soulbinding(),
                new Repentance(),
                new Totem.TotemSpiritguard());
    }
}
