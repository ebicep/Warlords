package com.ebicep.warlords.classes.shaman.specs.spiritguard;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;
import org.bukkit.entity.Player;

public class Spiritguard extends AbstractShaman {
    public Spiritguard(Player player) {
        super(player, 5530, 305, 10,
                new FallenSouls(),
                new Chain("Spirit Link", -236, -446, 9, 40, 20, 175,
                        "§7Links your spirit with up to §c3 §7enemy\n" +
                                "§7players, dealing §c%dynamic.value% §7- §c%dynamic.value% §7damage\n" +
                                "§7to the first target hit. Each additional hit\n" +
                                "§7deals §c10% §7reduced damage. You gain §e40%\n" +
                                "§7speed for §61.5 §7seconds, and take §c20%\n" +
                                "§7reduced damage for §64.5 §7seconds."),
                new Soulbinding(),
                new Repentance(),
                new temp());
    }
}
