package com.ebicep.warlords.pve.rewards;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractReward {

    protected Map<Currencies, Long> rewards = new LinkedHashMap<>();
    protected String from;
    @Field("time_claimed")
    protected Instant timeClaimed;

    public AbstractReward() {
    }

    public AbstractReward(LinkedHashMap<Currencies, Long> rewards, String from) {
        this.rewards = rewards;
        this.from = from;
    }

    public Map<Currencies, Long> getRewards() {
        return rewards;
    }

    public String getFrom() {
        return from;
    }

    public Instant getTimeClaimed() {
        return timeClaimed;
    }

    public void setTimeClaimed() {
        this.timeClaimed = Instant.now();
    }

}
