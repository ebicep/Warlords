package com.ebicep.warlords.guilds.upgrades.permanent;

import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

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
            itemBuilder.addLore(
                    Component.empty(),
                    Component.text("Max Tier", NamedTextColor.GRAY)
            );
        } else {
            itemBuilder.addLore(
                    Component.empty(),
                    Component.text("Click to Upgrade", NamedTextColor.YELLOW)
            );
        }
    }

}
