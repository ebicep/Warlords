package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class LightInfusion extends AbstractAbility {

    public LightInfusion(int cooldown, String description) {
        super("Light Infusion", 0, 0, cooldown, -120, 0, 0, description);
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        player.setWalkSpeed(WarlordsPlayer.infusionSpeed);
        warlordsPlayer.setInfusion(3);
        warlordsPlayer.subtractEnergy(energyCost);
    }
}
