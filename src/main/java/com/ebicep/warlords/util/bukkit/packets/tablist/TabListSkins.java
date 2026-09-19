package com.ebicep.warlords.util.bukkit.packets.tablist;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Reads texture properties from a live player's profile for fake tab entries.
 */
public final class TabListSkins {

    private TabListSkins() {
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
