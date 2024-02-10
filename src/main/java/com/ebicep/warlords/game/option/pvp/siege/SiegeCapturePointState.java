package com.ebicep.warlords.game.option.pvp.siege;

import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.java.StringUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SiegeCapturePointState implements SiegeState, Listener, TimerSkipAbleMarker {

    public static final double RADIUS = 5;
    public static final float CAPTURE_RATE = 3.5f;

    private final SiegeOption siegeOption;
    private final Map<Team, TeamCaptureData> teamCapturePercentage = new HashMap<>();
    private Game game;
    private Team capturingTeam;
    private CircleEffect circleEffect;

    public SiegeCapturePointState(SiegeOption siegeOption) {
        this.siegeOption = siegeOption;
    }

    @Override
    public void start(@Nonnull Game game) {
        this.game = game;
        game.registerEvents(this);
        TeamMarker.getTeams(game).forEach(team -> teamCapturePercentage.put(team, new TeamCaptureData(team, 0, 0)));
        this.circleEffect = new CircleEffect(
                game,
                null,
                siegeOption.getLocation(),
                RADIUS,
                .05,
                new CircumferenceEffect(Particle.CRIT).particles(20)
        );
        game.forEachOnlinePlayer((player, team) -> showBossBar(player));
    }

    @Override
    public boolean tick(int ticksElapsed) {
        if (circleEffect != null) {
            circleEffect.playEffects();
        }
        capturingTeam = getCapturingTeam(getPlayersAroundPoint());
        boolean teamCaptured = updateTeamCapturePercentage();
        if (teamCaptured) {
            game.addPoints(capturingTeam, 1);
            hideBossBars();
            Map<UUID, Map<Specializations, SiegeStats>> playerSiegeStats = siegeOption.getPlayerSiegeStats();
            game.warlordsPlayers()
                .forEach(warlordsPlayer -> {
                    Specializations spec = warlordsPlayer.getSpecClass();
                    if (warlordsPlayer.getTeam() == capturingTeam) {
                        playerSiegeStats.computeIfAbsent(warlordsPlayer.getUuid(), uuid -> new HashMap<>())
                                        .computeIfAbsent(spec, specializations -> new SiegeStats())
                                        .addPointsCaptured();
                    } else {
                        playerSiegeStats.computeIfAbsent(warlordsPlayer.getUuid(), uuid -> new HashMap<>())
                                        .computeIfAbsent(spec, specializations -> new SiegeStats())
                                        .addPointsCapturedFail();
                    }
                });
            return true;
        }
        return false;
    }

    @Override
    public void end() {
        hideBossBars();
        HandlerList.unregisterAll(this);
    }

    @Override
    public SiegeState getNextState() {
        return new SiegePayloadState(siegeOption, capturingTeam);
    }

    @Override
    public Component getSidebarComponent(int ticksElapsed) {
        return Component.text("Time Elapsed: ", NamedTextColor.WHITE)
                        .append(Component.text(StringUtils.formatTimeLeft(ticksElapsed / 20), NamedTextColor.GREEN));
    }

    @Override
    public int maxSeconds() {
        return -1;
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        showBossBar(player);
    }

    @Nullable
    protected Team getCapturingTeam(Stream<WarlordsEntity> players) {
        Map<Team, List<WarlordsEntity>> perTeam = players.collect(Collectors.groupingBy(WarlordsEntity::getTeam, Collectors.toList()));
        if (perTeam.isEmpty()) {
            return null;
        }
        // check if multiple teams on point
        perTeam.entrySet().removeIf(teamListEntry -> teamListEntry.getValue().isEmpty());
        perTeam.forEach((team, warlordsEntities) -> {
            Map<UUID, Map<Specializations, SiegeStats>> playerSiegeStats = siegeOption.getPlayerSiegeStats();
            warlordsEntities.forEach(warlordsEntity -> playerSiegeStats.computeIfAbsent(warlordsEntity.getUuid(), uuid -> new HashMap<>())
                                                                       .computeIfAbsent(warlordsEntity.getSpecClass(), specializations -> new SiegeStats())
                                                                       .addTimeOnPointTicks());
        });
        if (perTeam.size() > 1) {
            return null;
        }
        return perTeam.keySet().iterator().next();
    }

    protected Stream<WarlordsEntity> getPlayersAroundPoint() {
        return PlayerFilter.entitiesAround(siegeOption.getLocation(), RADIUS, RADIUS, RADIUS)
                           .stream()
                           .filter(wp -> wp.getGame() == game && wp.isAlive());
    }

    private boolean updateTeamCapturePercentage() {
        for (Map.Entry<Team, TeamCaptureData> entry : teamCapturePercentage.entrySet()) {
            Team team = entry.getKey();
            TeamCaptureData teamCaptureData = entry.getValue();
            if (team == capturingTeam) {
                teamCaptureData.standingTimer++;
                if (teamCaptureData.standingTimer < 20) {
                    continue;
                }
                teamCaptureData.standingTimer = 0;
                teamCaptureData.addPercentage(CAPTURE_RATE);
                if (teamCaptureData.percentage < 100) {
                    continue;
                }
                return true;
            }
            teamCaptureData.standingTimer = 0;
        }
        return false;
    }

    private void hideBossBars() {
        game.forEachOnlinePlayer((player, team) -> teamCapturePercentage.forEach((t, teamCaptureData) -> player.hideBossBar(teamCaptureData.bossBar)));
    }

    private void showBossBar(Player player) {
        teamCapturePercentage.forEach((team, teamCaptureData) -> player.showBossBar(teamCaptureData.bossBar));
    }

    @EventHandler
    public void onRespawnGive(WarlordsGiveRespawnEvent event) {
        event.getRespawnTimer().set(12);
    }

    @Override
    public void skipTimer(int delayInTicks) {
        teamCapturePercentage.forEach((team, teamCaptureData) -> teamCaptureData.setPercentage(99));
    }

    static final class TeamCaptureData {
        private final BossBar bossBar;
        private float percentage;
        private int standingTimer;

        TeamCaptureData(Team team, float percentage, int standingTimer) {
            this.bossBar = BossBar.bossBar(
                    Component.text("0%", team.getTeamColor()),
                    0,
                    team.getBossBarColor(),
                    BossBar.Overlay.PROGRESS
            );
            this.percentage = percentage;
            this.standingTimer = standingTimer;
        }

        public void setPercentage(float percentage) {
            this.percentage = percentage;
            updateBossBar();
        }

        public void updateBossBar() {
            bossBar.progress(MathUtils.clamp(percentage / 100, 0, 1))
                   .name(Component.text(Math.round(percentage) + "%", bossBar.name().color()));
        }

        public void addPercentage(float percentage) {
            this.percentage += percentage;
            updateBossBar();
        }

    }
}
