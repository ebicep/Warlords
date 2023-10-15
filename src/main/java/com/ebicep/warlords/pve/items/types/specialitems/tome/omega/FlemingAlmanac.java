package com.ebicep.warlords.pve.items.types.specialitems.tome.omega;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsCoinSummaryEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsLegendFragmentGainEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class FlemingAlmanac extends SpecialOmegaTome implements AppliesToWarlordsPlayer {
    public FlemingAlmanac() {

    }

    public FlemingAlmanac(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "So when can I turn stone into gold?";
    }

    @Override
    public String getBonus() {
        return "Triples the amount of coins you gain but halve the amount of Legend Fragments you gain.";
    }

    @Override
    public String getName() {
        return "Fleming's Almanac";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onEvent(WarlordsCoinSummaryEvent event) {
                if (!Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
                    return;
                }
                LinkedHashMap<String, Long> currencyToAdd = event.getCurrencyToAdd();
                currencyToAdd.forEach((s, aLong) -> currencyToAdd.put(s, aLong * 3));
            }

            @EventHandler
            public void onEvent(WarlordsLegendFragmentGainEvent event) {
                if (!Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
                    return;
                }
                AtomicLong currencyToAdd = event.getLegendFragments();
                currencyToAdd.getAndUpdate(value -> value / 2);
            }
        });
    }

}
