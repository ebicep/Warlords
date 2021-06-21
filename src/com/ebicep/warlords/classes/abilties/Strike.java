package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import org.bukkit.entity.LivingEntity;

public class Strike extends AbstractAbility {

    public Strike(String name, int minDamageHeal, int maxDamageHeal, int cooldown, int energyCost, int critChance, int critMultiplier, String description) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, description);
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        PlayerFilter.entitiesAround(warlordsPlayer, 3.6, 3.6, 3.6)
            .aliveEnemiesOf(warlordsPlayer)
            .closestFirst(warlordsPlayer)
            .requireLineOfSight(warlordsPlayer)
            .first((nearPlayer) -> {
            if (Utils.getLookingAt(player, nearPlayer.getEntity()) && Utils.hasLineOfSight(player, nearPlayer.getEntity())) {
                PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);
                warlordsPlayer.subtractEnergy(energyCost);

                System.out.println("NEAR HIT " + nearPlayer);
                //PALADIN
                if (name.contains("Avenger") || name.contains("Crusader") || name.contains("Protector")) {
                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(player.getLocation(), "paladin.paladinstrike.activation", 2, 1);
                    }
                    //check consecrate then boost dmg
                    if (name.contains("Avenger")) {
                        if (standingOnConsecrate(player, nearPlayer.getEntity())) {
                            nearPlayer.addHealth(warlordsPlayer, name, Math.round(minDamageHeal * 1.2f), Math.round(maxDamageHeal * 1.2f), critChance, critMultiplier);
                        } else {
                            nearPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                        }
                        nearPlayer.subtractEnergy(6);
                        if (warlordsPlayer.getWrathDuration() != -1) {
                            PlayerFilter.entitiesAround(nearPlayer, 3.6, 3.6, 3.6)
                                .aliveEnemiesOf(warlordsPlayer)
                                .closestFirst(nearPlayer)
                                .limit(2)
                                .forEach((nearNearPlayer) -> {
                                System.out.println("NEAR NEAR HIT " + nearNearPlayer);
                                //checking if player is in consecrate
                                if (standingOnConsecrate(player, nearNearPlayer.getEntity())) {
                                    nearNearPlayer.addHealth(warlordsPlayer, name, Math.round(minDamageHeal * 1.2f), Math.round(maxDamageHeal * 1.2f), critChance, critMultiplier);
                                } else {
                                    nearNearPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                                }
                                nearNearPlayer.subtractEnergy(6);
                            });
                        }
                    } else if (name.contains("Crusader")) {
                        //checking if player is in consecrate
                        if (standingOnConsecrate(player, nearPlayer)) {
                            nearPlayer.addHealth(warlordsPlayer, name, Math.round(minDamageHeal * 1.15f), Math.round(maxDamageHeal * 1.15f), critChance, critMultiplier);
                        } else {
                            nearPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                        }
                        //reloops near players to give energy to
                        PlayerFilter.entitiesAround(warlordsPlayer, 5, 5, 5)
                            .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                            .closestFirst(warlordsPlayer)
                            .limit(2)
                            .first((nearTeamPlayer) -> 
                                nearTeamPlayer.addEnergy(warlordsPlayer, name, 24)
                            );
                    } else if (name.contains("Protector")) {
                        nearPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                    }

                } else if (name.contains("Berserker")) {
                    nearPlayer.setBerserkerWounded(3);
                    nearPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);

                    nearPlayer.getGame().forEachOnlinePlayer((player1, t) ->
                        player1.playSound(player.getLocation(), "warrior.mortalstrike.impact", 2, 1)
                    );
                } else if (name.contains("Defender")) {
                    nearPlayer.setDefenderWounded(3);
                    nearPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);

                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        player1.playSound(player.getLocation(), "warrior.mortalstrike.impact", 2, 1);
                    }
                } else if (name.contains("Cripp")) {
                    nearPlayer.setCrippled(3);
                    nearPlayer.addHealth(warlordsPlayer, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);

                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        player1.playSound(player.getLocation(), "warrior.mortalstrike.impact", 2, 1);
                    }
                }
            }
        });
    }

    private boolean standingOnConsecrate(Player owner, WarlordsPlayer standing) {
        return standingOnConsecrate(owner, standing.getEntity());
    }
    private boolean standingOnConsecrate(Player owner, LivingEntity standing) {
        for (Entity entity : owner.getWorld().getEntities()) {
            if (entity instanceof ArmorStand && entity.hasMetadata("Consecrate - " + owner.getName())) {
                if (entity.getLocation().clone().add(0, 2, 0).distanceSquared(standing.getLocation()) < 5 * 5.25) {
                    return true;
                }
            }
        }
        return false;
    }
}
