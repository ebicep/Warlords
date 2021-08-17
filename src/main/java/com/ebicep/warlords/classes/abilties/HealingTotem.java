package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.internal.AbstractTotemBase;
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

    private final int range = 6;
    private final int duration = 4;

    public HealingTotem() {
        super("Healing Totem", 168, 224, 62.64f, 60, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        //TODO change this
        description = "§7Place a totem on the ground that\n" +
                "§7pulses constantly, healing nearby\n" +
                "§7allies for §a" + minDamageHeal + " §7- §a" + Math.floor(minDamageHeal * 1.354) + " §7every\n" +
                "§7second. Before disappearing, the totem\n" +
                "§7will let out a final pulse that heals for\n" +
                "§a" + maxDamageHeal + " §7- §a" + Math.floor(maxDamageHeal * 1.354) + "§7. Lasts §6" + duration +
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
        wp.getCooldownManager().addCooldown(name, this.getClass(), new HealingTotem(), "TOTEM", duration, wp, CooldownTypes.ABILITY);


        new BukkitRunnable() {
            int timeLeft = duration;

            @Override
            public void run() {

                if (timeLeft >= 0) {
                    Location particleLoc = totemStand.getLocation().clone().add(0, 1.6, 0);
                    ParticleEffect.VILLAGER_HAPPY.display(0.4F, 0.2F, 0.4F, 0.05F, 5, particleLoc, 500);

                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(totemStand.getLocation(), "shaman.earthlivingweapon.impact", 2, 1);
                    }

                    //1
                    //1.4
                    //1.8
                    //2.2
                    //2.6
                    float healMultiplier = 1 + (.4f * (5 - timeLeft));
                    PlayerFilter.entitiesAround(totemStand, range, range, range)
                            .aliveTeammatesOf(wp)
                            .forEach((nearPlayer) -> {
                                nearPlayer.addHealth(
                                        wp,
                                        name,
                                        minDamageHeal * healMultiplier,
                                        maxDamageHeal * healMultiplier,
                                        critChance,
                                        critMultiplier
                                );
                            });
                } else {
                    totemStand.remove();
                    this.cancel();
                }
                timeLeft--;
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }


}