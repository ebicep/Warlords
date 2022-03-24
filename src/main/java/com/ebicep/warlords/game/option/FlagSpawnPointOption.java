package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.flags.*;
import com.ebicep.warlords.game.option.marker.*;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import net.minecraft.server.v1_8_R3.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Module for spawning a flag
 */
public class FlagSpawnPointOption implements Option {
    
    public static final boolean DEFAULT_REGISTER_COMPASS_MARKER = true;

    @Nonnull
    private final FlagInfo info;
    @Nonnull
    private final FlagRenderer renderer;
    @Nonnull
    private SimpleScoreboardHandler scoreboard;
    @Nonnull
    private Game game;
    private final boolean registerCompassMarker;

    public FlagSpawnPointOption(@Nonnull Location loc, @Nonnull Team team) {
        this(loc, team, DEFAULT_REGISTER_COMPASS_MARKER);
    }

    public FlagSpawnPointOption(@Nonnull Location loc, @Nonnull Team team, boolean registerCompassMarker) {
        this.info = new FlagInfo(team, loc, this::onFlagUpdate);
        this.renderer = new FlagRenderer(info);
        this.registerCompassMarker = registerCompassMarker;
    }

    @Override
    public void register(Game game) {
        this.game = game;
        // We register a gamemarker to prevent any captures for our own team if we lost our flag
        game.registerGameMarker(FlagCaptureInhibitMarker.class, pFlag -> {
            return !(info.getFlag() instanceof SpawnFlagLocation) && info.getTeam() == pFlag.getPlayer().getTeam();
        });
        game.registerGameMarker(DebugLocationMarker.class, DebugLocationMarker.create(Material.BANNER, 0, this.getClass(),
                "Flag spawn: " + info.getTeam(),
                this.info.getSpawnLocation()
        ));
        game.registerGameMarker(DebugLocationMarker.class, DebugLocationMarker.create(Material.BANNER, 15, this.getClass(),
                "Flag: " + info.getTeam(),
                () -> info.getFlag().getLocation(),
                () -> info.getFlag().getDebugInformation()
        ));
        FlagHolder holder = FlagHolder.create(() -> info);
        game.registerGameMarker(FlagHolder.class, holder);
        if (this.registerCompassMarker) {
            game.registerGameMarker(CompassTargetMarker.class, holder);
        }
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(info.getTeam() == Team.RED ? 20 : 21, "flag") {
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {
                String flagName = info.getTeam().coloredPrefix();
                FlagLocation flag = info.getFlag();
                if (flag instanceof SpawnFlagLocation) {
                    return singletonList(flagName + " Flag: " + ChatColor.GREEN + "Safe");
                } else if (flag instanceof PlayerFlagLocation) {
                    PlayerFlagLocation pFlag = (PlayerFlagLocation) flag;
                    String extra = pFlag.getPickUpTicks() == 0 ? "" : ChatColor.YELLOW + " +" + pFlag.getComputedHumanMultiplier() + "§e%";
                    return singletonList(flagName + " Flag: " + ChatColor.RED + "Stolen!" + extra);
                } else if (flag instanceof GroundFlagLocation) {
                    GroundFlagLocation gFlag = (GroundFlagLocation) flag;
                    return singletonList(flagName + " Flag: " + ChatColor.YELLOW + "Dropped! " + ChatColor.GRAY + gFlag.getDespawnTimerSeconds());
                } else {
                    return singletonList(flagName + " Flag: " + ChatColor.GRAY + "Respawning...");
                }
            }
        });
        game.registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.LOW)
            public void onArmorStandBreak(EntityDamageByEntityEvent event) {
                boolean isOurArmorStand = renderer.getRenderedArmorStands().contains(event.getEntity());
                WarlordsPlayer wp = Warlords.getPlayer(event.getDamager());
                if (wp != null && wp.getGame() == game && isOurArmorStand) {
                    onFlagInteract(wp);
                    event.setCancelled(true);
                }
            }

            @EventHandler(priority = EventPriority.LOW)
            public void onPotentialFlagInteract(PlayerInteractEntityEvent event) {
                onPotentialFlagInteract((PlayerEvent)event);
            }

            @EventHandler(priority = EventPriority.LOW)
            public void onPotentialFlagInteract(PlayerInteractEvent event) {
                onPotentialFlagInteract((PlayerEvent)event);
            }

            private void onPotentialFlagInteract(PlayerEvent event) {
                WarlordsPlayer wp = Warlords.getPlayer(event.getPlayer());
                if (wp != null && wp.getGame() == game) {
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
                    checkFlagInteract(playerLocation, wp, from, to, renderer);
                }
            }

            private void checkFlagInteract(Location playerLocation, WarlordsPlayer wp, Vec3D from, Vec3D to, FlagRenderer render) {
                Location entityLoc = new Location(playerLocation.getWorld(), 0, 0, 0);
                for(Entity stand : render.getRenderedArmorStands()) {
                    stand.getLocation(entityLoc);
                    if (entityLoc.getWorld() == playerLocation.getWorld() && entityLoc.distanceSquared(playerLocation) < 5 * 5) {
                        AxisAlignedBB aabb = new AxisAlignedBB(
                                entityLoc.getX() - 0.5,
                                entityLoc.getY(),
                                entityLoc.getZ() - 0.5,
                                entityLoc.getX() + 0.5,
                                entityLoc.getY() + 2,
                                entityLoc.getZ() + 0.5
                        );
                        MovingObjectPosition mop = aabb.a(from, to);
                        if(mop != null) {
                            onFlagInteract(wp);
                            break;
                        }
                    }
                }
            }

            private void onFlagInteract(WarlordsPlayer wp) {
                Team team = wp.getTeam();
                if (wp.isDead()) {
                    return;
                }

                if (wp.getFlagPickCooldown() != 0) {
                    wp.sendMessage("§cYou cannot pick up the flag yet!");
                    return;
                }

                if (renderer.getLastFlagState() != info.getFlag()) {
                    // Prevent the player from interacting when the render state is outdated
                    return;
                }

                wp.setFlagDropCooldown(2);

                if (info.getFlag() instanceof GroundFlagLocation) {
                    GroundFlagLocation groundFlagLocation = (GroundFlagLocation) info.getFlag();
                    if (team == info.getTeam()) {
                        // Return flag
                        info.setFlag(new SpawnFlagLocation(info.getSpawnLocation(), wp));
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
                        wp.sendMessage("§cYou cannot steal your own team's flag!");
                    } else {
                        // Steal flag
                        info.setFlag(new PlayerFlagLocation(wp, 0));
                        wp.getCooldownManager().addCooldown(new RegularCooldown<FlagSpawnPointOption>(
                                "Flag Damage Resistance",
                                "RES",
                                FlagSpawnPointOption.class,
                                null,
                                wp,
                                CooldownTypes.BUFF,
                                cooldownManager -> {},
                                15 * 20
                        ) {
                            @Override
                            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                return currentDamageValue * .9f;
                            }
                        });
                    }
                }
            }
        });
    }

    private boolean flagIsInCaptureZone(PlayerFlagLocation playerFlagLocation) {
        for (FlagCaptureMarker flag : game.getMarkers(FlagCaptureMarker.class)) {
            if (flag.shouldCountAsCapture(playerFlagLocation)) {
                return true;
            }
        }
        return false;
    }

    private boolean flagCaptureIsNotBlocked(PlayerFlagLocation playerFlagLocation) {
        for (FlagCaptureInhibitMarker blocker : game.getMarkers(FlagCaptureInhibitMarker.class)) {
            if (blocker.isInhibitingFlagCapture(playerFlagLocation)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void start(Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                if (!(info.getFlag() instanceof PlayerFlagLocation)) {
                    return;
                }
                PlayerFlagLocation playerFlagLocation = (PlayerFlagLocation) info.getFlag();
                if (flagIsInCaptureZone(playerFlagLocation) && !flagCaptureIsNotBlocked(playerFlagLocation)) {
                    FlagHolder.update(game, info -> new WaitingFlagLocation(
                            info.getSpawnLocation(),
                            info.getFlag() == playerFlagLocation ? playerFlagLocation.getPlayer() : null
                    ));
                }
            }
        }.runTaskTimer(0, 4);
        new GameRunnable(game) {
            @Override
            public void run() {
                FlagLocation newFlag = info.getFlag().update(info);
                if (newFlag != null) {
                    info.setFlag(newFlag);
                }
                renderer.checkRender();
            }
        }.runTaskTimer(0, 1);
    }

	@Override
	public void onGameCleanup(Game game) {
		this.renderer.reset();
	}

    public FlagInfo getInfo() {
        return info;
    }

    public FlagRenderer getRenderer() {
        return renderer;
    }

    private void onFlagUpdate(FlagInfo info, FlagLocation old) {
        scoreboard.markChanged();
        Bukkit.getPluginManager().callEvent(new WarlordsFlagUpdatedEvent(game, info, old));
        renderer.checkRender();
    }

}
