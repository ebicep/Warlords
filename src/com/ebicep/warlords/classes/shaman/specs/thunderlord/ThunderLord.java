package com.ebicep.warlords.classes.shaman.specs.thunderlord;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;
import org.bukkit.entity.Player;

public class ThunderLord extends AbstractShaman {
    public ThunderLord(Player player) {
        super(player, 5200, 305, 0,
                new LightningBolt(),
                new Chain("Chain Lightning", -294, -575, 10, 40, 20, 175,
                        "§7Discharge a bolt of lightning at the\n" +
                                "§7targeted enemy player that deals\n" +
                                "§c294 §7- §c575 §7damage and jumps to\n" +
                                "§e4 §7additional targets within §e15\n" +
                                "§7blocks. Each time the lightning jumps\n" +
                                "§7the damage is decreased by §c15%§7.\n" +
                                "§7You gain §e10% §7damage resistance for\n" +
                                "§7each target hit, up to §e30% §7damage\n" +
                                "§7resistance. This buff lasts §64.5 §7seconds."),
                new Windfury(),
                new LightningRod(),
                new Totem.TotemThunderlord());
    }
}
