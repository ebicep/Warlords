package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.tablist.GameTabListManager;
import com.ebicep.warlords.tablist.LobbyTabListManager;
import com.ebicep.warlords.tablist.TabListLayout;
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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Owns the per-game {@link GameTabListManager}: poller, join/quit hide hooks, cleanup on close.
 * Modes supply a {@link TabListLayout}; other Options may attach {@code TabSubgroup}s via
 * {@link #manager()} after register. Disabled for {@link GameMode#LOBBY}.
 */
public class CustomTabListOption implements Option {

    private final TabListLayout layout;
    private GameTabListManager manager;
    private GameRunnable poller;

    public CustomTabListOption(@Nonnull TabListLayout layout) {
        this.layout = Objects.requireNonNull(layout, "layout");
    }

    @Override
    public boolean isEnabled(@Nonnull Game game) {
        return game.getGameMode() != GameMode.LOBBY;
    }

    @Nonnull
    public TabListLayout layout() {
        return layout;
    }

    @Nonnull
    public GameTabListManager manager() {
        if (manager == null) {
            throw new IllegalStateException("CustomTabListOption not registered yet");
        }
        return manager;
    }

    /**
     * Enabled, registered tab-list option for this game, if any.
     */
    @Nonnull
    public static Optional<CustomTabListOption> get(@Nonnull Game game) {
        List<CustomTabListOption> options = game.getOption(CustomTabListOption.class);
        if (options.isEmpty()) {
            return Optional.empty();
        }
        CustomTabListOption option = options.getFirst();
        if (!option.isEnabled(game) || option.manager == null) {
            return Optional.empty();
        }
        return Optional.of(option);
    }

    @Override
    public void register(@Nonnull Game game) {
        manager = new GameTabListManager(game, layout);
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
        // Start during PreLobby so queue players are isolated from the global lobby tab
        startPoller(game);
    }

    @Override
    public void start(@Nonnull Game game) {
        // Idempotent flush when match play begins (viewers may already be on game tab from PreLobby)
        game.onlinePlayers().forEach(entry -> {
            Player player = entry.getKey();
            LobbyTabListManager.get().onLeaveLobby(player.getUniqueId());
            manager.addViewerAndFlush(player.getUniqueId());
        });
        startPoller(game);
    }

    private void startPoller(@Nonnull Game game) {
        if (poller != null || manager == null) {
            return;
        }
        poller = new GameRunnable(game) {
            @Override
            public void run() {
                if (manager != null) {
                    manager.tick();
                }
            }
        };
        poller.runTaskTimer(1, 1);
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        if (poller != null) {
            poller.cancel();
            poller = null;
        }
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
