package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Sharpshooter extends BaseSet {

    private int instakillChance;

    @Override
    public void init() {
        super.init();
        this.instakillChance = getValue("instakillChance", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "sharpshooter";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(instakillChance);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Detecting ranged primary attacks.
            // 2. Rolling for instakillChance.
            // 3. Ensuring the target is not a boss before executing the kill.
        }

    }

}