package com.ebicep.warlords.pve.items.types.specialitems.tome.omega;

import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropMobDropEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.mobs.MobDrop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Set;

public class CommandmentNoEleven extends SpecialOmegaTome implements AppliesToWarlordsPlayer {
    public CommandmentNoEleven() {

    }

    public CommandmentNoEleven(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getName() {
        return "Commandment No. Eleven";
    }

    @Override
    public String getBonus() {
        return "+15% chance for Zenith to drop a Zenith Star.";
    }

    @Override
    public String getDescription() {
        return "No way? No. Yahweh.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onMobDrop(WarlordsDropMobDropEvent event) {
                if (!Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
                    return;
                }
                if (event.getMobDrop() != MobDrop.ZENITH_STAR) {
                    return;
                }
                event.addModifier(.15);
            }
        });
    }

}
