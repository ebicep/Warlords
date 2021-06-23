package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class EarthenSpike extends AbstractAbility {

    public EarthenSpike() {
        super("Earthen Spike", -476, -662, 0, 120, 15, 175,
                "§7Send forth an underground earth spike\n" +
                        "§7that locks onto a targeted enemy player.\n" +
                        "§7When the spike reaches its target it\n" +
                        "§7emerges from the ground, dealing §c476 §7-\n" +
                        "§c662 §7damage to any nearby enemies and\n" +
                        "§7launches them up into the air.");
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        Location location = player.getLocation();
        PlayerFilter.entitiesAround(player, 8.5D, 6.0D, 8.5D)
            .aliveEnemiesOf(wp)
            .forEach((p) -> {
            if (Utils.getLookingAt(player, p.getEntity()) && Utils.hasLineOfSight(player, p.getEntity())) {
                FallingBlock block = player.getWorld().spawnFallingBlock(location.clone(), location.getWorld().getBlockAt((int) location.getX(), (int) location.getY(), (int) location.getZ()).getType(), (byte) 0);
                block.setVelocity(new Vector(0, .2, 0));
                p.subtractEnergy(energyCost);
                EarthenSpikeBlock earthenSpikeBlock = new EarthenSpikeBlock(new CustomFallingBlock(block, block.getLocation().getY() - .2), p, wp);

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        earthenSpikeBlock.addDuration();

                        List<CustomFallingBlock> customFallingBlocks = earthenSpikeBlock.getFallingBlocks();
                        FallingBlock lastFallingBlock = customFallingBlocks.get(customFallingBlocks.size() - 1).getBlock();
                        WarlordsPlayer target = earthenSpikeBlock.getTarget();
                        WarlordsPlayer user = earthenSpikeBlock.getUser();

                        if (earthenSpikeBlock.getDuration() > 30) {
                            //out of time
                            earthenSpikeBlock.setDuration(-1);
                            this.cancel();
                        } else if (Math.abs(target.getLocation().getX() - lastFallingBlock.getLocation().getX()) + Math.abs(target.getLocation().getZ() - lastFallingBlock.getLocation().getZ()) > 1) {

                            // TODO: make sounds actually sound accurate to live instead of an earthquake, just threw them in for now
                            for (Player player1 : player.getWorld().getPlayers()) {
                                player1.playSound(lastFallingBlock.getLocation(), "shaman.earthenspike.animation.d", 1.5F, 1);
                            }

                            Location newLocation = lastFallingBlock.getLocation();
                            //moving diagonally
                            if (Math.abs(target.getLocation().getX() - newLocation.getX()) >= Math.abs(target.getLocation().getZ() - newLocation.getZ())) {

                                if (target.getLocation().getX() < newLocation.getX()) {
                                    newLocation.add(-1, 0, 0);
                                } else {
                                    newLocation.add(1, 0, 0);
                                }
                            } else {

                                for (Player player1 : player.getWorld().getPlayers()) {
                                    player1.playSound(lastFallingBlock.getLocation(), "shaman.earthenspike.animation.b", 1.5F, 1);
                                }

                                if (target.getLocation().getZ() < newLocation.getZ()) {
                                    newLocation.add(0, 0, -1);
                                } else {
                                    newLocation.add(0, 0, 1);
                                }
                            }
                            //moving vertically
                            if (target.getLocation().getY() < newLocation.getY()) {

                                for (int j = 0; j < 10; j++) {
                                    if (newLocation.getWorld().getBlockAt(newLocation.clone().add(0, -1, 0)).getType() == Material.AIR) {
                                        newLocation.add(0, -1, 0);
                                    } else {
                                        break;
                                    }
                                }
                            } else {
                                for (int j = 0; j < 10; j++) {
                                    if (newLocation.getWorld().getBlockAt(newLocation).getType() != Material.AIR) {
                                        newLocation.add(0, 1, 0);
                                    } else {
                                        break;
                                    }
                                }
                            }
                            FallingBlock newBlock = target.getWorld().spawnFallingBlock(newLocation, newLocation.getWorld().getBlockAt(newLocation.clone().add(0, -1, 0)).getType(), newLocation.getWorld().getBlockAt(newLocation.clone().add(0, -1, 0)).getData());
                            newBlock.setVelocity(new Vector(0, .2, 0));
                            newBlock.setDropItem(false);
                            System.out.println(newLocation);
                            customFallingBlocks.add(new CustomFallingBlock(newBlock, newBlock.getLocation().getY() - .20));
                        } else {
                            //impact
                            Location targetLocation = target.getLocation();
                            PlayerFilter.entitiesAround(targetLocation, .6, 1.5, .6)
                                .aliveEnemiesOf(wp)
                                .forEach(warlordsPlayer -> {
                                warlordsPlayer.addHealth(user, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                                if (Utils.getDistance(warlordsPlayer, 1) < 2.5) {
                                    warlordsPlayer.setVelocity(new Vector(0, .6, 0));
                                }
                            });

                            for (Player player1 : wp.getWorld().getPlayers()) {
                                player1.playSound(wp.getLocation(), "shaman.earthenspike.impact", 2, 1);
                            }

                            targetLocation.setYaw(0);
                            for (int i = 0; i < 25; i++) {
                                if (targetLocation.getWorld().getBlockAt(targetLocation.clone().add(0, -1, 0)).getType() == Material.AIR) {
                                    targetLocation.add(0, -1, 0);
                                } else {
                                    break;
                                }
                            }
                            ArmorStand stand = (ArmorStand) targetLocation.getWorld().spawnEntity(targetLocation.add(0, -.6, 0), EntityType.ARMOR_STAND);
                            stand.setHelmet(new ItemStack(Material.BROWN_MUSHROOM));
                            stand.setGravity(false);
                            stand.setVisible(false);
                            stand.setMarker(true);

                            new BukkitRunnable() {

                                @Override
                                public void run() {
                                    stand.remove();
                                    this.cancel();
                                }

                            }.runTaskTimer(Warlords.getInstance(), 10, 0);

                            earthenSpikeBlock.setDuration(-1);
                            this.cancel();
                        }
                        if (target.isDeath()) {
                            earthenSpikeBlock.setDuration(-1);
                            this.cancel();
                        }
                        System.out.println(earthenSpikeBlock.duration);
                    }

                }.runTaskTimer(Warlords.getInstance(), 0, (long) 2);

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        for (CustomFallingBlock fallingBlock : earthenSpikeBlock.getFallingBlocks()) {
                            FallingBlock block = fallingBlock.getBlock();
                            if (block.isValid()) {
                                if (block.getLocation().getY() <= fallingBlock.getyLevelToRemove()) {// || block.getTicksLived() >= 10) {
                                    block.remove();
                                    earthenSpikeBlock.addRemoved();
                                }
                            }
                        }
                        if (earthenSpikeBlock.getDuration() == -1 && earthenSpikeBlock.getRemoved() == earthenSpikeBlock.getFallingBlocks().size()) {
                            this.cancel();
                        }
                    }

                }.runTaskTimer(Warlords.getInstance(), 0, 0);
            }
        });
    }

    public class EarthenSpikeBlock {

        private List<CustomFallingBlock> fallingBlocks = new ArrayList<>();
        private final WarlordsPlayer target;
        private final WarlordsPlayer user;
        private int duration;
        private int removed;

        public EarthenSpikeBlock(CustomFallingBlock block, WarlordsPlayer target, WarlordsPlayer user) {
            fallingBlocks.add(block);
            this.target = target;
            this.user = user;
            this.duration = 0;
            this.removed = 0;
        }

        public WarlordsPlayer getTarget() {
            return target;
        }

        public WarlordsPlayer getUser() {
            return user;
        }

        public List<CustomFallingBlock> getFallingBlocks() {
            return fallingBlocks;
        }

        public void setFallingBlocks(List<CustomFallingBlock> fallingBlocks) {
            this.fallingBlocks = fallingBlocks;
        }

        public void addDuration() {
            this.duration++;
        }

        public int getDuration() {
            return duration;
        }

        public void addRemoved() {
            this.removed++;
        }

        public int getRemoved() {
            return removed;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }

    public class CustomFallingBlock {
        private FallingBlock block;
        private double yLevelToRemove;

        public CustomFallingBlock(FallingBlock block, double yLevelToRemove) {
            this.block = block;
            this.yLevelToRemove = yLevelToRemove;
        }

        public FallingBlock getBlock() {
            return block;
        }

        public void setBlock(FallingBlock block) {
            this.block = block;
        }

        public double getyLevelToRemove() {
            return yLevelToRemove;
        }

        public void setyLevelToRemove(double yLevelToRemove) {
            this.yLevelToRemove = yLevelToRemove;
        }
    }
}
