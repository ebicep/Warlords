package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class EchoOfRuin extends BaseSet {

    private float damagePerKillPercent;
    private boolean loseStacksOnDeath;

    @Override
    public void init() {
        super.init();
        // Using float for the 0.5% decimal value
        this.damagePerKillPercent = getValue("damagePerKillPercent", float.class);
        this.loseStacksOnDeath = getValue("loseStacksOnDeath", boolean.class);
    }

    @Override
    public String getConfigFieldName() {
        return "echoOfRuin";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(damagePerKillPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        private int stacks = 0;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Listening for kill events triggered by the player.
            // 2. Incrementing 'stacks' and updating the damage multiplier by 0.5% per stack.
            // 3. Listening for the player's death event.
            // 4. If loseStacksOnDeath is true, resetting stacks to 0 and recalculating damage.
        }

    }

}