package com.ebicep.warlords.classes.warrior.specs.revenant;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;
import org.bukkit.entity.Player;

public class Revenant extends AbstractWarrior {
    public Revenant(Player player) {
        super(player, 6300, 305, 0,
                new Strike("Crippling Strike", -362, -483, 0, 100, 15, 200,
                        "§7Strike the targeted enemy player,\n" +
                        "§7causing §c%dynamic.value% §7- §c%dynamic.value% §7damage\n" +
                        "§7and §ccrippling §7them for §63 §7seconds.\n" +
                        "§7A §ccrippled §7player deals §c12.5% §7less\n" +
                        "§7damage for the duration of the effect."),

                new RecklessCharge(),
                new temp(),
                new OrbsOfLife(),
                new UndyingArmy());
    }
}
