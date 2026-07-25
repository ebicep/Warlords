package com.ebicep.warlords.effects;

import org.bukkit.entity.Player;

import java.util.List;

public interface EffectPlayer<T> {

    void playEffect(T baseData, List<Player> allies, List<Player> enemies);

    void updateCachedData(T baseData);

    boolean needsUpdate();
}
