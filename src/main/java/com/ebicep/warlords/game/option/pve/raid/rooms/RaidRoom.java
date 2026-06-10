package com.ebicep.warlords.game.option.pve.raid.rooms;

public interface RaidRoom {

    void onStart();

    void tick();

    boolean isComplete();

    void onComplete();

    void cleanup();

}
