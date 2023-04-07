package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.addons.ItemAddonClassBonus;
import com.ebicep.warlords.pve.items.modifiers.ItemModifier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import org.bukkit.ChatColor;

import java.util.Set;

public abstract class SpecialItem extends AbstractItem implements BonusStats {

    public SpecialItem() {
    }

    public SpecialItem(ItemType type, ItemTier tier) {
        super(type, tier);
    }

    public SpecialItem(ItemType type, ItemTier tier, Set<BasicStatPool> statPool) {
        super(type, tier, statPool);
    }

    @Override
    public ItemBuilder generateItemBuilder() {
        ItemBuilder itemBuilder = getBaseItemBuilder();
        addStatPoolAndBlessing(itemBuilder);
        itemBuilder.addLore(
                "",
                ChatColor.GREEN + "Bonus" + (this instanceof ItemAddonClassBonus ? " (" + ((ItemAddonClassBonus) this).getClasses().name + "):" : ""),
                WordWrap.wrapWithNewline(ChatColor.GRAY + getBonus(), 160)
        );
        addItemScoreAndWeight(itemBuilder);
        itemBuilder.addLore(
                "",
                WordWrap.wrapWithNewline(ChatColor.DARK_GRAY.toString() + ChatColor.ITALIC + getDescription(), 160)
        );
        return itemBuilder;
    }

    @Override
    public String getItemName() {
        ItemModifier itemModifier = getItemModifier();
        if (itemModifier != null) {
            return (modifier > 0 ? ChatColor.GREEN : ChatColor.RED) + getName();
        }
        return ChatColor.GRAY + getName();
    }

    public abstract String getName();

    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
    }

    public abstract String getBonus();

    public abstract String getDescription();

    public Class<?> craftsInto() {
        return null;
    }

}
