package com.ebicep.warlords.pve.rewards;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public abstract class AbstractReward {

    @Id
    private String id;
    protected Currencies currency;
    protected Long amount;
    protected String from;
    @Field("time_claimed")
    protected Instant timeClaimed;

    public AbstractReward(Currencies currency, Long amount, String from) {
        this.currency = currency;
        this.amount = amount;
        this.from = from;
    }

    public Currencies getCurrency() {
        return currency;
    }

    public Long getAmount() {
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
