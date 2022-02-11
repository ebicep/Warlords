package com.ebicep.warlords.game.flags;

import com.ebicep.warlords.player.WarlordsPlayer;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
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

    @Override
    public List<String> getDebugInformation() {
        return Arrays.asList(
                "Type: " + this.getClass().getSimpleName(),
                "Player: " + this.getPlayer().getName(),
                "pickUpTicks: " + getPickUpTicks(),
                "pickUpTicks / 20: " + getPickUpTicks() / 20,
                "Multiplier: +" + getComputedHumanMultiplier() + "%"
        );
    }

    public static PlayerFlagLocation of(@Nonnull FlagLocation flag, WarlordsPlayer player) {
        return flag instanceof GroundFlagLocation ? new PlayerFlagLocation(player, ((GroundFlagLocation) flag).getDamageTimer())
                : new PlayerFlagLocation(player, 0);
    }
	
}
