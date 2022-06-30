package com.ebicep.warlords.game.flags;

import com.ebicep.warlords.player.ingame.AbstractWarlordsEntity;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class PlayerFlagLocation implements FlagLocation {

    private final AbstractWarlordsEntity player;
    private int pickUpTicks;

    public PlayerFlagLocation(AbstractWarlordsEntity player, int pickUpTicks) {
        this.player = player;
        this.pickUpTicks = pickUpTicks;
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    public AbstractWarlordsEntity getPlayer() {
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

    public static PlayerFlagLocation of(@Nonnull FlagLocation flag, AbstractWarlordsEntity player) {
        return flag instanceof GroundFlagLocation ? new PlayerFlagLocation(player, ((GroundFlagLocation) flag).getDamageTimer())
                : new PlayerFlagLocation(player, 0);
    }
	
}
