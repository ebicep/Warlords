package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public enum SpendableRandomItem implements Spendable {

    ALPHA("Random Alpha Item", ItemTier.ALPHA.textColor, itemType -> itemType.create(ItemTier.ALPHA)),
    BETA("Random Beta Item", ItemTier.BETA.textColor, itemType -> itemType.create(ItemTier.BETA)),
    GAMMA("Random Gamma Item", ItemTier.GAMMA.textColor, itemType -> itemType.create(ItemTier.GAMMA)),
    DELTA("Random Delta Item", ItemTier.DELTA.textColor, itemType -> itemType.create(ItemTier.DELTA)),

    ;

    public static final SpendableRandomItem[] VALUES = values();
    public final String name;
    public final TextColor textColor;
    public final Function<ItemType, AbstractItem> createItem;

    SpendableRandomItem(String name, TextColor textColor, Function<ItemType, AbstractItem> createItem) {
        this.name = name;
        this.textColor = textColor;
        this.createItem = createItem;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TextColor getTextColor() {
        return textColor;
    }

    @Override
    public ItemStack getItem() {
        return SkullUtils.getSkullFrom(SkullID.QUESTION_MARK);
    }

    @Override
    public void addToPlayer(DatabasePlayer databasePlayer, long amount) {
        for (long i = 0; i < amount; i++) {
            databasePlayer.getPveStats().getItemsManager().addItem(createItem.apply(ItemType.getRandom()));
        }
    }

    @Override
    public Long getFromPlayer(DatabasePlayer databasePlayer) {
        return 0L;
    }
}
