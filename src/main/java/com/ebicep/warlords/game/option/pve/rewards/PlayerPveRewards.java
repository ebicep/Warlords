package com.ebicep.warlords.game.option.pve.rewards;

import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class PlayerPveRewards {
    private final LinkedHashMap<String, Long> cachedBaseCoinSummary = new LinkedHashMap<>();
    private final List<AbstractWeapon> weaponsFound = new ArrayList<>();
    private final HashMap<MobDrops, Long> mobDropsGained = new HashMap<>();
    private final HashMap<Integer, Long> waveDamage = new HashMap<>();
    private long legendFragmentGain = 0;

    public List<AbstractWeapon> getWeaponsFound() {
        return weaponsFound;
    }

    public HashMap<MobDrops, Long> getMobDropsGained() {
        return mobDropsGained;
    }

    public long getLegendFragmentGain() {
        return legendFragmentGain;
    }

    public void setLegendFragmentGain(long legendFragmentGain) {
        this.legendFragmentGain = legendFragmentGain;
    }

    public LinkedHashMap<String, Long> getCachedBaseCoinSummary() {
        return cachedBaseCoinSummary;
    }

    public void setCachedBaseCoinSummary(LinkedHashMap<String, Long> cachedBaseCoinSummary) {
        this.cachedBaseCoinSummary.clear();
        this.cachedBaseCoinSummary.putAll(cachedBaseCoinSummary);
    }

    public HashMap<Integer, Long> getWaveDamage() {
        return waveDamage;
    }
}
