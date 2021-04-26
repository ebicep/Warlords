package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;


public class InspiringPresence extends AbstractAbility {

    public InspiringPresence() {
        super("Inspiring Presence", 0, 0, 60 + 10, 0, 0, 0, "Inspiring Presence description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        player.setWalkSpeed(WarlordsPlayer.presenceSpeed);
        warlordsPlayer.setPresence(12);
        player.playSound(player.getLocation(), "paladin.inspiringpresence.activation", 1, 1);

        List<Entity> near = player.getNearbyEntities(6.0D, 2.0D, 6.0D);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Warlords.getPlayer(player).setPresence(12);
            }
        }
    }
}