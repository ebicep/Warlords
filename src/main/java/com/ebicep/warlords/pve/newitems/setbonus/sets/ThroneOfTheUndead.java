package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class ThroneOfTheUndead extends BaseSet {

    private int respawnTimeReductionPercent;
    private int respawnHealthPercent;
    private int respawnEnergy;

    @Override
    public void init() {
        super.init();
        this.respawnTimeReductionPercent = getValue("respawnTimeReductionPercent", int.class);
        this.respawnHealthPercent = getValue("respawnHealthPercent", int.class);
        this.respawnEnergy = getValue("respawnEnergy", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "throneOfTheUndead";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(respawnTimeReductionPercent, respawnHealthPercent, respawnEnergy);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Intercepting the player's death/respawn timer logic to apply 
            //    respawnTimeReductionPercent.
            // 2. Setting current health to respawnHealthPercent of max on respawn.
            // 3. Wiping current energy to respawnEnergy on respawn.
        }

    }

}