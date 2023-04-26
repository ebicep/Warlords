package com.ebicep.warlords.guilds;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum GuildPermissions {

    INVITE("Invite Players", "Invite players to the guild.", Material.DARK_OAK_DOOR),
    KICK("Kick Players", "Kicks players from the guild.", Material.PISTON),
    MUTE("Mute the Guild", "Mute the guild chat.", Material.JUKEBOX),
    MUTE_PLAYERS("Mutes Players", "Mute players in the guild.", Material.JUKEBOX),
    BYPASS_MUTE("Bypass Mute", "Bypass guild chat mute", Material.NOTE_BLOCK),
    CHANGE_ROLE("Promote/Demote Players", "Promote or demote players up to their own rank", Material.STICKY_PISTON),
    CHANGE_NAME("Change Guild Name", "Change the guild's name", Material.WRITABLE_BOOK),
    PURCHASE_UPGRADES("Purchase Upgrades", "Purchase Upgrades for the Guild", Material.ENCHANTING_TABLE),
    OFFICER_CHAT("Use Officer Chat", "Allows player to use officer chat", Material.ENDER_EYE),
    MODIFY_TAG("Modify Guild Tag", "Allows player to modify the guild's tag", Material.NAME_TAG),
    MODIFY_MOTD("Modify Guild MOTD", "Allows player to modify the guild's MOTD", Material.OAK_SIGN),

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
                .name(Component.text(name, NamedTextColor.GREEN))
                .loreLEGACY(
                        ChatColor.GRAY + description,
                        "",
                        enabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled",
                        ChatColor.YELLOW + "Click to toggle"
                )
                .get();
    }
}
