package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.costs.DailyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.pve.mobs.bosses.Ghoulcaller;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.springframework.data.annotation.Transient;

public class HuntGhoulcallerI extends AbstractBounty implements TracksDuringGame, DailyCost, DailyRewardSpendable2 {

    @Transient
    private int newKills = 0;

    @Override
    public String getName() {
        return "Hunt-Ghoulcaller";
    }

    @Override
    public String getDescription() {
        return "Defeat Ghoulcaller in Normal Mode.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.HUNT_GHOULCALLER_I;
    }

    @Override
    public void reset() {
        newKills = 0;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(WarlordsDeathEvent event) {
        if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof Ghoulcaller) {
            newKills++;
        }
    }

    @Override
    public boolean trackGame(Game game) {
        return BountyUtils.waveDefenseMatchesDifficulty(game, DifficultyIndex.NORMAL);
    }

    @Override
    public long getNewValue() {
        return newKills;
    }
}
