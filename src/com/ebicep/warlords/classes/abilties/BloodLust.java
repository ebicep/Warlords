package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class BloodLust extends AbstractAbility {

    public BloodLust() {
        super("Blood Lust", 0, 0, 32, 20, 0, 0, "blood lust description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.subtractEnergy(energyCost);
        warlordsPlayer.setBloodLust(15);
    }
}
