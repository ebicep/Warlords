package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@CommandAlias("discord")
@Conditions("database:player|bot")
public class DiscordCommand extends BaseCommand {

    public static BidiMap<UUID, Long> playerLinkKeys = new DualHashBidiMap<>();

    public static Long getRandomNumber(int min, int max) {
        return min + (long) (Math.random() * (max - min));
    }

    @Subcommand("link")
    @Description("Links your discord account to your minecraft account")
    public void link(Player player) {
        if (playerLinkKeys.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "There is already an active key for your account, wait until it expires to link again.");
            return;
        }
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        if (databasePlayer.getDiscordID() != null) {
            player.sendMessage(ChatColor.RED + "Your account is already linked! (/discord unlink) to unlink your account.");
            return;
        }
        //random 5 digit key
        Long key = getRandomNumber(10000, 100000);
        while (playerLinkKeys.containsValue(key)) {
            key = getRandomNumber(10000, 100000);
        }
        playerLinkKeys.put(player.getUniqueId(), key);
        player.sendMessage(ChatColor.GRAY + "Your discord link key is " + ChatColor.GREEN + key + ChatColor.GRAY + ". Direct message (Balancer Bot) this key to link your account. This key will expire in 1 minute.");
        Warlords.newChain().delay(1, TimeUnit.MINUTES).sync(() -> playerLinkKeys.remove(player.getUniqueId())).execute();

        BotManager.sendDebugMessage(
                new EmbedBuilder()
                        .setColor(16776960)
                        .setTitle("Link Key Created - " + key)
                        .setDescription("UUID: " + player.getUniqueId() + "\n" + "IGN: " + player.getName())
                        .build()
        );
    }

    @Subcommand("unlink")
    @Description("Unlinks your discord account from your minecraft account")
    public void unlink(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        if (databasePlayer.getDiscordID() == null) {
            player.sendMessage(ChatColor.RED + "Your account has not been linked! (/discord link) to link your account.");
            return;
        }
        Long oldID = databasePlayer.getDiscordID();
        databasePlayer.setDiscordID(null);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        player.sendMessage(ChatColor.GRAY + "Your account has been unlinked.");

        BotManager.sendDebugMessage(
                new EmbedBuilder()
                        .setColor(15158332)
                        .setTitle("Player Unlinked - " + oldID)
                        .setDescription("UUID: " + player.getUniqueId() + "\n" + "IGN: " + player.getName())
                        .build()
        );
    }

    @Subcommand("info")
    @Description("Shows information about your linked discord account")
    public void info(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        if (databasePlayer.getDiscordID() == null) {
            player.sendMessage(ChatColor.RED + "Your account has not been linked! (/discord link) to link your account.");
        } else {
            Warlords.newChain()
                    .async(() -> BotManager.jda.retrieveUserById(databasePlayer.getDiscordID()).queue(user -> {
                        if (user == null) {
                            player.sendMessage(ChatColor.GREEN + "Your account is linked to (" + databasePlayer.getDiscordID() + ").");
                        } else {
                            player.sendMessage(ChatColor.GREEN + "Your account is linked to " + user.getAsTag() + " (" + databasePlayer.getDiscordID() + ").");
                        }
                    }))
                    .execute();
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.showHelp();
    }
}
