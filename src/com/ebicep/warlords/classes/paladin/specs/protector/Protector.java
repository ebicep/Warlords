package com.ebicep.warlords.classes.paladin.specs.protector;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Protector extends AbstractPaladin {

    public Protector(Player player) {
        super(player, 5750, 385, 0,
                new Strike("Protector's Strike", -261, -352, 0, 90, 20, 175,
                        "§7Strike the targeted enemy player,\n" +
                        "§7causing §c261 §7- §c352 §7damage\n" +
                        "§7and healing two nearby allies for\n" +
                        "§a100% §7of the damage done. Also\n" +
                        "§7heals yourself by §a50% §7of the\n" +
                        "§7damage done."),

                new Consecrate(-96, -130, 10, 15, 200),

                new LightInfusion(16,
                        "§7You become infused with light,\n" +
                        "§7restoring §a120 §7energy and\n" +
                        "§7increasing your movement speed by\n" +
                        "§e40% §7for §63 §7seconds"),

                new HolyRadiance(20, 20, 15, 175,
                        "§7Radiate with holy energy, healing\n" +
                        "§7yourself and all nearby allies for\n" +
                        "§a%dynamic.value% §7- §a%dynamic.value% §7health."),
                new HammerOfLight());
    }

}
