package com.ebicep.warlords.classes.mage.specs.pyromancer;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.ArcaneShield;
import com.ebicep.warlords.classes.abilties.Inferno;
import com.ebicep.warlords.classes.abilties.TimeWarp;
import com.ebicep.warlords.classes.abilties.temp;
import com.ebicep.warlords.classes.mage.AbstractMage;
import org.bukkit.entity.Player;

public class Pyromancer extends AbstractMage {
    public Pyromancer(Player player) {
        super(player, 5200, 305, 20, 14, 0,
                new temp(),
                new temp(),
                new TimeWarp(),
                new ArcaneShield(),
                new Inferno());
    }
}
