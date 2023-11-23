package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.WeeklyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import org.springframework.data.annotation.Transient;

import java.util.UUID;

public class HuntChampionI extends AbstractBounty implements TracksDuringGame, WeeklyCost, WeeklyRewardSpendable1 {

    @Transient
    private int newKills = 0;

    @Override
    public String getName() {
        return "Hunt-Champion";
    }

    @Override
    public String getDescription() {
        return "Kill 500 Champion enemies in any gamemode.";
    }

    @Override
    public int getTarget() {
        return 500;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.HUNT_CHAMPION_I;
    }

    @Override
    public void reset() {
        newKills = 0;
    }

    @Override
    public void onFinalDamageHeal(UUID uuid, WarlordsDamageHealingFinalEvent event) {
        if (!event.getAttacker().getUuid().equals(uuid)) {
            return;
        }
        if (!event.isDead()) {
            return;
        }
        if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof ChampionMob) {
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
