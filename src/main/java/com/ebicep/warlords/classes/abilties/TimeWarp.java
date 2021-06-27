package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TimeWarp extends AbstractAbility {

    private int counter = 0;

    public TimeWarp() {
        super("Time Warp", 0, 0, 28.19f, 30, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Activate to place a time rune on\n" +
                "§7the ground. After §65 §7seconds,\n" +
                "§7you will warp back to that location\n" +
                "§7and restore §a30% §7of your health";
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        TimeWarpPlayer timeWarpPlayer = new TimeWarpPlayer(warlordsPlayer, player.getLocation(), player.getLocation().getDirection(), 5);
        warlordsPlayer.getCooldownManager().addCooldown(TimeWarp.this.getClass(), "TIME", 5, warlordsPlayer, CooldownTypes.ABILITY);
        warlordsPlayer.subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.timewarp.activation", 2, 1);
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                if (timeWarpPlayer.getWarlordsPlayer().isDeath()) {
                    counter = 0;
                    this.cancel();
                }

                //PARTICLES
                if (counter % 2 == 0) {
                    if (timeWarpPlayer.getTime() != 0) {
                        for (Location location : warlordsPlayer.getTrail()) {
                            ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(175, 0, 175), location, 500);
                        }
                    }
                }

                if (counter % 4 == 0) {

                    if (timeWarpPlayer.getTime() != 0) {
                        warlordsPlayer.getTrail().add(player.getLocation());
                    }
                }

                if (counter % 4 == 0) {
                    if (timeWarpPlayer.getTime() != 0) {
                        ParticleEffect.SPELL_WITCH.display(0F, 0F, 0F, 0.001F, 6, timeWarpPlayer.getLocation(), 500);
                    }

                    int points = 6;
                    double radius = 0.5d;
                    Location origin = timeWarpPlayer.getLocation();

                    for (int e = 0; e < points; e++) {
                        double angle = 2 * Math.PI * e / points;
                        Location point = origin.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
                        ParticleEffect.CLOUD.display(0.1F, 0F, 0.1F, 0.001F, 1, point, 500);
                    }

                }

                //TIME WARPS
                if (counter % 20 == 0) {
                    if (timeWarpPlayer.getTime() != 0) {
                        timeWarpPlayer.setTime(timeWarpPlayer.getTime() - 1);
                    } else {
                        WarlordsPlayer player = timeWarpPlayer.getWarlordsPlayer();
                        player.addHealth(player, "Time Warp", (player.getMaxHealth() * .3f), (player.getMaxHealth() * .3f), -1, 100);
                        for (Player player1 : player.getEntity().getWorld().getPlayers()) {
                            player1.playSound(timeWarpPlayer.getLocation(), "mage.timewarp.teleport", 1, 1);
                        }
                        timeWarpPlayer.getLocation().setDirection(timeWarpPlayer.getFacing());
                        player.getEntity().teleport(timeWarpPlayer.getLocation());

                        warlordsPlayer.getTrail().clear();
                        counter = 0;
                        this.cancel();
                    }
                }
                counter++;
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 0);
    }

    public static class TimeWarpPlayer {

        private WarlordsPlayer warlordsPlayer;
        private Location location;
        private Vector facing;
        private int time;

        public TimeWarpPlayer(WarlordsPlayer warlordsPlayer, Location location, Vector facing, int time) {
            this.warlordsPlayer = warlordsPlayer;
            this.location = location;
            this.facing = facing;
            this.time = time;
        }

        public WarlordsPlayer getWarlordsPlayer() {
            return warlordsPlayer;
        }

        public void setWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
            this.warlordsPlayer = warlordsPlayer;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public Vector getFacing() {
            return facing;
        }

        public void setFacing(Vector facing) {
            this.facing = facing;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }
    }
}
