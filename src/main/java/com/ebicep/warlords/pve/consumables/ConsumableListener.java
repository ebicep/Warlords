package com.ebicep.warlords.pve.consumables;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.consumables.vials.VialEffect;
import com.ebicep.warlords.pve.consumables.vials.VialManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ConsumableListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInsigniaGain(WarlordsAddCurrencyEvent event) {
        if (!(event.getWarlordsEntity() instanceof WarlordsPlayer warlordsPlayer) || !warlordsPlayer.isInPve()) {
            return;
        }
        double multiplier = VialManager.getMultiplier(warlordsPlayer.getDatabasePlayer(), VialEffect.INSIGNIA_GAIN);
        event.setCurrencyToAdd((float) (event.getCurrencyToAdd() * multiplier));
    }
}
