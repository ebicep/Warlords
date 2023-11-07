package com.ebicep.warlords.game.option.pvp.siege;

import com.ebicep.warlords.events.player.ingame.WarlordsPlayerHorseEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.cuboid.GateOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.util.java.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Objects;

public class SiegeWaitState implements SiegeState, Listener, TimerSkipAbleMarker {

    private final SiegeOption siegeOption;
    private Game game;
    private boolean skipped = false;

    public SiegeWaitState(SiegeOption siegeOption) {
        this.siegeOption = siegeOption;
    }

    @Override
    public void start(@Nonnull Game game) {
        this.game = game;
        game.registerEvents(this);
        game.getOptions().forEach(option -> {
            if (option instanceof GateOption gateOption) {
                gateOption.closeGates();
            }
        });
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            warlordsPlayer.teleport(Objects.requireNonNull(LobbyLocationMarker.getFirstLobbyLocation(game, warlordsPlayer.getTeam())).getLocation());
            warlordsPlayer.getCooldownManager().clearCooldowns();
        });
    }

    @Override
    public boolean tick(int ticksElapsed) {
        return ticksElapsed >= maxSeconds() * 20 || skipped;
    }

    @Override
    public void end() {
        HandlerList.unregisterAll(this);
        game.getOptions().forEach(option -> {
            if (option instanceof GateOption gateOption) {
                gateOption.openGates();
            }
        });
    }

    @Override
    public SiegeState getNextState() {
        return new SiegeCapturePointState(siegeOption);
    }

    @Override
    public Component getSidebarComponent(int ticksElapsed) {
        return Component.text("Time Left: ", NamedTextColor.WHITE)
                        .append(Component.text(StringUtils.formatTimeLeft(maxSeconds() - ticksElapsed / 20), NamedTextColor.GREEN));
    }

    @Override
    public int maxSeconds() {
        return 20;
    }

    @Override
    public void skipTimer(int delayInTicks) {
        skipped = true;
    }

    @EventHandler
    public void onHorse(WarlordsPlayerHorseEvent e) {
        e.setCancelled(true);
    }

}
