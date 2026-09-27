package com.ebicep.warlords.util.bukkit.packets.tablist;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.ebicep.warlords.util.bukkit.Colors;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Texture properties for fake tab entries: live players and solid {@link Colors} heads.
 */
public final class TabListSkins {

    private TabListSkins() {
    }

    /**
     * Signed solid-color head texture for decorative tab rows.
     *
     * @return {@code [texture, signature]}
     */
    @Nonnull
    public static String[] textureAndSignature(@Nonnull Colors color) {
        TabListColorSkins.SkinPair pair = TabListColorSkins.get(color);
        return new String[]{pair.value(), pair.signature()};
    }

    /**
     * @return {@code [texture, signature]} or {@code null} if the player has no textures property
     */
    @Nullable
    public static String[] textureAndSignature(@Nonnull Player player) {
        PlayerProfile profile = player.getPlayerProfile();
        for (ProfileProperty property : profile.getProperties()) {
            if ("textures".equals(property.getName())) {
                return new String[]{property.getValue(), property.getSignature()};
            }
        }
        return null;
    }
}
