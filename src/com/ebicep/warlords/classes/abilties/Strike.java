package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.util.Utils;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class Strike extends AbstractAbility {


    public Strike(String name, int minDamageHeal, int maxDamageHeal, int cooldown, int energyCost, int critChance, int critMultiplier, String description) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, description);
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        List<Entity> near = player.getNearbyEntities(5.0D, 5.0D, 5.0D);
        System.out.println(near);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                double distance = player.getLocation().distanceSquared(nearPlayer.getLocation());
                if (nearPlayer.getGameMode() != GameMode.SPECTATOR && Utils.getLookingAt(player, nearPlayer) && distance < 3.3 * 3.3) {
                    //TODO seperate every strike using description
                    WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
                    warlordsPlayer.subtractEnergy(energyCost);
                    Warlords.getPlayer(nearPlayer).addHealth(minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                    System.out.println("NEAR HIT " + nearPlayer);

                    if (description.contains("avenger") && warlordsPlayer.getWrath() != -1) {
                        Warlords.getPlayer(nearPlayer).subtractEnergy(6);
                        List<Entity> nearNearPlayers = nearPlayer.getNearbyEntities(5.0D, 5.0D, 5.0D);
                        System.out.println(nearNearPlayers);
                        nearNearPlayers.remove(player);
                        int counter = 0;
                        for (Entity nearEntity : nearNearPlayers) {
                            if (nearEntity instanceof Player) {
                                Player nearNearPlayer = (Player) nearEntity;
                                double distanceNearPlayer = nearPlayer.getLocation().distanceSquared(nearNearPlayer.getLocation());
                                if (nearNearPlayer.getGameMode() != GameMode.SPECTATOR && distanceNearPlayer < 3.3 * 3.3) {
                                    System.out.println("NEAR NEAR HIT " + nearNearPlayer);
                                    Warlords.getPlayer(nearNearPlayer).addHealth(minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                                    Warlords.getPlayer(nearNearPlayer).subtractEnergy(6);
                                    counter++;
                                    if (counter == 2)
                                        break;
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
}
