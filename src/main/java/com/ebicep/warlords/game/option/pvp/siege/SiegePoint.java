package com.ebicep.warlords.game.option.pvp.siege;

import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SiegePoint implements Option {

    public static final double RADIUS = 5;
    public static final float CAPTURE_RATE = 3.5f;

    private final Location location;
    private final Map<Team, TeamCaptureData> teamCapturePercentage = new HashMap<>();
    private Game game;
    private Team capturingTeam;
    private CircleEffect circleEffect;

    public SiegePoint(Location location) {
        this.location = location;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        TeamMarker.getTeams(game).forEach(team -> teamCapturePercentage.put(team, new TeamCaptureData(team, 0, 0)));
        this.circleEffect = new CircleEffect(
                game,
                null,
                location,
                RADIUS,
                new CircumferenceEffect(Particle.CRIT).particles(20)
        );
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {

            int ticksElapsed = 0;

            @Override
            public void run() {
                if (game.isState(EndState.class)) {
                    return;
                }
                if (circleEffect != null) {
                    circleEffect.playEffects();
                }
                capturingTeam = getCapturingTeam(getPlayersAroundPoint());
                updateTeamCapturePercentage();
                ticksElapsed++;
            }


        }.runTaskTimer(1, 0);
    }

    @Nullable
    protected Team getCapturingTeam(Stream<WarlordsEntity> players) {
        Map<Team, List<WarlordsEntity>> perTeam = players.collect(Collectors.groupingBy(WarlordsEntity::getTeam, Collectors.toList()));
        if (perTeam.isEmpty()) {
            return null;
        }
        // check if multiple teams on point
        perTeam.entrySet().removeIf(teamListEntry -> teamListEntry.getValue().isEmpty());
        if (perTeam.size() > 1) {
            return null;
        }
        return perTeam.keySet().iterator().next();
    }

    protected Stream<WarlordsEntity> getPlayersAroundPoint() {
        return PlayerFilter.entitiesAround(location, RADIUS, RADIUS, RADIUS)
                           .stream()
                           .filter(wp -> wp.getGame() == game && wp.isAlive());
    }

    private void updateTeamCapturePercentage() {
        teamCapturePercentage.forEach((team, teamCaptureData) -> {
            if (team == capturingTeam) {
                teamCaptureData.standingTimer++;
                if (teamCaptureData.standingTimer < 20) {
                    return;
                }
                teamCaptureData.standingTimer = 0;
                teamCaptureData.percentage += CAPTURE_RATE;
                teamCaptureData.bossBar
                        .progress(MathUtils.clamp(teamCaptureData.percentage / 100, 0, 1))
                        .name(Component.text(Math.round(teamCaptureData.percentage) + "%", NamedTextColor.WHITE));
                if (!(teamCaptureData.percentage >= 100)) {
                    return;
                }
                WarlordsGameTriggerWinEvent event = new WarlordsGameTriggerWinEvent(game, SiegePoint.this, capturingTeam);
                Bukkit.getPluginManager().callEvent(event);
                hideBossBars();
                return;
            }
            teamCaptureData.standingTimer = 0;
        });
    }

    private void hideBossBars() {
        game.forEachOnlinePlayer((player, team) -> teamCapturePercentage.forEach((t, teamCaptureData) -> player.hideBossBar(teamCaptureData.bossBar)));
    }

    @Override
    public void onGameEnding(@Nonnull Game game) {
        hideBossBars();
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        teamCapturePercentage.forEach((team, teamCaptureData) -> player.showBossBar(teamCaptureData.bossBar));
    }

    static final class TeamCaptureData {
        private final BossBar bossBar;
        private float percentage;
        private int standingTimer;

        TeamCaptureData(Team team, float percentage, int standingTimer) {
            this.bossBar = BossBar.bossBar(
                    Component.text("0%", NamedTextColor.WHITE),
                    0,
                    team.bossBarColor,
                    BossBar.Overlay.NOTCHED_10
            );
            this.percentage = percentage;
            this.standingTimer = standingTimer;
        }

    }
}
