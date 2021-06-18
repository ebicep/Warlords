package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.Utils;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class EarthenSpike extends AbstractAbility {

    public EarthenSpike() {
        super("Earthen Spike", -476, -662, 0, 120, 15, 175
        );
    }

    @Override
    public void updateDescription() {
        description = "§7Send forth an underground earth spike\n" +
                "§7that locks onto a targeted enemy player.\n" +
                "§7When the spike reaches its target it\n" +
                "§7emerges from the ground, dealing §c" + -minDamageHeal + " §7-\n" +
                "§c" + -maxDamageHeal + " §7damage to any nearby enemies and\n" +
                "§7launches them up into the air.";
    }

    @Override
    public void onActivate(Player player) {
        Location location = player.getLocation();
        List<Entity> near = player.getNearbyEntities(8.5D, 6.0D, 8.5D);
        near = Utils.filterOutTeammates(near, player);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                if (nearPlayer.getGameMode() != GameMode.SPECTATOR && Utils.getLookingAt(player, nearPlayer) && Utils.hasLineOfSight(player, nearPlayer)) {
                    PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);

                    //TODO fix block getting glitched into world
                    FallingBlock block = player.getWorld().spawnFallingBlock(location.clone(), location.getWorld().getBlockAt((int) location.getX(), (int) location.getY(), (int) location.getZ()).getType(), (byte) 0);
                    block.setVelocity(new Vector(0, .2, 0));
                    Warlords.getPlayer(player).subtractEnergy(energyCost);
                    EarthenSpikeBlock earthenSpikeBlock = new EarthenSpikeBlock(new CustomFallingBlock(block, block.getLocation().getY() - .2), nearPlayer, Warlords.getPlayer(player));

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            earthenSpikeBlock.addDuration();

                            List<CustomFallingBlock> customFallingBlocks = earthenSpikeBlock.getFallingBlocks();
                            FallingBlock lastFallingBlock = customFallingBlocks.get(customFallingBlocks.size() - 1).getBlock();
                            Player target = earthenSpikeBlock.getTarget();
                            WarlordsPlayer user = earthenSpikeBlock.getUser();

                            for (Player player1 : player.getWorld().getPlayers()) {
                                player1.playSound(lastFallingBlock.getLocation(), "shaman.earthenspike.animation.d", 2, 1);
                            }

                            if (earthenSpikeBlock.getDuration() > 30) {
                                //out of time
                                earthenSpikeBlock.setDuration(-1);
                                this.cancel();
                            } else if (Math.abs(target.getLocation().getX() - lastFallingBlock.getLocation().getX()) + Math.abs(target.getLocation().getZ() - lastFallingBlock.getLocation().getZ()) > 1) {

                                Location newLocation = lastFallingBlock.getLocation();
                                //moving diagonally
                                if (Math.abs(target.getLocation().getBlockX() - newLocation.getBlockX()) > 0) {
                                    if (target.getLocation().getX() < newLocation.getX()) {
                                        newLocation.add(-1, 0, 0);
                                    } else {
                                        newLocation.add(1, 0, 0);
                                    }
                                    if (Math.abs(target.getLocation().getBlockZ() - newLocation.getBlockZ()) > 0) {
                                        if (target.getLocation().getZ() < newLocation.getZ()) {
                                            FallingBlock newBlock = target.getWorld().spawnFallingBlock(newLocation, newLocation.getWorld().getBlockAt(newLocation.clone().add(0, -1, -1)).getType(), newLocation.getWorld().getBlockAt(newLocation.clone().add(0, -1, 0)).getData());
                                            newBlock.setVelocity(new Vector(0, .25, 0));
                                            newBlock.setDropItem(false);
                                            customFallingBlocks.add(new CustomFallingBlock(newBlock, newBlock.getLocation().getY() - .20));
                                        } else {
                                            FallingBlock newBlock = target.getWorld().spawnFallingBlock(newLocation, newLocation.getWorld().getBlockAt(newLocation.clone().add(0, -1, 1)).getType(), newLocation.getWorld().getBlockAt(newLocation.clone().add(0, -1, 0)).getData());
                                            newBlock.setVelocity(new Vector(0, .25, 0));
                                            newBlock.setDropItem(false);
                                            customFallingBlocks.add(new CustomFallingBlock(newBlock, newBlock.getLocation().getY() - .20));
                                        }
                                    }
                                }
                                if (Math.abs(target.getLocation().getBlockZ() - newLocation.getBlockZ()) > 0) {
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
                                customFallingBlocks.add(new CustomFallingBlock(newBlock, newBlock.getLocation().getY() - .20));
                            } else {
                                //impact
                                Location targetLocation = target.getLocation();
                                List<Entity> onSameBlock = (List<Entity>) targetLocation.getWorld().getNearbyEntities(targetLocation, .6, 1.5, .6);
                                onSameBlock = Utils.filterOutTeammates(onSameBlock, user.getPlayer());
                                for (Entity entity : onSameBlock) {
                                    if (entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR) {
                                        Warlords.getPlayer((Player) entity).addHealth(user, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                                        if (Utils.getDistance(entity, 1) < 2.5) {
                                            entity.setVelocity(new Vector(0, .6, 0));
                                        }
                                    }
                                }

                                for (Player player1 : entity.getWorld().getPlayers()) {
                                    player1.playSound(entity.getLocation(), "shaman.earthenspike.impact", 2, 1);
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
                            if (target.getGameMode() == GameMode.SPECTATOR) {
                                earthenSpikeBlock.setDuration(-1);
                                this.cancel();
                            }
                            System.out.println(earthenSpikeBlock.duration);
                        }

                    }.runTaskTimer(Warlords.getInstance(), 0, 2);

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

                    break;
                }
            }
        }
    }

    public class EarthenSpikeBlock {

        private List<CustomFallingBlock> fallingBlocks = new ArrayList<>();
        private final Player target;
        private final WarlordsPlayer user;
        private int duration;
        private int removed;

        public EarthenSpikeBlock(CustomFallingBlock block, Player target, WarlordsPlayer user) {
            fallingBlocks.add(block);
            this.target = target;
            this.user = user;
            this.duration = 0;
            this.removed = 0;
        }

        public Player getTarget() {
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
