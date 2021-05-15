package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class IceBarrier extends AbstractAbility {

    public IceBarrier() {
        super("Ice Barrier", 0, 0, 47, 0, 0, 0,
                "§7Surround yourself with a layer of\n" +
                        "§7of cold air, reducing damage taken by\n" +
                        "§c50%§7, While active, taking melee\n" +
                        "damage reduces the attacker's movement\n" +
                        "speed by §e20% §7for §62 §7seconds. Lasts\n" +
                        "§66 §7seconds.");
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.setIceBarrier(6 * 20 - 10);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.icebarrier.activation", 1, 1);
        }
    }
}
