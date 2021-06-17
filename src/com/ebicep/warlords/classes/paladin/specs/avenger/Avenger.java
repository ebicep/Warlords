package com.ebicep.warlords.classes.paladin.specs.avenger;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;
import org.bukkit.entity.Player;

public class Avenger extends AbstractPaladin {

    public Avenger(Player player) {
        super(player, 6300, 305, 0,
                new Strike("Avenger's Strike", -359, -485, 0, 90, 25, 185),
                new Consecrate(-158.4f, -213.6f, 50, 20, 175, 20),
                new LightInfusion(16),
                new HolyRadiance(20, 20, 15, 175),
                new AvengersWrath());
    }
}
