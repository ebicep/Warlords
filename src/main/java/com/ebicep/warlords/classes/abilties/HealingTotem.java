package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.internal.AbstractTotemBase;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class HealingTotem extends AbstractTotemBase {

    public HealingTotem() {
        super("Healing Totem", 168, 841, 62.64f, 60, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Place a totem on the ground that\n" +
                "§7pulses constantly, healing nearby\n" +
                "§7allies for §a" + minDamageHeal + " §7- §a" + Math.floor(minDamageHeal * 1.354) + " §7every\n" +
                "§7second. Before disappearing, the totem\n" +
                "§7will let out a final pulse that heals for\n" +
                "§a" + maxDamageHeal + " §7- §a" + Math.floor(maxDamageHeal * 1.354) + "§7. Lasts §65" +
                " §7seconds.";
    }

    @Override
    protected ItemStack getTotemItemStack() {
        return new ItemStack(Material.RED_ROSE, 1, (short) 7);
    }

    @Override
    protected void onTotemStand(ArmorStand totemStand, WarlordsPlayer warlordsPlayer) {
        totemStand.setMetadata("healing-totem-" + warlordsPlayer.getName(), new FixedMetadataValue(Warlords.getInstance(), true));
    }

    @Override
    protected void playSound(Player player, Location location) {
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(location, "shaman.totem.activation", 2, 1);
        }
    }

    @Override
    protected void onActivation(WarlordsPlayer wp, Player player, ArmorStand totemStand) {
        wp.getCooldownManager().addCooldown(this.getClass(), new HealingTotem(), "TOTEM", 5, wp, CooldownTypes.ABILITY);


        new BukkitRunnable() {
            int timeLeft = 5;

            @Override
            public void run() {

                if (timeLeft != 0) {

                    Location particleLoc = totemStand.getLocation().clone().add(0, 1.6, 0);
                    ParticleEffect.VILLAGER_HAPPY.display(0.4F, 0.2F, 0.4F, 0.05F, 5, particleLoc, 500);

                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(totemStand.getLocation(), "shaman.earthlivingweapon.impact", 2, 1);
                    }

                    PlayerFilter.entitiesAround(totemStand, 6, 6, 6)
                            .aliveTeammatesOf(wp)
                            .forEach((nearPlayer) -> {
                                nearPlayer.addHealth(
                                        wp,
                                        name,
                                        minDamageHeal,
                                        minDamageHeal * 1.354f,
                                        critChance,
                                        critMultiplier
                                );
                            });
                } else {

                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(totemStand.getLocation(), "shaman.heal.impact", 2, 1);
                    }

                    new FallingBlockWaveEffect(totemStand.getLocation().clone().add(0, 1, 0), 7, 1.2, Material.SAPLING, (byte) 1).play();

                    PlayerFilter.entitiesAround(totemStand, 6, 6, 6)
                            .aliveTeammatesOf(wp)
                            .forEach((nearPlayer) -> {
                                nearPlayer.addHealth(
                                        wp,
                                        name,
                                        maxDamageHeal,
                                        maxDamageHeal * 1.354f,
                                        critChance,
                                        critMultiplier
                                );
                            });

                    totemStand.remove();
                    this.cancel();
                }
                timeLeft--;
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }


}