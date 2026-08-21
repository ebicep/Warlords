package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class GoldDigger extends BaseSet {

    private int bonusCurrencyPercent;

    @Override
    public void init() {
        super.init();
        this.bonusCurrencyPercent = getValue("bonusCurrencyPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "golddigger";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(bonusCurrencyPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getGame().registerEvents(new Listener() {
                @EventHandler
                public void onCurrencyAdd(WarlordsAddCurrencyEvent event) {
                    if (event.getWarlordsEntity().equals(warlordsPlayer)) {
                        float currencyToAdd = event.getCurrencyToAdd();
                        event.setCurrencyToAdd(currencyToAdd * (1 + (bonusCurrencyPercent / 100f)));
                    }
                }
            });
        }
    }
}
