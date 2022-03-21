package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class EarthenSpike extends AbstractAbility {

    private final int radius = 10;

    private static final String[] REPEATING_SOUND = new String[]{
            "shaman.earthenspike.animation.a",
            "shaman.earthenspike.animation.b",
            "shaman.earthenspike.animation.c",
            "shaman.earthenspike.animation.d",
    };

    public EarthenSpike() {
        super("Earthen Spike", 404, 562, 0, 100, 15, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Send forth an underground earth spike\n" +
                "§7that locks onto a targeted enemy player.\n" +
                "§7When the spike reaches its target it\n" +
                "§7emerges from the ground, dealing §c" + format(minDamageHeal) + " §7-\n" +
                "§c" + format(maxDamageHeal) + " §7damage to any nearby enemies and\n" +
                "§7launches them up into the air." +
                "\n\n" +
                "§7Has an initial cast range of §e" + radius + " §7blocks.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        Location location = player.getLocation();
        for (WarlordsPlayer p : PlayerFilter
                .entitiesAround(player, radius, radius, radius)
                .aliveEnemiesOf(wp)
                .lookingAtFirst(wp)
        ) {
            if (Utils.isLookingAt(player, p.getEntity()) && Utils.hasLineOfSight(player, p.getEntity())) {
                PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);

                FallingBlock block = spawnFallingBlock(location, location);
                EarthenSpikeBlock earthenSpikeBlock = new EarthenSpikeBlock(new CustomFallingBlock(block, block.getLocation().getY() - .2), p, wp);
                wp.subtractEnergy(energyCost);


                new GameRunnable(wp.getGame()) {
                    private final float SPEED = 1;
                    private final float SPEED_SQUARED = SPEED * SPEED;
                    private final Location spikeLoc = location;
                    {
                        spikeLoc.setY(spikeLoc.getBlockY());
                    }

                    @Override
                    public void run() {
                        earthenSpikeBlock.addDuration();

                        List<CustomFallingBlock> customFallingBlocks = earthenSpikeBlock.getFallingBlocks();
                        WarlordsPlayer target = earthenSpikeBlock.getTarget();
                        WarlordsPlayer user = earthenSpikeBlock.getUser();

                        if (earthenSpikeBlock.getDuration() % 5 == 1) {
                            Utils.playGlobalSound(spikeLoc, REPEATING_SOUND[(earthenSpikeBlock.getDuration() / 5) % 4], 2, 1);
                        }

                        if (earthenSpikeBlock.getDuration() > 30) {
                            //out of time
                            earthenSpikeBlock.setDuration(-1);
                            this.cancel();
                            return;
                        }

                        Vector change = target.getLocation().toVector().subtract(spikeLoc.toVector());
                        change.setY(0);
                        double length = change.lengthSquared();
                        if (length > SPEED_SQUARED) {
                            change.multiply(1 / (Math.sqrt(length) / SPEED));
                            spikeLoc.add(change);

                            //moving vertically
                            if (target.getLocation().getY() < spikeLoc.getY()) {
                                for (int j = 0; j < 10; j++) {
                                    if (spikeLoc.clone().add(0, -1, 0).getBlock().getType() == Material.AIR) {
                                        spikeLoc.add(0, -1, 0);
                                    } else {
                                        break;
                                    }
                                }
                            } else {
                                for (int j = 0; j < 10; j++) {
                                    if (spikeLoc.getBlock().getType() != Material.AIR) {
                                        spikeLoc.add(0, 1, 0);
                                    } else {
                                        break;
                                    }
                                }
                            }
                            //temp fix for block glitch
                            for (int i = 0; i < 10; i++) {
                                if (spikeLoc.getBlock().getType() != Material.AIR) {
                                    spikeLoc.add(0, 1, 0);
                                } else {
                                    break;
                                }
                            }

                            FallingBlock newBlock = spawnFallingBlock(spikeLoc, spikeLoc);
                            customFallingBlocks.add(new CustomFallingBlock(newBlock, newBlock.getLocation().getY() - .20));
                        } else {
                            //impact
                            Location targetLocation = target.getLocation();
                            for (WarlordsPlayer spikeTarget : PlayerFilter
                                    .entitiesAround(targetLocation, 2.5, 2.5, 2.5)
                                    .aliveEnemiesOf(wp)
                            ) {
                                spikeTarget.addDamageInstance(user, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
                                //todo tweak distance to ground where you cant get kbed up (1.82 is max jump blocks, double spike kb might be possible with this)
                                if (Utils.getDistance(spikeTarget.getEntity(), .1) < 1.82) {
                                    spikeTarget.setVelocity(new Vector(0, .625, 0));
                                }
                            }

                            Utils.playGlobalSound(wp.getLocation(), "shaman.earthenspike.impact", 2, 1);

                            targetLocation.setYaw(0);
                            for (int i = 0; i < 100; i++) {
                                if (targetLocation.clone().add(0, -1, 0).getBlock().getType() == Material.AIR) {
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
                        if (target.isDead()) {
                            earthenSpikeBlock.setDuration(-1);
                            this.cancel();
                        }

                    }

                }.runTaskTimer(0, 2);

                new GameRunnable(wp.getGame()) {

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

                }.runTaskTimer(0, 0);
                break;
            }
        }
        return true;
    }


    private FallingBlock spawnFallingBlock(Location location, Location blockTypeAndData) {
        Location spawnLocation = location.clone();
        for (int i = 0; i < 100; i++) {
            if (spawnLocation.getBlock().getType() != Material.AIR) {
                spawnLocation.add(0, 1, 0);
            } else {
                break;
            }
        }
        FallingBlock newBlock = spawnLocation.getWorld().spawnFallingBlock(
                spawnLocation,
                blockTypeAndData.clone().add(0, -1, 0).getBlock().getType(),
                blockTypeAndData.clone().add(0, -1, 0).getBlock().getData()
        );
        newBlock.setVelocity(new Vector(0, .25, 0));
        newBlock.setDropItem(false);
        WarlordsEvents.addEntityUUID(newBlock);
        return newBlock;
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
