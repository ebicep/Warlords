package com.ebicep.warlords.classes.paladin.specs.avenger;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;
import org.bukkit.entity.Player;

public class Avenger extends AbstractPaladin {

    public Avenger(Player player) {
        super(player, 6300, 305, 0,
                new Strike("Avenger's Strike", -431, -582, 0, 90, 25, 185,
                        "§7Strike the targeted enemy player,\n" +
                        "§7causing §c%dynamic.value% §7- §c%dynamic.value% §7damage\n" +
                        "§7and removing §e6 §7energy."),

                new Consecrate(-158, -214, 50, 20, 175),

                new LightInfusion(16,
                        "§7You become infused with light,\n" +
                        "§7restoring §a120 §7energy and\n" +
                        "§7increasing your movement speed by\n" +
                        "§e40% §7for §63 §7seconds"),

                new HolyRadiance(20, 20, 15, 175,
                        "§7Radiate with holy energy, healing\n" +
                        "§7yourself and all nearby allies for\n" +
                        "§a582 §7- §a760 §7health."),
                new AvengersWrath());
    }
}
