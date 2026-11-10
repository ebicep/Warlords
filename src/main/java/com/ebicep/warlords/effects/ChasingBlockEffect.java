package com.ebicep.warlords.effects;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.TriConsumer;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public class ChasingBlockEffect {

    @Nullable
    private final Game game;
    private final float speed;
    private final float speedSquared;
    @Nullable
    private final BlockData blockState;
    private final Supplier<Location> destination;
    private final TriConsumer<Integer, Location, Integer> onMove;
    private final Runnable onDestinationReached;
    private final int maxTicks;

    private Location currentLocation;
    private int ticksElapsed = 0;

    public ChasingBlockEffect(
            @Nullable Game game,
            float speed,
            @Nullable BlockData blockState,
            Supplier<Location> destination,
            Runnable onDestinationReached,
            TriConsumer<Integer, Location, Integer> onMove,
            int maxTicks
    ) {
        this.game = game;
        this.speed = speed;
        this.speedSquared = speed * speed;
        this.blockState = blockState;
        this.destination = destination;
        this.onDestinationReached = onDestinationReached;
        this.onMove = onMove;
        this.maxTicks = maxTicks;
    }

    public void start(Location startingLocation) {
        this.currentLocation = startingLocation.clone();
        if (game != null) {
            new GameRunnable(game) {
                @Override
                public void run() {
                    if (ticksElapsed >= maxTicks) {
                        this.cancel();
                        return;
                    }
                    ChasingBlockEffect.this.run();
                }
            }.runTaskTimer(0, 0);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (ticksElapsed >= maxTicks) {
                        this.cancel();
                        return;
                    }
                    ChasingBlockEffect.this.run();
                }
            }.runTaskTimer(Warlords.getInstance(), 0, 0);
        }
    }

    private void run() {
        Location destinationLocation = destination.get();
        if (destinationLocation == null) {
            cancel();
            return;
        }
        Vector change = destinationLocation.toVector().subtract(currentLocation.toVector());
        change.setY(0);
        double length = change.lengthSquared();
        // moving
        if (length > speedSquared) {
            change.multiply(1 / (Math.sqrt(length) / speed));
            LocationBuilder oldLocation = new LocationBuilder(currentLocation);
            currentLocation.add(change);
            oldLocation.faceTowards(currentLocation);
            double lastGroundY = currentLocation.getY();
            // loop through the distance in case where the distance is greater than 1, dont want block skipping
            // does look slightly weird since chunks of blocks will be placed/spawned at once with the same velocity (not a smooth transition)
            for (int i = 0; i < speed; i++) {
                snapToGround(oldLocation, destinationLocation);
                lastGroundY = oldLocation.getY();
                FallingBlockDebrisEffect.spawn(
                        oldLocation.clone(),
                        Objects.requireNonNullElseGet(blockState, () -> oldLocation.getBlock().getRelative(BlockFace.DOWN, 1).getBlockData()),
                        0.25,
                        0.15
                );
                onMove.accept(ticksElapsed, oldLocation.clone(), i);
                oldLocation.forward(1);
            }
            currentLocation.setY(lastGroundY);
        } else {
            LocationBuilder horizontalDestination = new LocationBuilder(destinationLocation);
            horizontalDestination.setY(currentLocation.getY());
            LocationBuilder spawnLocation = new LocationBuilder(currentLocation).faceTowards(horizontalDestination).forward(1);
            snapToGround(spawnLocation, destinationLocation);
            FallingBlockDebrisEffect.spawn(
                    spawnLocation.clone(),
                    Objects.requireNonNullElseGet(blockState, () -> spawnLocation.getBlock().getRelative(BlockFace.DOWN, 1).getBlockData()),
                    0.25,
                    0.15
            );
            //reached destination
            onDestinationReached.run();
            cancel();
        }
        ticksElapsed++;
    }

    private static void snapToGround(LocationBuilder loc, Location destination) {
        if (destination.getY() < loc.getY()) {
            for (int j = 0; j < 10; j++) {
                if (loc.clone().add(0, -1, 0).getBlock().getType() == Material.AIR) {
                    loc.add(0, -1, 0);
                } else {
                    break;
                }
            }
        }
        for (int j = 0; j < 10; j++) {
            if (loc.getBlock().getType() != Material.AIR) {
                loc.add(0, 1, 0);
            } else {
                break;
            }
        }
    }

    public void cancel() {
        ticksElapsed = maxTicks;
    }

    public static class Builder {

        private @Nullable Game game = null;
        private float speed = 1;
        private @Nullable BlockData block = null;
        private Supplier<Location> destination;
        private TriConsumer<Integer, Location, Integer> onMove = (ticksElapsed, loc, index) -> {};
        private Runnable onDestinationReached = () -> {};
        private int maxTicks = 200;

        public Builder setGame(@Nullable Game game) {
            this.game = game;
            return this;
        }

        public Builder setSpeed(float speed) {
            this.speed = speed;
            return this;
        }

        public Builder setBlock(@Nullable BlockData block) {
            this.block = block;
            return this;
        }

        public Builder setDestination(Supplier<Location> destination) {
            this.destination = destination;
            return this;
        }

        public Builder setOnMove(TriConsumer<Integer, Location, Integer> onMove) {
            this.onMove = onMove;
            return this;
        }

        public Builder setOnDestinationReached(Runnable onDestinationReached) {
            this.onDestinationReached = onDestinationReached;
            return this;
        }

        public Builder setMaxTicks(int maxTicks) {
            this.maxTicks = maxTicks;
            return this;
        }

        public ChasingBlockEffect create() {
            return new ChasingBlockEffect(game, speed, block, destination, onDestinationReached, onMove, maxTicks);
        }
    }
}
