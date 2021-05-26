package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class LightningRod extends AbstractAbility {

    public LightningRod() {
        super("Lightning Rod", 0, 0, 32, 0, 0, 0,
                "§7Call down an energizing bolt of lightning\n" +
                        "§7upon yourself, restoring §a30% §7health and\n" +
                        "§e160 §7energy and knock all nearby enemies back.");
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.addEnergy(warlordsPlayer, name, 160);
        warlordsPlayer.addHealth(warlordsPlayer, name, (int) (warlordsPlayer.getMaxHealth() * .3), (int) (warlordsPlayer.getMaxHealth() * .3), critChance, critMultiplier);

        Location playerLocation = player.getLocation();

        List<Entity> near = player.getNearbyEntities(5.0D, 5.0D, 5.0D);
        near = Utils.filterOutTeammates(near, player);

        for (Entity entity : near) {
            final Location otherLocation = entity.getLocation();
            if (entity instanceof Player && otherLocation.distanceSquared(playerLocation) < 30) {
                if (entity != player) {
                    //knockback
                    final Location loc = entity.getLocation();
                    final Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.5).setY(0.4);

                    entity.setVelocity(v);
                }
            }
        }

        pulseDamage(warlordsPlayer, near);

        // TODO: add effects around player with armorstands
        player.getWorld().spigot().strikeLightningEffect(playerLocation, true);
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.lightningrod.activation", 2, 1);
        }
    }

    private void pulseDamage(WarlordsPlayer warlordsPlayer, List<Entity> near) {
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                if (Utils.totemDownAndClose(warlordsPlayer, nearPlayer) && nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                    Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, warlordsPlayer.getSpec().getOrange().getName(), warlordsPlayer.getSpec().getOrange().getMinDamageHeal(), warlordsPlayer.getSpec().getOrange().getMaxDamageHeal(), warlordsPlayer.getSpec().getOrange().getCritChance(), warlordsPlayer.getSpec().getOrange().getCritMultiplier());
                }
            }
        }
    }
}
