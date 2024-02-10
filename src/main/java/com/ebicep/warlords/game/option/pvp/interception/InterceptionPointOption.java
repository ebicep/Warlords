package com.ebicep.warlords.game.option.pvp.interception;

import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.game.WarlordsIntersectionCaptureEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.CompassTargetMarker;
import com.ebicep.warlords.game.option.marker.DebugLocationMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InterceptionPointOption implements Option {

    public static final double DEFAULT_MIN_CAPTURE_RADIUS = 3.5;
    public static final double DEFAULT_MAX_CAPTURE_RADIUS = 5;
    public static final double DEFAULT_CAPTURE_SPEED = 0.01;
    private static final Material NEUTRAL_MATERIAL = Material.WHITE_WOOL;
    private final Location location;
    private Game game;
    @Nonnull
    private String name;
    @Nonnegative
    private double minCaptureRadius;
    @Nonnegative
    private double maxCaptureRadius;
    private Team teamOwning = null;
    private Team teamAttacking = null;
    private Team teamInCircle = null;
    @Nonnegative
    private double captureProgress = 0;
    private boolean inConflict = false; // if multiple teams on point at once
    private double captureSpeed;
    private SimpleScoreboardHandler scoreboard;
    private BlockDisplay[] woolDisplay = new BlockDisplay[4];
    @Nullable
    private CircleEffect effectPlayer;
    private Block glassBlock;


    public InterceptionPointOption(String name, Location location) {
        this(name, location, DEFAULT_MIN_CAPTURE_RADIUS, DEFAULT_MAX_CAPTURE_RADIUS, DEFAULT_CAPTURE_SPEED);
    }

    public InterceptionPointOption(
            @Nonnull String name,
            Location location,
            @Nonnegative double minCaptureRadius,
            @Nonnegative double maxCaptureRadius,
            @Nonnegative double captureSpeed
    ) {
        this.name = name;
        this.location = location;
        this.glassBlock = location.clone().subtract(0, .5, 0).getBlock();
        this.maxCaptureRadius = maxCaptureRadius;
        this.minCaptureRadius = minCaptureRadius;
        this.captureSpeed = captureSpeed;
    }

    public InterceptionPointOption(String name, Location location, @Nonnegative double minCaptureRadius) {
        this(name, location, minCaptureRadius, minCaptureRadius * 2, DEFAULT_CAPTURE_SPEED);
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        game.getPreviousBlocks().putIfAbsent(new LocationUtils.LocationBlockHolder(glassBlock.getLocation()), glassBlock.getType());
        game.registerGameMarker(CompassTargetMarker.class, new CompassTargetMarker() {
            @Override
            public int getCompassTargetPriority(WarlordsEntity player) {
                return (int) player.getDeathLocation().distanceSquared(location) / -100;
            }

            @Override
            public Component getToolbarName(WarlordsEntity player) {
                TextComponent.Builder status = Component.text();
                status.append(Component.text(name + " ", teamAttacking == null ? NamedTextColor.GRAY : teamAttacking.getTeamColor()));
                status.append(Component.text((int) Math.floor(captureProgress * 100) + "%", teamInCircle == null ? NamedTextColor.GRAY : teamInCircle.getTeamColor()));
                status.append(Component.text(" - ", NamedTextColor.WHITE));
                if (inConflict) {
                    status.append(Component.text("In conflict", NamedTextColor.GOLD));
                } else if (teamOwning != player.getTeam()) {
                    if (teamInCircle != player.getTeam()) {
                        status.append(Component.text("Not under your control!", NamedTextColor.RED));
                    } else {
                        status.append(Component.text("Your team is capturing this", NamedTextColor.AQUA));
                    }
                } else {
                    if (teamInCircle != player.getTeam()) {
                        status.append(Component.text("Point is under attack!", NamedTextColor.RED));
                    } else {
                        status.append(Component.text("Safe", NamedTextColor.GREEN));
                    }
                }
                return status.build();
            }

            @Override
            public Location getLocation() {
                return location;
            }
        });
        game.registerGameMarker(DebugLocationMarker.class,
                DebugLocationMarker.create(Material.TORCH, 0, this.getClass(), Component.text("Capture point: " + name), () -> location, () -> Arrays.asList(
                        Component.text("inConflict: " + inConflict),
                        Component.text("teamOwning: " + teamOwning),
                        Component.text("teamAttacking: " + teamAttacking),
                        Component.text("teamInCircle: " + teamInCircle),
                        Component.text("minCaptureRadius: " + minCaptureRadius),
                        Component.text("maxCaptureRadius: " + maxCaptureRadius),
                        Component.text("radius: " + computeCurrentRadius())
                ))
        );
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(19, "interception") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                TextComponent.Builder component = Component.text();
                component.append(Component.text(
                        name + ": ",
                        NamedTextColor.GOLD
                ));
                component.append(Component.text(
                        (int) Math.floor(captureProgress * 100) + "%",
                        teamInCircle == null ? NamedTextColor.GRAY : teamInCircle.getTeamColor()
                ));
                return Collections.singletonList(component.build());
            }
        });
    }

    @Override
    public void start(@Nonnull Game game) {
        Location clone = this.location.getBlock().getLocation().clone();
        clone.add(.175, -.35, .175);
        for (int i = woolDisplay.length - 1; i >= 0; i--) {
            clone.add(0, this.captureProgress * 1 + 0.25, 0);
            woolDisplay[i] = clone.getWorld().spawn(clone, BlockDisplay.class, false, blockDisplay -> {
                blockDisplay.setBlock(Material.WHITE_WOOL.createBlockData());
                blockDisplay.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(.65f), new AxisAngle4f()));
            });
        }
        updateArmorStandsAndEffect(null);
        scoreboard.registerChangeHandler(this::updateArmorStandsAndEffect);
        new GameRunnable(game) {

            int ticksElapsed = 0;

            @Override
            public void run() {
                Stream<WarlordsEntity> computePlayers = computePlayers();
                double speed = updateTeamInCircle(computePlayers);
                updateTeamHackProcess(speed);
                if (effectPlayer != null) {
                    effectPlayer.playEffects();
                }
                Material newGlassBlock;
                if (ticksElapsed % 20 == 0) {
                    if (inConflict) {
                        newGlassBlock = Material.PURPLE_STAINED_GLASS;
                    } else if (teamInCircle != teamOwning) {
                        newGlassBlock = ticksElapsed % 40 == 0 ? teamInCircle.getGlass() : Material.WHITE_STAINED_GLASS;
                    } else {
                        newGlassBlock = teamOwning != null ? teamOwning.getGlass() : Material.WHITE_STAINED_GLASS;
                    }
                    if (newGlassBlock != glassBlock.getType()) {
                        glassBlock.setType(newGlassBlock);
                    }
                }
                ticksElapsed++;
            }
        }.runTaskTimer(1, 1);
    }

    private void updateArmorStandsAndEffect(ScoreboardHandler handler) {
        Location clone = this.location.getBlock().getLocation().clone();
        clone.add(.175, -.35, .175);
        for (int i = woolDisplay.length - 1; i >= 0; i--) {
            clone.add(0, this.captureProgress * 1 + 0.25, 0);
            woolDisplay[i].teleport(clone);
            Material material = getMaterial(i == 0 ? this.inConflict ? null : this.teamAttacking : this.teamOwning);
            if (!material.equals(woolDisplay[i].getBlock().getMaterial())) {
                woolDisplay[i].setBlock(material.createBlockData());
            }
        }
        double computedCurrentRadius = this.computeCurrentRadius();
        if (this.effectPlayer == null || this.effectPlayer.getTeam() != teamOwning) {
            this.effectPlayer = new CircleEffect(game, teamOwning, location, computedCurrentRadius);
            this.effectPlayer.addEffect(new CircumferenceEffect(Particle.CRIT).particles(20));
        }
        if (this.effectPlayer.getRadius() != computedCurrentRadius) {
            this.effectPlayer.setRadius(computedCurrentRadius);
        }
    }

    protected Stream<WarlordsEntity> computePlayers() {
        double radius = computeCurrentRadius();
        return PlayerFilter.entitiesAround(location, radius, radius, radius).stream().filter(wp -> wp.getGame() == game && wp.isAlive());
    }

    protected double updateTeamInCircle(Stream<WarlordsEntity> players) {
        Map<Team, List<WarlordsEntity>> perTeam = players.collect(Collectors.groupingBy(WarlordsEntity::getTeam, Collectors.toList()));
        if (perTeam.isEmpty()) {
            teamInCircle = teamOwning;
            inConflict = false;
            return captureSpeed * 0.2;
        }
        Map.Entry<Team, List<WarlordsEntity>> highest = perTeam.entrySet()
                                                               .stream()
                                                               .max(Comparator.comparing((Map.Entry<Team, List<WarlordsEntity>> e) -> e.getValue().size()))
                                                               .get();
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
                    WarlordsEntity capturer = computePlayers().filter(wp -> wp.getTeam() == teamInCircle).collect(Utils.randomElement());
                    Component message = Component.text(capturer == null ? "???" : capturer.getName(), teamAttacking.getTeamColor())
                                                 .append(Component.text(" is capturing the ", NamedTextColor.YELLOW))
                                                 .append(Component.text(name, NamedTextColor.GRAY))
                                                 .append(Component.text("!"));
                    game.forEachOnlinePlayer((p, t) -> {
                        p.sendMessage(message);
                        p.showTitle(Title.title(
                                Component.empty(),
                                message,
                                Title.Times.times(Ticks.duration(0), Ticks.duration(60), Ticks.duration(0))
                        ));
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
                        WarlordsEntity capturer = computePlayers().filter(wp -> wp.getTeam() == teamOwning).collect(Utils.randomElement());
                        Component message = Component.text(capturer == null ? "???" : capturer.getName(), teamOwning.getTeamColor())
                                                     .append(Component.text(" has captured ", NamedTextColor.YELLOW))
                                                     .append(Component.text(name, teamOwning.getTeamColor()))
                                                     .append(Component.text("!"));
                        game.forEachOnlinePlayer((p, t) -> {
                            p.sendMessage(message);
                            p.showTitle(Title.title(
                                    Component.empty(),
                                    message,
                                    Title.Times.times(Ticks.duration(0), Ticks.duration(60), Ticks.duration(0))
                            ));
                            if (t != null) {
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

    private Material getMaterial(Team team) {
        return team == null ? NEUTRAL_MATERIAL : team.getWool();
    }

    protected double computeCurrentRadius() {
        return minCaptureRadius + captureProgress * (maxCaptureRadius - minCaptureRadius);
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(@Nonnull String name) {
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

}
