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

    private Hologram(String name, Location location, Function<Player, HologramData> playerDataFunction, InteractManager interactManager, VisibilityManager visibilityManager) {
        this.name = name;
        this.location = location;
        this.playerDataFunction = playerDataFunction;
        this.interactManager = interactManager;
        this.visibilityManager = visibilityManager;
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

        public Hologram build() {
            return new Hologram(name, location, playerDataFunction, interactManager, visibilityManager);
        }
    }
}
