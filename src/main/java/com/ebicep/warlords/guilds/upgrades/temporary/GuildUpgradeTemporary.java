package com.ebicep.warlords.guilds.upgrades.temporary;

import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.DateUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        itemBuilder.addLore(
                Component.empty(),
                Component.text("Click to Purchase", NamedTextColor.GRAY)
        );
    }

    @Override
    public List<Component> getLore() {
        List<Component> lore = new ArrayList<>(super.getLore());
        lore.add(Component.text("Time Left: ")
                          .append(Component.text(DateUtil.getTimeTill(expirationDate,
                                  false,
                                  true,
                                  true,
                                  true
                          ), NamedTextColor.GREEN))
        );
        lore.add(Component.empty());
        lore.add(Component.text(">>> ACTIVE <<<", NamedTextColor.YELLOW));

        return lore;
    }

}
