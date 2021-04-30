package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class ArcaneShield extends AbstractAbility {

    public ArcaneShield() {
        super("Arcane Shield", 0, 0, 32, 40, 0, 0, "arcane shield description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.setArcaneShield(6);
        warlordsPlayer.setArcaneShieldHealth((int) (warlordsPlayer.getMaxHealth() * .5));
    }
}
