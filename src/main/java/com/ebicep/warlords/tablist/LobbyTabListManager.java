package com.ebicep.warlords.tablist;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Global lobby tab-list manager. Active for online players who are not in a match,
 * including players in the persistent {@link GameMode#LOBBY} game.
 */
public final class LobbyTabListManager extends AbstractTabListManager {

    private static LobbyTabListManager instance;
    private BukkitTask task;
    private Listener listener;

    private LobbyTabListManager() {
    }

    @Nonnull
    public static LobbyTabListManager get() {
        if (instance == null) {
            instance = new LobbyTabListManager();
        }
        return instance;
    }

    public void start() {
        if (task != null) {
            return;
        }
        listener = new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                onRealPlayerJoin(event.getPlayer().getUniqueId());
            }

            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                onRealPlayerQuit(event.getPlayer().getUniqueId());
            }
        };
        Bukkit.getPluginManager().registerEvents(listener, Warlords.getInstance());
        task = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(Warlords.getInstance(), 1L, 1L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (listener != null) {
            HandlerList.unregisterAll(listener);
            listener = null;
        }
        clear();
    }

    @Nonnull
    @Override
    protected Collection<UUID> activeViewerIds() {
        Set<UUID> ids = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isLobbyViewer(player)) {
                ids.add(player.getUniqueId());
            }
        }
        return ids;
    }

    @Nullable
    @Override
    protected Player resolvePlayer(@Nonnull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !isLobbyViewer(player)) {
            return null;
        }
        return player;
    }

    private static boolean isLobbyViewer(Player player) {
        WarlordsEntity entity = Warlords.getPlayer(player);
        if (entity == null) {
            return true;
        }
        Game game = entity.getGame();
        return game == null || game.getGameMode() == GameMode.LOBBY;
    }
}
