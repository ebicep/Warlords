package com.ebicep.warlords.pve.items.types.specialitems.tome.omega;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsCoinSummaryEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.LinkedHashMap;
import java.util.Set;

public class FlemingAlmanac extends SpecialOmegaTome implements AppliesToWarlordsPlayer {
    public FlemingAlmanac(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public FlemingAlmanac() {

    }

    @Override
    public String getName() {
        return "Fleming's Almanac";
    }

    @Override
    public String getBonus() {
        return "Your total coins earned at the end of each game is doubled";
    }

    @Override
    public String getDescription() {
        return "So when can I turn stone into gold?";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onEvent(WarlordsCoinSummaryEvent event) {
                LinkedHashMap<String, Long> currencyToAdd = event.getCurrencyToAdd();
                currencyToAdd.forEach((s, aLong) -> currencyToAdd.put(s, aLong * 2));
            }
        });
    }

}
