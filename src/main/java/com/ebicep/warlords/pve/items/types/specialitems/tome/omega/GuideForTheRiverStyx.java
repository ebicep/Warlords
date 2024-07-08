package com.ebicep.warlords.pve.items.types.specialitems.tome.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Set;

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
        return "Negates damage done by Primary Attack projectiles.";
    }

    @Override
    public String getDescription() {
        return "Row, row, row, your boat, gently down the stream...";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
                    return;
                }
                if (event.isHealingInstance()) {
                    return;
                }
                if (!Utils.isPrimaryProjectile(event.getCause())) {
                    return;
                }
                event.setCancelled(true);
            }
        });
    }

}
