package com.ebicep.warlords.supporter;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SupporterEntitlementManager {

    @Field("subscriptions")
    private Set<String> subscriptions = new HashSet<>();
    @Field("temporary_entitlements")
    private Map<String, Instant> temporaryEntitlements = new HashMap<>();

    public boolean isActive() {
        cleanupExpired();
        return !getSubscriptions().isEmpty() || !getTemporaryEntitlements().isEmpty();
    }

    public boolean hasSubscription() {
        return !getSubscriptions().isEmpty();
    }

    public boolean grantSubscription(String source) {
        return getSubscriptions().add(normalizeSource(source));
    }

    public boolean revokeSubscription(String source) {
        return getSubscriptions().remove(normalizeSource(source));
    }

    public Instant grantTemporary(String source, int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Supporter duration must be greater than zero days");
        }
        String normalizedSource = normalizeSource(source);
        Instant existing = getTemporaryEntitlements().get(normalizedSource);
        if (existing != null) {
            return existing;
        }
        cleanupExpired();
        Instant now = Instant.now();
        Instant base = getLatestTemporaryExpiry();
        if (base == null || base.isBefore(now)) {
            base = now;
        }
        Instant expiry = base.plus(days, ChronoUnit.DAYS);
        getTemporaryEntitlements().put(normalizedSource, expiry);
        return expiry;
    }

    public boolean revokeTemporary(String source) {
        return getTemporaryEntitlements().remove(normalizeSource(source)) != null;
    }

    public Instant getLatestTemporaryExpiry() {
        cleanupExpired();
        return getTemporaryEntitlements().values()
                .stream()
                .max(Instant::compareTo)
                .orElse(null);
    }

    public Set<String> getSubscriptions() {
        if (subscriptions == null) {
            subscriptions = new HashSet<>();
        }
        return subscriptions;
    }

    public Map<String, Instant> getTemporaryEntitlements() {
        if (temporaryEntitlements == null) {
            temporaryEntitlements = new HashMap<>();
        }
        return temporaryEntitlements;
    }

    public boolean cleanupExpired() {
        Instant now = Instant.now();
        return getTemporaryEntitlements().entrySet().removeIf(entry -> entry.getValue() == null || !entry.getValue().isAfter(now));
    }

    private String normalizeSource(String source) {
        if (source == null || source.isBlank()) {
            return "unknown";
        }
        return source.trim();
    }
}
