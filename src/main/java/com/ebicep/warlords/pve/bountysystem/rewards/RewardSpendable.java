package com.ebicep.warlords.pve.bountysystem.rewards;

import com.ebicep.warlords.pve.Spendable;

import java.util.LinkedHashMap;

public interface RewardSpendable {

    LinkedHashMap<Spendable, Long> getCurrencyReward();

    //TODO items

}
