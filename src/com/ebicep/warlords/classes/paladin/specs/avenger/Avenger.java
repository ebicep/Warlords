package com.ebicep.warlords.classes.paladin.specs.avenger;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Avenger extends AbstractPaladin {

    public Avenger(Player player) {
        super(new temp(),new Consecrate(), new LightInfusion(16, "avenger infusion"), new HolyRadiance(20,20,15,175, "avenger holy"), new AvengersWrath(), 6200, 305,player);
    }

    @Override
    public void onRightClick(PlayerInteractEvent e) {

    }
}
