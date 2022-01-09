package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.Matrix4d;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public class HeartToHeart extends AbstractAbility {

    public HeartToHeart() {
        super("Heart To Heart", 0, 0, 13, 40, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "PLACEHOLDER";
    }

    @Override
    public void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "rogue.hearttoheart.activation", 2, 1);
            player1.playSound(player.getLocation(), "rogue.hearttoheart.activation.alt", 2, 1.2f);
        }

        for (WarlordsPlayer heartTarget : PlayerFilter
                .entitiesAround(wp, 20, 20, 20)
                .aliveTeammatesOfExcludingSelf(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            if (Utils.isLookingAtMark(player, heartTarget.getEntity()) && Utils.hasLineOfSight(player, heartTarget.getEntity())) {
                new BukkitRunnable() {

                    final Location playerLoc = wp.getLocation();
                    int timer = 0;

                    @Override
                    public void run() {
                        timer++;

                        if (timer >= 8 || (heartTarget.isDead() || wp.isDead())) {
                            this.cancel();
                            if (!wp.getLocation().getBlock().getType().isSolid() && !wp.getLocation().add(0, 1, 0).getBlock().getType().isSolid()) {
                                return;
                            }
                        }

                        double target = timer / 8D;
                        Location targetLoc = heartTarget.getLocation();
                        Location newLocation = new Location(
                                playerLoc.getWorld(),
                                Utils.lerp(playerLoc.getX(), targetLoc.getX(), target),
                                Utils.lerp(playerLoc.getY(), targetLoc.getY(), target),
                                Utils.lerp(playerLoc.getZ(), targetLoc.getZ(), target),
                                targetLoc.getYaw(),
                                targetLoc.getPitch()
                        );
                        wp.teleportLocationOnly(newLocation);
                        newLocation.add(0, 1, 0);
                        Matrix4d center = new Matrix4d(newLocation);
                        for (float i = 0; i < 6; i++) {
                            double angle = Math.toRadians(i * 90) + timer * 0.6;
                            double width = 1.5D;
                            ParticleEffect.SPELL_WITCH.display(0, 0, 0, 0, 2,
                                    center.translateVector(playerLoc.getWorld(), 0, Math.sin(angle) * width, Math.cos(angle) * width), 500);
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 0, 1);
            }
        }
    }
}
