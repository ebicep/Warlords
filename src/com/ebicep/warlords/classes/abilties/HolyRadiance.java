package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class HolyRadiance extends AbstractAbility {

    public HolyRadiance(int cooldown, int energyCost, int critChance, int critMultiplier, String description) {
        super("Holy Radiance", 582, 760, cooldown, energyCost, critChance, critMultiplier, description);
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        List<Entity> near = player.getNearbyEntities(7.0D, 7.0D, 7.0D);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                double distance = player.getLocation().distanceSquared(nearPlayer.getLocation());
                if (nearPlayer.getGameMode() != GameMode.SPECTATOR && distance < 5 * 5) {
                    Warlords.getPlayer(nearPlayer).addHealth(minDamageHeal, maxDamageHeal, critChance, critMultiplier);

                }
            }
        }
        warlordsPlayer.subtractEnergy(energyCost);
        warlordsPlayer.addHealth(minDamageHeal, maxDamageHeal, critChance, critMultiplier);
    }
}
