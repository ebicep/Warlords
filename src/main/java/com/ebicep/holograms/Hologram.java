package com.ebicep.holograms;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.function.Function;

public class Hologram {

    private static int entityId = Integer.MAX_VALUE / 8;
    private int id = entityId++;
    private String name;
    private Location location;
    //    private Map<UUID, Data> playerData;
    private Function<Player, HologramData> playerDataFunction;
    private VisibilityManager visibilityManager = new VisibilityManager(VisibilityType.MANUAL);

    public Hologram(String name, Location location, Function<Player, HologramData> playerDataFunction) {
        this.name = name;
        this.location = location;
        this.playerDataFunction = playerDataFunction;
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
//        Data data = playerData.get(player.getUniqueId());
        HologramData data = getDataForPlayer(player);
        return playerLocation.distanceSquared(location) < data.getViewRange() * data.getViewRange();
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

//    public Map<UUID, Data> getPlayerData() {
//        return playerData;
//    }

    public VisibilityManager getVisibilityManager() {
        return visibilityManager;
    }
}
