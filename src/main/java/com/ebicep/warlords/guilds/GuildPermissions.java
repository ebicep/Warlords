package com.ebicep.warlords.guilds;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum GuildPermissions {

    INVITE("Invite Players", "Invite players to the guild.", Material.DARK_OAK_DOOR_ITEM),
    KICK("Kick Players", "Kicks players from the guild.", Material.PISTON_BASE),
    MUTE("Mute the Guild", "Mute the guild chat.", Material.JUKEBOX),
    MUTE_PLAYERS("Mutes Players", "Mute players in the guild.", Material.JUKEBOX),
    BYPASS_MUTE("Bypass Mute", "Bypass guild chat mute", Material.NOTE_BLOCK),
    CHANGE_ROLE("Promote/Demote Players", "Promote or demote players up to their own rank", Material.PISTON_STICKY_BASE),
    CHANGE_NAME("Change Guild Name", "Change the guild's name", Material.BOOK_AND_QUILL),
    PURCHASE_UPGRADES("Purchase Upgrades", "Purchase Upgrades for the Guild", Material.ENCHANTMENT_TABLE),
    OFFICER_CHAT("Use Officer Chat", "Allows player to use officer chat", Material.EYE_OF_ENDER),
    MODIFY_TAG("Modify Guild Tag", "Allows player to modify the guild's tag", Material.NAME_TAG),
    MODIFY_MOTD("Modify Guild MOTD", "Allows player to modify the guild's MOTD", Material.SIGN),

    ;

    public static final GuildPermissions[] VALUES = values();
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
