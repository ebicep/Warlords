package com.ebicep.warlords.database.repositories.events.pojos;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.rewards.AbstractReward;
import org.bukkit.ChatColor;

import java.util.LinkedHashMap;

public class GameEventReward extends AbstractReward {

    private long event;

    public GameEventReward() {
    }

    public GameEventReward(LinkedHashMap<Currencies, Long> rewards, String from, long event) {
        super(rewards, from);
        this.event = event;
    }

    @Override
    public ChatColor getNameColor() {
        return ChatColor.RED;
    }
}
