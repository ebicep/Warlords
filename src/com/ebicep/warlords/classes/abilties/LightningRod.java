package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class LightningRod extends AbstractAbility {

    public LightningRod() {
        super("Lightning Rod", 0, 0, 32, 0, 0, 0, "lightning rod description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.subtractEnergy(-160);
        warlordsPlayer.addHealth(warlordsPlayer, name, (int) (warlordsPlayer.getMaxHealth() * .3), (int) (warlordsPlayer.getMaxHealth() * .3), critChance, critMultiplier);

    }
}
