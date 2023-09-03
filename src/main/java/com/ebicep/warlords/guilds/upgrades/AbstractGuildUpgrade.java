package com.ebicep.warlords.guilds.upgrades;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.enchantments.Enchantment;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractGuildUpgrade<T extends Enum<T> & GuildUpgrade> {

    protected T upgrade;
    @Field("activation_date")
    protected Instant activationDate;
    protected int tier;

    public AbstractGuildUpgrade() {
    }

    public abstract void addItemClickLore(ItemBuilder itemBuilder);

    public void modifyItem(ItemBuilder itemBuilder) {
        itemBuilder.enchant(Enchantment.OXYGEN, 1);
        itemBuilder.lore(getLore());
    }

    public List<Component> getLore() {
        return Arrays.asList(
                Component.text("Current Tier: ", NamedTextColor.GRAY).append(Component.text(tier, NamedTextColor.GREEN)),
                Component.text("Effect Bonus: ", NamedTextColor.GRAY).append(Component.text(upgrade.getEffectBonusFromTier(tier), NamedTextColor.GREEN))
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
