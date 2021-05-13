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
        Vector viewDirection = player.getLocation().getDirection();
        List<Entity> near = player.getNearbyEntities(7.0D, 3.5D, 7.0D);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                Vector direction = nearPlayer.getLocation().subtract(player.getLocation()).toVector().normalize();
                if (viewDirection.dot(direction) > .73 && Warlords.getInstance().game.onSameTeam(warlordsPlayer, Warlords.getPlayer(nearPlayer))) {
                    Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                    if (name.contains("Water")) {
                        Location eye = player.getEyeLocation();
                        eye.setY(eye.getY() + .5);
                        //TODO fix kb
                        Vector toEntity = nearPlayer.getEyeLocation().toVector().subtract(eye.toVector());
                        nearPlayer.setVelocity(toEntity);
                        warlordsPlayer.subtractEnergy(energyCost);
                    }
                } else if (viewDirection.dot(direction) > .73 && !Warlords.getInstance().game.onSameTeam(warlordsPlayer, Warlords.getPlayer(nearPlayer))) {
                    Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                    if (name.contains("Freezing")) {
                        nearPlayer.setWalkSpeed(WarlordsPlayer.currentSpeed);
                        warlordsPlayer.setBreathSlowness(4 * 20 - 10);
                        warlordsPlayer.subtractEnergy(energyCost);
                    }

                }
            }
            //TODO breath animation
            Warlords.getBreaths().add(this);

            if (name.contains("Water")) {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player1.playSound(player.getLocation(), "mage.waterbreath.activation", 1, 1);
                }
            } else if (name.contains("Freezing")) {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player1.playSound(player.getLocation(), "mage.freezingbreath.activation", 1, 1);
                }
            }

        }
    }
}
