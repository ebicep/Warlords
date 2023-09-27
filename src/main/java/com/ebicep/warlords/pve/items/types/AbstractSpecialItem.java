package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.addons.ItemAddonClassBonus;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractSpecialItem extends AbstractItem implements BonusStats, BonusLore {

    public AbstractSpecialItem() {
        super();
    }

    public AbstractSpecialItem(ItemType type, ItemTier tier) {
        super(type, tier);
    }

    public AbstractSpecialItem(ItemType type, ItemTier tier, Set<BasicStatPool> statPool) {
        super(type, tier, statPool);
    }

    @Override
    public AbstractItem clone() {
        return null; // TODO if needed
    }

    @Override
    public ItemBuilder generateItemBuilder() {
        ItemBuilder itemBuilder = getBaseItemBuilder();
        addStatPoolAndModifier(itemBuilder, null);
        itemBuilder.addLore(Component.empty());
        itemBuilder.addLore(getBonusLore());
        addItemScore(itemBuilder, false);
        itemBuilder.addLore(Component.empty());
        itemBuilder.addLore(WordWrap.wrap(Component.text(getDescription(), NamedTextColor.DARK_GRAY, TextDecoration.ITALIC), 160));
        return itemBuilder;
    }

    public ItemStack generateItemStackWithObfuscatedStat(BasicStatPool stat) {
        return generateItemBuilderWithObfuscatedStat(stat).get();
    }

    public ItemBuilder generateItemBuilderWithObfuscatedStat(BasicStatPool stat) {
        ItemBuilder itemBuilder = getBaseItemBuilder();
        addStatPoolAndModifier(itemBuilder, stat);
        itemBuilder.addLore(Component.empty());
        itemBuilder.addLore(getBonusLore());
        addItemScore(itemBuilder, true);
        itemBuilder.addLore(Component.empty());
        itemBuilder.addLore(WordWrap.wrap(Component.text(getDescription(), NamedTextColor.DARK_GRAY, TextDecoration.ITALIC), 160));
        return itemBuilder;
    }

    @Override
    public Component getItemName() {
        return Component.text(getName(), getModifierColor());
    }

    public abstract String getName();

    public abstract String getBonus();

    public abstract String getDescription();

    @Override
    public List<Component> getBonusLore() {
        List<Component> bonusLore = new ArrayList<>();
        bonusLore.add(Component.text("Bonus" + (this instanceof ItemAddonClassBonus ? " (" + ((ItemAddonClassBonus) this).getClasses().name + "):" : ":"), NamedTextColor.GREEN));
        bonusLore.addAll(WordWrap.wrap(Component.text(getBonus(), NamedTextColor.GRAY), 160));
        return bonusLore;
    }
}
