package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractHolyRadianceBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.*;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class HolyRadianceCrusader extends AbstractHolyRadianceBase {

    private final int markRadius = 15;
    private int markDuration = 8;

    public HolyRadianceCrusader(float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super("Holy Radiance", minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, 6);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Radiate with holy energy, healing\n" +
                "§7yourself and all nearby allies for\n" +
                "§a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health." +
                "\n\n" +
                "§7You may look at an ally to mark\n" +
                "§7them for §6" + markDuration + " §7seconds. Increasing\n" +
                "§7their EPS by §e5 §7and speed by §e20%\n" +
                "§7§7for the duration. Mark has an optimal\n" +
                "§7range of §e" + markRadius + " §7blocks. However,\n" +
                "§7marking players from far away\n" +
                "§7will not give them healing.";

    }

    @Override
    public void chain(WarlordsPlayer wp, Player player) {
        for (WarlordsPlayer p : PlayerFilter
                .entitiesAround(player, markRadius, markRadius, markRadius)
                .aliveTeammatesOfExcludingSelf(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            if (Utils.isLookingAtMark(player, p.getEntity()) && Utils.hasLineOfSight(player, p.getEntity())) {
                wp.subtractEnergy(energyCost);

                for (Player player1 : player.getWorld().getPlayers()) {
                    player1.playSound(player.getLocation(), "paladin.consecrate.activation", 2, 0.65f);
                }

                PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);

                // chain particles
                EffectUtils.playParticleLinkAnimation(player.getLocation(), p.getLocation(), 255, 170, 0, 1);
                EffectUtils.playChainAnimation(wp.getLocation(), p.getLocation(), Material.PUMPKIN, 8);

                HolyRadianceCrusader tempMark = new HolyRadianceCrusader(minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
                p.addHealingInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false, false);
                p.getCooldownManager().addRegularCooldown(name, "CRUS MARK", HolyRadianceCrusader.class, tempMark, wp, CooldownTypes.BUFF, cooldownManager -> {
                }, markDuration * 20);
                p.getSpeed().addSpeedModifier("Crusader Mark Speed", 20, 20 * markDuration, "BASE");

                player.sendMessage(WarlordsPlayer.RECEIVE_ARROW + ChatColor.GRAY + " You have marked " + ChatColor.YELLOW + p.getName() + ChatColor.GRAY + "!");
                p.sendMessage(WarlordsPlayer.RECEIVE_ARROW + ChatColor.GRAY + " You have been granted " + ChatColor.YELLOW + "Crusader's Mark" + ChatColor.GRAY + " by " + wp.getName() + "!");

                new GameRunnable(wp.getGame()) {
                    @Override
                    public void run() {
                        if (p.getCooldownManager().hasCooldown(tempMark)) {
                            Location playerLoc = p.getLocation();
                            Location particleLoc = playerLoc.clone();
                            for (int i = 0; i < 4; i++) {
                                for (int j = 0; j < 10; j++) {
                                    double angle = j / 8D * Math.PI * 2;
                                    double width = 1;
                                    particleLoc.setX(playerLoc.getX() + Math.sin(angle) * width);
                                    particleLoc.setY(playerLoc.getY() + i / 6D);
                                    particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * width);

                                    ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255, 170, 0), particleLoc, 500);
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