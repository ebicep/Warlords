package com.ebicep.warlords.game.flags;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class WaitingFlagLocation extends AbstractLocationBasedFlagLocation {

    private int despawnTimer;
    private final WarlordsEntity scorer;

    public WaitingFlagLocation(Location location, WarlordsEntity scorer) {
        super(location);
        this.despawnTimer = 15 * 20;
        this.scorer = scorer;
    }

    public int getDespawnTimer() {
        return despawnTimer;
    }

    @Deprecated
    public boolean wasWinner() {
        return scorer != null;
    }

    @Nullable
    public WarlordsEntity getScorer() {
        return scorer;
    }

    @Override
    public FlagLocation update(FlagInfo info) {
        this.despawnTimer--;
        return this.despawnTimer <= 0 ? new SpawnFlagLocation(info.getSpawnLocation(), null) : null;
    }

    @Override
    public List<String> getDebugInformation() {
        return Arrays.asList("Type: " + this.getClass().getSimpleName(),
                "scorer: " + getScorer(),
                "despawnTimer: " + getDespawnTimer()
        );
    }
	
}
