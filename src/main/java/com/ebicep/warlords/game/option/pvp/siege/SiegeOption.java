package com.ebicep.warlords.game.option.pvp.siege;

import com.ebicep.warlords.abilities.GuardianBeam;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.TimerResetAbleMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.state.ClosedState;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class SiegeOption implements Option {

    private final Map<Team, Location> teamPayloadStart = new HashMap<>();
    private final Location location;
    private final Map<UUID, Map<Specializations, SiegeStats>> playerSiegeStats = new HashMap<>();
    private int totalTicksElapsed = 0;
    private int stateTicksElapsed = 0;
    private Game game;
    private SiegeState state;
    private SimpleScoreboardHandler sidebarScoreboardHandler;

    public SiegeOption(Location location) {
        this.location = location;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        state = new SiegeWaitState(this);
        game.registerGameMarker(ScoreboardHandler.class, sidebarScoreboardHandler = new SimpleScoreboardHandler(10, "state-time") {
                    @Nonnull
                    @Override
                    public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                        return Collections.singletonList(state.getSidebarComponent(stateTicksElapsed));
                    }
                }
        );
        game.registerGameMarker(TimerSkipAbleMarker.class, new TimerSkipAbleMarker() {
                    @Override
                    public int getDelay() {
                        return 0;
                    }

                    @Override
                    public void skipTimer(int delayInTicks) {
                        if (state == null || !(state instanceof TimerSkipAbleMarker timerSkipAbleMarker)) {
                            return;
                        }
                        timerSkipAbleMarker.skipTimer(delayInTicks);
                    }
                }
        );
        game.registerGameMarker(TimerResetAbleMarker.class, () -> {
                    if (state == null || !(state instanceof TimerResetAbleMarker timerSkipAbleMarker)) {
                        return;
                    }
                    timerSkipAbleMarker.reset();
                }
        );
    }

    @Override
    public void start(@Nonnull Game game) {
        state.start(game);
        new GameRunnable(game) {
            @Override
            public void run() {
                if (gameEnded()) {
                    this.cancel();
                    return;
                }
                boolean advanceStateFromTick = state.tick(stateTicksElapsed);
                totalTicksElapsed++;
                stateTicksElapsed++;
                if (sidebarScoreboardHandler != null) {
                    sidebarScoreboardHandler.markChanged();
                }
                if (!advanceStateFromTick) {
                    return;
                }
                if (gameEnded()) {
                    this.cancel();
                    return;
                }
                gotoNextState();
            }
        }.runTaskTimer(20, 0);
    }

    private boolean gameEnded() {
        return game.getState(EndState.class).isPresent() || game.getState(ClosedState.class).isPresent();
    }

    private void gotoNextState() {
        state.end();
        state = state.getNextState();
        state.start(game);
        stateTicksElapsed = 0;
        if (sidebarScoreboardHandler != null) {
            sidebarScoreboardHandler.markChanged();
        }
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Siege",
                null,
                SiegeOption.class,
                null,
                player,
                CooldownTypes.INTERNAL,
                cooldownManager -> {
                },
                false
        ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, "Siege", 1.15f);
                }
        ).addModifier(Modifier.MODIFY_OUTGOING_HEALING, (event, currentHealValue) -> {
            currentHealValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, "Siege", 0.75f);
                }
        ));
        for (AbstractAbility ability : player.getAbilities()) {
            if (ability instanceof GuardianBeam guardianBeam) {
                guardianBeam.getShieldValues().replaceAll(integer -> (int) (integer * .9f));
            }
        }
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        if (gameEnded()) {
            return;
        }
        state.updateInventory(warlordsPlayer, player);
    }

    public SiegeOption addPayloadStart(Team team, Location location) {
        teamPayloadStart.put(team, location);
        return this;
    }

    public Map<Team, Location> getTeamPayloadStart() {
        return teamPayloadStart;
    }

    public Location getLocation() {
        return location;
    }

    public int getStateTicksElapsed() {
        return stateTicksElapsed;
    }

    public SiegeState getState() {
        return state;
    }

    public Map<UUID, Map<Specializations, SiegeStats>> getPlayerSiegeStats() {
        return playerSiegeStats;
    }

    public int getTotalTicksElapsed() {
        return totalTicksElapsed;
    }

}
