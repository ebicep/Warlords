package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.rewards.RewardTypes;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("pvematerial")
@Conditions("database:player")
public class PvEMaterialCommand extends BaseCommand {

    @Subcommand("add")
    @Description("Add pve materials to your inventory, you can only 1 star piece at a time")
    public void add(Player player, RewardTypes type, @Conditions("limits:min=0") Integer amount) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        type.give.accept(databasePlayer, amount.floatValue());
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        ChatChannels.playerSendMessage(player, ChatColor.GREEN + "Gave yourself " + ChatColor.YELLOW + amount + " " + ChatColor.LIGHT_PURPLE + type.name + (amount != 1 ? "s" : ""), ChatChannels.DEBUG, true);
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.showHelp();
    }

}
