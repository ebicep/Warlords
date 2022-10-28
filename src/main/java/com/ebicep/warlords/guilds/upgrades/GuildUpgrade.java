package com.ebicep.warlords.guilds.upgrades;

import com.ebicep.warlords.game.Game;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.UUID;

public interface GuildUpgrade {

    String getName();

    String getDescription();

    Material getMaterial();

    double getValueFromTier(int tier);

    String getEffectBonusFromTier(int tier);


    /**
     * @param game       the game to modify - main purpose is adding listeners
     * @param validUUIDs uuids that are allowed to get the effect of this upgrade (since listener will affect all players we need to filter out players which guilds have this upgrade)
     * @param tier
     */
    default void onGame(Game game, HashSet<UUID> validUUIDs, int tier) {

    }

    AbstractGuildUpgrade<?> createUpgrade(int tier);

}
