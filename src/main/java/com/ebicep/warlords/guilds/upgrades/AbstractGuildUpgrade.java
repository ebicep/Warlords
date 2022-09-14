package com.ebicep.warlords.guilds.upgrades;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public abstract class AbstractGuildUpgrade<T extends Enum<T> & GuildUpgrade> {

    protected T upgrade;
    @Field("activation_date")
    protected Instant activationDate;
    protected int tier;

    public abstract void addItemClickLore(ItemBuilder itemBuilder);

    public void modifyItem(ItemBuilder itemBuilder) {
        itemBuilder.enchant(Enchantment.OXYGEN, 1);
        addItemLore(itemBuilder);
        itemBuilder.addLore(ChatColor.YELLOW + "\n>>> ACTIVE <<<");
    }

    protected void addItemLore(ItemBuilder itemBuilder) {
        itemBuilder.lore(
                ChatColor.GRAY + "Current Tier: " + ChatColor.GREEN + tier,
                ChatColor.GRAY + "Effect Bonus: " + ChatColor.GREEN + upgrade.getEffectBonusFromTier(tier)
        );
    }

    public boolean isMatchingUpgrade(AbstractGuildUpgrade<?> otherUpgrade) {
        return otherUpgrade.getUpgrade() == upgrade;
    }

    public T getUpgrade() {
        return upgrade;
    }

    public Instant getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Instant activationDate) {
        this.activationDate = activationDate;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }
}
