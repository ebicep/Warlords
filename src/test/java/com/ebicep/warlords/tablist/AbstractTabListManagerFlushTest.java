package com.ebicep.warlords.tablist;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit coverage for immediate flush handoff helpers.
 * <p>
 * Manual checklist (in-game):
 * <ul>
 *   <li>Quit from public PreLobby — name disappears from other players' Tab (no ghost).</li>
 *   <li>Leave a match to lobby while staying online — lobby Tab returns within ~1 tick.</li>
 *   <li>Match start while holding Tab — no long empty/vanilla flash (same-tick game flush).</li>
 *   <li>Promote / change honorific while AFK in lobby — Tab prefix updates without join/leave.</li>
 * </ul>
 */
class AbstractTabListManagerFlushTest {

    @Test
    void flushViewerNowWithUnresolvedPlayerDoesNotThrow() {
        TestManager manager = new TestManager();
        UUID id = UUID.randomUUID();
        manager.active.add(id);
        manager.addViewer(id);

        assertDoesNotThrow(() -> manager.flushViewerNow(id));
        assertNotNull(manager.getSession(id));
    }

    @Test
    void addViewerAndFlushCreatesSession() {
        TestManager manager = new TestManager();
        UUID id = UUID.randomUUID();
        manager.active.add(id);

        manager.addViewerAndFlush(id);

        assertNotNull(manager.getSession(id));
        assertTrue(manager.getGroups().isEmpty() || manager.getSession(id) != null);
    }

    @Test
    void removeViewerClearsSession() {
        TestManager manager = new TestManager();
        UUID id = UUID.randomUUID();
        manager.active.add(id);
        manager.addViewer(id);
        manager.active.remove(id);
        manager.removeViewer(id);

        assertNull(manager.getSession(id));
    }

    private static final class TestManager extends AbstractTabListManager {
        final Set<UUID> active = new HashSet<>();

        @Nonnull
        @Override
        protected Collection<UUID> activeViewerIds() {
            return active;
        }

        @Nullable
        @Override
        protected Player resolvePlayer(@Nonnull UUID uuid) {
            return null;
        }
    }
}
