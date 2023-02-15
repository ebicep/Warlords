package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EarthenSpike extends AbstractAbility {

    private static final String[] REPEATING_SOUND = new String[]{
            "shaman.earthenspike.animation.a",
            "shaman.earthenspike.animation.b",
            "shaman.earthenspike.animation.c",
            "shaman.earthenspike.animation.d",
    };

    public int playersSpiked = 0;
    public int carrierSpiked = 0;

    private int radius = 10;
    private float speed = 1;
    private double spikeHitbox = 2.5;
    private double verticalVelocity = .625;

    public EarthenSpike() {
        super("Earthen Spike", 404, 562, 0, 100, 15, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Send forth an underground earth spike that locks onto a targeted enemy player. When the spike reaches its target it emerges from the ground, dealing" +
                formatRangeDamage(minDamageHeal, maxDamageHeal) + "damage to any nearby enemies and launches them up into the air." +
                "\n\nHas an initial cast range of §e" + radius + " §7blocks.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Spiked", "" + playersSpiked));
        info.add(new Pair<>("Times Carrier Spiked", "" + carrierSpiked));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        Location location = player.getLocation();
        for (WarlordsEntity spikeTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveEnemiesOf(wp)
                .lookingAtFirst(wp)
        ) {
            if (Utils.isLookingAt(player, spikeTarget.getEntity()) && Utils.hasLineOfSight(player, spikeTarget.getEntity())) {
                addTimesUsed();
                AbstractPlayerClass.sendRightClickPacket(player);

                FallingBlock block = spawnFallingBlock(location, location);
                EarthenSpikeBlock earthenSpikeBlock = new EarthenSpikeBlock(new CustomFallingBlock(block, block.getLocation().getY() - .2), spikeTarget, wp);
                wp.subtractEnergy(energyCost, false);

                new GameRunnable(wp.getGame()) {
                    private final float SPEED_SQUARED = speed * speed;
                    private final Location spikeLoc = location;

                    {
                        spikeLoc.setY(spikeLoc.getBlockY());
                    }

                    @Override
                    public void run() {
                        earthenSpikeBlock.addDuration();

                        List<CustomFallingBlock> customFallingBlocks = earthenSpikeBlock.getFallingBlocks();
                        WarlordsEntity target = earthenSpikeBlock.getTarget();
                        WarlordsEntity user = earthenSpikeBlock.getUser();

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
                            change.multiply(1 / (Math.sqrt(length) / speed));
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
                            for (WarlordsEntity spikeTarget : PlayerFilter
                                    .entitiesAround(targetLocation, spikeHitbox, spikeHitbox, spikeHitbox)
                                    .aliveEnemiesOf(wp)
                            ) {
                                playersSpiked++;
                                if (spikeTarget.hasFlag()) {
                                    carrierSpiked++;
                                }
                                spikeTarget.addDamageInstance(user, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
                                //todo tweak distance to ground where you cant get kbed up (1.82 is max jump blocks, double spike kb might be possible with this)
                                if (Utils.getDistance(spikeTarget.getEntity(), .1) < 1.82) {
                                    spikeTarget.setVelocity(name, new Vector(0, verticalVelocity, 0), false);
                                }
                            }

                            if (pveUpgrade) {
                                new GameRunnable(wp.getGame()) {
                                    @Override
                                    public void run() {
                                        new FallingBlockWaveEffect(targetLocation.add(0, 1, 0), 4, 0.9, Material.DIRT).play();
                                        for (WarlordsEntity wave : PlayerFilter
                                                .entitiesAround(targetLocation, 6, 6, 6)
                                                .aliveEnemiesOf(wp)
                                        ) {
                                            wave.addDamageInstance(wp, "Earthen Rupture", 548, 695, -1, 100, false);
                                            wave.addSpeedModifier(wp, "Spike Slow", -50, 20);
                                        }
                                        Utils.playGlobalSound(targetLocation, Sound.BLOCK_GRAVEL_BREAK, 2, 0.5f);
                                        ParticleEffect.EXPLOSION_LARGE.display(1, 1, 1, 0.01f, 2, targetLocation, 500);
                                    }
                                }.runTaskLater(15);
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
                blockTypeAndData.clone().add(0, -1, 0).getBlock().getBlockData()
        );
        newBlock.setVelocity(new Vector(0, .25, 0));
        newBlock.setDropItem(false);
        WarlordsEvents.addEntityUUID(newBlock);
        return newBlock;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }


    public double getVerticalVelocity() {
        return verticalVelocity;
    }

    public void setVerticalVelocity(double verticalVelocity) {
        this.verticalVelocity = verticalVelocity;
    }

    public double getSpikeHitbox() {
        return spikeHitbox;
    }

    public void setSpikeHitbox(double spikeHitbox) {
        this.spikeHitbox = spikeHitbox;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public class EarthenSpikeBlock {

        private final WarlordsEntity target;
        private final WarlordsEntity user;
        private List<CustomFallingBlock> fallingBlocks = new ArrayList<>();
        private int duration;
        private int removed;

        public EarthenSpikeBlock(CustomFallingBlock block, WarlordsEntity target, WarlordsEntity user) {
            fallingBlocks.add(block);
            this.target = target;
            this.user = user;
            this.duration = 0;
            this.removed = 0;
        }

        public WarlordsEntity getTarget() {
            return target;
        }

        public WarlordsEntity getUser() {
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

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public void addRemoved() {
            this.removed++;
        }

        public int getRemoved() {
            return removed;
        }
    }

    public static class CustomFallingBlock {
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
