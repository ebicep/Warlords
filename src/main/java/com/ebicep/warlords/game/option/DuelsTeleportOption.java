package com.ebicep.warlords.game.option;

import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.events.WarlordsRespawnEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.event.EventHandler;

public class DuelsTeleportOption extends TeleportOnEventOption {

    @EventHandler
    public void onDeathEvent(WarlordsDeathEvent e) {
        for (WarlordsPlayer wp : PlayerFilter
                .playingGame(game)
                .isAlive()
        ) {
            wp.respawn();
            wp.getCooldownManager().removeAbilityCooldowns();
            wp.getCooldownManager().removeBuffCooldowns();
            wp.getCooldownManager().removeDebuffCooldowns();

            wp.setEnergy(wp.getSpec().getMaxEnergy());
            wp.getSpec().getRed().setCurrentCooldown(0);
            wp.getSpec().getPurple().setCurrentCooldown(0);
            wp.getSpec().getBlue().setCurrentCooldown(0);
            wp.getSpec().getOrange().setCurrentCooldown(0);
            wp.setHorseCooldown(0);
            wp.updateRedItem();
            wp.updatePurpleItem();
            wp.updateBlueItem();
            wp.updateOrangeItem();
            wp.updateHorseItem();
        }
        preventPlayerMovement = true;
    }

    @EventHandler
    public void onRespawnEvent(WarlordsRespawnEvent e) {
        preventPlayerMovement = false;
    }
}
