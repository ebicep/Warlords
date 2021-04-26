package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Berserk extends AbstractAbility {

    public Berserk() {
        super("Berserk", 0, 0, 47, 30, 0, 0, "berserker berserk");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.setBerserk(18);
        warlordsPlayer.subtractEnergy(energyCost);
    }
}
