package com.ebicep.warlords.effects;

import com.ebicep.customentities.nms.SelfRemovingFallingBlock;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ChasingBlockEffect {

    @Nullable
    private final Game game;
    private final float speed;
    private final float speedSquared;
    @Nullable
    private final BlockData blockState;
    private final Supplier<Location> destination;
    private final Consumer<Integer> onTick;
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
            Consumer<Integer> onTick,
            int maxTicks
    ) {
        this.game = game;
        this.speed = speed;
        this.speedSquared = speed * speed;
        this.blockState = blockState;
        this.destination = destination;
        this.onDestinationReached = onDestinationReached;
        this.onTick = onTick;
        this.maxTicks = maxTicks;
    }

    public void start(Location startingLocation) {
        this.currentLocation = startingLocation;
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
        onTick.accept(ticksElapsed);
        Vector change = destinationLocation.toVector().subtract(currentLocation.toVector());
        change.setY(0);
        double length = change.lengthSquared();
        // moving
        if (length > speedSquared) {
            change.multiply(1 / (Math.sqrt(length) / speed));
            currentLocation.add(change);

            //moving vertically
            if (destinationLocation.getY() < currentLocation.getY()) {
                for (int j = 0; j < 10; j++) {
                    if (currentLocation.clone().add(0, -1, 0).getBlock().getType() == Material.AIR) {
                        currentLocation.add(0, -1, 0);
                    } else {
                        break;
                    }
                }
            }
            for (int i = 0; i < 10; i++) {
                if (currentLocation.getBlock().getType() != Material.AIR) {
                    currentLocation.add(0, 1, 0);
                } else {
                    break;
                }
            }
            new SelfRemovingFallingBlock(
                    currentLocation,
                    Objects.requireNonNullElseGet(blockState, () -> currentLocation.getBlock().getRelative(BlockFace.DOWN, 1).getBlockData()),
                    .2,
                    block -> block.setVelocity(new Vector(0, .25, 0))
            );
        } else {
            //reached destination
            onDestinationReached.run();
            cancel();
        }
        ticksElapsed++;
    }

    public void cancel() {
        ticksElapsed = maxTicks;
    }

    public static class Builder {

        private @Nullable Game game = null;
        private float speed = 1;
        private @Nullable BlockData block = null;
        private Supplier<Location> destination;
        private Consumer<Integer> onTick = i -> {};
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

        public Builder setOnTick(Consumer<Integer> onTick) {
            this.onTick = onTick;
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
            return new ChasingBlockEffect(game, speed, block, destination, onDestinationReached, onTick, maxTicks);
        }
    }
}
