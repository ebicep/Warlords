package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.internal.AbstractHolyRadianceBase;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.*;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

public class HolyRadianceProtector extends AbstractHolyRadianceBase {

    private final int markRadius = 15;
    private int markDuration = 8;

    public HolyRadianceProtector(float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super("Holy Radiance", minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, 6);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Radiate with holy energy, healing\n" +
                "§7yourself and all nearby allies for\n" +
                "§a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health." +
                "\n\n" +
                "§7You may look at an ally to mark\n" +
                "§7them for §6" + markDuration + " §7seconds. Mark has an\n" +
                "§7optimal range of §e" + markRadius + " §7blocks. However,\n" +
                "§7marking players from far away\n" +
                "§7will not give them healing.";
    }

    @Override
    public void chain(WarlordsPlayer wp, Player player) {
        for (WarlordsPlayer markTarget : PlayerFilter
                .entitiesAround(player, markRadius, markRadius, markRadius)
                .aliveTeammatesOfExcludingSelf(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            if (Utils.isLookingAtMark(player, markTarget.getEntity()) && Utils.hasLineOfSight(player, markTarget.getEntity())) {
                wp.subtractEnergy(energyCost);

                for (Player player1 : player.getWorld().getPlayers()) {
                    player1.playSound(player.getLocation(), "paladin.consecrate.activation", 2, 0.65f);
                }

                PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);


                // chain particles
                EffectUtils.playParticleLinkAnimation(player.getLocation(), markTarget.getLocation(), 0, 255, 70);
                EffectUtils.playChainAnimation(wp.getLocation(), markTarget.getLocation(), Material.RED_ROSE, 8);

                HolyRadianceProtector tempMark = new HolyRadianceProtector(minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
                markTarget.getCooldownManager().addRegularCooldown(name, "PROT MARK", HolyRadianceProtector.class, tempMark, wp, CooldownTypes.BUFF, cooldownManager -> {
                }, markDuration * 20);

                player.sendMessage(WarlordsPlayer.RECEIVE_ARROW + ChatColor.GRAY + " You have marked " + ChatColor.GREEN + markTarget.getName() + ChatColor.GRAY + "!");
                markTarget.sendMessage(WarlordsPlayer.RECEIVE_ARROW + ChatColor.GRAY + " You have been granted " + ChatColor.GREEN + "Protector's Mark" + ChatColor.GRAY + " by " + wp.getName() + "!");

                new GameRunnable(wp.getGame()) {
                    @Override
                    public void run() {
                        if (markTarget.getCooldownManager().hasCooldown(tempMark)) {
                            Location playerLoc = markTarget.getLocation();
                            Location particleLoc = playerLoc.clone();
                            for (int i = 0; i < 4; i++) {
                                for (int j = 0; j < 10; j++) {
                                    double angle = j / 9D * Math.PI * 2;
                                    double width = 1;
                                    particleLoc.setX(playerLoc.getX() + Math.sin(angle) * width);
                                    particleLoc.setY(playerLoc.getY() + i / 6D);
                                    particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * width);

                                    ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(0, 255, 70), particleLoc, 500);
                                }
                            }
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(0, 10);
            } else {
                player.sendMessage("§cYour mark was out of range or you did not target a player!");
            }
        }
    }

    public void setMarkDuration(int markDuration) {
        this.markDuration = markDuration;
    }
}