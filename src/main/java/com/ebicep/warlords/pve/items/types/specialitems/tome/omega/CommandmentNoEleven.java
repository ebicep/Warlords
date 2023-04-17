package com.ebicep.warlords.pve.items.types.specialitems.tome.omega;

import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropMobDropEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class CommandmentNoEleven extends SpecialOmegaTome implements AppliesToWarlordsPlayer {
    @Override
    public String getName() {
        return "Commandment No. Eleven";
    }

    @Override
    public String getBonus() {
        return "Chance for Zenith to drop a Zenith Star is doubled.";
    }

    @Override
    public String getDescription() {
        return "No way? No. Yahweh.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onMobDrop(WarlordsDropMobDropEvent event) {
                if (!Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
                    return;
                }
                if (event.getMobDrop() != MobDrops.ZENITH_STAR) {
                    return;
                }
                AtomicDouble dropRate = event.getDropRate();
                dropRate.set(dropRate.get() * 2);
            }
        });
    }

}
