package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@CommandAlias("admin")
@CommandPermission("group.adminisrator")
@Conditions("database:player")
public class AdminCommand extends BaseCommand {

    public static final Set<DatabasePlayerPvE> BYPASSED_PLAYER_CURRENCIES = new HashSet<>();

    @Subcommand("bypasscurrencies")
    @Description("Bypasses player pve currency costs - Prevents any from being added")
    public void bypassCurrencies(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        if (BYPASSED_PLAYER_CURRENCIES.contains(databasePlayer.getPveStats())) {
            BYPASSED_PLAYER_CURRENCIES.remove(databasePlayer.getPveStats());
            ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Disabled Bypassing Currencies", true);
        } else {
            BYPASSED_PLAYER_CURRENCIES.add(databasePlayer.getPveStats());
            ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Enabled Bypassing Currencies", true);
        }
    }

}