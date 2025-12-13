package com.ebicep.warlords.pve.bountysystem.bounties.boltaroevent;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.BoltaroBonanzaOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.BoltarosLairOption;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.FightersGloryReward2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.springframework.data.annotation.Transient;

public class CowardI extends AbstractBounty implements TracksDuringGame, EventCost, FightersGloryReward2 {

    @Transient
    private int secondsInSafeZone = 0;
    @Transient
    private GameRunnable trackingTask;

    @Override
    public String getName() {
        return "Coward";
    }

    @Override
    public String getDescription() {
        return "Hide in Boltaro's Cave for " + (getTarget() / 60) + " minutes.";
    }

    @Override
    public int getTarget() {
        return 10 * 60;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.COWARD_I;
    }

    @Override
    public void reset() {
        secondsInSafeZone = 0;
        if (trackingTask != null) {
            trackingTask.cancel();
            trackingTask = null;
        }
    }

    @Override
    public boolean trackGame(Game game) {
        if (!DatabaseGameEvent.eventIsActive()) {
            return false;
        }
        boolean shouldTrack = game.getOptions().stream().anyMatch(option ->
                option instanceof BoltaroBonanzaOption || option instanceof BoltarosLairOption
        );
        if (shouldTrack) {
            startTracking(game);
        }
        return shouldTrack;
    }

    private void startTracking(Game game) {
        trackingTask = new GameRunnable(game) {
            @Override
            public void run() {
                game.warlordsPlayers().forEach(warlordsPlayer -> {
                    if (warlordsPlayer.getCooldownManager().hasCooldownFromActionBarName("SAFE")) {
                        secondsInSafeZone++;
                    }
                });
            }
        };
        trackingTask.runTaskTimer(0, 20); // Every second
    }

    @Override
    public long getNewValue() {
        return secondsInSafeZone;
    }
}