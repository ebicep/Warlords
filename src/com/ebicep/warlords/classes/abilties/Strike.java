package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.util.Utils;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class Strike extends AbstractAbility {

    public Strike(String name, int minDamageHeal, int maxDamageHeal, int cooldown, int energyCost, int critChance, int critMultiplier, String description) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, description);
    }

    @Override
    public void onActivate(Player player) {
        List<Entity> near = player.getNearbyEntities(5.0D, 5.0D, 5.0D);
        near = Utils.filterOutTeammates(near, player);
        System.out.println(near);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                //TODO check if you should just remove distance because near gets nearest already
                double distance = player.getLocation().distanceSquared(nearPlayer.getLocation());
                if (nearPlayer.getGameMode() != GameMode.SPECTATOR && Utils.getLookingAt(player, nearPlayer) && distance < 3.3 * 3.3) {
                    WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
                    warlordsPlayer.subtractEnergy(energyCost);

                    System.out.println("NEAR HIT " + nearPlayer);
                    //PALADIN
                    if (name.contains("Avenger") || name.contains("Crusader") || name.contains("Protector")) {
                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                            player1.playSound(player.getLocation(), "paladin.paladinstrike.activation", 1, 1);
                        }
                        //check consecrate then boost dmg
                        if (name.contains("Avenger")) {
                            Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                            Warlords.getPlayer(nearPlayer).subtractEnergy(6);
                            if (warlordsPlayer.getWrath() != -1) {
                                List<Entity> nearNearPlayers = nearPlayer.getNearbyEntities(5.0D, 5.0D, 5.0D);
                                nearNearPlayers = Utils.filterOutTeammates(nearNearPlayers, player);
                                int counter = 0;
                                for (Entity nearEntity : nearNearPlayers) {
                                    if (nearEntity instanceof Player) {
                                        Player nearNearPlayer = (Player) nearEntity;
                                        double distanceNearPlayer = nearPlayer.getLocation().distanceSquared(nearNearPlayer.getLocation());
                                        if (nearNearPlayer.getGameMode() != GameMode.SPECTATOR && distanceNearPlayer < 3.3 * 3.3) {
                                            System.out.println("NEAR NEAR HIT " + nearNearPlayer);
                                            //checking if player is in consecrate
                                            boolean inConsecrate = false;
                                            for (int i = 0; i < Warlords.damageHealCircles.size(); i++) {
                                                DamageHealCircle damageHealCircle = Warlords.damageHealCircles.get(i);
                                                if (damageHealCircle.getPlayer() == player) {
                                                    double consecrateDistance = damageHealCircle.getLocation().distanceSquared(player.getLocation());
                                                    if (consecrateDistance < damageHealCircle.getRadius() * damageHealCircle.getRadius()) {
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
                        } else if (name.contains("Crusader")) {
                            int counter = 0;
                            //checking if player is in consecrate
                            boolean inConsecrate = false;
                            for (int i = 0; i < Warlords.damageHealCircles.size(); i++) {
                                DamageHealCircle damageHealCircle = Warlords.damageHealCircles.get(i);
                                if (damageHealCircle.getPlayer() == player) {
                                    double consecrateDistance = damageHealCircle.getLocation().distanceSquared(player.getLocation());
                                    if (consecrateDistance < damageHealCircle.getRadius() * damageHealCircle.getRadius()) {
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
                            List<Entity> nearNearPlayers = nearPlayer.getNearbyEntities(5.0D, 5.0D, 5.0D);
                            nearNearPlayers.remove(player);
                            nearNearPlayers = Utils.filterOnlyTeammates(nearNearPlayers, player);
                            for (Entity nearEntity2 : nearNearPlayers) {
                                if (nearEntity2 instanceof Player) {
                                    Player nearTeamPlayer = (Player) nearEntity2;
                                    Warlords.getPlayer(nearTeamPlayer).addEnergy(warlordsPlayer, name, 24);
                                    counter++;
                                    if (counter == 2)
                                        break;
                                }
                            }
                            break;
                        } else if (name.contains("Protector")) {
                            //self heal 50%
                            Warlords.getPlayer(player).addHealth(warlordsPlayer, name, -minDamageHeal / 2, -maxDamageHeal / 2, critChance, critMultiplier);

                            int counter = 0;
                            //checking if player is in consecrate
                            boolean inConsecrate = false;
                            for (int i = 0; i < Warlords.damageHealCircles.size(); i++) {
                                DamageHealCircle damageHealCircle = Warlords.damageHealCircles.get(i);
                                if (damageHealCircle.getPlayer() == player) {
                                    double consecrateDistance = damageHealCircle.getLocation().distanceSquared(player.getLocation());
                                    if (consecrateDistance < damageHealCircle.getRadius() * damageHealCircle.getRadius()) {
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
                            //reloops near players to give health to
                            List<Entity> nearNearPlayers = nearPlayer.getNearbyEntities(5.0D, 5.0D, 5.0D);
                            nearNearPlayers.remove(player);
                            nearNearPlayers = Utils.filterOnlyTeammates(nearNearPlayers, player);
                            for (Entity nearEntity2 : nearNearPlayers) {
                                if (nearEntity2 instanceof Player) {
                                    Player nearTeamPlayer = (Player) nearEntity2;
                                    if (inConsecrate) {
                                        Warlords.getPlayer(nearTeamPlayer).addHealth(warlordsPlayer, name, Math.round(minDamageHeal * 1.2f * -1), Math.round(maxDamageHeal * 1.2f * -1), critChance, critMultiplier);
                                    } else {
                                        Warlords.getPlayer(nearTeamPlayer).addHealth(warlordsPlayer, name, minDamageHeal * -1, maxDamageHeal * -1, critChance, critMultiplier);
                                    }
                                    counter++;
                                    if (counter == 2)
                                        break;
                                    ;
                                }

                            }
                            break;
                        }

                    } else if (name.contains("Berserker")) {
                        Warlords.getPlayer(nearPlayer).setBerserkerWounded(3);
                        Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);

                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                            player1.playSound(player.getLocation(), "warrior.mortalstrike.impact", 1, 1);
                        }
                    } else if (name.contains("Defender")) {
                        Warlords.getPlayer(nearPlayer).setDefenderWounded(3);
                        Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);

                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                            player1.playSound(player.getLocation(), "warrior.mortalstrike.impact", 1, 1);
                        }
                    } else if (name.contains("Cripp")) {
                        Warlords.getPlayer(nearPlayer).setCrippled(3);
                        Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);

                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                            player1.playSound(player.getLocation(), "warrior.mortalstrike.impact", 1, 1);
                        }
                    }
                    break;
                }
            }
        }
    }
}
