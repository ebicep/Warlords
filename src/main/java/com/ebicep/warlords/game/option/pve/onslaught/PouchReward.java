package com.ebicep.warlords.game.option.pve.onslaught;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseRewards;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemsManager;
import com.ebicep.warlords.pve.newitems.SpendableRandomNewItem;
import com.ebicep.warlords.pve.rewards.AbstractReward;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.RandomCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PouchReward extends AbstractReward {

    private static final RandomCollection<SpendableRandomNewItem> ASCENDANT_ITEM_POOL = new RandomCollection<SpendableRandomNewItem>()
            .add(80, SpendableRandomNewItem.EPIC)
            .add(10, SpendableRandomNewItem.SOVEREIGN)
            .add(10, SpendableRandomNewItem.LEGENDARY);

    private PouchType pouchType;
    private NewItem newItem;

    public PouchReward() {
    }

    public PouchReward(LinkedHashMap<Spendable, Long> rewards, PouchType pouchType) {
        super(rewards, pouchType.name);
        this.pouchType = pouchType;
    }

    @Nullable
    public static SpendableRandomNewItem addRolledAscendantItem(Map<Spendable, Long> rewards) {
        SpendableRandomNewItem item = ASCENDANT_ITEM_POOL.next();
        if (item != null) {
            rewards.merge(item, 1L, Long::sum);
        }
        return item;
    }

    @Override
    public void giveToPlayer(DatabasePlayer databasePlayer) {
        super.giveToPlayer(databasePlayer);
        if (newItem != null) {
            databasePlayer.getPveStats().getNewItemsManager().addItem(newItem);
        }
    }

    @Override
    public void unGiveToPlayer(DatabasePlayer databasePlayer) {
        super.unGiveToPlayer(databasePlayer);
        if (newItem == null) {
            return;
        }
        NewItemsManager newItemsManager = databasePlayer.getPveStats().getNewItemsManager();
        newItemsManager.getItemInventory()
                .stream()
                .filter(item -> item.getUUID().equals(newItem.getUUID()))
                .findFirst()
                .ifPresent(newItemsManager::removeItem);
    }

    @Override
    public List<Component> getLore() {
        List<Component> lore = new ArrayList<>(super.getLore());
        if (newItem != null) {
            lore.add(Component.empty());
            lore.add(Component.text("Item: ", NamedTextColor.GRAY).append(newItem.getHoverComponent()));
        }
        return lore;
    }

    @Override
    public TextColor getNameColor() {
        return NamedTextColor.AQUA;
    }

    public enum PouchType {
        SYNTHETIC("Synthetic Pouch"),
        ASPIRANT("Aspirant Pouch"),
        ASCENDANT("Ascendant Pouch"),

        ;

        public final String name;

        PouchType(String name) {
            this.name = name;
        }

        public RandomCollection<Pair<Spendable, Long>> getLootPool() {
            return switch (this) {
                case SYNTHETIC -> OnslaughtRewards.SYNTHETIC_POUCH_LOOT_POOL;
                case ASPIRANT -> OnslaughtRewards.ASPIRANT_POUCH_LOOT_POOL;
                case ASCENDANT -> WaveDefenseRewards.ASCENDANT_POUCH_LOOT_POOL;
            };
        }
    }

}
