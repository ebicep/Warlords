package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;

public class Boulder extends AbstractAbility {

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
    public void onActivate(Player player) {

        Location location = player.getLocation();
        Vector speed = player.getLocation().getDirection().multiply(0.55);
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, 0.25, 0), EntityType.ARMOR_STAND);
        stand.setHelmet(new ItemStack(Material.LONG_GRASS, 1, (short) 2));
        stand.setCustomName("Boulder");
        stand.setCustomNameVisible(false);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.setMarker(false);

        Warlords.getPlayer(player).subtractEnergy(energyCost);

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

                speed.add(new Vector(0, -0.00765, 0));
                Location newLoc = stand.getLocation();
                newLoc.add(speed);
                stand.teleport(newLoc);

                if (speed.getY() < 0) {
                    stand.setHeadPose(new EulerAngle(speed.getY() / 2 * -1, 0, 0));
                } else {
                    stand.setHeadPose(new EulerAngle(speed.getY() * -1, 0, 0));
                }

                boolean boulderExplode = false;
                List<Entity> near = null;

                if (last) {
                    Location particleLoc = newLoc.clone().add(0, 2, 0);
                    ParticleEffect.CRIT.display(0.3F, 0.3F, 0.3F, 0.1F, 4, particleLoc, 500);
                }
                if (!newLoc.getBlock().isEmpty()) {
                    boulderExplode = true;
                    near = (List<Entity>) newLoc.getWorld().getNearbyEntities(newLoc, 5, 5, 5);
                    near = Utils.filterOutTeammates(near, player);

                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(newLoc, "shaman.boulder.impact", 2, 1);
                    }

                } else {
                    Collection<Entity> nearbyEntities = stand.getWorld().getNearbyEntities(stand.getLocation().clone().add(0, .5, 0), 1.25, 1.25, 1.25);
                    for (Entity entity : nearbyEntities) {
                        if (entity instanceof Player) {
                            if (!Warlords.game.onSameTeam(player, (Player) entity) && ((Player) entity).getGameMode() != GameMode.SPECTATOR) {
                                boulderExplode = true;
                                near = (List<Entity>) newLoc.getWorld().getNearbyEntities(newLoc, 5.5, 5.5, 5.5);
                                near = Utils.filterOutTeammates(near, player);
                                near.remove(entity);

                                for (Player player1 : player.getWorld().getPlayers()) {
                                    player1.playSound(newLoc, "shaman.boulder.impact", 2, 1);
                                }

                                final Vector v = entity.getLocation().toVector().subtract(location.toVector()).normalize().multiply(1.05).setY(0.3);
                                entity.setVelocity(v);

                                Warlords.getPlayer((Player) entity).addHealth(Warlords.getPlayer(player), name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);

                                break;
                            }
                        }
                    }
                }

                if (boulderExplode) {
                    stand.remove();
                    for (Entity entity2 : near) {
                        if (entity2 instanceof Player) {
                            Player nearPlayer = (Player) entity2;
                            if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                final Vector v = nearPlayer.getLocation().toVector().subtract(newLoc.toVector()).normalize().multiply(1.05).setY(0.3);
                                nearPlayer.setVelocity(v);

                                Warlords.getPlayer(nearPlayer).addHealth(Warlords.getPlayer(player), name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                            }
                        }
                    }
                    newLoc.setPitch(-12);
                    newLoc.add(0, 1, 0);
                    for (int i = 0; i < 24; i++) {
                        if (location.getWorld().getBlockAt(newLoc).getType() == Material.AIR) {
                            FallingBlock fallingBlock;
                            switch ((int) (Math.random() * 3)) {
                                case 0:
                                    fallingBlock = newLoc.getWorld().spawnFallingBlock(newLoc.clone().add(0, 0.5, 0), Material.DIRT, (byte) 0);
                                    break;
                                case 1:
                                    fallingBlock = newLoc.getWorld().spawnFallingBlock(newLoc.clone().add(0, 0.5, 0), Material.STONE, (byte) 0);
                                    break;
                                case 2:
                                    fallingBlock = newLoc.getWorld().spawnFallingBlock(newLoc.clone().add(0, 0.5, 0), Material.DIRT, (byte) 2);
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + (int) (Math.random() * 3));
                            }
                            fallingBlock.setVelocity(newLoc.getDirection().add(new Vector(0, 0.2, 0)).normalize().multiply(.5));
                            fallingBlock.setDropItem(false);
                            fallingBlock.setTicksLived(4);
                            newLoc.setYaw((float) (newLoc.getYaw() + Math.random() * 25 + 12));
                            WarlordsEvents.addEntityUUID(fallingBlock.getUniqueId());
                        }
                    }
                    this.cancel();
                }
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 1);


        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.boulder.activation", 1.5F, 1);
        }
    }
}