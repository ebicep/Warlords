package com.ebicep.warlords.tablist;

import net.kyori.adventure.text.Component;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Resolved content for one custom tab-list row (viewer-specific after {@link TabEntrySource} runs).
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

    boolean sameVisual(TabEntry other) {
        return latency == other.latency
                && Objects.equals(displayName, other.displayName)
                && Objects.equals(skinTexture, other.skinTexture)
                && Objects.equals(skinSignature, other.skinSignature);
    }

    boolean sameSkin(TabEntry other) {
        return Objects.equals(skinTexture, other.skinTexture)
                && Objects.equals(skinSignature, other.skinSignature);
    }
}
