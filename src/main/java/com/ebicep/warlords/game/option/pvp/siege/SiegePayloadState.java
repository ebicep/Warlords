package com.ebicep.warlords.game.option.pvp.siege;

import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.marker.TimerResetAbleMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.game.option.payload.Payload;
import com.ebicep.warlords.game.option.payload.PayloadBrain;
import com.ebicep.warlords.game.option.payload.PayloadRendererCoalCart;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.java.StringUtils;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SiegePayloadState implements SiegeState, Listener, TimerSkipAbleMarker, TimerResetAbleMarker {

    private static final int OVERTIME_TICKS = 20 * 5;
    private final SiegeOption siegeOption;
    private final Team escortingTeam;
    private final Map<UUID, Map<Specializations, SiegeStats>> playerSiegeStats;
    private Game game;
    private Payload payload;
    private int transitionTickDelay = 0; // for animations/title screens
    private int ticksElapsedAtTransition = -1;
    private PayloadMoveInfo payloadInfo;
    private int noEscortTicks = 0; // if no one is escorting for 5 seconds, payload moves backwards
    private int overtimeTicksLeft = -1;
    private BossBar overtimeBossBar;
    private int bonusRespawn = 0;


    public SiegePayloadState(SiegeOption siegeOption, Team escortingTeam) {
        this.siegeOption = siegeOption;
        this.playerSiegeStats = siegeOption.getPlayerSiegeStats();
        this.escortingTeam = escortingTeam;
    }

    @Override
    public void start(@Nonnull Game game) {
        this.game = game;
        payload = new Payload(
                game,
                new PayloadBrain(siegeOption.getTeamPayloadStart().get(escortingTeam), PayloadBrain.DEFAULT_FORWARD_MOVE_PER_TICK, .0375),
                new PayloadRendererCoalCart(),
                escortingTeam
        ) {
            @Override
            public boolean tick(int ticksElapsed) {
                payloadInfo = getPayloadMove(brain.getCurrentLocation());
                if (overtimeTicksLeft > 0) {
                    if (payloadInfo.pushers() <= 0) {
                        overtimeTicksLeft--;
                        if (overtimeTicksLeft == 0) {
                            ticksElapsedAtTransition = ticksElapsed;
                            onPayloadDefended();
                            return false;
                        }
                    } else {
                        overtimeTicksLeft = OVERTIME_TICKS;
                    }
                }
                double payloadMove = payloadInfo.payloadMove();
                if (payloadMove != 0) {
                    boolean reachedEnd = brain.tick(payloadMove);
                    if (reachedEnd) {
                        return true;
                    }
                }
                renderEffects(ticksElapsed);
                showBossBar(payloadInfo.netEscorting());
                return false;
            }

            // returns net escorting + to move per tick
            private PayloadMoveInfo getPayloadMove(Location oldLocation) {
                int escorting = 0;
                int escortingBatteries = 0;
                int nonEscorting = 0;
                int nonEscortingBatteries = 0;
                for (WarlordsEntity warlordsEntity : PlayerFilterGeneric
                        .entitiesAround(oldLocation, MOVE_RADIUS, MOVE_RADIUS, MOVE_RADIUS)
                        .isAlive()
                ) {
                    if (warlordsEntity.getTeam() == escortingTeam) {
                        escorting++;
                        if (warlordsEntity.getCooldownManager().hasCooldownFromName("Payload Battery")) {
                            escortingBatteries++;
                        }
                        playerSiegeStats.computeIfAbsent(warlordsEntity.getUuid(), uuid -> new HashMap<>())
                                        .computeIfAbsent(warlordsEntity.getSpecClass(), specializations -> new SiegeStats())
                                        .addTimeOnPayloadEscorting();
                    } else {
                        nonEscorting++;
//                        if (warlordsEntity.getCooldownManager().hasCooldownFromName("Payload Battery")) {
//                            nonEscortingBatteries++;
//                        }
                        playerSiegeStats.computeIfAbsent(warlordsEntity.getUuid(), uuid -> new HashMap<>())
                                        .computeIfAbsent(warlordsEntity.getSpecClass(), specializations -> new SiegeStats())
                                        .addTimeOnPayloadDefending();
                    }
                }
                int netEscorting = escorting - nonEscorting;
                int netEscortBatteries = escortingBatteries - nonEscortingBatteries;
                // contested
                if (escorting != 0 && nonEscorting != 0) {
                    noEscortTicks = 0;
                    contested = true;
                    return new PayloadMoveInfo(escorting, nonEscorting, netEscorting, 0.0);
                }
                contested = false;
                if (escorting > 0) {
                    noEscortTicks = 0;
                    double moveMultiplier = netEscortBatteries > 0 ? 1.5 : 1;
                    moveMultiplier += overtimeTicksLeft > 0 ? .2 : 0;
                    return new PayloadMoveInfo(escorting, nonEscorting, netEscorting, brain.getForwardMovePerTick() * moveMultiplier);
                }
                noEscortTicks++;
                if (noEscortTicks < 5 * 20) {
                    return new PayloadMoveInfo(escorting, nonEscorting, netEscorting, 0.0);
                }
                // move backwards, every defender adds 10% to the speed
                double backwardMovePerTick = brain.getBackwardMovePerTick() * (1 + nonEscorting * .1);
                return new PayloadMoveInfo(escorting, nonEscorting, netEscorting, -backwardMovePerTick);
            }
        };
        this.payload.getRenderer().addRenderPathRunnable(game, payload.getBrain().getStart(), payload.getBrain().getPath());

        game.registerEvents(this);
    }

    @Override
    public boolean tick(int ticksElapsed) {
        if (transitionTickDelay > 0) {
            transitionTickDelay--;
            return transitionTickDelay == 0;
        }
        if (ticksElapsed < 20) {
            payload.renderEffects(ticksElapsed);
            return false;
        }
        if (overtimeTicksLeft == -1 && ticksElapsed / 20 >= maxSeconds()) {
            if (payloadInfo.pushers() > 0) {
                overtimeBossBar = BossBar.bossBar(
                        Component.text("OVERTIME!", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD),
                        1,
                        BossBar.Color.PURPLE,
                        BossBar.Overlay.PROGRESS
                );
                game.forEachOnlinePlayer((player, team) -> {
                    player.showTitle(Title.title(
                            Component.text("OVERTIME!", NamedTextColor.LIGHT_PURPLE),
                            Component.empty(),
                            Title.Times.times(Ticks.duration(0), Ticks.duration(60), Ticks.duration(0))
                    ));
                    player.sendMessage(Component.text("Overtime is now active! The payload will now move 20% faster!", NamedTextColor.LIGHT_PURPLE));
                    player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1, 1);
                });
                overtimeTicksLeft = OVERTIME_TICKS;
                return false;
            }
            ticksElapsedAtTransition = ticksElapsed;
            onPayloadDefended();
            return false;
        }
        if (overtimeTicksLeft > 0) {
            overtimeBossBar.progress(MathUtils.lerp(0, 1, (float) overtimeTicksLeft / OVERTIME_TICKS));
            game.forEachOnlinePlayer((player, team) -> player.showBossBar(overtimeBossBar));
        }
        boolean captured = payload.tick(ticksElapsed);
        if (captured) {
            ticksElapsedAtTransition = ticksElapsed;
            onPayloadCapture();
        }
        return false;
    }

    @Override
    public void end() {
        payload.cleanup();
        HandlerList.unregisterAll(this);
        if (overtimeBossBar != null) {
            game.forEachOnlinePlayer((player, team) -> player.hideBossBar(overtimeBossBar));
        }
    }

    @Override
    public SiegeState getNextState() {
        return new SiegeWaitState(siegeOption);
    }

    @Override
    public Component getSidebarComponent(int ticksElapsed) {
        int seconds = Math.max(maxSeconds() - (ticksElapsedAtTransition != -1 ? ticksElapsedAtTransition : ticksElapsed) / 20, 0);
        return Component.text("Time Left: ", NamedTextColor.WHITE)
                        .append(Component.text(StringUtils.formatTimeLeft(seconds), NamedTextColor.GREEN));
    }

    @Override
    public int maxSeconds() {
        return 4 * 60;
    }

    private void onPayloadCapture() {
        transitionTickDelay = 80;
        game.addPoints(escortingTeam, 1);
        game.forEachOnlinePlayer((player, team) -> {
            boolean isCapturedTeam = team == escortingTeam;
            player.showTitle(Title.title(
                    Component.text(isCapturedTeam ? "OBJECTIVE CAPTURED" : "OBJECTIVE LOST", isCapturedTeam ? NamedTextColor.GREEN : NamedTextColor.RED, TextDecoration.BOLD),
                    Component.text("Payload Captured!", NamedTextColor.GRAY),
                    Title.Times.times(Ticks.duration(0), Ticks.duration(60), Ticks.duration(20))
            ));
        });
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            boolean isCapturedTeam = warlordsPlayer.getTeam() == escortingTeam;
            if (isCapturedTeam) {
                playerSiegeStats.computeIfAbsent(warlordsPlayer.getUuid(), uuid -> new HashMap<>())
                                .computeIfAbsent(warlordsPlayer.getSpecClass(), specializations -> new SiegeStats())
                                .addPayloadsEscorted();
            } else {
                playerSiegeStats.computeIfAbsent(warlordsPlayer.getUuid(), uuid -> new HashMap<>())
                                .computeIfAbsent(warlordsPlayer.getSpecClass(), specializations -> new SiegeStats())
                                .addPayloadsDefendedFail();
            }
        });
    }

    private void onPayloadDefended() {
        transitionTickDelay = 80;
        EnumSet<Team> defendingTeams = EnumSet.copyOf(TeamMarker.getTeams(game));
        defendingTeams.remove(escortingTeam);
        defendingTeams.forEach(team -> game.addPoints(team, 1));
        game.forEachOnlinePlayer((player, team) -> {
            boolean isNotEscortingTeam = team != escortingTeam;
            player.showTitle(Title.title(
                    Component.text(isNotEscortingTeam ? "OBJECTIVE DEFENDED" : "PUSH FAILED", isNotEscortingTeam ? NamedTextColor.GREEN : NamedTextColor.RED, TextDecoration.BOLD),
                    Component.text("Times Up!", NamedTextColor.GRAY),
                    Title.Times.times(Ticks.duration(0), Ticks.duration(60), Ticks.duration(20))
            ));
        });
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            boolean isNotEscortingTeam = warlordsPlayer.getTeam() != escortingTeam;
            if (isNotEscortingTeam) {
                playerSiegeStats.computeIfAbsent(warlordsPlayer.getUuid(), uuid -> new HashMap<>())
                                .computeIfAbsent(warlordsPlayer.getSpecClass(), specializations -> new SiegeStats())
                                .addPayloadsDefended();
            } else {
                playerSiegeStats.computeIfAbsent(warlordsPlayer.getUuid(), uuid -> new HashMap<>())
                                .computeIfAbsent(warlordsPlayer.getSpecClass(), specializations -> new SiegeStats())
                                .addPayloadsEscortedFail();
            }
        });
    }

    @EventHandler
    public void onDamageHeal(WarlordsDamageHealingEvent event) {
        if (transitionTickDelay > 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAbilityActivate(WarlordsAbilityActivateEvent.Pre event) {
        if (transitionTickDelay > 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRespawnGive(WarlordsGiveRespawnEvent event) {
        Team team = event.getWarlordsEntity().getTeam();
        event.getRespawnTimer().set((team == escortingTeam ? 14 : 18) + bonusRespawn);
    }

    @Override
    public void skipTimer(int delayInTicks) {
        PayloadBrain payloadBrain = payload.getBrain();
        payloadBrain.tick(PayloadBrain.DEFAULT_FORWARD_MOVE_PER_TICK * 15);
    }

    @Override
    public void reset() {
        if (payload == null) {
            return;
        }
        payload.getBrain().setMappedPathIndex(0);
        payload.getBrain().reset();
    }

    public Payload getPayload() {
        return payload;
    }

    private record PayloadMoveInfo(int pushers, int defenders, int netEscorting, double payloadMove) {
    }
}
