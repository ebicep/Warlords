package com.ebicep.warlords.pve.rewards;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public abstract class AbstractReward {

    protected RewardTypes reward;
    protected float amount;
    protected String from;
    @Field("time_claimed")
    protected Instant timeClaimed;

    public AbstractReward(RewardTypes reward, float amount, String from) {
        this.reward = reward;
        this.amount = amount;
        this.from = from;
    }

    public RewardTypes getReward() {
        return reward;
    }

    public float getAmount() {
        return amount;
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
