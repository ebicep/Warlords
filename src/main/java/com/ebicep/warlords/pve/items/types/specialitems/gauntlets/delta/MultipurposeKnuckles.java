package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega.RobinHoodsGloves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

public class MultipurposeKnuckles extends SpecialDeltaGauntlet implements AppliesToWarlordsPlayer {

    public MultipurposeKnuckles() {

    }

    public MultipurposeKnuckles(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {
            @EventHandler
            public void onCurrencyAdd(WarlordsAddCurrencyEvent event) {
                if (event.getWarlordsEntity().equals(warlordsPlayer)) {
                    float currencyToAdd = event.getCurrencyToAdd();
                    event.setCurrencyToAdd(currencyToAdd * 1.1f);
                }
            }
        });
    }

    @Override
    public String getName() {
        return "Swiper's Claws";
    }

    @Override
    public String getBonus() {
        return "Gain 10% more insignia from all sources.";
    }

    @Override
    public String getDescription() {
        return "Swiper no swiping!";
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new RobinHoodsGloves(statPool);
    }
}
