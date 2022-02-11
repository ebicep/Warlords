/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebicep.warlords.game.flags;

import com.ebicep.warlords.game.Team;

import java.util.function.BiConsumer;
import org.bukkit.Location;

public class FlagInfo {
	
    private FlagLocation flag;
    private final Location spawnLocation;
    private final Team team;
    private final BiConsumer<FlagInfo, FlagLocation> onUpdate;

    public FlagInfo(Team team, Location spawnLocation, BiConsumer<FlagInfo, FlagLocation> onUpdate) {
        this.team = team;
        this.spawnLocation = spawnLocation;
        this.flag = new SpawnFlagLocation(this.spawnLocation, null);
        this.onUpdate = onUpdate;
    }

    public FlagLocation getFlag() {
        return flag;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public Team getTeam() {
        return team;
    }

    public void setFlag(FlagLocation flag) {
        FlagLocation old = this.flag;
        this.flag = flag;
        onUpdate.accept(this, old);
    }

    void update() {
        FlagLocation updated = flag.update(this);
        if (updated != null) {
            setFlag(updated);
        }
    }
	
}
