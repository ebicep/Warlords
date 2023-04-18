package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@CommandAlias("admin")
@CommandPermission("group.administrator")
public class AdminCommand extends BaseCommand {

    public static final Set<DatabasePlayerPvE> BYPASSED_PLAYER_CURRENCIES = new HashSet<>();
    public static boolean DISABLE_RESTART_CHECK = false;
    public static boolean DISABLE_SPECTATOR_MESSAGES = false;

    @Subcommand("bypasscurrencies")
    @Description("Bypasses player pve currency costs - Prevents any from being added")
    public void bypassCurrencies(Player player) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            if (BYPASSED_PLAYER_CURRENCIES.contains(databasePlayer.getPveStats())) {
                BYPASSED_PLAYER_CURRENCIES.remove(databasePlayer.getPveStats());
                ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Disabled Bypassing Currencies");
            } else {
                BYPASSED_PLAYER_CURRENCIES.add(databasePlayer.getPveStats());
                ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Enabled Bypassing Currencies");
            }
        });
    }

    @Subcommand("disablegames")
    @Description("Prevents games from being started")
    public void disableGames(CommandIssuer issuer) {
        GameManager.gameStartingDisabled = !GameManager.gameStartingDisabled;
        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Disabled Games = " + GameManager.gameStartingDisabled);
    }

    @Subcommand("disablerestartcheck")
    @Description("Removes restart check that prevents games from being started")
    public void disableRestartCheck(CommandIssuer issuer) {
        DISABLE_RESTART_CHECK = !DISABLE_RESTART_CHECK;
        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Restart Check = " + DISABLE_RESTART_CHECK);
    }

    @Subcommand("removenearbyentities")
    @Description("Removes all nearby entities in range")
    public void removeEntitiesNearBy(Player player, @Conditions("limits:min=1,max=20") Integer range) {
        player.getWorld()
              .getNearbyEntities(player.getLocation(), range, range, range)
              .forEach(Entity::remove);
    }

    @Subcommand("banspec")
    @Description("Bans a specialization from being used")
    public void banSpec(Player player, Specializations spec) {
        spec.setBanned(true);
        ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Banned " + spec.name);
    }

    @Subcommand("unbanspec")
    @Description("Unbans a specialization from being used")
    public void unbanSpec(Player player, Specializations spec) {
        spec.setBanned(false);
        ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Unbanned " + spec.name);
    }

    @Subcommand("disablespectatormessages")
    @Description("Disables spectator messages")
    public void disableSpectatorMessages(CommandIssuer issuer) {
        DISABLE_SPECTATOR_MESSAGES = !DISABLE_SPECTATOR_MESSAGES;
        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Spectator Messages = " + DISABLE_SPECTATOR_MESSAGES, true);
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}