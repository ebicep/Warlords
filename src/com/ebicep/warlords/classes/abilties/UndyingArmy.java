package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class UndyingArmy extends AbstractAbility {

    public UndyingArmy() {
        super("Undying Army", 0, 0, 60 + 10, 20, 0, 0,
                "§7When you or nearby allies take\n" +
                        "§7fatal damage within §610 §7seconds,\n" +
                        "§7instantly restore them to §a100% §7health\n" +
                        "§7instead. They will take §c500 §7TRUE DAMAGE\n" +
                        "§7every second for the rest of their life.\n" +
                        "§7Allies not revived will heal for §a200 §7+\n" +
                        "§a35% §7of their missing health §610 §7seconds\n" +
                        "§7after this abilty was cast.");
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.setUndyingArmy(10);
        warlordsPlayer.setUndyingArmyBy(warlordsPlayer);
        List<Entity> near = player.getNearbyEntities(4.0D, 4.0D, 4.0D);
        near = Utils.filterOnlyTeammates(near, player);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                    Warlords.getPlayer(nearPlayer).setUndyingArmy(10);
                    Warlords.getPlayer(nearPlayer).setUndyingArmyBy(warlordsPlayer);
                }
            }
        }

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), Sound.ZOMBIE_IDLE, 1, 1.1f);
        }
    }
}
