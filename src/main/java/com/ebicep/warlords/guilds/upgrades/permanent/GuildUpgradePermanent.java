package com.ebicep.warlords.guilds.upgrades.permanent;

import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;

import java.time.Instant;

public class GuildUpgradePermanent extends AbstractGuildUpgrade<GuildUpgradesPermanent> {

    public GuildUpgradePermanent() {
    }

    public GuildUpgradePermanent(GuildUpgradesPermanent guildBlessings, int tier) {
        this.upgrade = guildBlessings;
        this.activationDate = Instant.now();
        this.tier = tier;
    }

    @Override
    public void addItemClickLore(ItemBuilder itemBuilder) {
        if (tier == 9) {
            itemBuilder.addLore(ChatColor.GRAY + "\nMax Tier");
        } else {
            itemBuilder.addLore(ChatColor.YELLOW + "\nClick to Upgrade");
        }
    }

}
