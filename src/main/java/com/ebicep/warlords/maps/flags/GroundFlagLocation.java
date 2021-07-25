/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebicep.warlords.maps.flags;

import org.bukkit.Location;

public class GroundFlagLocation extends AbstractLocationBasedFlagLocation implements FlagLocation {
	
    final int damageTimer;
    int despawnTimer;

    public GroundFlagLocation(Location location, int damageTimer) {
        super(location);
        this.damageTimer = damageTimer;
        this.despawnTimer = 15 * 20;
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
        return this.despawnTimer <= 0 ? new SpawnFlagLocation(info.getSpawnLocation(), null) : null;
    }
}
