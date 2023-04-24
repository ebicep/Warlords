package com.ebicep.warlords.pve.weapons.weaponaddons;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import net.kyori.adventure.text.Component;

import java.util.LinkedHashMap;
import java.util.List;

public interface StatsRerollable {

    void reroll();

    default List<Component> getRerollCostLore() {
        LinkedHashMap<Spendable, Long> cost = new LinkedHashMap<>() {{
            put(Currencies.COIN, (long) getRerollCost());
        }};
        return PvEUtils.getCostLore(cost, true);
    }

    int getRerollCost();

}
