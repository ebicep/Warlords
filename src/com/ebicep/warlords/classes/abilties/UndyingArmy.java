package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class UndyingArmy extends AbstractAbility {

    public UndyingArmy() {
        super("Undying Army", 0, 0, 60 + 10, 20, 0, 0, "undying army description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.setUndyingArmy(10);
        warlordsPlayer.setUndyingArmyBy(warlordsPlayer);
        List<Entity> near = player.getNearbyEntities(4.0D, 4.0D, 4.0D);
        near.remove(player);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                    Warlords.getPlayer(nearPlayer).setUndyingArmy(10);
                    Warlords.getPlayer(nearPlayer).setUndyingArmyBy(warlordsPlayer);
                }
            }
        }


        // TODO: double check/fix armys activation sound
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), Sound.ZOMBIE_IDLE, 1, 1);
        }
    }
}
