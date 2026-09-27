package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class AnomalyRewardPool {

    private final String name;
    private final long coins;
    private final int cacheIndex;
    private final long ethereumCrystals;
    private final double newItemChance;

    public AnomalyRewardPool(String name, long coins, int cacheIndex, long ethereumCrystals, double newItemChance) {
        this.name = name;
        this.coins = coins;
        this.cacheIndex = cacheIndex;
        this.ethereumCrystals = ethereumCrystals;
        this.newItemChance = newItemChance;
    }

    public AnomalyRewardCache createCache(NewItemsSetBonus featuredLegendarySet, long rotationStart) {
        LinkedHashMap<Spendable, Long> cacheCurrencies = new LinkedHashMap<>();
        currencyRewards(rotationStart).forEach(cacheCurrencies::put);
        return new AnomalyRewardCache(cacheCurrencies, name, rotationStart, rollNewItem(featuredLegendarySet));
    }

    private Map<Currencies, Long> currencyRewards(long rotationStartEpochSecond) {
        Currencies secondaryCurrency = AnomalyRotation.getSecondaryRewardCurrency(rotationStartEpochSecond);
        LinkedHashMap<Currencies, Long> rewards = new LinkedHashMap<>();
        rewards.put(Currencies.COIN, coins);
        rewards.put(secondaryCurrency, AnomalyRotation.getSecondaryRewardAmount(secondaryCurrency, cacheIndex));
        rewards.put(Currencies.ETHEREUM_CRYSTAL, ethereumCrystals);
        return Collections.unmodifiableMap(rewards);
    }

    @Nullable
    private NewItem rollNewItem(NewItemsSetBonus featuredLegendarySet) {
        if (ThreadLocalRandom.current().nextDouble() >= newItemChance) {
            return null;
        }
        NewItemTier tier = rollItemTier();
        if (tier == NewItemTier.LEGENDARY) {
            return new NewItem(featuredLegendarySet);
        }
        return NewItemsUtils.generateRandomItem(tier);
    }

    private NewItemTier rollItemTier() {
        double roll = ThreadLocalRandom.current().nextDouble(100);
        if (roll < 50) {
            return NewItemTier.COMMON;
        }
        if (roll < 80) {
            return NewItemTier.RARE;
        }
        if (roll < 95) {
            return NewItemTier.EPIC;
        }
        if (roll < 97.5) {
            return NewItemTier.SOVEREIGN;
        }
        return NewItemTier.LEGENDARY;
    }

    public List<Component> getLore() {
        List<Component> lore = new ArrayList<>(PvEUtils.getCostLore(getCurrencies(), "Guaranteed", false));
        lore.add(Component.empty());
        lore.add(Component.text("Item chance: " + Math.round(newItemChance * 100) + "%", NamedTextColor.AQUA));
        lore.add(Component.text(" - Common: 50%", NewItemTier.COMMON.getTextColor()));
        lore.add(Component.text(" - Rare: 30%", NewItemTier.RARE.getTextColor()));
        lore.add(Component.text(" - Epic: 15%", NewItemTier.EPIC.getTextColor()));
        lore.add(Component.text(" - Sovereign: 2.5%", NewItemTier.SOVEREIGN.getTextColor()));
        lore.add(Component.text(" - Legendary: 2.5%", NewItemTier.LEGENDARY.getTextColor()));
        return lore;
    }

    public String getName() {
        return name;
    }

    public Map<Currencies, Long> getCurrencies() {
        return currencyRewards(AnomalyRotation.getRotationStart().getEpochSecond());
    }
}
