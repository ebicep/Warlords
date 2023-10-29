package com.ebicep.warlords.game.option.payload;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.List;

public interface PayloadRenderer {

    void init(@Nonnull Game game);

    void move(Location newCenter);

    void playEffects(int ticksElapsed, Location center, double radius);

    void cleanup();

    default void addRenderPathRunnable(Game game, Location start, List<PayloadBrain.PathEntry> path) {
        new GameRunnable(game) {

            static final double RENDER_MOVE = .5;
            final Location displayLocation = start.clone();
            double mappedPathIndex = 0;

            @Override
            public void run() {
                if (game.isState(EndState.class)) {
                    this.cancel();
                    return;
                }
                Pair<PayloadBrain.PathEntry, Integer> currentPathLocationMapped = PayloadBrain.getPathLocationMapped(path, mappedPathIndex, start);
                int pathIndex = currentPathLocationMapped.getB();
                if (pathIndex >= path.size() - 1) {
                    displayLocation.set(start.getX(), start.getY(), start.getZ());
                    mappedPathIndex = 0;
                    return;
                }
                PayloadBrain.PathEntry currentPathEntry = currentPathLocationMapped.getA();
                PayloadBrain.PathEntry nextPathEntry = path.get(pathIndex + 1);
                LocationBuilder currentPathLoc = new LocationBuilder(currentPathEntry.location());
                currentPathLoc.faceTowards(nextPathEntry.location());
                currentPathLoc.forward(mappedPathIndex - currentPathEntry.mappedIndex());
                displayLocation.set(currentPathLoc.getX(), currentPathLoc.getY(), currentPathLoc.getZ());
                EffectUtils.displayParticle(Particle.WATER_WAKE, displayLocation.clone().add(0, 1, 0), 1);
                mappedPathIndex += RENDER_MOVE;
            }
        }.runTaskTimer(0, 0);
    }

    default void renderPath(List<PayloadBrain.PathEntry> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            LocationBuilder location = new LocationBuilder(path.get(i).location());
            Location nextLocation = path.get(i + 1).location();
            // set vector facing nextLocation
            location.setDirection(nextLocation.toVector().subtract(location.toVector()).normalize());

            // render particle line towards nextLocation
            for (int j = 0; j < 10; j++) {
                EffectUtils.displayParticle(Particle.VILLAGER_HAPPY, location.clone().add(0, 1, 0), 1);
                location.forward(.1);
            }
        }
    }


}
