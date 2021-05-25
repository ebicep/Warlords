package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class Intervene extends AbstractAbility {

    public Intervene() {
        super("Intervene", 0, 0, 15, 20, 0, 0,
                "§7Protect the target ally, reducing\n" +
                "§7the damage they take by §e100%\n" +
                "§7and redirecting §e50% §7of the damage\n" +
                "§7they would have taken back to you.\n" +
                "§7You can protect the target for a maximum\n" +
                "§7of §c3600 §7damage. You must remain within\n" +
                "§e15 §7blocks of each other. Lasts §65 §7seconds.");
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        List<Entity> near = player.getNearbyEntities(6.0D, 6.0D, 6.0D);
        near = Utils.filterOnlyTeammates(near, player);
        System.out.println(near);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                if (nearPlayer.getGameMode() != GameMode.SPECTATOR && Utils.getLookingAt(player, nearPlayer)) {
                    WarlordsPlayer nearWarlordsPlayer = Warlords.getPlayer(nearPlayer);
                    warlordsPlayer.setIntervened(nearWarlordsPlayer);
                    warlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 You are now protecting " + nearWarlordsPlayer.getName() + " with your §eIntervene!");
                    nearWarlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 " + warlordsPlayer.getName() + "is shielding you with their " + ChatColor.YELLOW + "Intervene" + ChatColor.GRAY + "!");
                    nearWarlordsPlayer.setIntervenedBy(warlordsPlayer);
                    nearWarlordsPlayer.setInterveneDuration(6);
                    nearWarlordsPlayer.setInterveneDamage(0);
                }
            }
        }

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.intervene.impact", 1, 1);
        }
    }
}
