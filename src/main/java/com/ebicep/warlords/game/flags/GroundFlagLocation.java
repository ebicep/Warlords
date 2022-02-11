/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebicep.warlords.game.flags;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.bukkit.Location;

public class GroundFlagLocation extends AbstractLocationBasedFlagLocation implements FlagLocation {
	
    int damageTimer;
    int despawnTimer;

    public GroundFlagLocation(Location location, int damageTimer) {
        super(location);
        this.damageTimer = damageTimer;
        this.despawnTimer = 15 * 20;
    }

    public GroundFlagLocation(PlayerFlagLocation playerFlagLocation) {
        this(playerFlagLocation.getLocation(), playerFlagLocation.getPickUpTicks());
    }

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
    public FlagLocation update(FlagInfo info) {
        this.despawnTimer--;
        this.damageTimer++;
        return this.despawnTimer <= 0 ? new SpawnFlagLocation(info.getSpawnLocation(), null) : null;
    }

    @Override
    public List<String> getDebugInformation() {
        return Arrays.asList(
                "Type: " + this.getClass().getSimpleName(),
                "Despawn ticks: " + getDespawnTimer(),
                "Despawn seconds: " + getDespawnTimerSeconds(),
                "damageTimer: " + getDamageTimer()
        );
    }

    public static GroundFlagLocation of(@Nonnull FlagLocation flag) {
        return flag instanceof PlayerFlagLocation ? new GroundFlagLocation((PlayerFlagLocation) flag)
                : new GroundFlagLocation(flag.getLocation(), 0);
    }
}
