package com.ebicep.warlords.guilds;

import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.signgui.SignGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.ebicep.warlords.menu.Menu.*;
import static com.ebicep.warlords.util.warlords.Utils.woolSortedByColor;

public class GuildMenu {

    public static void openGuildMenu(Guild guild, Player player, int page) {
        Menu menu = new Menu(guild.getName(), 9 * 6);

        if (player.getUniqueId().equals(guild.getCurrentMaster())) {
            menu.setItem(4, 0,
                    new ItemBuilder(Material.LEVER)
                            .name(ChatColor.GREEN + "Edit Permissions")
                            .get(),
                    (m, e) -> openRoleSelectorMenu(guild, player));
        }

        int playerPerPage = 36;
        List<GuildPlayer> guildPlayers = guild.getPlayers();
        for (int i = 0; i < playerPerPage; i++) {
            int index = ((page - 1) * playerPerPage) + i;
            if (index < guildPlayers.size()) {
                GuildPlayer guildPlayer = guildPlayers.get(index);
                menu.setItem(i % 9, i / 9 + 1,
                        new ItemBuilder(HeadUtils.getHead(guildPlayer.getUUID())) //TODO check if this lags
                                .name(ChatColor.GREEN + guildPlayer.getName())
                                .lore(ChatColor.GRAY + "Role: " + ChatColor.AQUA + guild.getRoleOfPlayer(guildPlayer.getUUID()).getRoleName())
                                .get(),
                        (m, e) -> {

                        });
            } else {
                break;
            }
        }

        if (page - 1 > 0) {
            menu.setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (page - 1))
                            .get(),
                    (m, e) -> openGuildMenu(guild, player, page - 1)
            );
        }
        if (guild.getPlayers().size() > (page * playerPerPage)) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (page + 1))
                            .get(),
                    (m, e) -> openGuildMenu(guild, player, page + 1)
            );
        }

        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openRoleSelectorMenu(Guild guild, Player player) {
        Menu menu = new Menu("Role Selector", 9 * 5);

        List<GuildRole> guildRoles = guild.getRoles();
        menu.setItem(4, 0,
                new ItemBuilder(Material.SIGN)
                        .name(ChatColor.GREEN + "Create a new role")
                        .get(),
                (m, e) -> {
                    if (guildRoles.size() >= 6) {
                        Guild.sendGuildMessage(player, ChatColor.RED + "You can only have a maximum of 5 roles.");
                        return;
                    }
                    SignGUI.open(player, new String[]{"", "^^^^^^", "Enter new", "role name"}, (p, lines) -> {
                        String roleName = lines[0];
                        if (roleName.isEmpty()) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You must enter a role name!");
                            return;
                        }
                        if (guildRoles.stream().anyMatch(role -> role.getRoleName().equalsIgnoreCase(roleName))) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "A role with that name already exists!");
                            return;
                        }
                        if (roleName.length() < 3) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Role names must be at least 3 characters long!");
                            return;
                        }
                        GuildRole role = new GuildRole(roleName);
                        guildRoles.add(1, role);
                        Guild.sendGuildMessage(player, ChatColor.GREEN + "Role created: " + roleName);
                    });
                }
        );

        for (int i = 1; i < guildRoles.size(); i++) {
            GuildRole role = guildRoles.get(i);
            menu.setItem(i, 2,
                    new ItemBuilder(woolSortedByColor[i + 4])
                            .name(ChatColor.GREEN + role.getRoleName())
                            .get(),
                    (m, e) -> openRoleEditor(guild, role, player)
            );
        }

        menu.setItem(4, 4, MENU_BACK, (m, e) -> openGuildMenu(guild, player, 1));
        menu.openForPlayer(player);
    }

    public static void openRoleEditor(Guild guild, GuildRole role, Player player) {
        Menu menu = new Menu("Role Editor", 9 * 4);

        List<GuildRole> guildRoles = guild.getRoles();

        int column = 1;
        int row = 1;

        Set<GuildPermissions> permissions = role.getPermissions();
        for (GuildPermissions value : GuildPermissions.values()) {
            menu.setItem(column, row,
                    value.getItemStack(permissions.contains(value)),
                    (m, e) -> {
                        if (permissions.contains(value)) {
                            permissions.remove(value);
                        } else {
                            permissions.add(value);
                        }
                        openRoleEditor(guild, role, player);
                    }
            );
            column++;
            if (column > 7) {
                column = 1;
                row++;
            }
        }

        menu.setItem(1, 3,
                new ItemBuilder(Material.GRASS)
                        .name(ChatColor.GREEN + "Click to set as default role")
                        .lore(ChatColor.GRAY + "Current default: " + ChatColor.GREEN + guild.getDefaultRoleName())
                        .get(),
                (m, e) -> {
                    guild.setDefaultRole(role.getRoleName());
                    openRoleEditor(guild, role, player);
                });

        menu.setItem(3, 3,
                new ItemBuilder(Material.NAME_TAG)
                        .name(ChatColor.GREEN + "Click to rename role")
                        .lore(ChatColor.GRAY + "Current name: " + ChatColor.GREEN + role.getRoleName())
                        .get(),
                (m, e) -> {
                    SignGUI.open(player, new String[]{"", "^^^^^^", "Enter new", "role name"}, (p, lines) -> {
                        String roleName = lines[0];
                        if (roleName.isEmpty()) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You must enter a role name!");
                            return;
                        }
                        if (guildRoles.stream().anyMatch(r -> r.getRoleName().equalsIgnoreCase(roleName))) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "A role with that name already exists!");
                            return;
                        }
                        if (roleName.length() < 3) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Role names must be at least 3 characters long!");
                            return;
                        }
                        role.setRoleName(roleName);
                        openRoleEditor(guild, role, player);
                    });
                });

        List<String> lore = new ArrayList<>();
        for (int i = 1; i < guildRoles.size(); i++) {
            lore.add("" + (guildRoles.get(i).equals(role) ? ChatColor.GREEN : ChatColor.GRAY) + i + ". " + guildRoles.get(i).getRoleName());
        }
        menu.setItem(5, 3,
                new ItemBuilder(Material.SIGN)
                        .name(ChatColor.GREEN + "Click to change role level")
                        .lore(lore)
                        .get(),
                (m, e) -> {
                    int roleIndex = guildRoles.indexOf(role);
                    if (roleIndex == guildRoles.size() - 1) {
                        guildRoles.remove(role);
                        guildRoles.add(1, role);
                    } else {
                        guildRoles.remove(role);
                        guildRoles.add(roleIndex + 1, role);
                    }
                    openRoleEditor(guild, role, player);
                });

        menu.setItem(7, 3,
                new ItemBuilder(Material.LAVA_BUCKET)
                        .name(ChatColor.GREEN + "Click to delete role")
                        .lore(lore)
                        .get(),
                (m, e) -> {
                    if (!role.getPlayers().isEmpty()) {
                        Guild.sendGuildMessage(player, ChatColor.RED + "You must remove all players from this role before deleting it!");
                        return;
                    }

                    SignGUI.open(player, new String[]{"", "Type CONFIRM", "Exiting will read", "current text!"}, (p, lines) -> {
                        String confirmation = lines[0];
                        if (confirmation.equals("CONFIRM")) {
                            guildRoles.remove(role);
                            openRoleSelectorMenu(guild, player);
                        } else {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Role " + role.getRoleName() + " was not deleted because you did not input CONFIRM");
                        }
                    });
                });


        menu.setItem(4, 3, MENU_BACK, (m, e) -> openRoleSelectorMenu(guild, player));
        menu.openForPlayer(player);
    }
}
