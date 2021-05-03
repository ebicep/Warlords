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

public class Intervene extends AbstractAbility {

    public Intervene() {
        super("Intervene", 0, 0, 15, 20, 0, 0, "intervene description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        //TODO change intervene disntace?
        List<Entity> near = player.getNearbyEntities(5.0D, 5.0D, 5.0D);
        System.out.println(near);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                if (nearPlayer.getGameMode() != GameMode.SPECTATOR && Utils.getLookingAt(player, nearPlayer)) {
                    WarlordsPlayer nearWarlordsPlayer = Warlords.getPlayer(nearPlayer);
                    warlordsPlayer.setIntervened(nearWarlordsPlayer);
                    warlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 You are now protecting " + nearWarlordsPlayer.getName() + " with your §eIntervene!");
                    nearWarlordsPlayer.setIntervenedBy(warlordsPlayer);
                    nearWarlordsPlayer.setIntervene(6);
                    nearWarlordsPlayer.setInterveneDamage(0);
                }
            }
        }

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "warrior.intervene.impact", 1, 1);
        }
    }
}
