package com.ebicep.holograms;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class InteractManager {

    private final List<Integer> ids = new ArrayList<>();
    private final Consumer<Player> onClick;
    private final Function<Player, InteractData> playerDataFunction;

    public InteractManager(Consumer<Player> onClick, Function<Player, InteractData> playerDataFunction) {
        this.onClick = onClick;
        this.playerDataFunction = playerDataFunction;
    }

    public void recomputeIDs() {
        ids.clear();
        ids.add(HologramManager.entityId++); // TODO
    }

    public List<Integer> getIds() {
        return ids;
    }

    public Consumer<Player> getOnClick() {
        return onClick;
    }

    public InteractData getDataForPlayer(Player player) {
        return playerDataFunction.apply(player);
    }

}
