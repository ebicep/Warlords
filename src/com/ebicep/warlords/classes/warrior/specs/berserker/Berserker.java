package com.ebicep.warlords.classes.warrior.specs.berserker;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;
import com.sun.jndi.ldap.Ber;
import org.bukkit.entity.Player;

public class Berserker extends AbstractWarrior {
    public Berserker(Player player) {
        super(player, 6300, 305, 0,
                new Strike("Wounding Strike Berserker", -596, -759, 0, 100, 20, 175,
                        "§7Strike the targeted enemy player,\n" +
                                "§7causing §c%dynamic.value% §7- §c%dynamic.value% §7damage\n" +
                                "§7and §cwounding §7them for §63 §7seconds.\n" +
                                "§7A wounded player receives §c35% §7less\n" +
                                "§7healing for the duration of the effect."),

                new temp(),
                new GroundSlam("Ground Slam", -449, -606, 1, 60, 15, 200, "ground slam description", Warlords.getPlayer(player)),
                new BloodLust(),
                new Berserk());
    }
}
