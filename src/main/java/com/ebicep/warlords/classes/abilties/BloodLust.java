package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.entity.Player;

public class BloodLust extends AbstractAbility {

    public BloodLust() {
        super("Blood Lust", 0, 0, 31.32f, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7You lust for blood, healing yourself\n" +
                "§7for §a65% §7of all the damage you deal.\n" +
                "§7Lasts §615 §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        warlordsPlayer.subtractEnergy(energyCost);
        warlordsPlayer.setBloodLustDuration(15);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.bloodlust.activation", 2, 1);
        }
    }
}
