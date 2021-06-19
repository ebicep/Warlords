package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.paladin.specs.avenger.Avenger;
import com.ebicep.warlords.classes.paladin.specs.crusader.Crusader;
import com.ebicep.warlords.classes.paladin.specs.protector.Protector;
import com.ebicep.warlords.classes.warrior.specs.berserker.Berserker;
import com.ebicep.warlords.classes.warrior.specs.defender.Defender;
import com.ebicep.warlords.classes.warrior.specs.revenant.Revenant;
import com.ebicep.warlords.util.Utils;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class Strike extends AbstractAbility {

    public Strike(String name, float minDamageHeal, float maxDamageHeal, int cooldown, int energyCost, int critChance, int critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    @Override
    public void updateDescription() {
        if (name.contains("Avenger")) {
            description = "§7Strike the targeted enemy player,\n" +
                    "§7causing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                    "§7and removing §e6 §7energy.";
        } else if (name.contains("Crusader")) {
            description = "§7Strike the targeted enemy player,\n" +
                    "§7causing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " damage\n" +
                    "§7and restoring §e24 §7energy to two nearby\n" +
                    "§7within §e10 §7blocks.";
        } else if (name.contains("Protector")) {
            description = "§7Strike the targeted enemy player,\n" +
                    "§7causing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                    "§7and healing two nearby allies for\n" +
                    "§a100% §7of the damage done. Also\n" +
                    "§7heals yourself by §a50% §7of the\n" +
                    "§7damage done.";
        } else if (name.contains("Crippling")) {
            description = "§7Strike the targeted enemy player,\n" +
                    "§7causing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                    "§7and §ccrippling §7them for §63 §7seconds.\n" +
                    "§7A §ccrippled §7player deals §c12.5% §7less\n" +
                    "§7damage for the duration of the effect.";
        } else if (critMultiplier == 175) {
            description = "§7Strike the targeted enemy player,\n" +
                    "§7causing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                    "§7and §cwounding §7them for §63 §7seconds.\n" +
                    "§7A wounded player receives §c35% §7less\n" +
                    "§7healing for the duration of the effect.";
        } else if (critMultiplier == 200) {
            description = "§7Strike the targeted enemy player,\n" +
                    "§7causing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                    "§7and §cwounding §7them for §63 §7seconds.\n" +
                    "§7A wounded player receives §c25% §7less\n" +
                    "§7healing for the duration of the effect.";
        }
    }

    @Override
    public void onActivate(Player player) {
        List<Entity> near = player.getNearbyEntities(5.0D, 5.0D, 5.0D);
        near = Utils.filterOutTeammates(near, player);
        for (Entity entity : near) {
            if (entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR) {
                Player nearPlayer = (Player) entity;
                //TODO check if you should just remove distance because near gets nearest already
                double distance = player.getLocation().distanceSquared(nearPlayer.getLocation());
                if (Utils.getLookingAt(player, nearPlayer) && distance < 3.6 * 3.6 && Utils.hasLineOfSight(player, nearPlayer)) {
                    PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);
                    WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
                    warlordsPlayer.subtractEnergy(energyCost);

                    //PALADIN
                    if (warlordsPlayer.getSpec() instanceof Avenger || warlordsPlayer.getSpec() instanceof Crusader || warlordsPlayer.getSpec() instanceof Protector) {
                        for (Player player1 : player.getWorld().getPlayers()) {
                            player1.playSound(player.getLocation(), "paladin.paladinstrike.activation", 2, 1);
                        }
                        //check consecrate then boost dmg
                        if (warlordsPlayer.getSpec() instanceof Avenger) {
                            if (standingOnConsecrate(player, nearPlayer)) {
                                Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, (minDamageHeal * 1.2f), (maxDamageHeal * 1.2f), critChance, critMultiplier);
                            } else {
                                Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                            }
                            Warlords.getPlayer(nearPlayer).subtractEnergy(6);
                            if (warlordsPlayer.getWrathDuration() != -1) {
                                List<Entity> nearNearPlayers = nearPlayer.getNearbyEntities(5.0D, 5.0D, 5.0D);
                                nearNearPlayers = Utils.filterOutTeammates(nearNearPlayers, player);
                                int counter = 0;
                                for (Entity nearEntity : nearNearPlayers) {
                                    if (nearEntity instanceof Player) {
                                        Player nearNearPlayer = (Player) nearEntity;
                                        double distanceNearPlayer = nearPlayer.getLocation().distanceSquared(nearNearPlayer.getLocation());
                                        if (nearNearPlayer.getGameMode() != GameMode.SPECTATOR && distanceNearPlayer < 3.6 * 3.6) {
                                            System.out.println("NEAR NEAR HIT " + nearNearPlayer);
                                            //checking if player is in consecrate
                                            if (standingOnConsecrate(player, nearNearPlayer)) {
                                                Warlords.getPlayer(nearNearPlayer).addHealth(warlordsPlayer, name, (minDamageHeal * 1.2f), (maxDamageHeal * 1.2f), critChance, critMultiplier);
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
                        } else if (warlordsPlayer.getSpec() instanceof Crusader) {
                            int counter = 0;
                            //checking if player is in consecrate
                            if (standingOnConsecrate(player, nearPlayer)) {
                                Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, (minDamageHeal * 1.15f), (maxDamageHeal * 1.15f), critChance, critMultiplier);
                            } else {
                                Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                            }
                            //reloops near players to give energy to
                            List<Entity> nearNearPlayers = nearPlayer.getNearbyEntities(10.0D, 10.0D, 10.0D);
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
                        } else if (warlordsPlayer.getSpec() instanceof Protector) {
                            if (standingOnConsecrate(player, nearPlayer)) {
                                Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, (minDamageHeal * 1.15f), (maxDamageHeal * 1.15f), critChance, critMultiplier);
                            } else {
                                Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                            }
                        }

                    } else if (warlordsPlayer.getSpec() instanceof Berserker) {
                        Warlords.getPlayer(nearPlayer).setBerserkerWounded(3);
                        Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);

                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                            player1.playSound(player.getLocation(), "warrior.mortalstrike.impact", 2, 1);
                        }
                    } else if (warlordsPlayer.getSpec() instanceof Defender) {
                        Warlords.getPlayer(nearPlayer).setDefenderWounded(3);
                        Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);

                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                            player1.playSound(player.getLocation(), "warrior.mortalstrike.impact", 2, 1);
                        }
                    } else if (warlordsPlayer.getSpec() instanceof Revenant) {
                        Warlords.getPlayer(nearPlayer).setCrippled(3);
                        Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);

                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                            player1.playSound(player.getLocation(), "warrior.mortalstrike.impact", 2, 1);
                        }
                    }
                    break;
                }
            }
        }
    }

    private boolean standingOnConsecrate(Player owner, Player standing) {
        for (Entity entity : owner.getWorld().getEntities()) {
            if (entity instanceof ArmorStand && entity.hasMetadata("Consecrate - " + owner.getName())) {
                if (entity.getLocation().clone().add(0, 2, 0).distanceSquared(standing.getLocation()) < 5 * 5.25) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
}
