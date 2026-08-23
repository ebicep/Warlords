package com.ebicep.warlords.database.repositories.events.pojos;

import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.rewards.AbstractReward;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.time.Instant;
import java.util.LinkedHashMap;

public class GameEventReward extends AbstractReward {

    private long event;

    public GameEventReward() {
    }

    public GameEventReward(LinkedHashMap<Spendable, Long> rewards, String from, long event) {
        super(rewards, from);
        this.event = event;
    }

    @Override
    public TextColor getNameColor() {
        return NamedTextColor.RED;
    }

    @Override
    public Instant getTimeGiven() {
        Instant given = super.getTimeGiven();
        return given != null ? given : Instant.ofEpochSecond(event);
    }

    public long getEvent() {
        return event;
    }

}
