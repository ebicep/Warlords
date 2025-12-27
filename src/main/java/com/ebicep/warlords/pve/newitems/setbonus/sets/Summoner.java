package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Summoner extends BaseSet {

    private int minionDamage;
    private int minionSpeed;

    @Override
    public void init() {
        super.init();
        this.minionDamage = getValue("minionDamage", int.class);
        this.minionSpeed = getValue("minionSpeed", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "summoner";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(minionDamage, minionSpeed);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for buffing summoned mobs damage/speed
            // and ensuring summons are converted to Aspects.
        }

    }

}