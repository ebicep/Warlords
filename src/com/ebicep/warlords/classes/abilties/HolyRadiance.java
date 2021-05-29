package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.Matrix4d;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class HolyRadiance extends AbstractAbility {

    public HolyRadiance(int cooldown, int energyCost, int critChance, int critMultiplier, String description) {
        super("Holy Radiance", 582, 760, cooldown, energyCost, critChance, critMultiplier, description);
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        List<Entity> near = player.getNearbyEntities(6.0D, 6.0D, 6.0D);
        near = Utils.filterOnlyTeammates(near, player);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                double distance = player.getLocation().distanceSquared(nearPlayer.getLocation());
                if (nearPlayer.getGameMode() != GameMode.SPECTATOR && distance < 3 * 3) {
                    Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                }
            }
        }

        warlordsPlayer.subtractEnergy(energyCost);
        warlordsPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.holyradiance.activation", 1.5F, 1);
        }

        for (int i = 0; i < 10; i++) {
            Location particleLoc = player.getLocation().add(0, 1.2, 0);
            ParticleEffect.VILLAGER_HAPPY.display(1F, 1F, 1F, 0.1F, 2, particleLoc, 500);
            ParticleEffect.FIREWORKS_SPARK.display(1F, 1F, 1F, 0.1F, 1, particleLoc, 500);
        }
    }
}
