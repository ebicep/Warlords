package com.ebicep.warlords.database.repositories.player.pojos.cache.support;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

/**
 * Boots MockBukkit before any test classes load so {@code Bukkit.getWorld("MainLobby")} and Paper
 * registries are available when game/player POJOs initialize.
 */
public final class PushCacheLauncherSessionListener implements LauncherSessionListener {

    @Override
    public void launcherSessionOpened(LauncherSession session) {
        ServerMock server = MockBukkit.mock();
        server.addSimpleWorld("MainLobby");
    }

    @Override
    public void launcherSessionClosed(LauncherSession session) {
        MockBukkit.unmock();
    }
}
