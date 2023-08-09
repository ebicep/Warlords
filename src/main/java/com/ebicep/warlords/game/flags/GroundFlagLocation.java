/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebicep.warlords.game.flags;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class GroundFlagLocation extends AbstractLocationBasedFlagLocation implements FlagLocation {
	
    int damageTimer;
    int despawnTimer;

    public GroundFlagLocation(Location location, int damageTimer) {
        super(location);
        this.damageTimer = damageTimer;
        this.despawnTimer = 15 * 20;
    }

    public GroundFlagLocation(PlayerFlagLocation playerFlagLocation) {
        this(playerFlagLocation.getLocation(), playerFlagLocation.getPlayer().isDead() ? playerFlagLocation.getPickUpTicks() + 600 : playerFlagLocation.getPickUpTicks());
    }

    @Nonnull
    @Override
    public Location getLocation() {
        return location;
    }

    public int getDamageTimer() {
        return damageTimer;
    }

    public int getDespawnTimer() {
        return despawnTimer;
    }

    public int getDespawnTimerSeconds() {
        return this.despawnTimer / 20;
    }

    @Override
    public FlagLocation update(@Nonnull FlagInfo info) {
        this.despawnTimer--;
        this.damageTimer++;
        return this.despawnTimer <= 0 ? new SpawnFlagLocation(info.getSpawnLocation(), null) : null;
    }

    @Nonnull
    @Override
    public List<TextComponent> getDebugInformation() {
        return Arrays.asList(
                Component.text("Type: " + this.getClass().getSimpleName()),
                Component.text("Despawn ticks: " + getDespawnTimer()),
                Component.text("Despawn seconds: " + getDespawnTimerSeconds()),
                Component.text("damageTimer: " + getDamageTimer())
        );
    }

    public static GroundFlagLocation of(@Nonnull FlagLocation flag) {
        return flag instanceof PlayerFlagLocation ? new GroundFlagLocation((PlayerFlagLocation) flag)
                : new GroundFlagLocation(flag.getLocation(), 0);
    }
}
