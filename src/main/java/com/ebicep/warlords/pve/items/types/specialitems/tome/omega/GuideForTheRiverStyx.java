package com.ebicep.warlords.pve.items.types.specialitems.tome.omega;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveRespawnEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class GuideForTheRiverStyx extends SpecialOmegaTome implements AppliesToWarlordsPlayer {
    public GuideForTheRiverStyx() {

    }

    public GuideForTheRiverStyx(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getName() {
        return "Guide for the River Styx";
    }

    @Override
    public String getBonus() {
        return "Significantly increased respawn speed.";
    }

    @Override
    public String getDescription() {
        return "Row, row, row, your boat, gently down the stream...";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onEvent(WarlordsGiveRespawnEvent event) {
                AtomicInteger respawnTimer = event.getRespawnTimer();
                respawnTimer.set((int) (respawnTimer.get() * .75));
            }
        });
    }

}
