package com.ebicep.warlords.classes.paladin.specs.protector;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;
import org.bukkit.entity.Player;

public class Protector extends AbstractPaladin {

    public Protector(Player player) {
        super(player, 5750, 385, 0,
                new Strike("Protector's Strike", -261, -352, 0, 90, 20, 175),
                new Consecrate(-96, -130, 10, 15, 200, 15),
                new LightInfusion(16),
                new HolyRadiance(10, 30, 15, 175),
                new HammerOfLight());
    }

}
