package com.ebicep.warlords.maps.flags;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.state.PlayingState;
import com.ebicep.warlords.player.WarlordsPlayer;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import net.minecraft.server.v1_8_R3.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FlagManager implements Listener {

    private final FlagInfo red;
    private final FlagInfo blue;

    private final FlagRenderer redRenderer;
    private final FlagRenderer blueRenderer;

    private final BukkitTask task;
    final PlayingState gameState;
    private int scoreTick = 0;

    @Nonnull
    public FlagInfo getRed() {
        return red;
    }

    @Nonnull
    public FlagInfo getBlue() {
        return blue;
    }

    @Nonnull
    public FlagInfo get(@Nonnull Team team) {
        return team == Team.RED ? this.red : this.blue;
    }

    public FlagManager(PlayingState gameState, Location redFlagRespawn, Location blueFlagRespawn) {
        this.red = new FlagInfo(Team.RED, redFlagRespawn, this);
        this.blue = new FlagInfo(Team.BLUE, blueFlagRespawn, this);

        this.redRenderer = new FlagRenderer(red);
        this.blueRenderer = new FlagRenderer(blue);

        final Warlords plugin = Warlords.getInstance();
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1, 1);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.gameState = gameState;
    }

    public void checkScore(Team team) {
        if (
                this.get(team).getFlag() instanceof SpawnFlagLocation &&
                        this.get(team.enemy()).getFlag() instanceof PlayerFlagLocation &&
                        this.get(team.enemy()).getFlag().getLocation().distanceSquared(this.get(team).getSpawnLocation()) < 4 * 4
        ) {
            for (Team t : Team.values()) {
                FlagInfo info = get(t);
                info.setFlag(new WaitingFlagLocation(info.getSpawnLocation(), t != team));
            }
        }
    }

    public void tick() {
        if (++scoreTick > 7) {
            checkScore(Team.RED);
            checkScore(Team.BLUE);
            scoreTick = 0;
        }

        this.red.update();
        this.blue.update();
        this.redRenderer.checkRender();
        this.blueRenderer.checkRender();
    }

    @EventHandler
    public void onPlayerDeath(WarlordsDeathEvent event) {
        dropFlag(event.getPlayer());
    }

    public boolean dropFlag(Player player) {
        return dropFlag(Warlords.getPlayer(player));
    }

    public boolean dropFlag(@Nullable WarlordsPlayer player) {
        if (player == null) {
            return false;
        }
        FlagInfo info = get(player.getTeam().enemy());
        if (info.getFlag() instanceof PlayerFlagLocation) {
            PlayerFlagLocation playerFlagLocation = (PlayerFlagLocation) info.getFlag();
            if (playerFlagLocation.getPlayer() == player) {
                info.setFlag(new GroundFlagLocation(player.getLocation(), playerFlagLocation.getPickUpTicks()));
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onArmorStandBreak(EntityDamageByEntityEvent event) {
        WarlordsPlayer wp = Warlords.getPlayer(event.getDamager());
        if (wp != null) {
            if(this.blueRenderer.getRenderedArmorStands().contains(event.getEntity())) {
                onFlagInteract(wp, this.blue);
                event.setCancelled(true);
            }
            if (this.redRenderer.getRenderedArmorStands().contains(event.getEntity())) {
                onFlagInteract(wp, this.red);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPotentialFlagInteract(PlayerInteractEntityEvent event) {
        onPotentialFlagInteract((PlayerEvent)event);
    }

    @EventHandler
    public void onPotentialFlagInteract(PlayerInteractEvent event) {
        onPotentialFlagInteract((PlayerEvent)event);
    }

    private void onPotentialFlagInteract(PlayerEvent event) {
        WarlordsPlayer wp = Warlords.getPlayer(event.getPlayer());
        if (wp != null) {
            Location playerLocation = wp.getEntity().getEyeLocation();
            Vector direction = wp.getEntity().getLocation().getDirection().multiply(3);
            Vec3D from = new Vec3D(
                    playerLocation.getX(),
                    playerLocation.getY(),
                    playerLocation.getZ()
            );
            Vec3D to = new Vec3D(
                    playerLocation.getX() + direction.getX(),
                    playerLocation.getY() + direction.getY(),
                    playerLocation.getZ() + direction.getZ()
            );
            checkFlagInteract(playerLocation, wp, from, to, this.blueRenderer);
            checkFlagInteract(playerLocation, wp, from, to, this.redRenderer);
        }
    }

    private void checkFlagInteract(Location playerLocation, WarlordsPlayer wp, Vec3D from, Vec3D to, FlagRenderer render) {
        Location entityLoc = new Location(playerLocation.getWorld(), 0, 0, 0);
        for(Entity stand : render.getRenderedArmorStands()) {
            stand.getLocation(entityLoc);
            if(entityLoc.getWorld() == playerLocation.getWorld() && entityLoc.distanceSquared(playerLocation) < 5 * 5) {
                AxisAlignedBB aabb = new AxisAlignedBB(
                        entityLoc.getX() - 0.3,
                        entityLoc.getY(),
                        entityLoc.getZ() - 0.3,
                        entityLoc.getX() + 0.3,
                        entityLoc.getY() + 1.7,
                        entityLoc.getZ() + 0.3
                );
                MovingObjectPosition mop = aabb.a(from, to);
                if(mop != null) {
                    onFlagInteract(wp, render.getInfo());
                    break;
                }
            }
        }
    }

    private void onFlagInteract(WarlordsPlayer wp, FlagInfo info) {
        Team team = wp.getTeam();
        if (wp.isDeath()) {
            return;
        }

        if ((info.getTeam() == Team.BLUE ? this.blueRenderer : this.redRenderer).getLastFlagState() != info.getFlag()) {
            return;
        }

        if (info.getFlag() instanceof GroundFlagLocation) {
            GroundFlagLocation groundFlagLocation = (GroundFlagLocation) info.getFlag();
            if (team == info.getTeam()) {
                // Return flag
                info.setFlag(new SpawnFlagLocation(info.getSpawnLocation(), wp.getName()));
            } else {
                // Steal flag
                info.setFlag(new PlayerFlagLocation(wp, groundFlagLocation.getDamageTimer()));
                if (wp.getEntity().getVehicle() != null) {
                    wp.getEntity().getVehicle().remove();
                }
            }
        } else if (info.getFlag() instanceof SpawnFlagLocation) {
            if (team == info.getTeam()) {
                // Nothing
                wp.sendMessage("§cYou can't steal your own team's flag!");
            } else {
                // Steal flag
                info.setFlag(new PlayerFlagLocation(wp, 0));
            }
        }
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        dropFlag(event.getPlayer());
    }


    public void stop() {
        this.blueRenderer.reset();
        this.redRenderer.reset();
        this.task.cancel();
        HandlerList.unregisterAll(this);
    }

    public boolean hasFlag(WarlordsPlayer warlordsPlayer) {
        return getPlayerWithBlueFlag() == warlordsPlayer || getPlayerWithRedFlag() == warlordsPlayer;
    }

    public WarlordsPlayer getPlayerWithBlueFlag() {
        if (blue.getFlag() instanceof PlayerFlagLocation) {
            return ((PlayerFlagLocation) blue.getFlag()).getPlayer();
        }
        return null;
    }

    public WarlordsPlayer getPlayerWithRedFlag() {
        if (red.getFlag() instanceof PlayerFlagLocation) {
            return ((PlayerFlagLocation) red.getFlag()).getPlayer();
        }
        return null;
    }


}