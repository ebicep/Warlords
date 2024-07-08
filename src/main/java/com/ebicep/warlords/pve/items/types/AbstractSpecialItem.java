package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.addons.ItemAddonClassBonus;
import com.ebicep.warlords.pve.items.addons.ItemAddonSpecBonus;
import com.ebicep.warlords.pve.items.modifiers.UpgradeTreeBonus;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractSpecialItem extends AbstractItem implements BonusStats, BonusLore {

    @Field("upgrade_tree_bonus")
    @Nullable
    private UpgradeTreeBonus upgradeTreeBonus;

    public AbstractSpecialItem() {
        super();
    }

    public AbstractSpecialItem(ItemType type, ItemTier tier) {
        this(type, tier, tier.generateStatPool());
    }

    public AbstractSpecialItem(ItemType type, ItemTier tier, Set<BasicStatPool> statPool) {
        super(type, tier, statPool);
    }

    @Override
    public void applyRandomModifier() {
        super.applyRandomModifier();
        if (getTier() == ItemTier.GAMMA) {
            upgradeTreeBonus = getType().upgradeTreeBonuses[ThreadLocalRandom.current().nextInt(getType().upgradeTreeBonuses.length)];
        }
    }

    @Override
    public AbstractItem clone() {
        return null; // TODO if needed
    }

    @Override
    public Component getItemName() {
        return Component.text(getName(), getModifierColor());
    }

    @Override
    public ItemBuilder generateItemBuilder() {
        ItemBuilder itemBuilder = getBaseItemBuilder();
        addStatPoolAndModifier(itemBuilder, null);
        itemBuilder.addLore(Component.empty());
        itemBuilder.addLore(getBonusLore());
        addItemScore(itemBuilder, false);
        if (isFavorite()) {
            itemBuilder.addLore(Component.empty(), Component.text("FAVORITE", NamedTextColor.LIGHT_PURPLE));
        }
        itemBuilder.addLore(Component.empty());
        itemBuilder.addLore(WordWrap.wrap(Component.text(getDescription(), NamedTextColor.DARK_GRAY, TextDecoration.ITALIC), 160));
        return itemBuilder;
    }

    @Override
    public List<Component> getBonusLore() {
        List<Component> bonusLore = new ArrayList<>();
        TextComponent.Builder bonusBuilder = Component.text("Bonus", NamedTextColor.GREEN)
                                                      .toBuilder();
        if (this instanceof ItemAddonClassBonus itemAddonClassBonus) {
            bonusBuilder.append(Component.text(" (" + itemAddonClassBonus.getClasses().name + ")"));
        }
        if (this instanceof ItemAddonSpecBonus itemAddonSpecBonus) {
            bonusBuilder.append(Component.text(" (" + itemAddonSpecBonus.getSpec().name + ")"));
        }
        bonusBuilder.append(Component.text(":"));
        bonusLore.add(bonusBuilder.build());
        if (getTier() == ItemTier.GAMMA) {
            bonusLore.add(Component.text(getUpgradeTreeBonusDescription(1), NamedTextColor.GRAY));
        } else {
            bonusLore.addAll(WordWrap.wrap(Component.text(getBonus(), NamedTextColor.GRAY), 160));
        }
        return bonusLore;
    }

    public abstract String getDescription();

    public String getUpgradeTreeBonusDescription(int level) {
        if (upgradeTreeBonus == null && getTier() == ItemTier.GAMMA) {
            upgradeTreeBonus = getType().upgradeTreeBonuses[ThreadLocalRandom.current().nextInt(getType().upgradeTreeBonuses.length)];
        }
        if (upgradeTreeBonus == null) {
            return "Invalid item. Please report it!";
        }
        return upgradeTreeBonus.getDescription(level);
    }

    public abstract String getBonus();

    public abstract String getName();

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

    public @org.jetbrains.annotations.Nullable UpgradeTreeBonus getUpgradeTreeBonus() {
        return upgradeTreeBonus;
    }
}
