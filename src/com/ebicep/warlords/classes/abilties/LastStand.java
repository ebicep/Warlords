package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class LastStand extends AbstractAbility {

    public LastStand() {
        super("Last Stand", 0, 0, 58, 40, 0, 0,
                "§7Enter a defensive stance,\n" +
                        "§7reducing all damage you take by\n" +
                        "§c50% §7for §612 §7seconds and also\n" +
                        "§7reduces all damage nearby allies take\n" +
                        "§7by §c40% §7for §66 §7seconds. You are\n" +
                        "§ahealed §7for the amount of damage\n" +
                        "§7prevented on allies.");
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.setLastStandedBy(warlordsPlayer);
        warlordsPlayer.setLastStand(12);
        List<Entity> near = player.getNearbyEntities(4.0D, 4.0D, 4.0D);
        near = Utils.filterOnlyTeammates(near, player);
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

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.laststand.activation", 1, 1);
        }
    }
}
