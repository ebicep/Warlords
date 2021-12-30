package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.maps.state.EndState;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TimeWarp extends AbstractAbility {

    private final int duration = 5;
    private int warpHealPercentage = 30;

    public TimeWarp() {
        super("Time Warp", 0, 0, 28.19f, 30, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Activate to place a time rune on\n" +
                "§7the ground. After §6" + duration + " §7seconds,\n" +
                "§7you will warp back to that location\n" +
                "§7and restore §a" + warpHealPercentage + "% §7of your health";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);
        wp.getCooldownManager().addCooldown(name, TimeWarp.this.getClass(), new TimeWarp(), "TIME", duration, wp, CooldownTypes.ABILITY);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.timewarp.activation", 3, 1);
        }
        wp.getGame().getGameTasks().put(

                new BukkitRunnable() {

                    int time = duration - 1;
                    private int counter = 1;
                    final Location warpLocation = wp.getLocation();
                    final List<Location> warpTrail = new ArrayList<>();

                    @Override
                    public void run() {
                        if (!wp.getGame().isGameFreeze()) {

                            if (wp.isDeath() || wp.getGame().getState() instanceof EndState) {
                                counter = 0;
                                this.cancel();
                            }

                            //PARTICLES
                            if (counter % 4 == 0) {
                                if (time != 0) {
                                    for (Location location : warpTrail) {
                                        ParticleEffect.SPELL_WITCH.display(0.01f, 0, 0.01f, 0.001f, 1, location, 500);
                                    }
                                }
                            }

                            if (counter % 4 == 0) {
                                if (time != 0) {
                                    warpTrail.add(player.getLocation());
                                }
                            }

                            if (counter % 4 == 0) {
                                if (time != 0) {
                                    ParticleEffect.SPELL_WITCH.display(0.1f, 0, 0.1f, 0.001f, 4, warpLocation, 500);
                                }

                                int points = 6;
                                double radius = 0.5d;

                                for (int e = 0; e < points; e++) {
                                    double angle = 2 * Math.PI * e / points;
                                    Location point = warpLocation.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
                                    ParticleEffect.CLOUD.display(0.1F, 0, 0.1F, 0.001F, 1, point, 500);
                                }

                            }

                            //TIME WARPS
                            if (counter % 20 == 0) {
                                if (time != 0) {
                                    time -= 1;
                                } else {
                                    wp.healHealth(wp, "Time Warp", wp.getMaxHealth() * (warpHealPercentage / 100f), wp.getMaxHealth() * (warpHealPercentage / 100f), -1, 100, false);
                                    for (Player player1 : wp.getEntity().getWorld().getPlayers()) {
                                        player1.playSound(wp.getLocation(), "mage.timewarp.teleport", 1, 1);
                                    }
                                    wp.getEntity().teleport(warpLocation);

                                    warpTrail.clear();
                                    counter = 0;
                                    this.cancel();
                                }
                            }
                            counter++;
                        }
                    }

                }.runTaskTimer(Warlords.getInstance(), 0, 0),
                System.currentTimeMillis()
        );
    }

    public void setWarpHealPercentage(int warpHealPercentage) {
        this.warpHealPercentage = warpHealPercentage;
    }
}
