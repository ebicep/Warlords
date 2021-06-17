package com.ebicep.warlords.classes.paladin.specs.crusader;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;
import org.bukkit.entity.Player;

public class Crusader extends AbstractPaladin {

    public Crusader(Player player) {
        super(player, 6850, 305, 20,
                new Strike("Crusader's Strike", -326, -441, 0, 90, 25, 175,
                        "§7Strike the targeted enemy player,\n" +
                                "§7causing §c326 §7- §c441 damage\n" +
                                "§7and restoring §e24 §7energy to two nearby\n" +
                                "§7within §e10 §7blocks."),

                new Consecrate(-144, -194.4f, 50, 15, 200),

                new LightInfusion(16,
                        "§7You become infused with light,\n" +
                                "§7restoring §a120 §7energy and\n" +
                                "§7increasing your movement speed by\n" +
                                "§e40% §7for §63 §7seconds"),

                new HolyRadiance(20, 20, 15, 175,
                        "§7Radiate with holy energy, healing\n" +
                                "§7yourself and all nearby allies for\n" +
                                "§a582 §7- §a760 §7health."),

                new InspiringPresence());
    }

}
