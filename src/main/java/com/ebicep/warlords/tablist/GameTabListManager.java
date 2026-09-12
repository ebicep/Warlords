package com.ebicep.warlords.tablist;

import com.ebicep.warlords.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Per-game tab list manager. Viewers are all online game members including spectators.
 */
public class GameTabListManager extends AbstractTabListManager {

    private final Game game;

    public GameTabListManager(@Nonnull Game game) {
        this.game = Objects.requireNonNull(game, "game");
    }

    @Nonnull
    public Game getGame() {
        return game;
    }

    @Nonnull
    @Override
    protected Collection<UUID> activeViewerIds() {
        return game.onlinePlayers()
                .map(entry -> entry.getKey().getUniqueId())
                .collect(Collectors.toSet());
    }

    @Nullable
    @Override
    protected Player resolvePlayer(@Nonnull UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return null;
        }
        return game.getPlayers().containsKey(uuid) ? player : null;
    }
}
