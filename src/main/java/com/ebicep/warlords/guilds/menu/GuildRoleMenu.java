package com.ebicep.warlords.guilds.menu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildPermissions;
import com.ebicep.warlords.guilds.GuildRole;
import com.ebicep.warlords.guilds.logs.types.oneplayer.roles.*;
import com.ebicep.warlords.guilds.logs.types.oneplayer.roles.permissions.GuildLogPermissionAdd;
import com.ebicep.warlords.guilds.logs.types.oneplayer.roles.permissions.GuildLogPermissionRemove;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.Utils;
import de.rapha149.signgui.SignGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;


public class GuildRoleMenu {

    public static void openRoleSelectorMenu(Guild guild, Player player) {
        Menu menu = new Menu("Role Selector", 9 * 5);

        List<GuildRole> guildRoles = guild.getRoles();
        menu.setItem(4, 0,
                new ItemBuilder(Material.OAK_SIGN)
                        .name(Component.text("Create a new role", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> {
                    if (guildRoles.size() >= 8) {
                        Guild.sendGuildMessage(player, ChatColor.RED + "You can only have a maximum of 7 roles.");
                        return;
                    }
                    new SignGUI()
                            .lines("", "^^^^^^", "Enter new", "role name")
                            .onFinish((p, lines) -> {
                                String roleName = lines[0].trim();
                                if (roleName.isEmpty()) {
                                    Guild.sendGuildMessage(player, ChatColor.RED + "You must enter a role name!");
                                    return null;
                                }
                                if (guildRoles.stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase(roleName))) {
                                    Guild.sendGuildMessage(player, ChatColor.RED + "A role with that name already exists!");
                                    return null;
                                }
                                if (roleName.length() < 3) {
                                    Guild.sendGuildMessage(player, ChatColor.RED + "Role names must be at least 3 characters long!");
                                    return null;
                                }
                                GuildRole role = new GuildRole(roleName);
                                guildRoles.add(1, role);
                                Guild.sendGuildMessage(player, ChatColor.GREEN + "Role created: " + roleName);
                                guild.log(new GuildLogRoleCreate(player.getUniqueId(), roleName));
                                guild.queueUpdate();
                                openRoleEditorAfterTick(guild, player, role);
                                return null;
                            }).open(player);
                }
        );

        for (int i = 1; i < guildRoles.size(); i++) {
            GuildRole role = guildRoles.get(i);
            menu.setItem(i, 2,
                    new ItemBuilder(Utils.getWoolFromIndex(i + 4))
                            .name(Component.text(role.getRoleName(), NamedTextColor.GREEN))
                            .get(),
                    (m, e) -> openRoleEditor(guild, role, player)
            );
        }

        menu.setItem(4, 4, MENU_BACK, (m, e) -> GuildMenu.openGuildMenu(guild, player, 1));
        menu.openForPlayer(player);
    }

    private static void openRoleEditorAfterTick(Guild guild, Player player, GuildRole role) {
        new BukkitRunnable() {
            @Override
            public void run() {
                openRoleEditor(guild, role, player);
            }
        }.runTaskLater(Warlords.getInstance(), 1);
    }

    public static void openRoleEditor(Guild guild, GuildRole role, Player player) {
        Menu menu = new Menu("Role Editor", 9 * 5);

        List<GuildRole> guildRoles = guild.getRoles();

        int column = 1;
        int row = 1;

        Set<GuildPermissions> permissions = role.getPermissions();
        for (GuildPermissions value : GuildPermissions.VALUES) {
            menu.setItem(column, row,
                    value.getItemStack(permissions.contains(value)),
                    (m, e) -> {
                        if (permissions.contains(value)) {
                            permissions.remove(value);
                            guild.log(new GuildLogPermissionRemove(player.getUniqueId(), role.getRoleName(), value));
                        } else {
                            permissions.add(value);
                            guild.log(new GuildLogPermissionAdd(player.getUniqueId(), role.getRoleName(), value));
                        }
                        guild.queueUpdate();
                        openRoleEditor(guild, role, player);
                    }
            );
            column++;
            if (column > 7) {
                column = 1;
                row++;
            }
        }

        if (!guild.getDefaultRole().equals(role)) {
            menu.setItem(1, 4,
                    new ItemBuilder(Material.GRASS)
                            .name(Component.text("Click to set as default role", NamedTextColor.GREEN))
                            .loreLEGACY(ChatColor.GRAY + "Current default: " + ChatColor.GREEN + guild.getDefaultRoleName())
                            .get(),
                    (m, e) -> {
                        guild.setDefaultRole(role.getRoleName());
                        guild.log(new GuildLogRoleSetDefault(player.getUniqueId(), role.getRoleName()));
                        guild.queueUpdate();
                        openRoleEditor(guild, role, player);
                    }
            );
        } else {
            menu.setItem(1, 4,
                    new ItemBuilder(Material.GRASS)
                            .name(Component.text("This is the default role", NamedTextColor.GREEN))
                            .get(),
                    (m, e) -> {

                    }
            );
        }
        menu.setItem(3, 4,
                new ItemBuilder(Material.NAME_TAG)
                        .name(Component.text("Click to rename role", NamedTextColor.GREEN))
                        .loreLEGACY(ChatColor.GRAY + "Current name: " + ChatColor.GREEN + role.getRoleName())
                        .get(),
                (m, e) -> {
                    new SignGUI()
                            .lines("", "^^^^^^", "Enter new", "role name")
                            .onFinish((p, lines) -> {
                                String newRoleName = lines[0];
                                if (newRoleName.isEmpty()) {
                                    Guild.sendGuildMessage(player, ChatColor.RED + "You must enter a role name!");
                                    return null;
                                }
                                if (guildRoles.stream().anyMatch(r -> r.getRoleName().equalsIgnoreCase(newRoleName))) {
                                    Guild.sendGuildMessage(player, ChatColor.RED + "A role with that name already exists!");
                                    return null;
                                }
                                if (newRoleName.length() < 3) {
                                    Guild.sendGuildMessage(player, ChatColor.RED + "Role names must be at least 3 characters long!");
                                    return null;
                                }
                                if (guild.getDefaultRole().equals(role)) {
                                    guild.setDefaultRole(newRoleName);
                                }
                                Guild.sendGuildMessage(player, ChatColor.GREEN + "Role " + role.getRoleName() + " was renamed to " + newRoleName);
                                guild.log(new GuildLogRoleRename(player.getUniqueId(), role.getRoleName(), newRoleName));
                                role.setRoleName(newRoleName);
                                guild.queueUpdate();
                                openRoleEditorAfterTick(guild, player, role);
                                return null;
                            }).open(player);
                }
        );

        List<String> lore = new ArrayList<>();
        for (int i = 1; i < guildRoles.size(); i++) {
            lore.add("" + (guildRoles.get(i).equals(role) ? ChatColor.GREEN : ChatColor.GRAY) + i + ". " + guildRoles.get(i).getRoleName());
        }
        menu.setItem(5, 4,
                new ItemBuilder(Material.OAK_SIGN)
                        .name(Component.text("Click to change role level", NamedTextColor.GREEN))
                        .loreLEGACY(lore)
                        .get(),
                (m, e) -> {
                    int roleIndex = guildRoles.indexOf(role);
                    int newRoleIndex;
                    if (roleIndex == guildRoles.size() - 1) {
                        newRoleIndex = 1;
                    } else {
                        newRoleIndex = roleIndex + 1;
                    }
                    guildRoles.remove(role);
                    guildRoles.add(newRoleIndex, role);
                    guild.log(new GuildLogRoleChangeLevel(player.getUniqueId(), role.getRoleName(), roleIndex, newRoleIndex));
                    guild.queueUpdate();
                    openRoleEditor(guild, role, player);
                }
        );

        menu.setItem(7, 4,
                new ItemBuilder(Material.LAVA_BUCKET)
                        .name(Component.text("Click to delete role", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> {
                    if (guild.getDefaultRole().equals(role)) {
                        Guild.sendGuildMessage(player, ChatColor.RED + "You cannot delete the default role. Reassign it first!");
                        return;
                    }
                    if (!role.getPlayers().isEmpty()) {
                        Guild.sendGuildMessage(player, ChatColor.RED + "You must remove all players from this role before deleting it!");
                        return;
                    }

                    new SignGUI()
                            .lines("", "Type CONFIRM", "Exiting will read", "current text!")
                            .onFinish((p, lines) -> {
                                String confirmation = lines[0];
                                if (confirmation.equals("CONFIRM")) {
                                    guildRoles.remove(role);
                                    guild.log(new GuildLogRoleDelete(player.getUniqueId(), role.getRoleName()));
                                    guild.queueUpdate();
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            openRoleSelectorMenu(guild, player);
                                        }
                                    }.runTaskLater(Warlords.getInstance(), 1);
                                } else {
                                    Guild.sendGuildMessage(player,
                                            ChatColor.RED + "Role " + role.getRoleName() + " was not deleted because you did not input CONFIRM"
                                    );
                                }
                                return null;
                            }).open(player);
                }
        );


        menu.setItem(4, 4, MENU_BACK, (m, e) -> openRoleSelectorMenu(guild, player));
        menu.openForPlayer(player);
    }
}
