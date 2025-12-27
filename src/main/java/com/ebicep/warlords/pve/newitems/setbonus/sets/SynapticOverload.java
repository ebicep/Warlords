package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class SynapticOverload extends BaseSet {

    private int upgradeEffectivenessIncreasePercent;
    private int freeAbilityUpgrades;

    @Override
    public void init() {
        super.init();
        this.upgradeEffectivenessIncreasePercent = getValue("upgradeEffectivenessIncreasePercent", int.class);
        this.freeAbilityUpgrades = getValue("freeAbilityUpgrades", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "synapticOverload";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(upgradeEffectivenessIncreasePercent, freeAbilityUpgrades);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Modifying the scaling/multipliers of ability upgrades.
            // 2. Granting the player free upgrade currency or direct ability points.
        }

    }

}