package com.ebicep.warlords.guilds.upgrades.temporary;

import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.DateUtil;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    public List<String> getLore() {
        List<String> lore = new ArrayList<>(super.getLore());
        lore.add(ChatColor.GRAY + "Time Left: " + ChatColor.GREEN + DateUtil.getTimeTill(expirationDate,
                false,
                true,
                true,
                true
        ));
        lore.add(ChatColor.YELLOW + "\n>>> ACTIVE <<<");

        return lore;
    }

}
