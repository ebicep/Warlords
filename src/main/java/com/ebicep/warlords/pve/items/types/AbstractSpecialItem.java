package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.addons.ItemAddonClassBonus;
import com.ebicep.warlords.pve.items.modifiers.ItemModifier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;

import java.util.Set;

public abstract class AbstractSpecialItem extends AbstractItem implements BonusStats, BonusLore {

    public AbstractSpecialItem() {
    }

    public AbstractSpecialItem(ItemType type, ItemTier tier) {
        super(type, tier);
    }

    public AbstractSpecialItem(ItemType type, ItemTier tier, Set<BasicStatPool> statPool) {
        super(type, tier, statPool);
    }

    @Override
    public ItemBuilder generateItemBuilder() {
        ItemBuilder itemBuilder = getBaseItemBuilder();
        addStatPoolAndBlessing(itemBuilder);
        itemBuilder.addLore(
                "",
                getBonusLore()
        );
        addItemScoreAndWeight(itemBuilder);
        itemBuilder.addLore(Component.empty());
        itemBuilder.addLoreC(
                WordWrap.wrap(Component.text(getDescription(), NamedTextColor.DARK_AQUA, TextDecoration.ITALIC), 160)
        );
        return itemBuilder;
    }

    @Override
    public Component getItemName() {
        ItemModifier itemModifier = getItemModifier();
        if (itemModifier != null) {
            return Component.text(getName(), modifier > 0 ? NamedTextColor.GREEN : NamedTextColor.RED);
        }
        return Component.text(getName(), NamedTextColor.GRAY);
    }

    public abstract String getName();

    public abstract String getBonus();

    public abstract String getDescription();

    @Override
    public String getBonusLore() {
        return ChatColor.GREEN + "Bonus" + (this instanceof ItemAddonClassBonus ? " (" + ((ItemAddonClassBonus) this).getClasses().name + "):" : ":") + "\n" +
                WordWrap.wrapWithNewline(ChatColor.GRAY + getBonus(), 160);
    }
}
