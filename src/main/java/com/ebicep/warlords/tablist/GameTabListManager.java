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
 * Content comes from a {@link TabListLayout} plus any subgroups other Options attach.
 */
public class GameTabListManager extends AbstractTabListManager {

    private final Game game;
    private final TabListLayout layout;

    public GameTabListManager(@Nonnull Game game, @Nonnull TabListLayout layout) {
        this.game = Objects.requireNonNull(game, "game");
        this.layout = Objects.requireNonNull(layout, "layout");
        this.layout.apply(this, game);
    }

    @Nonnull
    public Game getGame() {
        return game;
    }

    @Nonnull
    public TabListLayout getLayout() {
        return layout;
    }

    /**
     * Rebuild player mirror rows on the next {@link #tick()} / {@link #addViewerAndFlush} sync
     * even if the online UUID set did not change.
     */
    public void requestPlayerContentRefresh() {
        layout.forcePlayerSync();
    }

    @Override
    public void tick() {
        layout.sync(this, game);
        super.tick();
    }

    @Override
    public void addViewerAndFlush(@Nonnull UUID viewerId) {
        layout.sync(this, game);
        super.addViewerAndFlush(viewerId);
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
