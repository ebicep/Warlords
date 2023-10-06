package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.WeeklyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import org.springframework.data.annotation.Transient;

import java.util.UUID;

public class HuntIntermediateI extends AbstractBounty implements TracksDuringGame, WeeklyCost, WeeklyRewardSpendable1 {

    @Transient
    private int newKills = 0;

    @Override
    public String getName() {
        return "Hunt-Intermediate";
    }

    @Override
    public String getDescription() {
        return "Kill 500 Intermediate enemies in any gamemode.";
    }

    @Override
    public int getTarget() {
        return 500;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.HUNT_INTERMEDIATE_I;
    }

    @Override
    public void reset() {
        newKills = 0;
    }

    @Override
    public void onKill(UUID uuid, WarlordsDeathEvent event) {
        if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof IntermediateMob) {
            newKills++;
        }
    }

    @Override
    public boolean trackGame(Game game) {
        return true;
    }

    @Override
    public long getNewValue() {
        return newKills;
    }
}
