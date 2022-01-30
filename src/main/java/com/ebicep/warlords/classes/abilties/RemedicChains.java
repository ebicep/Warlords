package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public class RemedicChains extends AbstractAbility {

    private final int linkBreakRadius = 15;
    private final int duration = 10;
    private final int alliesAffected = 2;

    public RemedicChains() {
        super("Remedic Chains", 506, 685, 22, 40, 20, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Bind yourself to §e" + alliesAffected + " §7allies near you, forcing them\n" +
                "§7them to naturally regenerate health as long as\n" +
                "§7the link is active. When the link expires or when you\n" +
                "§7are too far away from each other the link breaks.\n" +
                "§7Healing you and the allies for §a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health.\n" +
                "§7Breaking the link early will only heal the allies for\n" +
                "§a10% §7of the original amount." +
                "\n\n" +
                "§7The link will break if you are §e" + linkBreakRadius + " §7blocks apart.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        RemedicChains tempRemedicChain = new RemedicChains();

        int targethit = 0;

        for (WarlordsPlayer chainTarget : PlayerFilter
                .entitiesAround(player, 10, 10, 10)
                .aliveTeammatesOfExcludingSelf(wp)
                .closestFirst(wp)
                .limit(alliesAffected)
        ) {
            chainTarget.getCooldownManager().addRegularCooldown(
                    "Remedic Chains",
                    "REMEDIC",
                    RemedicChains.class,
                    tempRemedicChain,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {},
                    duration * 20
            );

            player.sendMessage(WarlordsPlayer.RECEIVE_ARROW + ChatColor.GRAY + " Your Remedic Chains is now protecting " + ChatColor.YELLOW + chainTarget.getName() + ChatColor.GRAY + "!");

            targethit++;
            wp.getGame().getGameTasks().put(
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        boolean outOfRange = wp.getLocation().distanceSquared(chainTarget.getLocation()) > linkBreakRadius * linkBreakRadius;

                        if (wp.getCooldownManager().hasCooldown(tempRemedicChain)) {

                            Location lineLocation = player.getLocation().add(0, 1, 0);
                            lineLocation.setDirection(lineLocation.toVector().subtract(chainTarget.getLocation().add(0, 1, 0).toVector()).multiply(-1));
                            for (int i = 0; i < Math.floor(player.getLocation().distance(chainTarget.getLocation())) * 2; i++) {
                                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(250, 200, 250), lineLocation, 500);
                                lineLocation.add(lineLocation.getDirection().multiply(0.5));
                            }

                            if (outOfRange) {
                                chainTarget.getCooldownManager().removeCooldown(tempRemedicChain);
                                chainTarget.addHealingInstance(wp, name, minDamageHeal * 0.1f, maxDamageHeal * 0.1f, critChance, critMultiplier, false, false);
                                chainTarget.sendMessage(WarlordsPlayer.GIVE_ARROW + ChatColor.RED + " You left the link range early!");
                                this.cancel();
                            } else {
                                chainTarget.setRegenTimer(0);
                            }

                            if (chainTarget.isDead()) {
                                this.cancel();
                            }
                        } else {
                            if (!outOfRange && chainTarget.isAlive()) {
                                chainTarget.addHealingInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false, false);
                            }

                            for (Player player1 : player.getWorld().getPlayers()) {
                                player1.playSound(chainTarget.getLocation(), "rogue.remedicchains.impact", 0.05f, 1.4f);
                            }
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 0, 8), System.currentTimeMillis());
        }

        if (targethit >= 1) {
            for (Player player1 : player.getWorld().getPlayers()) {
                player1.playSound(player.getLocation(), "rogue.remedicchains.activation", 2, 0.5f);
            }

            wp.subtractEnergy(energyCost);
            wp.getCooldownManager().addRegularCooldown(
                    "Remedic Chains",
                    "REMEDIC",
                    RemedicChains.class,
                    tempRemedicChain,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> wp.addHealingInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false, false),
                    duration * 20
            );
        }

        return targethit >= 1;
    }

}
