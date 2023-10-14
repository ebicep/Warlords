package com.ebicep.warlords.game.option.pvp.siege;

import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.payload.Payload;
import com.ebicep.warlords.game.option.payload.PayloadBrain;
import com.ebicep.warlords.game.option.payload.PayloadRendererCoalCart;
import com.ebicep.warlords.util.java.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public class SiegePayloadState implements SiegeState, Listener {

    private final SiegeOption siegeOption;
    private final Team escortingTeam;
    private Game game;
    private Payload payload;
    private int transitionTickDelay = 0; // for animations/title screens

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
        );
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
        if (ticksElapsed >= maxSeconds()) {
            onPayloadDefended();
            return false;
        }
        boolean captured = payload.tick(ticksElapsed);
        if (captured) {
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
        return Component.text("Time Left: ", NamedTextColor.WHITE)
                        .append(Component.text(StringUtils.formatTimeLeft(maxSeconds() - ticksElapsed / 20), NamedTextColor.GREEN));
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
}
