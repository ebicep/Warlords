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
                //TODO check if you should just remove distance because near gets nearest already
                //TODO check if player is on the other team
                double distance = player.getLocation().distanceSquared(nearPlayer.getLocation());
                if (nearPlayer.getGameMode() != GameMode.SPECTATOR && Utils.getLookingAt(player, nearPlayer) && distance < 3.3 * 3.3) {
                    //TODO seperate every strike using description
                    WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
                    warlordsPlayer.subtractEnergy(energyCost);

                    System.out.println("NEAR HIT " + nearPlayer);
                    if (name.contains("Avenger") || name.contains("Crusader") || name.contains("Protector")) {
                        //check consecrate then boost dmg
                        if (description.contains("avenger")) {
                            Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                            Warlords.getPlayer(nearPlayer).subtractEnergy(6);
                            if (warlordsPlayer.getWrath() != -1) {
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
                                            //checking if player is in consecrate
                                            boolean inConsecrate = false;
                                            for (int i = 0; i < Warlords.consecrates.size(); i++) {
                                                ConsecrateHammerCircle consecrateHammerCircle = Warlords.consecrates.get(i);
                                                if (consecrateHammerCircle.getPlayer() == player) {
                                                    double consecrateDistance = consecrateHammerCircle.getLocation().distanceSquared(player.getLocation());
                                                    if (consecrateDistance < consecrateHammerCircle.getRadius() * consecrateHammerCircle.getRadius()) {
                                                        inConsecrate = true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (inConsecrate) {
                                                Warlords.getPlayer(nearNearPlayer).addHealth(warlordsPlayer, name, Math.round(minDamageHeal * 1.2f), Math.round(maxDamageHeal * 1.2f), critChance, critMultiplier);
                                            } else {
                                                Warlords.getPlayer(nearNearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                                            }
                                            Warlords.getPlayer(nearNearPlayer).subtractEnergy(6);
                                            counter++;
                                            if (counter == 2)
                                                break;
                                        }
                                    }
                                }
                            }
                        } else if (description.contains("crusader")) {
                            List<Entity> nearNearPlayers = nearPlayer.getNearbyEntities(5.0D, 5.0D, 5.0D);
                            System.out.println(nearNearPlayers);
                            nearNearPlayers.remove(player);
                            int counter = 0;
                            //checking if player is in consecrate
                            boolean inConsecrate = false;
                            for (int i = 0; i < Warlords.consecrates.size(); i++) {
                                ConsecrateHammerCircle consecrateHammerCircle = Warlords.consecrates.get(i);
                                if (consecrateHammerCircle.getPlayer() == player) {
                                    double consecrateDistance = consecrateHammerCircle.getLocation().distanceSquared(player.getLocation());
                                    if (consecrateDistance < consecrateHammerCircle.getRadius() * consecrateHammerCircle.getRadius()) {
                                        inConsecrate = true;
                                        break;
                                    }
                                }
                            }
                            if (inConsecrate) {
                                Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, Math.round(minDamageHeal * 1.2f), Math.round(maxDamageHeal * 1.2f), critChance, critMultiplier);
                            } else {
                                Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                            }
                            //reloops near players to give energy to
                            for (Entity nearEntity2 : nearNearPlayers) {
                                if (nearEntity2 instanceof Player) {
                                    Player nearTeamPlayer = (Player) nearEntity2;
                                    //TODO check if near player is on the same team, then give energy
                                    Warlords.getPlayer(nearTeamPlayer).subtractEnergy(-24);
                                    break;
                                }
                                counter++;
                                if (counter == 2)
                                    break;
                            }
                            break;
                        } else if (description.contains("protector")) {
                            //self heal 50%
                            Warlords.getPlayer(player).addHealth(warlordsPlayer, name, minDamageHeal / 2, maxDamageHeal / 2, critChance, critMultiplier);
                            List<Entity> nearNearPlayers = nearPlayer.getNearbyEntities(5.0D, 5.0D, 5.0D);
                            System.out.println(nearNearPlayers);
                            nearNearPlayers.remove(player);
                            int counter = 0;
                            //checking if player is in consecrate
                            boolean inConsecrate = false;
                            for (int i = 0; i < Warlords.consecrates.size(); i++) {
                                ConsecrateHammerCircle consecrateHammerCircle = Warlords.consecrates.get(i);
                                if (consecrateHammerCircle.getPlayer() == player) {
                                    double consecrateDistance = consecrateHammerCircle.getLocation().distanceSquared(player.getLocation());
                                    if (consecrateDistance < consecrateHammerCircle.getRadius() * consecrateHammerCircle.getRadius()) {
                                        inConsecrate = true;
                                        break;
                                    }
                                }
                            }
                            if (inConsecrate) {
                                Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, Math.round(minDamageHeal * 1.2f), Math.round(maxDamageHeal * 1.2f), critChance, critMultiplier);
                            } else {
                                Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                            }
                            //reloops near players to give energy to
                            for (Entity nearEntity2 : nearNearPlayers) {
                                if (nearEntity2 instanceof Player) {
                                    Player nearTeamPlayer = (Player) nearEntity2;
                                    //TODO check if near player is on the same team, then give energy
                                    if (inConsecrate) {
                                        Warlords.getPlayer(nearTeamPlayer).addHealth(warlordsPlayer, name, Math.round(minDamageHeal * 1.2f * -1), Math.round(maxDamageHeal * 1.2f * -1), critChance, critMultiplier);
                                    } else {
                                        Warlords.getPlayer(nearTeamPlayer).addHealth(warlordsPlayer, name, minDamageHeal * -1, maxDamageHeal * -1, critChance, critMultiplier);
                                    }
                                    break;
                                }
                                counter++;
                                if (counter == 2)
                                    break;
                            }
                            break;
                        }
                    } else if (name.contains("Berserker")) {
                        Warlords.getPlayer(nearPlayer).setBerserkerWounded(3);
                        Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                    } else if (name.contains("Defender")) {
                        Warlords.getPlayer(nearPlayer).setDefenderWounded(3);
                        Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                    } else if (name.contains("Cripp")) {
                        Warlords.getPlayer(nearPlayer).setCrippled(3);
                        Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                    }
                    break;
                }
            }
        }
    }
}
