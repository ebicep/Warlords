/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebicep.warlords.maps.flags;

import com.ebicep.warlords.events.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.maps.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 *
 * @author fernando
 */
public class FlagInfo {
	
    private FlagLocation flag;
    private final Location spawnLocation;
    private final Team team;
    private final FlagManager flags;

    public FlagInfo(Team team, Location spawnLocation, final FlagManager flags) {
        this.flags = flags;
        this.team = team;
        this.spawnLocation = spawnLocation;
        this.flag = new SpawnFlagLocation(this.spawnLocation, null);
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
        Bukkit.getPluginManager().callEvent(new WarlordsFlagUpdatedEvent(flags.gameState.getGame(), flags.gameState, flags, this, team, old));
    }

    void update() {
        FlagLocation updated = flag.update(this);
        if (updated != null) {
            setFlag(updated);
        }
    }
	
}
