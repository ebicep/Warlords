package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Soothsayer extends BaseSet {

    private int runeChance;
    private int runeCount;

    @Override
    public void init() {
        super.init();
        this.runeChance = getValue("runeChance", int.class);
        this.runeCount = getValue("runeCount", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "soothsayer";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(runeChance, runeCount);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for the chance to trigger random runes on right-click
        }

    }

}