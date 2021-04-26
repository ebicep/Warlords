package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Earthliving extends AbstractAbility {

    public Earthliving() {
        super("Earthliving Weapon", 0, 0, 16, 30, 25, 240, "earthliving weapon");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.subtractEnergy(energyCost);
        warlordsPlayer.setEarthliving(8);
    }
}
