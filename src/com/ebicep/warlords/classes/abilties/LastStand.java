package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
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
        List<Entity> near = player.getNearbyEntities(7.0D, 7.0D, 7.0D);
        near.remove(player);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                double distance = player.getLocation().distanceSquared(nearPlayer.getLocation());
                if (nearPlayer.getGameMode() != GameMode.SPECTATOR && distance < 5 * 5) {
                    Warlords.getPlayer(nearPlayer).setLastStand(6);
                }
            }
        }
        warlordsPlayer.subtractEnergy(energyCost);
    }
}
