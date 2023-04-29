package com.ebicep.warlords.game.flags;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class PlayerFlagLocation implements FlagLocation {

    private final WarlordsEntity player;
    private int pickUpTicks;

    public PlayerFlagLocation(WarlordsEntity player, int pickUpTicks) {
        this.player = player;
        this.pickUpTicks = pickUpTicks;
    }

    @Nonnull
    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    public WarlordsEntity getPlayer() {
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

    @Nonnull
    @Override
    public List<TextComponent> getDebugInformation() {
        return Arrays.asList(
                Component.text("Type: " + this.getClass().getSimpleName()),
                Component.text("Player: " + this.getPlayer().getName()),
                Component.text("pickUpTicks: " + getPickUpTicks()),
                Component.text("pickUpTicks / 20: " + getPickUpTicks() / 20),
                Component.text("Multiplier: +" + getComputedHumanMultiplier() + "%")
        );
    }

    public static PlayerFlagLocation of(@Nonnull FlagLocation flag, WarlordsEntity player) {
        return flag instanceof GroundFlagLocation ? new PlayerFlagLocation(player, ((GroundFlagLocation) flag).getDamageTimer())
                : new PlayerFlagLocation(player, 0);
    }
	
}
