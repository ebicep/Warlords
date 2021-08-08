package com.ebicep.warlords.maps.flags;

import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.Location;

public class PlayerFlagLocation implements FlagLocation {
	
    private final WarlordsPlayer player;
    private int pickUpTicks;

    public PlayerFlagLocation(WarlordsPlayer player, int pickUpTicks) {
        this.player = player;
        this.pickUpTicks = pickUpTicks;
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    public WarlordsPlayer getPlayer() {
        return player;
    }

    public int getPickUpTicks() {
        return pickUpTicks;
    }

    public void setPickUpTicks(int modifier) {
        this.pickUpTicks = modifier;
    }

    public void addPickUpTicks(int amount) {
        this.pickUpTicks += amount;
    }

    public double getComputedMultiplier() {
        return 1 + (this.pickUpTicks / (20 * 3)) * 0.01;
    }

    public int getComputedHumanMultiplier() {
        return (this.pickUpTicks / (20 * 3));
    }

    @Override
    public FlagLocation update(FlagInfo info) {
        this.pickUpTicks++;
        return this.pickUpTicks % (20 * 3) == 0 ? new PlayerFlagLocation(player, pickUpTicks) : null;
    }
	
}
