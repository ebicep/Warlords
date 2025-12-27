package com.ebicep.warlords.pve.newitems;

import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.JavaUtils;
import com.ebicep.warlords.util.java.RandomCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import javax.annotation.Nonnull;
import java.util.Set;

public class NewItemsUtils {

    @Nonnull
    public static NewItem generateRandomItem() {
        RandomCollection<NewItemTier> weightedTiers = new RandomCollection<>();
        for (NewItemTier value : NewItemTier.VALUES) {
            weightedTiers.add(value.getWeight(), value);
        }
        NewItemTier tier = weightedTiers.next();
        return generateRandomItem(tier);
    }

    @Nonnull
    public static NewItem generateRandomItem(NewItemTier tier) {
        Set<NewItemsSetBonus> setBonuses = NewItemsSetBonus.BY_TIER.get(tier);
        if (setBonuses == null || setBonuses.isEmpty()) {
            throw new IllegalStateException("No set bonuses found for tier: " + tier);
        }
        NewItemsSetBonus setBonus = JavaUtils.randomFromSet(setBonuses);
        return new NewItem(setBonus);
    }

    public static void reloadConfig() {
        for (NewItemsSetBonus value : NewItemsSetBonus.VALUES) {
            value.init();
        }
        for (NewItemTier value : NewItemTier.VALUES) {
            value.init();
        }
    }

    public static Component createStarComponent(TextColor textColor, int starCount) {
        if (starCount <= 0 || starCount > 6) {
            throw new IllegalArgumentException("starCount must be between 0 and 6");
        }
        ComponentBuilder builder = ComponentBuilder.create().text("[", NamedTextColor.GRAY);
        if (starCount == 6) {
            builder.text("❂".repeat(6), textColor);
        } else {
            builder.text("❂".repeat(starCount), textColor)
                   .text("❂".repeat(6 - starCount), NamedTextColor.DARK_GRAY);
        }
        builder.text("]", NamedTextColor.GRAY);
        return builder.build();
    }

}
