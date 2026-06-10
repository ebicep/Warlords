package com.ebicep.warlords.game.option.pve.raid;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.raid.rooms.RaidRoom;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class RaidOption implements PveOption {

    private final RaidDefinition raidDefinition;
    private final List<RaidRoom> rooms = new ArrayList<>();

    private Game game;

    private RaidState state = RaidState.WAITING;

    private int currentRoomIndex = -1;
    private int transitionTicksLeft;
    private int ticksElapsed;

    protected RaidOption(RaidDefinition raidDefinition) {
        this.raidDefinition = raidDefinition;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;

        rooms.clear();
        rooms.addAll(raidDefinition.createRooms(this));

        if (rooms.isEmpty()) {
            throw new IllegalStateException("Raid has no rooms: " + raidDefinition.getName());
        }

        game.registerEvents(getBaseListener());
    }

    @Override
    public void start(@Nonnull Game game) {
        startNextRoom();

        new GameRunnable(game) {
            @Override
            public void run() {
                tickRaid();
            }
        }.runTaskTimer(0, 1);
    }

    private void tickRaid() {
        ticksElapsed++;

        switch (state) {
            case ACTIVE_ROOM -> tickActiveRoom();
            case TRANSITIONING -> tickTransition();
        }
    }

    private void tickActiveRoom() {
        RaidRoom currentRoom = getCurrentRoom()
                .orElseThrow(() -> new IllegalStateException("Raid has no active room"));

        mobTick();
        currentRoom.tick();

        if (currentRoom.isComplete()) {
            beginTransition();
        }
    }

    private void tickTransition() {
        transitionTicksLeft--;

        if (transitionTicksLeft <= 0) {
            startNextRoom();
        }
    }

    private void beginTransition() {
        getCurrentRoom().ifPresent(RaidRoom::cleanup);

        state = RaidState.TRANSITIONING;
        //transitionTicksLeft = raidDefinition.getTransitionTicks();

        onRoomTransition();
    }

    private void startNextRoom() {
        currentRoomIndex++;

        if (currentRoomIndex >= rooms.size()) {
            completeRaid();
            return;
        }

        RaidRoom room = rooms.get(currentRoomIndex);

        state = RaidState.ACTIVE_ROOM;

        onRoomStart(room);
        room.onStart();
    }

    protected void completeRaid() {
        if (state == RaidState.COMPLETED) {
            return;
        }

        state = RaidState.COMPLETED;

        getCurrentRoom().ifPresent(RaidRoom::cleanup);

        Bukkit.getPluginManager().callEvent(
                new WarlordsGameTriggerWinEvent(game, this, Team.BLUE)
        );
    }

    protected void failRaid() {
        if (state == RaidState.FAILED) {
            return;
        }

        state = RaidState.FAILED;

        getCurrentRoom().ifPresent(RaidRoom::cleanup);
    }

    protected void onRoomStart(RaidRoom room) {
    }

    protected void onRoomTransition() {
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        rooms.forEach(RaidRoom::cleanup);
        PveOption.super.onGameCleanup(game);
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public int getTicksElapsed() {
        return ticksElapsed;
    }

    public RaidDefinition getRaidDefinition() {
        return raidDefinition;
    }

    public RaidState getRaidState() {
        return state;
    }

    public Optional<RaidRoom> getCurrentRoom() {
        if (currentRoomIndex < 0 || currentRoomIndex >= rooms.size()) {
            return Optional.empty();
        }

        return Optional.of(rooms.get(currentRoomIndex));
    }

    public int getCurrentRoomIndex() {
        return currentRoomIndex;
    }

    public int getCurrentRoomNumber() {
        return currentRoomIndex + 1;
    }

    public int getTotalRooms() {
        return rooms.size();
    }

    public List<RaidRoom> getRooms() {
        return List.copyOf(rooms);
    }

}
