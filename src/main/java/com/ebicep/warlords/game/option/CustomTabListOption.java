package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.tablist.GameTabListManager;
import com.ebicep.warlords.tablist.LobbyTabListManager;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Owns the per-game {@link GameTabListManager}: poller, join/quit hide hooks, cleanup on close.
 * Disabled for {@link GameMode#LOBBY} — those viewers use {@link LobbyTabListManager}.
 */
public class CustomTabListOption implements Option {

    private GameTabListManager manager;

    @Override
    public boolean isEnabled(@Nonnull Game game) {
        return game.getGameMode() != GameMode.LOBBY;
    }

    @Nonnull
    public GameTabListManager manager() {
        if (manager == null) {
            throw new IllegalStateException("CustomTabListOption not registered yet");
        }
        return manager;
    }

    @Nonnull
    public static Optional<GameTabListManager> get(@Nonnull Game game) {
        List<CustomTabListOption> options = game.getOption(CustomTabListOption.class);
        if (options.isEmpty()) {
            return Optional.empty();
        }
        CustomTabListOption option = options.getFirst();
        if (!option.isEnabled(game) || option.manager == null) {
            return Optional.empty();
        }
        return Optional.of(option.manager);
    }

    @Override
    public void register(@Nonnull Game game) {
        manager = new GameTabListManager(game);
        game.registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                if (manager == null) {
                    return;
                }
                manager.onRealPlayerJoin(event.getPlayer().getUniqueId());
            }

            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                if (manager == null) {
                    return;
                }
                manager.onRealPlayerQuit(event.getPlayer().getUniqueId());
            }
        });
    }

    @Override
    public void start(@Nonnull Game game) {
        // Move viewers off lobby tab when match play begins
        game.onlinePlayers().forEach(entry -> {
            Player player = entry.getKey();
            LobbyTabListManager.get().onLeaveLobby(player.getUniqueId());
            manager.addViewerAndFlush(player.getUniqueId());
        });

        new GameRunnable(game) {
            @Override
            public void run() {
                manager.tick();
            }
        }.runTaskTimer(1, 1);
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        if (manager != null) {
            manager.clear();
            manager = null;
        }
    }

    @Override
    public void onPlayerReJoinGame(@Nonnull Player player) {
        if (manager == null) {
            return;
        }
        LobbyTabListManager.get().onLeaveLobby(player.getUniqueId());
        manager.addViewerAndFlush(player.getUniqueId());
    }

    /**
     * Leave game tab and, if the player is still on the server next tick, re-enter lobby tab.
     * Deferred one tick so disconnects (player still non-null during {@link PlayerQuitEvent})
     * do not re-add a ghost UUID to the lobby viewer set.
     * <p>
     * Manual checks: (1) quit from public PreLobby — name disappears from others' Tab;
     * (2) leave match to lobby while online — lobby tab returns without a long empty flash;
     * (3) promote a lobby player — Tab prefix updates without join/leave churn.
     */
    @Override
    public void onPlayerQuit(@Nonnull Player player) {
        if (manager == null) {
            return;
        }
        UUID id = player.getUniqueId();
        // Keep listed=false across handoff so the client never flashes the vanilla tab
        manager.removeViewer(id, false);
        new BukkitRunnable() {
            @Override
            public void run() {
                Player online = Bukkit.getPlayer(id);
                if (online != null && LobbyTabListManager.isLobbyViewer(online)) {
                    LobbyTabListManager.get().onEnterLobby(id);
                }
            }
        }.runTaskLater(Warlords.getInstance(), 1L);
    }
}
