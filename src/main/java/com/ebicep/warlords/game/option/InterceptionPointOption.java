package com.ebicep.warlords.game.option;

import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.WarlordsIntersectionCaptureEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.CompassTargetMarker;
import com.ebicep.warlords.game.option.marker.DebugLocationMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InterceptionPointOption implements Option {

    private static final ItemStack NEUTRAL_ITEM_STACK = new ItemStack(Material.WOOL);
	public static final double DEFAULT_MIN_CAPTURE_RADIUS = 3;
	public static final double DEFAULT_MAX_CAPTURE_RADIUS = 6;
	public static final double DEFAULT_CAPTURE_SPEED = 0.01;
	private Game game;
	@Nonnull
	private String name;
	private final Location location;
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
    private SimpleScoreboardHandler scoreboard;
    private ArmorStand[] middle = new ArmorStand[4];
    @Nullable
    private CircleEffect effectPlayer;

	public InterceptionPointOption(String name, Location location) {
		this(name, location, DEFAULT_MIN_CAPTURE_RADIUS, DEFAULT_MAX_CAPTURE_RADIUS, DEFAULT_CAPTURE_SPEED);
	}

	public InterceptionPointOption(String name, Location location, @Nonnegative double minCaptureRadius) {
		this(name, location, minCaptureRadius, minCaptureRadius * 2, DEFAULT_CAPTURE_SPEED);
	}

	public InterceptionPointOption(String name, Location location, @Nonnegative double minCaptureRadius, @Nonnegative double maxCaptureRadius, @Nonnegative double captureSpeed) {
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
				return (int) player.getDeathLocation().distanceSquared(location) / -100;
			}

			@Override
			public String getToolbarName(WarlordsPlayer player) {
				StringBuilder status = new StringBuilder();
				if (teamAttacking == null) {
					status.append(ChatColor.GRAY);
				} else {
					status.append(teamAttacking.teamColor());
				}
				status.append(name);
				status.append(" ");

				if (teamInCircle == null) {
					status.append(ChatColor.GRAY);
				} else {
					status.append(teamInCircle.teamColor());
				}
				status.append((int)Math.floor(captureProgress * 100)).append("%");
				status.append(ChatColor.WHITE).append(" - ");

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
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(19, "interception") {
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {
				StringBuilder status = new StringBuilder();
                if (teamAttacking == null) {
					status.append(ChatColor.GRAY);
				} else {
					status.append(teamAttacking.teamColor());
				}
				status.append(name);
				status.append(ChatColor.WHITE);
				status.append(": ");

				if (teamInCircle == null) {
					status.append(ChatColor.GRAY);
				} else {
					status.append(teamInCircle.teamColor());
				}
				status.append((int)Math.floor(captureProgress * 100)).append("%");
                return Collections.singletonList(status.toString());
            }
        });
	}
    
    private ItemStack getItem(Team team) {
        return team == null ? NEUTRAL_ITEM_STACK : team.getItem();
    }
    
    private void updateArmorstandsAndEffect(ScoreboardHandler handler) {
        Location clone = this.location.clone();
        clone.add(0, -1.7, 0);
        for (int i = middle.length - 1; i >= 0; i--) {
            clone.add(0, this.captureProgress * 1 + 0.25, 0);
            middle[i].teleport(clone);
            ItemStack item = getItem(i == 0 ? this.inConflict ? null : this.teamAttacking : this.teamOwning);
            if (!item.equals(middle[i].getHelmet())) {
                middle[i].setHelmet(item);
            }
        }
        double computedCurrentRadius = this.computeCurrentRadius();
        if (this.effectPlayer == null || this.effectPlayer.getTeam() != teamOwning) {
            this.effectPlayer = new CircleEffect(game, teamOwning, location, computedCurrentRadius);
            this.effectPlayer.addEffect(new CircumferenceEffect(ParticleEffect.CRIT).particles(20));
        }
        if (this.effectPlayer.getRadius() != computedCurrentRadius) {
            this.effectPlayer.setRadius(computedCurrentRadius);
        }
    }

	@Override
	public void start(Game game) {
        Location clone = this.location.clone();
        clone.add(0, -1.7, 0);
        for (int i = middle.length - 1; i >= 0; i--) {
            clone.add(0, this.captureProgress * 1 + 0.25, 0);
            middle[i] = location.getWorld().spawn(clone, ArmorStand.class);
            middle[i].setGravity(false);
            middle[i].setBasePlate(false);
            middle[i].setArms(false);
            middle[i].setVisible(false);
        }
        updateArmorstandsAndEffect(null);
        scoreboard.registerChangeHandler(this::updateArmorstandsAndEffect);
		new GameRunnable(game) {
			@Override
			public void run() {
				Stream<WarlordsPlayer> computePlayers = computePlayers();
				double speed = updateTeamInCircle(computePlayers);
				updateTeamHackProcess(speed);
                if (effectPlayer != null) {
                    effectPlayer.playEffects();
                }
			}
		}.runTaskTimer(1, 1);
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
			return captureSpeed * 0.2;
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
		return Math.max((currentTeamPresence - 1) * captureSpeed, captureSpeed);
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
                Team previousOwning = teamOwning;
				teamOwning = null;
				teamAttacking = teamInCircle;
				Bukkit.getPluginManager().callEvent(new WarlordsIntersectionCaptureEvent(this));
                if (previousOwning != null) {
                    WarlordsPlayer capturer = computePlayers().filter(wp -> wp.getTeam() == teamInCircle).collect(Utils.randomElement());
                    String message = teamAttacking.teamColor() + (capturer == null ? "???" : capturer.getName()) + " §eis capturing the " + ChatColor.GRAY + name + ChatColor.WHITE + "!";

                    game.forEachOnlinePlayer((p, t) -> {
                        p.sendMessage(message);
                        PacketUtils.sendTitle(p, "", message, 0, 60, 0);
                        if (t != null) {
                            if (t != teamOwning) {
                                p.playSound(location, "ctf.friendlyflagtaken", 500, 1);
                            } else {
                                p.playSound(location, "ctf.enemyflagtaken", 500, 1);
                            }
                        }
                    });
                }
			}
            scoreboard.markChanged();
		} else if (teamInCircle != null) {
			if (captureProgress < 1) {
				captureProgress += hackSpeed;
				if (captureProgress > 1) {
					captureProgress = 1;
					if (teamAttacking != teamOwning) {
						teamOwning = teamAttacking;
						Bukkit.getPluginManager().callEvent(new WarlordsIntersectionCaptureEvent(this));
                        WarlordsPlayer capturer = computePlayers().filter(wp -> wp.getTeam() == teamOwning).collect(Utils.randomElement());
                        String message = teamOwning.teamColor() + (capturer == null ? "???" : capturer.getName()) + " §ehas captured the " + teamOwning.teamColor() + name + ChatColor.WHITE + "!";
                        
                        game.forEachOnlinePlayer((p, t) -> {
                            p.sendMessage(message);
                            PacketUtils.sendTitle(p, "", message, 0, 60, 0);
                            if(t != null) {
                                if (t != teamOwning) {
                                    p.playSound(location, "ctf.enemycapturedtheflag", 500, 1);
                                } else {
                                    p.playSound(location, "ctf.enemyflagcaptured", 500, 1);
                                }
                            }
                        });
                    }
				}
			}
            scoreboard.markChanged();
		}
	}

}
