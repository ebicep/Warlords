package com.ebicep.warlords.game.option.respawn;

import com.ebicep.warlords.events.player.ingame.WarlordsRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom.SpawnDamageCooldown;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class RespawnSpawnDamageOption implements Option, Listener {

    private final int tickDuration;
    private final float damageBoost;

    public RespawnSpawnDamageOption(int tickDuration, float damageBoost) {
        this.tickDuration = tickDuration;
        this.damageBoost = damageBoost;
    }

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEvent(WarlordsRespawnEvent event) {
        if (event.getWarlordsEntity() instanceof WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new SpawnDamageCooldown(warlordsPlayer, tickDuration, damageBoost));
        }
    }

}
