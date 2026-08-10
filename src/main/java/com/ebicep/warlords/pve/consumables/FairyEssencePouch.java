package com.ebicep.warlords.pve.consumables;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Currencies;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public enum FairyEssencePouch implements Consumable {

    INSTANCE;

    @Override
    public String getId() {
        return "fairy_essence_pouch";
    }

    @Override
    public String getName() {
        return "Fairy Essence Pouch";
    }

    @Override
    public String getDescription() {
        return "Grants Fairy Essence instantly when purchased.";
    }

    @Override
    public String getEffectDescription() {
        return "+1,000 Fairy Essence";
    }

    @Override
    public Material getMaterial() {
        return Material.MAGENTA_BUNDLE;
    }

    @Override
    public long getPlayerCost() {
        return 500_000;
    }

    @Override
    public long getGuildUnlockCost() {
        return 500_000;
    }

    @Override
    public ConsumablePurchaseLimit getPurchaseLimit() {
        return ConsumablePurchaseLimit.WEEKLY;
    }

    @Override
    public void onConsume(DatabasePlayer databasePlayer, Player player) {
        databasePlayer.getPveStats().addCurrency(Currencies.FAIRY_ESSENCE, 1_000);
    }
}
