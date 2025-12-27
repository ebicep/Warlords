package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class HeartOfGlass extends BaseSet {

    private int maxHealthReductionPercent;
    private int damageReductionPenaltyPercent;
    private int damageIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.maxHealthReductionPercent = getValue("maxHealthReductionPercent", int.class);
        this.damageReductionPenaltyPercent = getValue("damageReductionPenaltyPercent", int.class);
        this.damageIncreasePercent = getValue("damageIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "heartOfGlass";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(maxHealthReductionPercent, damageReductionPenaltyPercent, damageIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for "Glass Cannon" mechanics:
            // 1. Subtract maxHealthReductionPercent from the player's max health.
            // 2. Reduce total damage reduction by damageReductionPenaltyPercent.
            // 3. Apply a multiplicative damage increase of damageIncreasePercent.
        }

    }

}