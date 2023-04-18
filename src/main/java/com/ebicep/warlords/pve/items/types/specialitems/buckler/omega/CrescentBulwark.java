package com.ebicep.warlords.pve.items.types.specialitems.buckler.omega;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.statpool.StatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CrescentBulwark extends SpecialOmegaBuckler implements AppliesToWarlordsPlayer {

    private static final HashMap<StatPool, Integer> BONUS_STATS = new HashMap<>() {{
        put(BasicStatPool.AGGRO_PRIO, -100);
    }};

    public CrescentBulwark(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public CrescentBulwark() {

    }

    @Override
    public String getName() {
        return "Crescent Bulwark";
    }

    @Override
    public String getBonus() {
        return "For every mob on the field, increase your ability to heal by 1%.";
    }

    @Override
    public String getDescription() {
        return "It's covered in olive oil. No, it doesn't come off.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getWorld().setTime(13000);
    }

    @Override
    public Map<StatPool, Integer> getBonusStats() {
        return BONUS_STATS;
    }

}
