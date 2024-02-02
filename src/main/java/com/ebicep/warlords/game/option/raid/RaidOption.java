package com.ebicep.warlords.game.option.raid;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.warlords.GameRunnable;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RaidOption implements PveOption {

    private SimpleScoreboardHandler scoreboard;
    private final Set<AbstractMob> mobs = new HashSet<>();
    private Raid raid;
    private Game game;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                //spawnNewMob(new Physira(new Location(game.getLocations().getWorld(), 711.5, 7, 179.5)));
            }
        }.runTaskLater(60);
    }

    @Override
    public Set<AbstractMob> getMobs() {
        return null;
    }

    @Override
    public int getTicksElapsed() {
        return 0;
    }

    public void spawnNewMob(AbstractMob abstractMob) {
        //abstractMob.toNPC(game, Team.RED, UUID.randomUUID());
        game.addNPC(abstractMob.getWarlordsNPC());
        mobs.add(abstractMob);
    }

    @Override
    public void spawnNewMob(AbstractMob mob, Team team) {

    }

    @Override
    public PveRewards<?> getRewards() {
        return null;
    }

    @Override
    public ConcurrentHashMap<AbstractMob, ? extends MobData> getMobsMap() {
        return null;
    }

    @Override
    public Game getGame() {
        return null;
    }
}
