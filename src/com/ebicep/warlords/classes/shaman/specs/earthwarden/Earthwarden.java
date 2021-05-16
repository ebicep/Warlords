package com.ebicep.warlords.classes.shaman.specs.earthwarden;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;
import org.bukkit.entity.Player;

public class Earthwarden extends AbstractShaman {

    public Earthwarden(Player player) {
        super(player, 5530, 305, 10,
                new EarthenSpike(),
                new Boulder(),
                new Earthliving(),
                new Chain("Chain Heal", 454, 613, 8, 40, 20, 175,
                        "§7Discharge a beam of energizing lightning\n" +
                                "§7that heals you and a targeted friendly\n" +
                                "§7player for §a%value% §7- §a%value% §7health and\n" +
                                "§7jumps to §c2 §7additional targets within\n" +
                                "§e10 §7blocks. Each time the lightning jumps\n" +
                                "§7the healing is reduced by §c10%§7.\n" +
                                "\n" +
                                "§7Each ally healed reduces the cooldown of\n" +
                                "§7Boulder by §62 §7seconds."),

                new Totem.TotemEarthwarden());
    }

}