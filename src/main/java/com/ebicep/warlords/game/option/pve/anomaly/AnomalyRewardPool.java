package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemsUtils;
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

    public static final double NEW_ITEM_CHANCE = 0.20;

    private final String name;
    private final Map<Currencies, Long> currencies;

    public AnomalyRewardPool(String name, long coins, long syntheticShards, long ethereumCrystals) {
        this.name = name;
        LinkedHashMap<Currencies, Long> rewards = new LinkedHashMap<>();
        rewards.put(Currencies.COIN, coins);
        rewards.put(Currencies.SYNTHETIC_SHARD, syntheticShards);
        rewards.put(Currencies.ETHEREUM_CRYSTAL, ethereumCrystals);
        this.currencies = Collections.unmodifiableMap(rewards);
    }

    @Nullable
    public NewItem grant(DatabasePlayer databasePlayer) {
        currencies.forEach((currency, amount) -> currency.addToPlayer(databasePlayer, amount));
        if (ThreadLocalRandom.current().nextDouble() >= NEW_ITEM_CHANCE) {
            return null;
        }
        NewItem item = NewItemsUtils.generateRandomItem(rollItemTier());
        databasePlayer.getPveStats().getNewItemsManager().addItem(item);
        return item;
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
        List<Component> lore = new ArrayList<>(PvEUtils.getCostLore(currencies, "Guaranteed", false));
        lore.add(Component.empty());
        lore.add(Component.text("NewItem chance: 20%", NamedTextColor.AQUA));
        lore.add(Component.text(" - Common: 50%", NamedTextColor.GREEN));
        lore.add(Component.text(" - Rare: 30%", NamedTextColor.BLUE));
        lore.add(Component.text(" - Epic: 15%", NamedTextColor.DARK_PURPLE));
        lore.add(Component.text(" - Sovereign: 2.5%", NewItemTier.SOVEREIGN.getTextColor()));
        lore.add(Component.text(" - Legendary: 2.5%", NewItemTier.LEGENDARY.getTextColor()));
        return lore;
    }

    public String getName() {
        return name;
    }

    public Map<Currencies, Long> getCurrencies() {
        return currencies;
    }
}