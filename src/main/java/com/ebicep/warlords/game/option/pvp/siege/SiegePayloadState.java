package com.ebicep.warlords.game.option.pvp.siege;

import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.game.option.payload.Payload;
import com.ebicep.warlords.game.option.payload.PayloadBrain;
import com.ebicep.warlords.game.option.payload.PayloadRendererCoalCart;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.StringUtils;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public class SiegePayloadState implements SiegeState, Listener, TimerSkipAbleMarker {

    private final SiegeOption siegeOption;
    private final Team escortingTeam;
    private Game game;
    private Payload payload;
    private int transitionTickDelay = 0; // for animations/title screens
    private int ticksElapsedAtTransition = -1;

    public SiegePayloadState(SiegeOption siegeOption, Team escortingTeam) {
        this.siegeOption = siegeOption;
        this.escortingTeam = escortingTeam;
    }

    @Override
    public void start(@Nonnull Game game) {
        this.game = game;
        payload = new Payload(
                game,
                new PayloadBrain(siegeOption.getTeamPayloadStart().get(escortingTeam), PayloadBrain.DEFAULT_FORWARD_MOVE_PER_TICK, .05),
                new PayloadRendererCoalCart(),
                escortingTeam
        ) {
            @Override
            public boolean tick(int ticksElapsed) {
                Pair<Integer, Double> payloadInfo = getPayloadMove(brain.getCurrentLocation());
                int netEscorting = payloadInfo.getA();
                double payloadMove = payloadInfo.getB();
                if (payloadMove != 0) {
                    boolean reachedEnd = brain.tick(payloadMove);
                    if (reachedEnd) {
                        return true;
                    }
                }
                renderEffects(ticksElapsed);
                showBossBar(netEscorting);
                return false;
            }

            // returns net escorting + to move per tick
            private Pair<Integer, Double> getPayloadMove(Location oldLocation) {
                int escorting = 0;
                int escortingBatteries = 0;
                int nonEscorting = 0;
                int nonEscortingBatteries = 0;
                for (WarlordsEntity warlordsEntity : PlayerFilterGeneric
                        .entitiesAround(oldLocation, MOVE_RADIUS, MOVE_RADIUS, MOVE_RADIUS)
                ) {
                    if (warlordsEntity.getTeam() == escortingTeam) {
                        escorting++;
                        if (warlordsEntity.getCooldownManager().hasCooldownFromName("Payload Battery")) {
                            escortingBatteries++;
                        }
                    } else {
                        nonEscorting++;
                        if (warlordsEntity.getCooldownManager().hasCooldownFromName("Payload Battery")) {
                            nonEscortingBatteries++;
                        }
                    }
                }
                int netEscorting = escorting - nonEscorting;
                int netEscortBatteries = escortingBatteries - nonEscortingBatteries;
                // contested
                if (netEscorting == 0) {
                    if (escortingBatteries == nonEscortingBatteries) {
                        return new Pair<>(netEscorting, 0.0);
                    }
                    return new Pair<>(netEscorting, netEscortBatteries * PayloadBrain.DEFAULT_FORWARD_MOVE_PER_TICK / 2);
                }
                if (escorting > nonEscorting) {
                    return new Pair<>(netEscorting, PayloadBrain.DEFAULT_FORWARD_MOVE_PER_TICK * (netEscortBatteries > 0 ? 1.5 : 1));
                }
                if (nonEscorting > escorting) {
                    return new Pair<>(netEscorting, -brain.getBackwardMovePerTick() * (netEscortBatteries > 0 ? 1.5 : 1));
                }
                return new Pair<>(netEscorting, 0.0);
            }
        };
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
        if (ticksElapsed / 20 >= maxSeconds()) {
            ticksElapsedAtTransition = ticksElapsed;
            onPayloadDefended();
            return false;
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
        return 20;//3 * 60;
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
    }

    @EventHandler
    public void onDamageHeal(WarlordsDamageHealingEvent event) {
        if (transitionTickDelay > 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAbilityActivate(WarlordsAbilityActivateEvent event) {
        if (transitionTickDelay > 0) {
            //event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRespawnGive(WarlordsGiveRespawnEvent event) {
        Team team = event.getWarlordsEntity().getTeam();
        event.getRespawnTimer().set(team == escortingTeam ? 8 : 12);
    }

    @Override
    public void skipTimer(int delayInTicks) {
        PayloadBrain payloadBrain = payload.getBrain();
        payloadBrain.tick(PayloadBrain.DEFAULT_FORWARD_MOVE_PER_TICK * 15);
    }
}
