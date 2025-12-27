package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Frostveil extends BaseSet {

    private int freezeChance;
    private int duration;
    private int frozenDamageBoost;

    @Override
    public void init() {
        super.init();
        this.freezeChance = getValue("freezeChance", int.class);
        this.duration = getValue("duration", int.class);
        this.frozenDamageBoost = getValue("frozenDamageBoost", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "frostveil";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(freezeChance, duration, frozenDamageBoost);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Hooking into attacks to trigger a freeze based on freezeChance for duration.
            // 2. Modifying damage dealt to enemies with the "frozen" state.
        }

    }

}