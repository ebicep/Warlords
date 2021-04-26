package com.ebicep.warlords.classes.paladin.specs.protector;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Protector extends AbstractPaladin {

    public Protector(Player player) {
        super(player, 5750, 385, 0,
                new Strike("Protector's Strike", -261, -352, 0, 90, 20, 175, "protector strike description"),
                new Consecrate(-96, -130, 10, 15, 200),
                new LightInfusion(16, "avenger infusion"),
                new HolyRadiance(20, 20, 15, 175, "avenger holy"),
                new HammerOfLight());
    }

}
