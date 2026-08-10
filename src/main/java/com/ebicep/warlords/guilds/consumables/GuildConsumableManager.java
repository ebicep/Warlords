package com.ebicep.warlords.guilds.consumables;

import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.pve.consumables.Consumable;

import java.util.Optional;

public final class GuildConsumableManager {

    private GuildConsumableManager() {
    }

    public static GuildConsumableUnlockData getOrCreateData(Guild guild) {
        return findData(guild).orElseGet(() -> {
            GuildConsumableUnlockData data = new GuildConsumableUnlockData();
            guild.addUpgrade(data);
            guild.queueUpdate();
            return data;
        });
    }

    public static Optional<GuildConsumableUnlockData> findData(Guild guild) {
        return guild.getUpgrades().stream()
                    .filter(GuildConsumableUnlockData.class::isInstance)
                    .map(GuildConsumableUnlockData.class::cast)
                    .findFirst();
    }

    public static boolean isUnlocked(Guild guild, Consumable consumable) {
        return findData(guild).map(data -> data.isUnlocked(consumable.getId())).orElse(false);
    }

    public static boolean unlock(Guild guild, Consumable consumable) {
        GuildConsumableUnlockData data = getOrCreateData(guild);
        if (!data.unlock(consumable.getId())) {
            return false;
        }
        guild.queueUpdate();
        return true;
    }
}
