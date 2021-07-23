package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class Boulder extends AbstractAbility {

    private static final double SPEED = 0.5;
    private static final double GRAVITY = -0.0139;

    public Boulder() {
        super("Boulder", -490, -731, 7.05f, 80, 15, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Launch a giant boulder that shatters\n" +
                "§7and deals §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                "§7to all enemies near the impact point\n" +
                "§7and knocks them back slightly.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);

        Location location = player.getLocation();
        Vector speed = player.getLocation().getDirection().multiply(SPEED);
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setHelmet(new ItemStack(Material.LONG_GRASS, 1, (short) 2));
        stand.setCustomName("Boulder");
        stand.setCustomNameVisible(false);
        stand.setGravity(false);
        stand.setBasePlate(false);
        stand.setArms(false);
        stand.setVisible(false);

        new BukkitRunnable() {

            @Override
            public void run() {
                quarterStep(false);
                quarterStep(false);
                quarterStep(false);
                quarterStep(true);
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
                newLoc.add(0, 1.7, 0);

                if (speed.getY() < 0) {
                    stand.setHeadPose(new EulerAngle(speed.getY() / 2 * -1, 0, 0));
                } else {
                    stand.setHeadPose(new EulerAngle(speed.getY() * -1, 0, 0));
                }
                boolean shouldExplode;

                if (last) {
                    ParticleEffect.CRIT.display(0.3F, 0.3F, 0.3F, 0.1F, 4, newLoc, 500);
                }

                WarlordsPlayer directHit = null;
                if (!newLoc.getBlock().isEmpty()) {
                    // Explode based on collision
                    shouldExplode = true;
                } else {
                    directHit = PlayerFilter
                            .entitiesAroundRectangle(newLoc, 1.25, 2.5, 1.25)
                            .aliveEnemiesOf(wp).findFirstOrNull();
                    shouldExplode = directHit != null;
                }


                if (shouldExplode) {
                    stand.remove();
                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(newLoc, "shaman.boulder.impact", 2, 1);
                    }

                    for (WarlordsPlayer p : PlayerFilter
                            .entitiesAround(newLoc, 5, 5, 5)
                            .aliveEnemiesOf(wp)
                    ) {
                        p.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);

                        Entity entity = p.getEntity();
                        Vector v;
                        if (p == directHit) {
                            v = player.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(-1.1).setY(0.3);
                        } else {
                            v = entity.getLocation().toVector().subtract(newLoc.toVector()).normalize().multiply(1.1).setY(0.3);
                        }
                        entity.setVelocity(v);

                    }
                    newLoc.setPitch(-12);
                    for (int i = 0; i < 24; i++) {
                        FallingBlock fallingBlock;
                        Location spawnLoc = newLoc.clone().add(0, 0.5, 0)
                                .subtract(speed)
                                .subtract(speed)
                                .subtract(speed);
                        Vector velocity = newLoc.getDirection().add(new Vector(0, 0.2, 0)).normalize().multiply(.45);
                        double initialCircleRadius = 5;
                        spawnLoc.add(new Vector(initialCircleRadius, 0, initialCircleRadius).multiply(velocity));
                        switch ((int) (Math.random() * 3)) {
                            case 0:
                                fallingBlock = newLoc.getWorld().spawnFallingBlock(spawnLoc, Material.DIRT, (byte) 0);
                                break;
                            case 1:
                                fallingBlock = newLoc.getWorld().spawnFallingBlock(spawnLoc, Material.STONE, (byte) 0);
                                break;
                            case 2:
                                fallingBlock = newLoc.getWorld().spawnFallingBlock(spawnLoc, Material.DIRT, (byte) 2);
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + (int) (Math.random() * 3));
                        }
                        fallingBlock.setVelocity(velocity);
                        fallingBlock.setDropItem(false);
                        fallingBlock.setTicksLived(4);
                        newLoc.setYaw((float) (newLoc.getYaw() + Math.random() * 25 + 12));
                        WarlordsEvents.addEntityUUID(fallingBlock.getUniqueId());
                    }
                    this.cancel();
                }
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 1);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.boulder.activation", 2, 1);
        }
    }
}