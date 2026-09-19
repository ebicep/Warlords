package com.ebicep.warlords.tablist;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.GuildTag;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.packets.tablist.TabListSkins;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global lobby tab-list manager. Active for online players who are not in a match,
 * including players in the persistent {@link GameMode#LOBBY} game.
 */
public final class LobbyTabListManager extends AbstractTabListManager {

    private static final String PLAYERS_GROUP = "players";

    private static LobbyTabListManager instance;
    private BukkitTask task;
    private Listener listener;
    private TabSubgroup playersSubgroup;
    private Set<UUID> lastSourceIds = Set.of();
    /** Authoritative lobby viewers — mutated only on enter/leave/join/quit. */
    private final Set<UUID> lobbyViewerIds = ConcurrentHashMap.newKeySet();

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
        ensurePlayersGroup();
        listener = new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                UUID id = event.getPlayer().getUniqueId();
                onEnterLobby(id);
                onRealPlayerJoin(id);
            }

            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                UUID id = event.getPlayer().getUniqueId();
                lobbyViewerIds.remove(id);
                onRealPlayerQuit(id);
                // Quitting player is still in getOnlinePlayers() during PlayerQuitEvent
                syncPlayerSources(id);
            }
        };
        Bukkit.getPluginManager().registerEvents(listener, Warlords.getInstance());
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isLobbyViewer(player)) {
                lobbyViewerIds.add(player.getUniqueId());
                addViewer(player.getUniqueId());
            }
        }
        task = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(Warlords.getInstance(), 1L, 1L);
        syncPlayerSources();
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
        playersSubgroup = null;
        lastSourceIds = Set.of();
        lobbyViewerIds.clear();
    }

    /**
     * Player became a lobby tab viewer (server join or returned from a match).
     */
    public void onEnterLobby(@Nonnull UUID viewerId) {
        lobbyViewerIds.add(viewerId);
        addViewer(viewerId);
        syncPlayerSources();
        flushViewerNow(viewerId);
    }

    /**
     * Player left the lobby tab (entered a non-{@link GameMode#LOBBY} match).
     * Does not restore {@code listed=true} so game tab can take over without a vanilla flash.
     */
    public void onLeaveLobby(@Nonnull UUID viewerId) {
        lobbyViewerIds.remove(viewerId);
        removeViewer(viewerId, false);
        syncPlayerSources();
    }

    /**
     * Force-rebuild lobby player rows (guild tag / membership / sort order / rank display changes).
     */
    public void refreshPlayerRows() {
        ensurePlayersGroup();
        lastSourceIds = Set.of();
        syncPlayerSources();
    }

    @Override
    public void tick() {
        pruneOfflineLobbyViewers();
        super.tick();
    }

    /**
     * Drop UUIDs that are no longer online (defense against handoff ghosts).
     */
    private void pruneOfflineLobbyViewers() {
        for (UUID id : List.copyOf(lobbyViewerIds)) {
            if (Bukkit.getPlayer(id) == null) {
                lobbyViewerIds.remove(id);
                removeViewer(id);
            }
        }
    }

    private void ensurePlayersGroup() {
        if (playersSubgroup != null && getGroup(PLAYERS_GROUP) != null) {
            return;
        }
        TabGroup group = group(PLAYERS_GROUP, 0, 0, 4, 80);
        playersSubgroup = new TabSubgroup(0);
        group.addSubgroup(playersSubgroup);
    }

    /**
     * Rebuild lobby player rows when the set of lobby viewers changes (join, quit, enter/leave match).
     */
    private void syncPlayerSources() {
        syncPlayerSources(null);
    }

    /**
     * @param excludeId player to omit from the source set (e.g. quitting player still in {@link Bukkit#getOnlinePlayers()})
     */
    private void syncPlayerSources(@Nullable UUID excludeId) {
        ensurePlayersGroup();
        Set<UUID> current = new HashSet<>(lobbyViewerIds);
        if (excludeId != null) {
            current.remove(excludeId);
        }
        if (current.equals(lastSourceIds)) {
            return;
        }
        lastSourceIds = Set.copyOf(current);

        List<Player> lobbyPlayers = new ArrayList<>(current.size());
        for (UUID id : current) {
            Player player = Bukkit.getPlayer(id);
            if (player != null) {
                lobbyPlayers.add(player);
            }
        }
        lobbyPlayers.sort(Comparator.comparing(LobbyTabListManager::lobbySortKey));

        // Eager resolve — lobby rows are viewer-independent
        List<TabEntrySource> sources = new ArrayList<>(lobbyPlayers.size());
        for (Player player : lobbyPlayers) {
            TabEntry entry = resolveLobbyPlayerEntry(player);
            sources.add(viewer -> entry);
        }
        playersSubgroup.setSources(sources);
    }

    @Nonnull
    private static TabEntry resolveLobbyPlayerEntry(@Nonnull Player player) {
        UUID playerId = player.getUniqueId();
        Component displayName = Permissions.getPrefixWithColor(player, true);
        Pair<Guild, GuildPlayer> guildPair = GuildManager.getGuildAndGuildPlayerFromPlayer(playerId);
        if (guildPair != null) {
            GuildTag tag = guildPair.getA().getTag();
            if (tag != null) {
                displayName = displayName.append(Component.space().append(tag.getTag(false)).compact());
            }
        }
        // logicalId = real player UUID so chat Tab-complete suggests their username
        TabEntry entry = TabEntry.of(displayName, player.getPing()).withLogicalId(playerId.toString());
        String[] skin = TabListSkins.textureAndSignature(player);
        if (skin != null) {
            entry = entry.withSkin(skin[0], skin[1]);
        }
        return entry;
    }

    /**
     * Same ordering as lobby scoreboard teams: rank ordinal, guild name, player name.
     */
    @Nonnull
    private static String lobbySortKey(@Nonnull Player player) {
        Permissions rank = Permissions.getPermission(player);
        String guildName = "";
        Pair<Guild, GuildPlayer> guildPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player.getUniqueId());
        if (guildPair != null) {
            guildName = guildPair.getA().getName();
        }
        String guildKey = guildName.isEmpty() ? "~" : guildName.toLowerCase(Locale.ROOT);
        return rank.ordinal() + "_" + guildKey + "_" + player.getName();
    }

    @Nonnull
    @Override
    protected Collection<UUID> activeViewerIds() {
        return Set.copyOf(lobbyViewerIds);
    }

    @Nullable
    @Override
    protected Player resolvePlayer(@Nonnull UUID uuid) {
        if (!lobbyViewerIds.contains(uuid)) {
            return null;
        }
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !isLobbyViewer(player)) {
            return null;
        }
        return player;
    }

    public static boolean isLobbyViewer(@Nonnull Player player) {
        WarlordsEntity entity = Warlords.getPlayer(player);
        if (entity == null) {
            return true;
        }
        Game game = entity.getGame();
        return game == null || game.getGameMode() == GameMode.LOBBY;
    }
}
