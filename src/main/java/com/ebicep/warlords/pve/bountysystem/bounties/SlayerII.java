package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.DailyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import org.springframework.data.annotation.Transient;

import java.util.UUID;

public class SlayerII extends AbstractBounty implements TracksDuringGame, DailyCost, DailyRewardSpendable1 {

    @Transient
    private int newKills = 0;

    @Override
    public String getName() {
        return "Slayer";
    }

    @Override
    public String getDescription() {
        return "Defeat " + getTarget() + " bosses in any gamemode.";
    }

    @Override
    public int getTarget() {
        return 20;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.SLAYER_II;
    }

    @Override
    public void reset() {
        newKills = 0;
    }

    @Override
    public void onKill(UUID uuid, WarlordsDeathEvent event) {
        if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof BossMob) {
            newKills++;
        }
    }

    @Override
    public long getNewValue() {
        return newKills;
    }
}
