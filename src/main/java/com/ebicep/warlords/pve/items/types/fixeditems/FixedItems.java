package com.ebicep.warlords.pve.items.types.fixeditems;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.ItemsManager;
import com.ebicep.warlords.pve.items.types.AbstractFixedItem;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public enum FixedItems implements Spendable {

    SHAWL_OF_MITHRA(ShawlOfMithra::new),
    SPIDER_GAUNTLET(SpiderGauntlet::new),

    ;

    public final Supplier<AbstractFixedItem> create;

    FixedItems(Supplier<AbstractFixedItem> create) {
        this.create = create;
    }

    @Override
    public String getName() {
        return create.get().getName();
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.GRAY;
    }

    @Override
    public ItemStack getItem() {
        return create.get().generateItemStack();
    }

    @Override
    public void addToPlayer(DatabasePlayer databasePlayer, long amount) {
        ItemsManager itemsManager = databasePlayer.getPveStats().getItemsManager();
        for (long i = 0; i < amount; i++) {
            itemsManager.addItem(create.get());
        }
    }

    @Override
    public Long getFromPlayer(DatabasePlayer databasePlayer) {
        AbstractFixedItem abstractFixedItem = create.get();
        return databasePlayer.getPveStats()
                             .getItemsManager()
                             .getItemInventory()
                             .stream()
                             .filter(item -> item.getClass().equals(abstractFixedItem.getClass()))
                             .count();
    }
}
