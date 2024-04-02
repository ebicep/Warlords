package com.ebicep.warlords.util.pve;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.ebicep.warlords.Warlords;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class SkullUtils {

    public static ItemStack getSkullFrom(Skull skullID) {
        return getSkullFrom(skullID.getTextureID());
    }

    public static ItemStack getSkullFrom(String base64) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        PlayerProfile playerProfile = Warlords.getInstance().getServer().createProfile(new UUID(
                base64.substring(base64.length() - 20).hashCode(),
                base64.substring(base64.length() - 10).hashCode()
        ));
        playerProfile.setProperty(new ProfileProperty(
                "textures",
                base64
        ));

        skullMeta.setPlayerProfile(playerProfile);
        skull.setItemMeta(skullMeta);
        return skull;
    }


}
