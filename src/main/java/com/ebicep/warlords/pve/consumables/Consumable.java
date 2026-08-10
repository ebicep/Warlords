package com.ebicep.warlords.pve.consumables;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.Duration;

public interface Consumable {

    String getId();

    String getName();

    String getDescription();

    String getEffectDescription();

    Material getMaterial();

    long getPlayerCost();

    long getGuildUnlockCost();

    default Duration getDuration() {
        return Duration.ZERO;
    }

    default boolean isTimed() {
        return !getDuration().isZero();
    }

    default String getActiveGroup() {
        return getId();
    }

    default ConsumablePurchaseLimit getPurchaseLimit() {
        return ConsumablePurchaseLimit.NONE;
    }

    default void onConsume(DatabasePlayer databasePlayer, Player player) {
    }
}
