package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Olympic extends BaseSet {

    private int jumpHeight;
    private int airResist;

    @Override
    public void init() {
        super.init();
        this.jumpHeight = getValue("jumpHeight", int.class);
        this.airResist = getValue("airResist", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "olympic";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(jumpHeight, airResist);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for jump height modification and 
            // damage reduction while the player's in-air state is true
        }

    }

}