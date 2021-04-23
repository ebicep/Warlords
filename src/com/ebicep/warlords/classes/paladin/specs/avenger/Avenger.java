package com.ebicep.warlords.classes.paladin.specs.avenger;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Avenger extends AbstractPaladin {

    public Avenger(Player player) {
        super(player, 6200, 305, 20, 20, 0,
                new Strike("Avenger's Strike", -427, -577, 0, 90, 25, 185, "avenger strike description"),
                new Consecrate(),
                new LightInfusion(16, "avenger infusion"),
                new HolyRadiance(20, 20, 15, 175, "avenger holy"),
                new AvengersWrath());
    }
}
