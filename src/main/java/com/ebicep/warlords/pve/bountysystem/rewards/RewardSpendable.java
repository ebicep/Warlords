package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.pve.Spendable;

import java.util.Map;

public interface RewardSpendable {

    Map<Spendable, Long> getCurrencyReward();

    //TODO items

}
