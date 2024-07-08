package com.ebicep.warlords.game.option.pve.treasurehunt;

import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.marker.CanStartGameMarker;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TreasureHuntOption implements PveOption {

    private Game game;
    private final ConcurrentHashMap<AbstractMob, MobData> mobs = new ConcurrentHashMap<>();
    private final AtomicInteger ticksElapsed = new AtomicInteger(0);
    private Random random;
    private Floor floor;
    private final Map<Room, DungeonRoomMarker> rooms = new HashMap<>();
    private final int amountOfRooms;

    public TreasureHuntOption(int amountOfRooms) {
        this.amountOfRooms = amountOfRooms;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        this.game.registerGameMarker(CanStartGameMarker.class, () -> this.floor.isValidPattern());
        this.random = new Random();

        BoundingBoxOption boundingBoxOption = (BoundingBoxOption) game.getOptions().stream()
                .filter(option -> option instanceof BoundingBoxOption)
                .findFirst()
                .get();
        List<DungeonRoomMarker> dungeonRoomMarkerList = game.getMarkers(DungeonRoomMarker.class);

        for (var marker : dungeonRoomMarkerList) {
            rooms.put(marker.getRoom(), marker);
        }

        new GameRunnable(game) {
            @Override
            public void run() {
                floor = Floor.generate(
                        boundingBoxOption.getMax().getBlockX() - boundingBoxOption.getMin().getBlockX(),
                        boundingBoxOption.getMax().getBlockZ() - boundingBoxOption.getMin().getBlockZ(),
                        dungeonRoomMarkerList.stream().map(DungeonRoomMarker::getRoom).toList(),
                        random,
                        amountOfRooms
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

        for (var placedRooms : floor.getPlacedRooms()) {
            rooms.get(placedRooms.getRoom()).renderInWorld(
                    boundingBoxOption.getMin().getBlockX() + placedRooms.getX(),
                    boundingBoxOption.getMin().getBlockY(),
                    boundingBoxOption.getMin().getBlockZ() + placedRooms.getZ()
            );
        }
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public void spawnNewMob(AbstractMob mob, Team team) {
        mob.toNPC(game, team, warlordsNPC -> {});
        game.addNPC(mob.getWarlordsNPC());
        mobs.put(mob, new MobData(ticksElapsed.get()));
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    @Override
    public ConcurrentHashMap<AbstractMob, ? extends MobData> getMobsMap() {
        return mobs;
    }

    @Override
    public PveRewards<?> getRewards() {
        return null;
    }

    @Override
    public Set<AbstractMob> getMobs() {
        return mobs.keySet();
    }

    @Override
    public int getTicksElapsed() {
        return ticksElapsed.get();
    }
}
