package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.List;

public class Chain extends AbstractAbility {

    public Chain(String name, int minDamageHeal, int maxDamageHeal, int cooldown, int energyCost, int critChance, int critMultiplier, String description) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, description);
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);

        //TODO add Your enemy is too far away!
        int hitCounter = 0;
        if (Utils.lookingAtTotem(player)) {
            //TODO Chain pulse animation
            for (Totem totem : Warlords.getTotems()) {
                if (totem.getOwner() == warlordsPlayer) {
                    Location location = player.getLocation().subtract(0, .5, 0);
                    location.setDirection(location.toVector().subtract(totem.getTotemArmorStand().getLocation().subtract(0, .5, 0).toVector()).multiply(-1));
                    spawnChain((int) (player.getLocation().distance(totem.getTotemArmorStand().getLocation()) * .9), location);
                    List<Entity> near = totem.getTotemArmorStand().getNearbyEntities(4.0D, 4.0D, 4.0D);
                    near.remove(player);
                    for (Entity entity : near) {
                        if (entity instanceof Player) {
                            Player nearPlayer = (Player) entity;
                            if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, warlordsPlayer.getSpec().getOrange().getName(), warlordsPlayer.getSpec().getOrange().getMinDamageHeal(), warlordsPlayer.getSpec().getOrange().getMaxDamageHeal(), warlordsPlayer.getSpec().getOrange().getCritChance(), warlordsPlayer.getSpec().getOrange().getCritMultiplier());
                            }
                        }
                    }
                    hitCounter++;
                    break;
                }
            }

        } else {
            //TODO add soulbinding and totem priority
            List<Entity> near = player.getNearbyEntities(20.0D, 18.0D, 20.0D);
            for (Entity entity : near) {
                if (entity instanceof Player) {
                    Player nearPlayer = (Player) entity;
                    if (nearPlayer.getGameMode() != GameMode.SPECTATOR && Utils.getLookingAt(player, nearPlayer)) {
                        //self heal
                        if (name.contains("Heal")) {
                            warlordsPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                        }
                        Location location = player.getLocation().subtract(0, .5, 0);
                        location.setDirection(location.toVector().subtract(nearPlayer.getLocation().subtract(0, .5, 0).toVector()).multiply(-1));
                        spawnChain((int) (player.getLocation().distance(nearPlayer.getLocation()) * .9), location);
                        Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                        if (warlordsPlayer.hasBoundPlayer(Warlords.getPlayer(nearPlayer))) {
                            healNearPlayers(player);
                        }

                        hitCounter++;
                        List<Entity> nearNearPlayers = nearPlayer.getNearbyEntities(10.0D, 9.0D, 10.0D);
                        nearNearPlayers.remove(player);
                        nearNearPlayers.remove(nearPlayer);
                        for (Entity entity1 : nearNearPlayers) {
                            if (entity1 instanceof ArmorStand) {
                                for (Totem totem : Warlords.getTotems()) {
                                    if (totem.getOwner() == warlordsPlayer) {
                                        location = nearPlayer.getLocation().subtract(0, .5, 0);
                                        location.setDirection(location.toVector().subtract(entity1.getLocation().subtract(0, .5, 0).toVector()).multiply(-1));
                                        spawnChain((int) (nearPlayer.getLocation().distance(entity1.getLocation()) * .9), location);
                                        List<Entity> totemNear = totem.getTotemArmorStand().getNearbyEntities(4.0D, 4.0D, 4.0D);
                                        near.remove(player);
                                        near.remove(nearPlayer);
                                        for (Entity totemEntity : totemNear) {
                                            if (totemEntity instanceof Player) {
                                                Player playerNearTotem = (Player) entity;
                                                if (playerNearTotem.getGameMode() != GameMode.SPECTATOR) {
                                                    Warlords.getPlayer(playerNearTotem).addHealth(warlordsPlayer, warlordsPlayer.getSpec().getOrange().getName(), warlordsPlayer.getSpec().getOrange().getMinDamageHeal(), warlordsPlayer.getSpec().getOrange().getMaxDamageHeal(), warlordsPlayer.getSpec().getOrange().getCritChance(), warlordsPlayer.getSpec().getOrange().getCritMultiplier());
                                                }
                                            }
                                        }
                                        //TODO fix this shit holy shit

                                    }
                                }
                            } else if (entity1 instanceof Player) {
                                Player nearNearPlayer = (Player) entity1;
                                if (nearNearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                    location = nearPlayer.getLocation().subtract(0, .5, 0);
                                    location.setDirection(location.toVector().subtract(nearNearPlayer.getLocation().subtract(0, .5, 0).toVector()).multiply(-1));
                                    spawnChain((int) (nearPlayer.getLocation().distance(nearNearPlayer.getLocation()) * .9), location);
                                    if (name.contains("Lightning")) {
                                        Warlords.getPlayer(nearNearPlayer).addHealth(warlordsPlayer, name, (int) (minDamageHeal * .85), (int) (maxDamageHeal * .85), critChance, critMultiplier);
                                    } else if (name.contains("Chain")) {
                                        Warlords.getPlayer(nearNearPlayer).addHealth(warlordsPlayer, name, (int) (minDamageHeal * .9), (int) (maxDamageHeal * .9), critChance, critMultiplier);
                                    } else if (name.contains("Spirit")) {
                                        Warlords.getPlayer(nearNearPlayer).addHealth(warlordsPlayer, name, (int) (minDamageHeal * .8), (int) (maxDamageHeal * .8), critChance, critMultiplier);
                                    }
                                    if (warlordsPlayer.hasBoundPlayer(Warlords.getPlayer(nearPlayer))) {
                                        healNearPlayers(player);
                                    }
                                    hitCounter++;
                                    List<Entity> nearNearNearPlayers = nearNearPlayer.getNearbyEntities(10.0D, 9.0D, 10.0D);
                                    nearNearNearPlayers.remove(player);
                                    nearNearNearPlayers.remove(nearPlayer);
                                    nearNearNearPlayers.remove(nearNearPlayer);
                                    for (Entity entity2 : nearNearNearPlayers) {
                                        if (entity2 instanceof Player) {
                                            Player nearNearNearPlayer = (Player) entity2;
                                            if (nearNearNearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                                location = nearNearPlayer.getLocation();
                                                location.setDirection(location.toVector().subtract(nearNearNearPlayer.getLocation().subtract(0, .5, 0).toVector()).multiply(-1));
                                                spawnChain((int) (nearNearPlayer.getLocation().distance(nearNearNearPlayer.getLocation()) * .9), location);
                                                if (name.contains("Lightning")) {
                                                    Warlords.getPlayer(nearNearNearPlayer).addHealth(warlordsPlayer, name, (int) (minDamageHeal * .7), (int) (maxDamageHeal * .7), critChance, critMultiplier);
                                                } else if (name.contains("Chain")) {
                                                    Warlords.getPlayer(nearNearPlayer).addHealth(warlordsPlayer, name, (int) (minDamageHeal * .8), (int) (maxDamageHeal * .8), critChance, critMultiplier);
                                                } else if (name.contains("Spirit")) {
                                                    Warlords.getPlayer(nearNearPlayer).addHealth(warlordsPlayer, name, (int) (minDamageHeal * .6), (int) (maxDamageHeal * .6), critChance, critMultiplier);
                                                }
                                                if (warlordsPlayer.hasBoundPlayer(Warlords.getPlayer(nearPlayer))) {
                                                    healNearPlayers(player);
                                                }
                                                hitCounter++;
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (hitCounter != 0) {
            warlordsPlayer.subtractEnergy(energyCost);
            if (name.contains("Lightning")) {
                warlordsPlayer.setChainLightning(hitCounter);
                warlordsPlayer.setChainLightningCooldown(4);
                warlordsPlayer.getSpec().getRed().setCooldown(cooldown);

                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player1.playSound(player.getLocation(), "shaman.chainlightning.activation", 1, 1);
                }
            } else if (name.contains("Chain")) {
                if (hitCounter * 2 > warlordsPlayer.getSpec().getRed().getCurrentCooldown()) {
                    warlordsPlayer.getSpec().getRed().setCurrentCooldown(0);
                } else {
                    warlordsPlayer.getSpec().getRed().setCurrentCooldown(warlordsPlayer.getSpec().getRed().getCurrentCooldown() - hitCounter * 2);
                }
                warlordsPlayer.updateRedItem();
                warlordsPlayer.getSpec().getBlue().setCooldown(cooldown);

                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player1.playSound(player.getLocation(), "shaman.chainheal.activation", 1, 1);
                }
                warlordsPlayer.updateBlueItem();
            } else if (name.contains("Spirit")) {
                warlordsPlayer.setSpiritLink(4);
                warlordsPlayer.getSpec().getRed().setCooldown(cooldown);

                // TODO: find spiritguards chain sounds somehow
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    player1.playSound(player.getLocation(), "shaman.chainheal.activation", 1, 1);
                }
            }
        }
    }

    private void healNearPlayers(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.addHealth(warlordsPlayer, warlordsPlayer.getSpec().getRed().getName(), 420, 420, -1, 100);
        int playersHealed = 0;
        List<Entity> near = player.getNearbyEntities(2.5D, 2D, 2.5D);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, warlordsPlayer.getSpec().getRed().getName(), 420, 420, -1, 100);
                playersHealed++;
                if (playersHealed == 2) break;
            }
        }
    }

    private void spawnChain(int distance, Location location) {
        if (name.contains("Lightning")) {
            for (int i = 0; i < distance; i++) {
                ArmorStand chain = location.getWorld().spawn(location, ArmorStand.class);
                chain.setHeadPose(new EulerAngle(location.getDirection().getY() * -1, 0, 0));
                chain.setHelmet(new ItemStack(Material.RED_MUSHROOM));
                chain.setGravity(false);
                chain.setVisible(false);
                location.add(location.getDirection().multiply(1.2));
                Warlords.getChains().add(chain);
            }
        } else if (name.contains("Chain")) {
            for (int i = 0; i < distance; i++) {
                ArmorStand chain = location.getWorld().spawn(location, ArmorStand.class);
                chain.setHeadPose(new EulerAngle(location.getDirection().getY() * -1, 0, 0));
                chain.setHelmet(new ItemStack(Material.RED_ROSE, 1, (short) 1));
                chain.setGravity(false);
                chain.setVisible(false);
                location.add(location.getDirection().multiply(1.2));
                Warlords.getChains().add(chain);
            }
        } else if (name.contains("Spirit")) {
            for (int i = 0; i < distance; i++) {
                ArmorStand chain = location.getWorld().spawn(location, ArmorStand.class);
                chain.setHeadPose(new EulerAngle(location.getDirection().getY() * -1, 0, 0));
                chain.setHelmet(new ItemStack(Material.SPRUCE_FENCE_GATE));
                chain.setGravity(false);
                chain.setVisible(false);
                location.add(location.getDirection().multiply(1.2));
                Warlords.getChains().add(chain);
            }
        }

    }

}
