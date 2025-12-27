package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class ForsakenFlux extends BaseSet {

    private int nonUltimateCooldownReductionPercent;
    private int primaryDamagePenaltyPercent;
    private int primaryHealingPenaltyPercent;

    @Override
    public void init() {
        super.init();
        this.nonUltimateCooldownReductionPercent = getValue("nonUltimateCooldownReductionPercent", int.class);
        this.primaryDamagePenaltyPercent = getValue("primaryDamagePenaltyPercent", int.class);
        this.primaryHealingPenaltyPercent = getValue("primaryHealingPenaltyPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "forsakenFlux";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(nonUltimateCooldownReductionPercent, primaryDamagePenaltyPercent, primaryHealingPenaltyPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Applying a 40% CDR multiplier specifically to non-ultimate skill slots.
            // 2. Intercepting primary attack events (Left Click/Weapon Skill).
            // 3. Reducing damage and healing output of those attacks by 75%.
        }

    }

}