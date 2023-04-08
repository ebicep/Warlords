package com.ebicep.warlords.pve.items.types.specialitems.buckler.delta;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.statpool.StatPool;

import java.util.HashMap;
import java.util.Map;

public class BucklerPiece extends SpecialDeltaBuckler {

    private static final HashMap<StatPool, Integer> BONUS_STATS = new HashMap<>() {{
        put(BasicStatPool.KB_RES, 50);
    }};

    @Override
    public String getName() {
        return "Buckler Piece";
    }

    @Override
    public String getBonus() {
        return "+5% Knockback Resistance.";
    }

    @Override
    public String getDescription() {
        return "Punk Hazard on a plate.";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {

    }

    @Override
    public Map<StatPool, Integer> getBonusStats() {
        return BONUS_STATS;
    }

}
