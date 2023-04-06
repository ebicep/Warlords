package com.ebicep.warlords.game.option.pve.rewards;

import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;

import java.util.*;

public class PlayerPveRewards {
    private final LinkedHashMap<String, Long> cachedBaseCoinSummary = new LinkedHashMap<>();
    private final List<AbstractWeapon> weaponsFound = new ArrayList<>();
    private final HashMap<MobDrops, Long> mobDropsGained = new HashMap<>();
    private final HashMap<Integer, Long> waveDamage = new HashMap<>();
    private long legendFragmentGain = 0;
    private long illusionShardGain = 0;
    private List<AbstractItem<?, ?>> itemsFound = new ArrayList<>();
    private int blessingsFound = 0;
    private Map<Spendable, Long> syntheticPouch = new HashMap<>();
    private Map<Spendable, Long> aspirantPouch = new HashMap<>();


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

    public long getIllusionShardGain() {
        return illusionShardGain;
    }

    public void setIllusionShardGain(long illusionShardGain) {
        this.illusionShardGain = illusionShardGain;
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

    public List<AbstractItem<?, ?>> getItemsFound() {
        return itemsFound;
    }

    public int getBlessingsFound() {
        return blessingsFound;
    }

    public void addBlessingsFound() {
        this.blessingsFound += 1;
    }

    public Map<Spendable, Long> getSyntheticPouch() {
        return syntheticPouch;
    }

    public void setSyntheticPouch(Map<Spendable, Long> syntheticPouch) {
        this.syntheticPouch = syntheticPouch;
    }

    public Map<Spendable, Long> getAspirantPouch() {
        return aspirantPouch;
    }

    public void setAspirantPouch(Map<Spendable, Long> aspirantPouch) {
        this.aspirantPouch = aspirantPouch;
    }
}
