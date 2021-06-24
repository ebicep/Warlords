package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.entity.Player;

public class IceBarrier extends AbstractAbility {

    public IceBarrier() {
        super("Ice Barrier", 0, 0, 46.98f, 0, 0, 0
        );
    }

    @Override
    public void updateDescription() {
        description = "§7Surround yourself with a layer of\n" +
                "§7of cold air, reducing damage taken by\n" +
                "§c50%§7, While active, taking melee\n" +
                "§7damage reduces the attacker's movement\n" +
                "§7speed by §e20% §7for §62 §7seconds. Lasts\n" +
                "§66 §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        warlordsPlayer.setIceBarrier(6);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.icebarrier.activation", 2, 1);
        }
    }
}
