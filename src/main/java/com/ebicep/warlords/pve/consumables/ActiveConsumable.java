package com.ebicep.warlords.pve.consumables;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class ActiveConsumable {

    @Field("consumable_id")
    private String consumableId;
    @Field("expires_at")
    private Instant expiresAt;

    public ActiveConsumable() {
    }

    public ActiveConsumable(String consumableId, Instant expiresAt) {
        this.consumableId = consumableId;
        this.expiresAt = expiresAt;
    }

    public String getConsumableId() {
        return consumableId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return expiresAt == null || !expiresAt.isAfter(Instant.now());
    }
}
