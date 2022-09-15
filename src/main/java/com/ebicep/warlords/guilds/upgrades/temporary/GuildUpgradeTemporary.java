package com.ebicep.warlords.guilds.upgrades.temporary;

import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.DateUtil;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class GuildUpgradeTemporary extends AbstractGuildUpgrade<GuildUpgradesTemporary> {

    @Field("expiration_date")
    private Instant expirationDate;

    public GuildUpgradeTemporary() {
    }

    public GuildUpgradeTemporary(GuildUpgradesTemporary guildUpgradesTemporary, int tier) {
        Instant now = Instant.now();
        this.upgrade = guildUpgradesTemporary;
        this.activationDate = now;
        this.expirationDate = guildUpgradesTemporary.expirationDate.apply(now);
        this.tier = tier;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    @Override
    public void addItemClickLore(ItemBuilder itemBuilder) {
        itemBuilder.addLore(ChatColor.GRAY + "\nClick to Purchase");
    }

    @Override
    protected void addItemLore(ItemBuilder itemBuilder) {
        super.addItemLore(itemBuilder);
        itemBuilder.addLore(ChatColor.GRAY + "Time Left: " + ChatColor.GREEN + DateUtil.getTimeTill(expirationDate,
                false,
                true,
                true,
                true
        ));
    }
}
