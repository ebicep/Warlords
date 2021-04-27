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
                new Strike("Wounding Strike Defender", -495, -662, 0, 100, 20, 200, "defending wounding strike description"),
                new temp(),
                new temp(),
                new Intervene(),
                new LastStand());
    }

}
