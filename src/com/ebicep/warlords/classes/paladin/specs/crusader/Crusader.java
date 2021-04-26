package com.ebicep.warlords.classes.paladin.specs.crusader;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Crusader extends AbstractPaladin {

    public Crusader(Player player) {
        super(player, 6200, 305, 20,
                new Strike("Crusader's Strike", -326, -441, 0, 90, 25, 175, "crusader strike description"),
                new Consecrate(-144, -194, 50, 15, 200),
                new LightInfusion(16, "crusader infusion"),
                new HolyRadiance(20, 20, 15, 175, "crusader holy"),
                new InspiringPresence());
    }

}
