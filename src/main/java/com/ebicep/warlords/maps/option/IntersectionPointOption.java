package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.events.WarlordsIntersectionCaptureEvent;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.option.marker.CompassTargetMarker;
import com.ebicep.warlords.maps.option.marker.DebugLocationMarker;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.PlayerFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

public class IntersectionPointOption implements Option {

	public static final double DEFAULT_MIN_CAPTURE_RADIUS = 3;
	public static final double DEFAULT_MAX_CAPTURE_RADIUS = 6;
	public static final double DEFAULT_CAPTURE_SPEED = 0.01;
	private Game game;
	@Nonnull
	private String name;
	private Location location;
	@Nonnegative
	private double minCaptureRadius;
	@Nonnegative
	private double maxCaptureRadius;
	private Team teamOwning = null;
	private Team teamAttacking = null;
	private Team teamInCircle = null;
	@Nonnegative
	private double captureProgress = 0;
	private boolean inConflict = false;
	private double captureSpeed;

	public IntersectionPointOption(String name, Location location) {
		this(name, location, DEFAULT_MIN_CAPTURE_RADIUS, DEFAULT_MAX_CAPTURE_RADIUS, DEFAULT_CAPTURE_SPEED);
	}

	public IntersectionPointOption(String name, Location location, @Nonnegative double minCaptureRadius) {
		this(name, location, minCaptureRadius, minCaptureRadius * 2, DEFAULT_CAPTURE_SPEED);
	}

	public IntersectionPointOption(String name, Location location, @Nonnegative double minCaptureRadius, @Nonnegative double maxCaptureRadius, @Nonnegative double captureSpeed) {
		this.name = name;
		this.location = location;
		this.maxCaptureRadius = maxCaptureRadius;
		this.minCaptureRadius = minCaptureRadius;
		this.captureSpeed = captureSpeed;
	}

	@Override
	public void register(Game game) {
		this.game = game;
		game.registerGameMarker(CompassTargetMarker.class, new CompassTargetMarker() {
			@Override
			public int getCompassTargetPriority(WarlordsPlayer player) {
				return (int) player.getDeathLocation().distanceSquared(location) / 100;
			}

			@Override
			public String getToolbarName(WarlordsPlayer player) {
				StringBuilder status = new StringBuilder();
				if (teamAttacking == null) {
					status.append(ChatColor.WHITE);
				} else {
					status.append(teamAttacking.teamColor());
				}
				status.append(name);
				status.append(" ");

				if (teamInCircle == null) {
					status.append(ChatColor.WHITE);
				} else {
					status.append(teamInCircle.teamColor());
				}
				status.append(Math.floor(captureProgress * 100));
				status.append(ChatColor.WHITE).append(": ");

				if (inConflict) {
					status.append(ChatColor.GOLD).append("In conflict");
				} else if (teamOwning != player.getTeam()) {
					if (teamInCircle != player.getTeam()) {
						status.append(ChatColor.RED).append("Not under your control!");
					} else {
						status.append(ChatColor.AQUA).append("Your team is capturing this");
					}
				} else {
					if (teamInCircle != player.getTeam()) {
						status.append(ChatColor.RED).append("Point is under attack!");
					} else {
						status.append(ChatColor.GREEN).append("Safe");
					}
				}
				return status.toString();
			}

			@Override
			public Location getLocation() {
				return location;
			}
		});
		game.registerGameMarker(DebugLocationMarker.class, DebugLocationMarker.create(Material.TORCH, 0, this.getClass(), "Capture point: " + name, () -> location, () -> Arrays.asList(
				"inConflict: " + inConflict,
				"teamOwning: " + teamOwning,
				"teamAttacking: " + teamAttacking,
				"teamInCircle: " + teamInCircle,
				"minCaptureRadius: " + minCaptureRadius,
				"maxCaptureRadius: " + maxCaptureRadius,
				"radius: " + computeCurrentRadius()
		)));
	}

	@Override
	public void start(Game game) {
		new GameRunnable(game) {
			@Override
			public void run() {
				Stream<WarlordsPlayer> computePlayers = computePlayers();
				double speed = updateTeamInCircle(computePlayers);
				updateTeamHackProcess(speed);
			}
		}.runTaskTimer(4, 5);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public double getMinCaptureRadius() {
		return minCaptureRadius;
	}

	public void setMinCaptureRadius(double minCaptureRadius) {
		this.minCaptureRadius = minCaptureRadius;
	}

	public double getMaxCaptureRadius() {
		return maxCaptureRadius;
	}

	public void setMaxCaptureRadius(double maxCaptureRadius) {
		this.maxCaptureRadius = maxCaptureRadius;
	}

	public double getCaptureSpeed() {
		return captureSpeed;
	}

	public void setCaptureSpeed(double captureSpeed) {
		this.captureSpeed = captureSpeed;
	}

	public Team getTeamOwning() {
		return teamOwning;
	}

	public Team getTeamAttacking() {
		return teamAttacking;
	}

	public Team getTeamInCircle() {
		return teamInCircle;
	}

	public boolean isInConflict() {
		return inConflict;
	}

	public Game getGame() {
		return game;
	}

	protected double computeCurrentRadius() {
		return minCaptureRadius + captureProgress * (maxCaptureRadius - minCaptureRadius);
	}

	protected Stream<WarlordsPlayer> computePlayers() {
		double radius = computeCurrentRadius();
		return PlayerFilter.entitiesAround(location, radius, radius, radius).stream().filter(wp -> wp.getGame() == game && wp.isAlive());
	}

	protected double updateTeamInCircle(Stream<WarlordsPlayer> players) {
		Map<Team, List<WarlordsPlayer>> perTeam = players.collect(Collectors.groupingBy(WarlordsPlayer::getTeam, Collectors.toList()));
		if (perTeam.isEmpty()) {
			teamInCircle = teamOwning;
			inConflict = false;
			return captureSpeed * 0.05;
		}
		Map.Entry<Team, List<WarlordsPlayer>> highest = perTeam.entrySet().stream().sorted(Comparator.comparing((Map.Entry<Team, List<WarlordsPlayer>> e) -> e.getValue().size()).reversed()).findFirst().get();
		int highestValue = highest.getValue().size();
		int otherTeamPresence = perTeam.values().stream().mapToInt(Collection::size).sum() - highestValue;
		int currentTeamPresence = highestValue - otherTeamPresence;
		// Calculate who is in the zone
		// If there are no other players from outside your team, yu ca always capture the point, else you need 2 peopl more
		if (currentTeamPresence > 1 || otherTeamPresence == 0) {
			teamInCircle = highest.getKey();
			inConflict = false;
		} else {
			teamInCircle = null;
			inConflict = true;
		}
		return (currentTeamPresence - 1) * captureSpeed;
	}

	protected void updateTeamHackProcess(double hackSpeed) {
		// Update the progress
		if (inConflict || hackSpeed <= 0) {
			return;
		}
		if (teamAttacking != teamInCircle) {
			captureProgress -= hackSpeed;
			if (captureProgress < 0) {
				captureProgress = 0;
				teamOwning = null;
				teamAttacking = teamInCircle;
				Bukkit.getPluginManager().callEvent(new WarlordsIntersectionCaptureEvent(this));
			}
		} else if (teamInCircle != null) {
			teamAttacking = teamInCircle;
			if (captureProgress < 1) {
				captureProgress += hackSpeed;
				if (captureProgress > 1) {
					captureProgress = 1;
					if (teamAttacking != teamOwning) {
						teamOwning = teamAttacking;
						Bukkit.getPluginManager().callEvent(new WarlordsIntersectionCaptureEvent(this));
					}
				}
			}
		}
	}

}
