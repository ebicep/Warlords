package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class ArenaShiftAbility {

    private final World world;
    private final Location arenaOne;
    private final Location arenaTwo;
    private final Location arenaThree;

    private final Location bossLocation;

    public ArenaShiftAbility(World world){
        this.world = world;
        // assign arenas
        arenaOne = new Location(world, 110.5, 12, 432.5);
        arenaTwo = new Location(world, 69.5, 31, 477.5);
        arenaThree = new Location(world, 150.5, 18, 480.5);

        bossLocation = new Location(world, 109.5, 20, 468.5);
    }

    public void teleportBoss(WarlordsEntity entity) {
        if (entity instanceof WarlordsNPC warlordsNPC) {
            NPC citizensNpc = warlordsNPC.getNpc();
            if (citizensNpc != null && citizensNpc.isSpawned()) {
                citizensNpc.teleport(bossLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        }
        entity.teleport(bossLocation);
    }

    public void teleportPlayersToArenaOne(List<WarlordsEntity> players) {
        players.forEach(player -> {
            player.teleport(arenaOne);
        });
    }

    public void teleportPlayersToArenaTwo(List<WarlordsEntity> players) {
        players.forEach(player -> {
            player.teleport(arenaTwo);
        });
    }

    public void teleportPlayersToArenaThree(List<WarlordsEntity> players) {
        players.forEach(player -> {
            player.teleport(arenaThree);
        });
    }

    public World getWorld() {
        return world;
    }

    public Location getArenaOne() {
        return arenaOne;
    }

    public Location getArenaTwo() {
        return arenaTwo;
    }

    public Location getArenaThree() {
        return arenaThree;
    }

    public Location getBossLocation() {
        return bossLocation;
    }
}
