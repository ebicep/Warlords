package com.ebicep.warlords.util.bukkit.packets.tablist;

import net.kyori.adventure.text.Component;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable description of one fake tab-list row for player-info packets.
 */
public record TabListEntry(
        UUID uuid,
        String profileName,
        @Nullable Component displayName,
        int latency,
        boolean listed,
        @Nullable String skinTexture,
        @Nullable String skinSignature
) {

    public TabListEntry {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(profileName, "profileName");
    }

    public static TabListEntry of(UUID uuid, String profileName) {
        return new TabListEntry(uuid, profileName, null, 0, true, null, null);
    }

    public static TabListEntry of(UUID uuid, String profileName, @Nullable Component displayName, int latency) {
        return new TabListEntry(uuid, profileName, displayName, latency, true, null, null);
    }

    public TabListEntry withDisplayName(@Nullable Component displayName) {
        return new TabListEntry(uuid, profileName, displayName, latency, listed, skinTexture, skinSignature);
    }

    public TabListEntry withLatency(int latency) {
        return new TabListEntry(uuid, profileName, displayName, latency, listed, skinTexture, skinSignature);
    }

    public TabListEntry withListed(boolean listed) {
        return new TabListEntry(uuid, profileName, displayName, latency, listed, skinTexture, skinSignature);
    }

    public TabListEntry withSkin(@Nullable String texture, @Nullable String signature) {
        return new TabListEntry(uuid, profileName, displayName, latency, listed, texture, signature);
    }
}
