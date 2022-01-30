package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.FireWorkEffectPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class HealingCure extends AbstractAbility {

    private static final double SPEED = 0.220;
    private static final double GRAVITY = -0.008;
    private static final float HITBOX = 5;

    private final int puddleMinHealing = 183;
    private final int puddleMaxHealing = 236;

    public HealingCure() {
        super("Healing Cure", 536, 644, 10, 80, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Throw a short range projectile, healing\n" +
                "§7allies for §a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health upon impact.\n" +
                "§7The projectile will form a small puddle that\n" +
                "§7heals allies for §a" + puddleMinHealing + " §7- §a " + puddleMaxHealing + " §7health per second.\n" +
                "§7Lasts §63 §7seconds." +
                "\n\n" +
                "§7Has an optimal range of §e25 §7blocks.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);

        Location location = player.getLocation();
        Vector speed = player.getLocation().getDirection().multiply(SPEED);
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setHelmet(new ItemStack(Material.LEAVES));
        stand.setCustomName("Healing Cure");
        stand.setCustomNameVisible(false);
        stand.setGravity(false);
        stand.setBasePlate(false);
        stand.setArms(false);
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
                            ParticleEffect.VILLAGER_HAPPY.display(0.1f, 0.1f, 0.1f, 0.1f, 4, newLoc.clone().add(0, -1, 0), 500);
                        }

                        WarlordsPlayer directHit = null;
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
                                    .aliveTeammatesOfExcludingSelf(wp).findFirstOrNull();
                            shouldExplode = directHit != null;
                            newLoc.add(0, -1, 0);
                        }

                        DamageHealCircle med = new DamageHealCircle(wp, newLoc.add(0, 1, 0), HITBOX, 3, puddleMinHealing, puddleMaxHealing, critChance, critMultiplier, name);

                        if (shouldExplode) {
                            stand.remove();
                            for (Player player1 : wp.getWorld().getPlayers()) {
                                player1.playSound(newLoc, "rogue.healingremedy.impact", 1.2f, 0.6f);
                                player1.playSound(newLoc, "mage.waterbolt.impact", 0.5f, 0.3f);
                            }

                            FireWorkEffectPlayer.playFirework(newLoc, FireworkEffect.builder()
                                    .withColor(Color.WHITE)
                                    .with(FireworkEffect.Type.BURST)
                                    .build());

                            for (WarlordsPlayer nearEntity : PlayerFilter
                                    .entitiesAround(newLoc, HITBOX, HITBOX, HITBOX)
                                    .aliveTeammatesOf(wp)
                            ) {
                                nearEntity.addHealingInstance(
                                        wp,
                                        name,
                                        minDamageHeal,
                                        maxDamageHeal,
                                        critChance,
                                        critMultiplier,
                                        false,
                                        false);
                            }

                            WarlordsPlayer directHitFinal = directHit;
                            BukkitTask task = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), med::spawn, 0, 1);
                            wp.getGame().getGameTasks().put(task, System.currentTimeMillis());
                            wp.getGame().getGameTasks().put(

                                    new BukkitRunnable() {

                                        @Override
                                        public void run() {
                                            if (!wp.getGame().isGameFreeze()) {
                                                PlayerFilter.entitiesAround(med.getLocation(), med.getRadius(), med.getRadius(), med.getRadius())
                                                        .aliveTeammatesOf(wp)
                                                        .forEach((ally) -> ally.addHealingInstance(
                                                                med.getWarlordsPlayer(),
                                                                med.getName(),
                                                                med.getMinDamage(),
                                                                med.getMaxDamage(),
                                                                med.getCritChance(),
                                                                med.getCritMultiplier(),
                                                                false,
                                                                false));

                                                med.setDuration(med.getDuration() - 1);

                                                if (med.getDuration() < 0) {
                                                    this.cancel();
                                                    task.cancel();
                                                }
                                            }
                                        }

                                    }.runTaskTimer(Warlords.getInstance(), 0, 20),
                                    System.currentTimeMillis()
                            );

                            this.cancel();
                        }
                    }

                }.runTaskTimer(Warlords.getInstance(), 0, 1),
                System.currentTimeMillis()
        );

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.frostbolt.activation", 2, 0.5f);
        }

        return true;
    }
}