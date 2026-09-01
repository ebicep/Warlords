package com.ebicep.warlords.pve.consumables;

import com.ebicep.warlords.util.java.DateUtil;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConsumableManager {

    @Field("inventory")
    private Map<String, Integer> inventory = new HashMap<>();
    @Field("active")
    private Map<String, ActiveConsumable> active = new HashMap<>();
    @Field("weekly_purchases")
    private Map<String, Long> weeklyPurchases = new HashMap<>();

    public int getAmount(Consumable consumable) {
        return getInventory().getOrDefault(consumable.getId(), 0);
    }

    public void add(Consumable consumable, int amount) {
        if (amount <= 0) {
            return;
        }
        getInventory().merge(consumable.getId(), amount, Integer::sum);
    }

    public boolean remove(Consumable consumable, int amount) {
        if (amount <= 0 || getAmount(consumable) < amount) {
            return false;
        }
        int remaining = getAmount(consumable) - amount;
        if (remaining == 0) {
            getInventory().remove(consumable.getId());
        } else {
            getInventory().put(consumable.getId(), remaining);
        }
        return true;
    }

    public void activate(Consumable consumable) {
        if (!consumable.isTimed()) {
            return;
        }
        cleanupExpired();
        getActive().put(consumable.getActiveGroup(), new ActiveConsumable(
                consumable.getId(),
                Instant.now().plus(consumable.getDuration())
        ));
    }

    public ActiveConsumable getActiveConsumable(String group) {
        cleanupExpired();
        return getActive().get(group);
    }

    public Consumable getActiveDefinition(String group) {
        ActiveConsumable activeConsumable = getActiveConsumable(group);
        return activeConsumable == null ? null : ConsumableRegistry.get(activeConsumable.getConsumableId());
    }

    public void cleanupExpired() {
        Iterator<Map.Entry<String, ActiveConsumable>> iterator = getActive().entrySet().iterator();
        while (iterator.hasNext()) {
            ActiveConsumable value = iterator.next().getValue();
            if (value == null || value.isExpired()) {
                iterator.remove();
            }
        }
    }

    public boolean hasPurchasedThisWeek(Consumable consumable) {
        return getWeeklyPurchases().getOrDefault(consumable.getId(), Long.MIN_VALUE) == DateUtil.getCurrentWeekStartEpochDay();
    }

    public void markPurchasedThisWeek(Consumable consumable) {
        getWeeklyPurchases().put(consumable.getId(), DateUtil.getCurrentWeekStartEpochDay());
    }

    public Map<String, Integer> getInventory() {
        if (inventory == null) {
            inventory = new HashMap<>();
        }
        return inventory;
    }

    public Map<String, ActiveConsumable> getActive() {
        if (active == null) {
            active = new HashMap<>();
        }
        return active;
    }

    public Map<String, Long> getWeeklyPurchases() {
        if (weeklyPurchases == null) {
            weeklyPurchases = new HashMap<>();
        }
        return weeklyPurchases;
    }
}
