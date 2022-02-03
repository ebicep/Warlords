package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.FireWorkEffectPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class IncendiaryCurse extends AbstractAbility {

    private static final double SPEED = 0.220;
    private static final double GRAVITY = -0.008;
    private static final float HITBOX = 5;

    public IncendiaryCurse() {
        super("Incendiary Curse", 408, 575, 9, 60, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Ignite the targeted area with a cross flame,\n" +
                    "§7dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage. §7After 3 seconds,\n" +
                    "§7enemies hit will burn for §f101 §7- §f146 §7true damage\n" +
                    "§7every second for §63 §7seconds.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);

        Location location = player.getLocation();
        Vector speed = player.getLocation().getDirection().multiply(SPEED);
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setHelmet(new ItemStack(Material.FIREBALL));
        stand.setGravity(false);
        stand.setVisible(false);
        wp.getGame().getGameTasks().put(
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (!wp.getGame().isGameFreeze()) {

                            quarterStep(false);
                            quarterStep(false);
                            quarterStep(false);
                            quarterStep(false);
                            quarterStep(false);
                            quarterStep(false);
                            quarterStep(true);
                        }
                    }

                    private void quarterStep(boolean last) {

                        if (!stand.isValid()) {
                            this.cancel();
                            return;
                        }

                        speed.add(new Vector(0, GRAVITY * SPEED, 0));
                        Location newLoc = stand.getLocation();
                        newLoc.add(speed);
                        stand.teleport(newLoc);
                        newLoc.add(0, 1.75, 0);

                        stand.setHeadPose(new EulerAngle(-speed.getY() * 3, 0, 0));

                        boolean shouldExplode;

                        if (last) {
                            ParticleEffect.FIREWORKS_SPARK.display(0.1f, 0.1f, 0.1f, 0.1f, 4, newLoc.clone().add(0, -1, 0), 500);
                        }

                        WarlordsPlayer directHit;
                        if (!newLoc.getBlock().isEmpty()
                                && newLoc.getBlock().getType() != Material.GRASS
                                && newLoc.getBlock().getType() != Material.BARRIER
                                && newLoc.getBlock().getType() != Material.VINE
                        ) {
                            // Explode based on collision
                            shouldExplode = true;
                        } else {
                            directHit = PlayerFilter
                                    .entitiesAroundRectangle(newLoc, 1, 2, 1)
                                    .aliveEnemiesOf(wp)
                                    .findFirstOrNull();
                            shouldExplode = directHit != null;
                        }

                        if (shouldExplode) {
                            stand.remove();
                            for (Player player1 : wp.getWorld().getPlayers()) {
                                player1.playSound(newLoc, "mage.flameburst.impact", 2, 0.5f);
                            }

                            FireWorkEffectPlayer.playFirework(newLoc, FireworkEffect.builder()
                                    .withColor(Color.ORANGE)
                                    .withColor(Color.RED)
                                    .with(FireworkEffect.Type.BURST)
                                    .build());

                            new FallingBlockWaveEffect(newLoc.clone().add(0, 1, 0), 5, 1, Material.FIRE, (byte) 1).play();

                            for (WarlordsPlayer nearEntity : PlayerFilter
                                    .entitiesAround(newLoc, HITBOX, HITBOX, HITBOX)
                                    .aliveEnemiesOf(wp)
                            ) {
                                nearEntity.addDamageInstance(
                                        wp,
                                        name,
                                        minDamageHeal,
                                        maxDamageHeal,
                                        critChance,
                                        critMultiplier,
                                        false
                                );
                            }

                            this.cancel();
                        }
                    }

                }.runTaskTimer(Warlords.getInstance(), 0, 1), System.currentTimeMillis()
        );

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.frostbolt.activation", 2, 0.7f);
        }

        return true;
    }
}
