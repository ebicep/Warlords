package com.ebicep.warlords.pve.newitems;

import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class EchelonTraderShop {

    private static final ZoneId ROTATION_ZONE = ZoneId.of("America/New_York");
    private static final long SHOP_SEED_SALT = 0x454348454C4F4E4CL;
    private static volatile EchelonTraderShop currentShop;

    private final Instant rotationStart;
    private final Instant nextRotation;
    private final List<NewItem> sovereignItems;
    private final List<NewItem> legendaryItems;

    private EchelonTraderShop(Instant rotationStart) {
        this.rotationStart = rotationStart;
        this.nextRotation = rotationStart.atZone(ROTATION_ZONE).plusWeeks(1).toInstant();

        Random random = new Random(rotationStart.getEpochSecond() ^ SHOP_SEED_SALT);
        this.sovereignItems = generateItems(NewItemTier.SOVEREIGN, random);
        this.legendaryItems = generateItems(NewItemTier.LEGENDARY, random);
    }

    public static EchelonTraderShop getCurrentShop() {
        Instant currentRotationStart = getCurrentRotationStart();
        EchelonTraderShop shop = currentShop;
        if (shop != null && shop.rotationStart.equals(currentRotationStart)) {
            return shop;
        }
        synchronized (EchelonTraderShop.class) {
            shop = currentShop;
            if (shop == null || !shop.rotationStart.equals(currentRotationStart)) {
                shop = new EchelonTraderShop(currentRotationStart);
                currentShop = shop;
            }
            return shop;
        }
    }

    private static List<NewItem> generateItems(NewItemTier tier, Random random) {
        Set<NewItemsSetBonus> setBonuses = NewItemsSetBonus.BY_TIER.get(tier);
        if (setBonuses == null || setBonuses.isEmpty()) {
            throw new IllegalStateException("No set bonuses found for tier: " + tier);
        }
        List<NewItemsSetBonus> orderedSetBonuses = setBonuses.stream()
                .sorted(Comparator.comparingInt(NewItemsSetBonus::ordinal))
                .toList();
        List<NewItem> items = new ArrayList<>(3);
        while (items.size() < 3) {
            NewItem item = new NewItem(orderedSetBonuses.get(random.nextInt(orderedSetBonuses.size())), random);
            if (items.stream().noneMatch(existing ->
                    existing.getSetBonus() == item.getSetBonus() && existing.getSlot() == item.getSlot())) {
                items.add(item);
            }
        }
        return List.copyOf(items);
    }

    private static Instant getCurrentRotationStart() {
        ZonedDateTime now = ZonedDateTime.now(ROTATION_ZONE);
        ZonedDateTime rotationStart = now
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.FRIDAY))
                .withHour(18)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        if (now.isBefore(rotationStart)) {
            rotationStart = rotationStart.minusWeeks(1);
        }
        return rotationStart.toInstant();
    }

    public Instant getRotationStart() {
        return rotationStart;
    }

    public Instant getNextRotation() {
        return nextRotation;
    }

    public List<NewItem> getSovereignItems() {
        return sovereignItems;
    }

    public List<NewItem> getLegendaryItems() {
        return legendaryItems;
    }
}
