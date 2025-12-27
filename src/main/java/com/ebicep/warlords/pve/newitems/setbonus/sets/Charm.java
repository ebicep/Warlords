package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Charm extends BaseSet {

    private int radius;
    private int damage;
    private int slow;

    @Override
    public void init() {
        super.init();
        this.radius = getValue("radius", int.class);
        this.damage = getValue("damage", int.class);
        this.slow = getValue("slow", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "charm";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(radius, damage, slow);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Logic for aura effect: radius, damage increase, and slowness
        }

    }

}