package com.ebicep.warlords.game.option.pvp.siege;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.cuboid.GateOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.util.java.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nonnull;
import java.util.Objects;

public class SiegeWaitState implements SiegeState, TimerSkipAbleMarker {

    private final SiegeOption siegeOption;
    private Game game;
    private boolean skipped = false;

    public SiegeWaitState(SiegeOption siegeOption) {
        this.siegeOption = siegeOption;
    }

    @Override
    public void start(@Nonnull Game game) {
        this.game = game;
        game.getOptions().forEach(option -> {
            if (option instanceof GateOption gateOption) {
                gateOption.closeGates();
            }
        });
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            warlordsPlayer.teleport(Objects.requireNonNull(LobbyLocationMarker.getFirstLobbyLocation(game, warlordsPlayer.getTeam())).getLocation());
        });
    }

    @Override
    public boolean tick(int ticksElapsed) {
        return ticksElapsed >= maxSeconds() * 20 || skipped;
    }

    @Override
    public void end() {
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
        return 30;
    }

    @Override
    public void skipTimer(int delayInTicks) {
        skipped = true;
    }
}
