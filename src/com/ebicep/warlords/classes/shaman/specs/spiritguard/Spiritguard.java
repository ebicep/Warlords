package com.ebicep.warlords.classes.shaman.specs.spiritguard;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.Repentance;
import com.ebicep.warlords.classes.abilties.Strike;
import com.ebicep.warlords.classes.abilties.temp;
import com.ebicep.warlords.classes.shaman.AbstractShaman;
import org.bukkit.entity.Player;

public class Spiritguard extends AbstractShaman {
    public Spiritguard(Player player) {
        super(player, 5530, 305, 10,
                new Strike("Avenger's Strike", -427, -577, 0, 90, 25, 185, "avenger strike description"),
                new temp(),
                new temp(),
                new Repentance(),
                new temp());
    }
}
