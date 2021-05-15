package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class BloodLust extends AbstractAbility {

    public BloodLust() {
        super("Blood Lust", 0, 0, 32, 20, 0, 0,
                "§7You lust for blood, healing yourself\n" +
                "§7for §a65% §7of all the damage you deal.\n" +
                "§7Lasts §615 §7seconds.");
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.subtractEnergy(energyCost);
        warlordsPlayer.setBloodLust(15);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.bloodlust.activation", 1, 1);
        }
    }
}
