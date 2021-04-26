package com.ebicep.warlords.classes.warrior.specs.berserker;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.Berserk;
import com.ebicep.warlords.classes.abilties.BloodLust;
import com.ebicep.warlords.classes.abilties.Strike;
import com.ebicep.warlords.classes.abilties.temp;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;
import com.sun.jndi.ldap.Ber;
import org.bukkit.entity.Player;

public class Berserker extends AbstractWarrior {
    public Berserker(Player player) {
        super(player, 6300, 305, 0,
                new Strike("Wounding Strike Berserker", -596, -759, 0, 100, 20, 175, "berserker wounding strike description"),
                new temp(),
                new temp(),
                new BloodLust(),
                new Berserk());
    }
}
