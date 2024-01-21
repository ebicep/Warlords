package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.AddCurrencyEvent;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.DailyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class GamblerI extends AbstractBounty implements TracksOutsideGame, DailyCost, DailyRewardSpendable1 {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAddCurrency(AddCurrencyEvent event) {
        if (!databasePlayer.getPveStats().equals(event.getDatabasePlayerPvE())) {
            return;
        }
        if (event.getCurrency() != Currencies.COIN) {
            return;
        }
        if (event.getAmount() > 0) {
            return;
        }
        value -= event.getAmount();
    }

    @Override
    public String getName() {
        return "Gambler";
    }

    @Override
    public String getDescription() {
        return "Spend " + NumberFormat.addCommaAndRound(getTarget()) + " Coins.";
    }

    @Override
    public int getTarget() {
        return 100_000;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.GAMBLER_I;
    }
}
