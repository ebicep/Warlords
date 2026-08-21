package com.ebicep.warlords.pve.consumables;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyEvent;
import com.ebicep.warlords.events.player.ingame.pve.drops.AbstractWarlordsDropRewardEvent;
import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropNewItemEvent;
import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropWeaponEvent;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.consumables.vials.VialEffect;
import com.ebicep.warlords.pve.consumables.vials.VialManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ConsumableListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInsigniaGain(WarlordsAddCurrencyEvent event) {
        if (!(event.getWarlordsEntity() instanceof WarlordsPlayer warlordsPlayer)
                || !GameMode.isPvE(warlordsPlayer.getGame().getGameMode())) {
            return;
        }
        double multiplier = VialManager.getMultiplier(warlordsPlayer.getDatabasePlayer(), VialEffect.INSIGNIA_GAIN);
        event.setCurrencyToAdd((float) (event.getCurrencyToAdd() * multiplier));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWeaponDrop(WarlordsDropWeaponEvent event) {
        applyDropMultiplier(event, VialEffect.WEAPON_DROP_RATE);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNewItemDrop(WarlordsDropNewItemEvent event) {
        applyDropMultiplier(event, VialEffect.ITEM_DROP_RATE);
    }

    private void applyDropMultiplier(AbstractWarlordsDropRewardEvent event, VialEffect effect) {
        if (!(event.getWarlordsEntity() instanceof WarlordsPlayer warlordsPlayer)
                || !GameMode.isPvE(warlordsPlayer.getGame().getGameMode())) {
            return;
        }
        double multiplier = VialManager.getMultiplier(warlordsPlayer.getDatabasePlayer(), effect);
        event.setModifier(event.getModifier() * multiplier);
    }
}
