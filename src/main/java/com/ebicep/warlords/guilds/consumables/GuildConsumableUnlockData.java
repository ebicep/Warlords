package com.ebicep.warlords.guilds.consumables;

import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class GuildConsumableUnlockData extends AbstractGuildUpgrade<GuildConsumableUnlockUpgrade> {

    @Field("unlocked_consumables")
    private Set<String> unlockedConsumables = new HashSet<>();

    public GuildConsumableUnlockData() {
        this.upgrade = GuildConsumableUnlockUpgrade.DATA;
        this.activationDate = Instant.now();
    }

    @Override
    public void addItemClickLore(ItemBuilder itemBuilder) {
    }

    public Set<String> getUnlockedConsumables() {
        if (unlockedConsumables == null) {
            unlockedConsumables = new HashSet<>();
        }
        return unlockedConsumables;
    }

    public boolean isUnlocked(String consumableId) {
        return getUnlockedConsumables().contains(consumableId);
    }

    public boolean unlock(String consumableId) {
        boolean added = getUnlockedConsumables().add(consumableId);
        this.tier = getUnlockedConsumables().size();
        return added;
    }
}
