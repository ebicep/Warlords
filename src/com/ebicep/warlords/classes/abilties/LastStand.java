package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class LastStand extends AbstractAbility {

    public LastStand() {
        super("Last Stand", 0, 0, 58, 40, 0, 0, "last stand description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.setLastStand(12);
        List<Entity> near = player.getNearbyEntities(4.0D, 4.0D, 4.0D);
        near.remove(player);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                    Warlords.getPlayer(nearPlayer).setLastStand(6);
                    Warlords.getPlayer(nearPlayer).setLastStandedBy(warlordsPlayer);
                    player.sendMessage("you last standed " + nearPlayer.getName());
                }
            }
        }
        warlordsPlayer.subtractEnergy(energyCost);

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "warrior.laststand.activation", 1, 1);
        }
    }
}
