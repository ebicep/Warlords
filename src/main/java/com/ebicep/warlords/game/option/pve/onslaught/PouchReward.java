package com.ebicep.warlords.game.option.pve.onslaught;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemsManager;
import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.pve.rewards.AbstractReward;
import com.ebicep.warlords.util.java.RandomCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PouchReward extends AbstractReward {

    private static final RandomCollection<NewItemTier> ASCENDANT_ITEM_TIER_POOL = new RandomCollection<NewItemTier>()
            .add(30, NewItemTier.COMMON)
            .add(25, NewItemTier.RARE)
            .add(25, NewItemTier.EPIC)
            .add(10, NewItemTier.SOVEREIGN)
            .add(10, NewItemTier.LEGENDARY);

    private PouchType pouchType;
    private NewItem newItem;

    public PouchReward() {
    }

    public PouchReward(LinkedHashMap<Spendable, Long> rewards, PouchType pouchType) {
        super(rewards, pouchType.name);
        this.pouchType = pouchType;
        if (pouchType == PouchType.ASCENDANT) {
            this.newItem = NewItemsUtils.generateRandomItem(ASCENDANT_ITEM_TIER_POOL.next());
        }
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
    }

}
