package com.ebicep.warlords.classes.warrior.specs.defender;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.Intervene;
import com.ebicep.warlords.classes.abilties.LastStand;
import com.ebicep.warlords.classes.abilties.Strike;
import com.ebicep.warlords.classes.abilties.temp;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;
import org.bukkit.entity.Player;

public class Defender extends AbstractWarrior {

    public Defender(Player player) {
        super(player, 7400, 305, 10,
                new Strike("Wounding Strike Defender", -498, -667, 0, 100, 20, 200,
                        "§7Strike the targeted enemy player,\n" +
                        "§7causing §c%dynamic.value% §7- §c%dynamic.value% §7damage\n" +
                        "§7and §cwounding §7them for §63 §7seconds.\n" +
                        "§7A wounded player receives §c25% §7less\n" +
                        "§7healing for the duration of the effect."),

                new temp(),
                new temp(),
                new Intervene(),
                new LastStand());
    }

}
