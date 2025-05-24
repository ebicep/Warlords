package com.ebicep.warlords.game.option.pvp;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsRespawnEvent;
import com.ebicep.warlords.game.option.TeleportOnEventOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.event.EventHandler;

public class DuelsTeleportOption extends TeleportOnEventOption {

    @EventHandler(ignoreCancelled = true)
    public void onDeathEvent(WarlordsDeathEvent e) {
        for (WarlordsEntity wp : PlayerFilter
                .playingGame(game)
                .isAlive()
        ) {
            wp.respawn();
            wp.getCooldownManager().removeAbilityCooldowns();
            wp.getCooldownManager().removeBuffCooldowns();
            wp.getCooldownManager().removeDebuffCooldowns();

            wp.setEnergy(wp.getSpec().getMaxEnergy());
            for (AbstractAbility ability : wp.getSpec().getAbilities()) {
                ability.setCurrentCooldown(0);
            }
            for (HorseOption horseOption : wp.getGame().getOption(HorseOption.class)) {
                horseOption.getHorseForPlayer(wp).setCurrentCooldown(0);
            }
            wp.updateInventory(true);
        }
        preventPlayerMovement = true;
    }

    @EventHandler
    public void onRespawnEvent(WarlordsRespawnEvent e) {
        preventPlayerMovement = false;
    }

}
