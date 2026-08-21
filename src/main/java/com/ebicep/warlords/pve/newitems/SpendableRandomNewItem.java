package com.ebicep.warlords.pve.newitems;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.inventory.ItemStack;

public enum SpendableRandomNewItem implements Spendable {

    COMMON(NewItemTier.COMMON),
    RARE(NewItemTier.RARE),
    EPIC(NewItemTier.EPIC),
    SOVEREIGN(NewItemTier.SOVEREIGN),
    LEGENDARY(NewItemTier.LEGENDARY),
    ASCENDANT(NewItemTier.ASCENDANT),

    ;

    public static final SpendableRandomNewItem[] VALUES = values();
    private final NewItemTier tier;

    SpendableRandomNewItem(NewItemTier tier) {
        this.tier = tier;
    }

    @Override
    public String getName() {
        return "Random " + tier.getName() + " Item";
    }

    @Override
    public TextColor getTextColor() {
        return tier.getTextColor();
    }

    @Override
    public ItemStack getItem() {
        return SkullUtils.getSkullFrom(SkullID.QUESTION_MARK);
    }

    @Override
    public void addToPlayer(DatabasePlayer databasePlayer, long amount) {
        for (long i = 0; i < amount; i++) {
            databasePlayer.getPveStats()
                          .getNewItemsManager()
                          .addItem(NewItemsUtils.generateRandomItem(tier));
        }
    }

    @Override
    public Long getFromPlayer(DatabasePlayer databasePlayer) {
        return 0L;
    }

    public NewItemTier getTier() {
        return tier;
    }

}
