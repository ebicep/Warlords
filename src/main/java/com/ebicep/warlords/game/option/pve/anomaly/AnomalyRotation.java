package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public final class AnomalyRotation {

    private static final long ROTATION_SEED_SALT = 0x414E4F4D414C594CL;

    private AnomalyRotation() {
    }

    public static Instant getRotationStart() {
        return Instant.now().truncatedTo(ChronoUnit.HOURS);
    }

    public static Instant getNextRotation() {
        return getRotationStart().plus(1, ChronoUnit.HOURS);
    }

    public static Anomalies getCurrentAnomaly() {
        long hour = getRotationStart().getEpochSecond() / 3600;
        return Anomalies.ROTATING[(int) Math.floorMod(hour, Anomalies.ROTATING.length)];
    }

    public static NewItemsSetBonus getGuaranteedLegendarySet() {
        List<NewItemsSetBonus> legendarySets = NewItemsSetBonus.BY_TIER.get(NewItemTier.LEGENDARY)
                .stream()
                .sorted(Comparator.comparingInt(NewItemsSetBonus::ordinal))
                .toList();
        if (legendarySets.isEmpty()) {
            throw new IllegalStateException("No Legendary NewItem sets are configured");
        }
        Random random = new Random(getRotationStart().getEpochSecond() ^ ROTATION_SEED_SALT);
        return legendarySets.get(random.nextInt(legendarySets.size()));
    }
}