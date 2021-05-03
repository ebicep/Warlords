package com.ebicep.warlords.classes.warrior.specs.revenant;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;
import org.bukkit.entity.Player;

public class Revenant extends AbstractWarrior {
    public Revenant(Player player) {
        super(player, 6300, 305, 0,
                new Strike("Crippling Strike", -362, -483, 0, 100, 15, 200, "crippling strike description"),
                new RecklessCharge(),
                new temp(),
                new OrbsOfLife(),
                new UndyingArmy());
    }
}
