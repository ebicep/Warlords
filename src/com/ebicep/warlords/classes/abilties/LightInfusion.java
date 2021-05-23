package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class LightInfusion extends AbstractAbility {

    public LightInfusion(int cooldown, String description) {
        super("Light Infusion", 0, 0, cooldown, -120, 0, 0, description);
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.getSpeed().changeCurrentSpeed("Infusion", 40, 3 * 20);
        warlordsPlayer.setInfusion(3 * 20);
        warlordsPlayer.subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.infusionoflight.activation", 2, 1);
        }
    }
}
