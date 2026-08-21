package com.ebicep.warlords.pve.consumables.vials;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.consumables.Consumable;
import com.ebicep.warlords.pve.consumables.ConsumableManager;

public final class VialManager {

    private VialManager() {
    }

    public static double getMultiplier(DatabasePlayer databasePlayer, VialEffect effect) {
        if (databasePlayer == null) {
            return 1;
        }
        ConsumableManager manager = databasePlayer.getPveStats().getConsumableManager();
        Consumable active = manager.getActiveDefinition(effect.getActiveGroup());
        if (active instanceof Vial vial && vial.getEffect() == effect) {
            return vial.getMultiplier();
        }
        return 1;
    }
}
