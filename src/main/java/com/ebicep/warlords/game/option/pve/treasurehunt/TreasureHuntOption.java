package com.ebicep.warlords.game.option.pve.treasurehunt;

import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TreasureHuntOption implements PveOption {

    private Game game;
    private final ConcurrentHashMap<AbstractMob<?>, Integer> mobs = new ConcurrentHashMap<>();
    private final AtomicInteger ticksElapsed = new AtomicInteger(0);
    private Random random;
    private Floor floor;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        this.random = new Random();
        BoundingBoxOption boundingBoxOption = (BoundingBoxOption) game.getOptions().stream()
                .filter(option -> option instanceof BoundingBoxOption)
                .findFirst()
                .get();
        List<DungeonRoomMarker> dungeonRoomMarkerList = game.getMarkers(DungeonRoomMarker.class);

        new GameRunnable(game) {
            @Override
            public void run() {
                floor = Floor.generate(
                        boundingBoxOption.getMax().getBlockX() - boundingBoxOption.getMin().getBlockX(),
                        boundingBoxOption.getMax().getBlockZ() - boundingBoxOption.getMin().getBlockZ(),
                        dungeonRoomMarkerList.stream().map(DungeonRoomMarker::getRoom).toList(),
                        random
                );

                if (floor.isValidPattern()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 0);
    }

    @Override
    public void start(@Nonnull Game game) {
        BoundingBoxOption boundingBoxOption = (BoundingBoxOption) game.getOptions().stream()
                .filter(option -> option instanceof BoundingBoxOption)
                .findFirst()
                .get();

        List<DungeonRoomMarker> dungeonRoomMarkerList = game.getMarkers(DungeonRoomMarker.class);

        for (int i = 0; i < dungeonRoomMarkerList.size(); i++) {
            dungeonRoomMarkerList.get(i).renderInWorld(
                    boundingBoxOption.getMin().getBlockX(),
                    boundingBoxOption.getMin().getBlockY(),
                    boundingBoxOption.getMin().getBlockZ()
            );
        }
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
    public ConcurrentHashMap<AbstractMob<?>, Integer> getMobsMap() {
        return mobs;
    }

    @Override
    public PveRewards<?> getRewards() {
        return null;
    }

    @Override
    public Set<AbstractMob<?>> getMobs() {
        return mobs.keySet();
    }

    @Override
    public int getTicksElapsed() {
        return ticksElapsed.get();
    }
}
