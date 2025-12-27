package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Sacrifice extends BaseSet {

    private int selfReviveHealthPercent;
    private int allyHealthReductionPercent;
    private int reviveCooldownSeconds;

    @Override
    public void init() {
        super.init();
        this.selfReviveHealthPercent = getValue("selfReviveHealthPercent", int.class);
        this.allyHealthReductionPercent = getValue("allyHealthReductionPercent", int.class);
        this.reviveCooldownSeconds = getValue("reviveCooldownSeconds", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "sacrifice";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(selfReviveHealthPercent, allyHealthReductionPercent, reviveCooldownSeconds);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Listening for the LivingDeathEvent or damage event that would be lethal.
            // 2. Checking if the revive cooldown (reviveCooldownSeconds) is ready.
            // 3. Finding a valid ally (non-dead, within range/prioritized).
            // 4. Setting the ally's health to 5% (based on allyHealthReductionPercent).
            // 5. Cancelling the player's death and setting health to selfReviveHealthPercent.
        }

    }

}