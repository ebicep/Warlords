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

    public RemedicChains() {
        super("Remedic Chains", 506, 685, 22, 40, 20, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Bind yourself to §e2 §7allies near you, PLACEHOLDER\n" +
                "§7When the link expires or when you run too far away from\n" +
                "§7each other the link breaks. Healing you and the allies\n" +
                "§7for §a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health. Breaking the link\n" +
                "§7early will only heal the allies for §a10% §7of\n" +
                "§7the original amount." +
                "\n\n" +
                "§7The link will break if you are §e" + linkBreakRadius + " §7blocks apart.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);
        RemedicChains tempRemedicChain = new RemedicChains();

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "rogue.remedicchains.activation", 2, 0.5f);
            player1.playSound(player.getLocation(), "mage.firebreath.activation", 1, 0.65f);
        }

        wp.getCooldownManager().addRegularCooldown(
                "Remedic Chains",
                "REMEDIC",
                RemedicChains.class,
                tempRemedicChain,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {},
                10 * 20
        );

        for (WarlordsPlayer chainTarget : PlayerFilter
                .entitiesAround(player, 10, 10, 10)
                .aliveTeammatesOfExcludingSelf(wp)
                .closestFirst(wp)
                .limit(2)
        ) {
            chainTarget.getCooldownManager().addRegularCooldown(
                    "Remedic Chains",
                    "REMEDIC",
                    RemedicChains.class,
                    tempRemedicChain,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {},
                    10 * 20
            );

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

                            if (outOfRange || wp.isDead()) {
                                chainTarget.getCooldownManager().removeCooldown(tempRemedicChain);
                                chainTarget.addHealingInstance(wp, name, minDamageHeal * 0.1f, maxDamageHeal * 0.1f, critChance, critMultiplier, false, false);
                                chainTarget.sendMessage(WarlordsPlayer.RECEIVE_ARROW + ChatColor.RED + "You left the link range early!");
                                this.cancel();
                            }
                        } else {
                            if (!outOfRange && chainTarget.isAlive()) {
                                chainTarget.addHealingInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false, false);
                            }
                            wp.addHealingInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false, false);
                            player.playSound(chainTarget.getLocation(), "paladin.holyradiance.activation", 0.5f, 0.7f);
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 0, 5),
                System.currentTimeMillis()
            );

            return true;
        }

        return false;
    }
}
