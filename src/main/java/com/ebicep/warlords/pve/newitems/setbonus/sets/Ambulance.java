package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Ambulance extends BaseSet {

    private int allyHealthThresholdPercent;
    private int movementSpeedBonusPercent;
    private int healingBonusToAllyPercent;

    @Override
    public void init() {
        super.init();
        this.allyHealthThresholdPercent = getValue("allyHealthThresholdPercent", int.class);
        this.movementSpeedBonusPercent = getValue("movementSpeedBonusPercent", int.class);
        this.healingBonusToAllyPercent = getValue("healingBonusToAllyPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "ambulance";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(allyHealthThresholdPercent, movementSpeedBonusPercent, healingBonusToAllyPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Monitoring health of all allies.
            // 2. Applying GLOW effect to allies below allyHealthThresholdPercent.
            // 3. Modifying movement speed when moving toward the low-health ally.
            // 4. Boosting outgoing healing toward those specific allies.
        }

    }

}