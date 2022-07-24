package com.ebicep.warlords.guilds;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum GuildPermissions {

    INVITE("Invite Players", "Invite players to the guild.", Material.DARK_OAK_DOOR_ITEM),
    KICK("Kick Players", "Kicks players from the guild.", Material.PISTON_BASE),
    MUTE("Mute the Guild", "Mute the guild chat.", Material.JUKEBOX),
    BYPASS_MUTE("Bypass Mute", "Bypass guild chat mute", Material.NOTE_BLOCK),
    CHANGE_ROLE("Promote/Demote Players", "Promote or demote players up to their own rank", Material.PISTON_STICKY_BASE),
    CHANGE_NAME("Change Guild Name", "Change the guild's name", Material.BOOK_AND_QUILL),

    ;

    public final String name;
    public final String description;
    public final Material material;


    GuildPermissions(String name, String description, Material material) {
        this.name = name;
        this.description = description;
        this.material = material;
    }

    public ItemStack getItemStack(boolean enabled) {
        return new ItemBuilder(material)
                .name(ChatColor.GREEN + name)
                .lore(
                        ChatColor.GRAY + description,
                        "",
                        enabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled",
                        ChatColor.YELLOW + "Click to toggle"
                )
                .get();
    }
}
