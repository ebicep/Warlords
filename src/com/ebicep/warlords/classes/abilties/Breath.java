package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class Breath extends AbstractAbility {

    public Breath(String name, int minDamageHeal, int maxDamageHeal, int cooldown, int energyCost, int critChance, int critMultiplier, String description) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, description);
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);

        if (name.contains("Water")) {
            warlordsPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
        }

        Vector viewDirection = player.getLocation().getDirection();
        List<Entity> near = player.getNearbyEntities(5.0D, 3.5D, 5.0D);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                Vector direction = nearPlayer.getLocation().subtract(player.getLocation()).toVector().normalize();
                if (viewDirection.dot(direction) > .72) {
                    if (name.contains("Water")) {
                        if (Warlords.game.onSameTeam(warlordsPlayer, Warlords.getPlayer(nearPlayer))) {
                            Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                        } else {
                            Location eye = player.getEyeLocation();
                            eye.setY(eye.getY() + .7);

                            final Location loc = entity.getLocation();
                            final Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-0.75).setY(0.3);

                            entity.setVelocity(v);;
                        }
                    } else if (name.contains("Freezing") && !Warlords.game.onSameTeam(warlordsPlayer, Warlords.getPlayer(nearPlayer))) {
                        Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                        Warlords.getPlayer(nearPlayer).getSpeed().changeCurrentSpeed("Freezing Breath", -35, 4 * 20);
                    }
                }
            }
            //TODO breath animation

            if (name.contains("Water")) {
                for (Player player1 : player.getWorld().getPlayers()) {
                    player1.playSound(player.getLocation(), "mage.waterbreath.activation", 1.5F, 1);
                }
            } else if (name.contains("Freezing")) {
                for (Player player1 : player.getWorld().getPlayers()) {
                    player1.playSound(player.getLocation(), "mage.freezingbreath.activation", 1.5F, 1);
                }
            }

        }
        warlordsPlayer.subtractEnergy(energyCost);
    }
}