package com.ebicep.holograms;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.function.Function;

public class Hologram {

    private final int id = HologramManager.entityId++;
    private final String name;
    private final Location location;
    private final Function<Player, HologramData> playerDataFunction;
    private final InteractManager interactManager;
    private final VisibilityManager visibilityManager;
    private int staticViewRange; // -1 if player dependent - static so data is not recomputed everytime to check if player within distance

    private Hologram(
            String name,
            Location location,
            Function<Player, HologramData> playerDataFunction,
            InteractManager interactManager,
            VisibilityManager visibilityManager,
            int staticViewRange
    ) {
        this.name = name;
        this.location = location;
        this.playerDataFunction = playerDataFunction;
        this.interactManager = interactManager;
        this.visibilityManager = visibilityManager;
        this.staticViewRange = staticViewRange;
    }

    public void deleteHologram() {
        HologramManager.deleteHologram(name);
    }

    public int getId() {
        return id;
    }

    public HologramData getDataForPlayer(Player player) {
        return playerDataFunction.apply(player);
    }

    public boolean withinRange(Player player) {
        Location playerLocation = player.getLocation();
        if (!Objects.equals(playerLocation.getWorld(), location.getWorld())) {
            return false;
        }
        if (staticViewRange != -1) {
            return playerLocation.distanceSquared(location) < staticViewRange * staticViewRange;
        }
        HologramData data = getDataForPlayer(player);
        return playerLocation.distanceSquared(location) < data.getViewRange() * data.getViewRange();
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public InteractManager getInteractManager() {
        return interactManager;
    }

    public VisibilityManager getVisibilityManager() {
        return visibilityManager;
    }

    public static class Builder {

        private final String name;
        private final Location location;
        private final Function<Player, HologramData> playerDataFunction;
        private InteractManager interactManager = null;
        private VisibilityManager visibilityManager = new VisibilityManager(VisibilityType.MANUAL);
        private int staticViewRange = HologramData.DEFAULT_VIEW_RANGE; // -1 if player dependent

        public Builder(String name, Location location, Function<Player, HologramData> playerDataFunction) {
            this.name = name;
            this.location = location;
            this.playerDataFunction = playerDataFunction;
        }

        public Builder setInteract(Function<Player, Boolean> onClick) {
            this.interactManager = new InteractManager(onClick, player -> new InteractData.Builder().build());
            return this;
        }

        public Builder setInteract(Function<Player, Boolean> onClick, Function<Player, InteractData> playerDataFunction) {
            this.interactManager = new InteractManager(onClick, playerDataFunction);
            return this;
        }

        public Builder setVisibility(VisibilityType visibilityType) {
            this.visibilityManager = new VisibilityManager(visibilityType);
            return this;
        }

        public Builder setStaticViewRange(int staticViewRange) {
            this.staticViewRange = staticViewRange;
            return this;
        }

        public Builder dynamicViewRange() {
            this.staticViewRange = -1;
            return this;
        }

        public Hologram build() {
            return new Hologram(name, location, playerDataFunction, interactManager, visibilityManager, staticViewRange);
        }
    }
}
