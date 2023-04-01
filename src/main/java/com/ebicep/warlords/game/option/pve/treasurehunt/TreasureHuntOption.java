package com.ebicep.warlords.game.option.pve.treasurehunt;

import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TreasureHuntOption implements Option, PveOption {

    private Game game;
    private final ConcurrentHashMap<AbstractMob<?>, Integer> mobs = new ConcurrentHashMap<>();
    private final AtomicInteger ticksElapsed = new AtomicInteger(0);

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
    }

    @Override
    public void start(@Nonnull Game game) {

    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public int playerCount() {
        return (int) game.warlordsPlayers().count();
    }

    @Override
    public void spawnNewMob(AbstractMob<?> mob, Team team) {
        mob.toNPC(game, team, UUID.randomUUID(), warlordsNPC -> {});
        game.addNPC(mob.getWarlordsNPC());
        mobs.put(mob, ticksElapsed.get());
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    @Override
    public Set<AbstractMob<?>> getMobs() {
        return mobs.keySet();
    }
}
