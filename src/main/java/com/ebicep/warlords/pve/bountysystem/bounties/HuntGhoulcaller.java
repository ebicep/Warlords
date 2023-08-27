package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.pve.mobs.bosses.Ghoulcaller;
import org.springframework.data.annotation.Transient;

import java.util.UUID;

public class HuntGhoulcaller extends AbstractBounty implements TracksDuringGame, DailyRewardSpendable2 {

    @Transient
    private int newKills = 0;

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public String getName() {
        return "Hunt-Ghoulcaller";
    }

    @Override
    public String getDescription() {
        return "Defeat Ghoulcaller in Normal Mode.";
    }

    @Override
    public Bounty getBounty() {
        return Bounty.HUNT_GHOULCALLER;
    }

    @Override
    public void onKill(UUID uuid, WarlordsDeathEvent event) {
        if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof Ghoulcaller) {
            newKills++;
        }
    }

    @Override
    public boolean trackGame(Game game) {
        return game.getOptions().stream().anyMatch(option -> option instanceof WaveDefenseOption waveDefenseOption && waveDefenseOption.getDifficulty() == DifficultyIndex.NORMAL);
    }

    @Override
    public int getNewValue() {
        return newKills;
    }

    @Override
    public void reset() {
        newKills = 0;
    }
}
