package com.ebicep.warlords.game.flags;

import com.ebicep.warlords.player.WarlordsPlayer;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.bukkit.Location;

public class WaitingFlagLocation extends AbstractLocationBasedFlagLocation {
	
    private int despawnTimer;
    private final WarlordsPlayer scorer;

    public WaitingFlagLocation(Location location, WarlordsPlayer scorer) {
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
    public WarlordsPlayer getScorer() {
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
