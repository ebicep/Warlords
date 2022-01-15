package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.Matrix4d;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class HeartToHeart extends AbstractAbility {

    private final int radius = 20;
    private final int vindDuration = 6;

    public HeartToHeart() {
        super("Heart To Heart", 0, 0, 13, 40, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Throw a chain towards an ally in a §e" + radius + " §7block radius." +
                "§7Grappling the Vindicator towards the ally. The grappled ally is" +
                "§7granted §6" + vindDuration + " §7seconds of the VIND status effect, making them" +
                "§7immune to de-buffs and they gain §625% §7knockback" +
                "resistance the duration.";
    }

    @Override
    public void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "rogue.hearttoheart.activation", 2, 1);
            player1.playSound(player.getLocation(), "rogue.hearttoheart.activation.alt", 2, 1.2f);
        }

        for (WarlordsPlayer heartTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            if (Utils.isLookingAtMark(player, heartTarget.getEntity()) && Utils.hasLineOfSight(player, heartTarget.getEntity())) {

                wp.subtractEnergy(energyCost);
                heartTarget.getCooldownManager().addCooldown("Vindicate Debuff Immunity", this.getClass(), HeartToHeart.class, "VIND", vindDuration, wp, CooldownTypes.BUFF);
                heartTarget.getCooldownManager().addCooldown("KB Resistance", this.getClass(), Vindicate.class, "KB", vindDuration, wp, CooldownTypes.BUFF);

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

                        Location from = wp.getLocation().add(0, -0.6, 0);
                        Location to = heartTarget.getLocation().add(0, -0.6, 0);
                        from.setDirection(from.toVector().subtract(to.toVector()).multiply(-1));
                        List<ArmorStand> chains = new ArrayList<>();
                        int maxDistance = (int) Math.round(to.distance(from));
                        for (int i = 0; i < maxDistance; i++) {
                            ArmorStand chain = from.getWorld().spawn(from, ArmorStand.class);
                            chain.setHeadPose(new EulerAngle(from.getDirection().getY() * -1, 0, 0));
                            chain.setGravity(false);
                            chain.setVisible(false);
                            chain.setBasePlate(false);
                            chain.setMarker(true);
                            chain.setHelmet(new ItemStack(Material.CLAY));
                            from.add(from.getDirection().multiply(1.1));
                            chains.add(chain);
                            if(to.distanceSquared(from) < .3) {
                                break;
                            }
                        }

                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                if (chains.size() == 0) {
                                    this.cancel();
                                }

                                for (int i = 0; i < chains.size(); i++) {
                                    ArmorStand armorStand = chains.get(i);
                                    if (armorStand.getTicksLived() > timer) {
                                        armorStand.remove();
                                        chains.remove(i);
                                        i--;
                                    }
                                }

                            }

                        }.runTaskTimer(Warlords.getInstance(), 0, 0);

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
