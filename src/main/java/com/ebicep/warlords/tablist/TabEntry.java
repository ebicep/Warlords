package com.ebicep.warlords.tablist;

import net.kyori.adventure.text.Component;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Resolved content for one custom tab-list row (viewer-specific after {@link TabEntrySource} runs).
 * <p>
 * For rows that mirror an online player, set {@link #logicalId} to that player's UUID string
 * ({@code playerId.toString()}). {@link TabViewerSession} then uses the real username as the
 * GameProfile name so chat Tab-complete suggests the player instead of a decorative slot name.
 */
public record TabEntry(
        @Nullable Component displayName,
        int latency,
        @Nullable String skinTexture,
        @Nullable String skinSignature,
        @Nullable String logicalId
) {

    public static TabEntry of(@Nullable Component displayName) {
        return new TabEntry(displayName, 0, null, null, null);
    }

    public static TabEntry of(@Nullable Component displayName, int latency) {
        return new TabEntry(displayName, latency, null, null, null);
    }

    public TabEntry withDisplayName(@Nullable Component displayName) {
        return new TabEntry(displayName, latency, skinTexture, skinSignature, logicalId);
    }

    public TabEntry withLatency(int latency) {
        return new TabEntry(displayName, latency, skinTexture, skinSignature, logicalId);
    }

    public TabEntry withSkin(@Nullable String texture, @Nullable String signature) {
        return new TabEntry(displayName, latency, texture, signature, logicalId);
    }

    public TabEntry withLogicalId(@Nullable String logicalId) {
        return new TabEntry(displayName, latency, skinTexture, skinSignature, logicalId);
    }

    boolean sameSkin(TabEntry other) {
        return Objects.equals(skinTexture, other.skinTexture)
                && Objects.equals(skinSignature, other.skinSignature);
    }
}
