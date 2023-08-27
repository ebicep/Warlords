package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.WeeklyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable2;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;

public class DevelopIII extends AbstractBounty implements TracksPostGame, WeeklyCost, WeeklyRewardSpendable2 {

    @Override
    public String getName() {
        return "Develop";
    }

    @Override
    public String getDescription() {
        return "Defeat " + getTarget() + " with a Legendary weapon equipped with a star piece attached in any gamemode.";
    }

    @Override
    public int getTarget() {
        return 500;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.DEVELOP_III;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer) {
        AbstractWeapon weapon = warlordsPlayer.getWeapon();
        if (weapon == null) {
            return;
        }
        if (weapon instanceof AbstractLegendaryWeapon legendaryWeapon && legendaryWeapon.getStarPieceStat() != null) {
            value += warlordsPlayer.getMinuteStats().total().getKills();
        }
    }

}
