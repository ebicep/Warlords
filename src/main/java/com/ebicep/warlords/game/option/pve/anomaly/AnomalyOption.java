package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.pve.mobs.AbstractMob;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AnomalyOption implements PveOption {

    private Game game;
    private final ConcurrentHashMap<AbstractMob, MobData> mobs = new ConcurrentHashMap<>();
    private final AtomicInteger ticksElapsed = new AtomicInteger(0);

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public Set<AbstractMob> getMobs() {
        return mobs.keySet();
    }

    @Override
    public int getTicksElapsed() {
        return ticksElapsed.get();
    }

    @Override
    public ConcurrentHashMap<AbstractMob, ? extends MobData> getMobsMap() {
        return mobs;
    }

    @Override
    public void spawnNewMob(AbstractMob mob, Team team) {

    }

    @Override
    public PveRewards<?> getRewards() {
        return null;
    }
}
