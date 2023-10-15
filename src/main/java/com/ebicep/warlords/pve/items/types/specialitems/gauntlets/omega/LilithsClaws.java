package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega;

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

public class LilithsClaws extends SpecialOmegaGauntlet implements AppliesToWarlordsPlayer {

    public LilithsClaws() {

    }

    public LilithsClaws(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getName() {
        return "Lilith's Claws";
    }

    @Override
    public String getBonus() {
        return "Double the amount of Legendary Fragments you gain but reduces the amount of coins you gain to 0.";
    }

    @Override
    public String getDescription() {
        return "Might as well condemn everyone, right?";
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
                currencyToAdd.forEach((s, aLong) -> currencyToAdd.put(s, 0L));
            }

            @EventHandler
            public void onEvent(WarlordsLegendFragmentGainEvent event) {
                if (!Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
                    return;
                }
                AtomicLong currencyToAdd = event.getLegendFragments();
                currencyToAdd.getAndUpdate(value -> value * 2);
            }
        });
    }
}
