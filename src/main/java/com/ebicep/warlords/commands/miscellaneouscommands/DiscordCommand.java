package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DiscordCommand implements CommandExecutor {

    public static BidiMap<UUID, Long> playerLinkKeys = new DualHashBidiMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        Player player = BaseCommand.requirePlayer(sender);

        if (player == null) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Invalid Arguments! /discord [link/unlink/info]");
            return true;
        }

        if (DatabaseManager.playerService == null) {
            sender.sendMessage(ChatColor.RED + "Problem connecting to the database");
            return true;
        }

        String input = args[0];
        switch (input.toLowerCase()) {
            case "link": {
                if (playerLinkKeys.containsKey(player.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "There is already an active key for your account, wait until it expires to link again.");
                    return true;
                }
                DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
                if (databasePlayer.getDiscordID() != null) {
                    sender.sendMessage(ChatColor.RED + "Your account is already linked! (/discord unlink) to unlink your account.");
                    return true;
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
                break;
            }
            case "unlink": {
                DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
                if (databasePlayer.getDiscordID() == null) {
                    sender.sendMessage(ChatColor.RED + "Your account has not been linked! (/discord link) to link your account.");
                    return true;
                }
                Long oldID = databasePlayer.getDiscordID();
                databasePlayer.setDiscordID(null);
                DatabaseManager.updatePlayerAsync(databasePlayer);
                player.sendMessage(ChatColor.GRAY + "Your account has been unlinked.");

                BotManager.sendDebugMessage(
                        new EmbedBuilder()
                                .setColor(15158332)
                                .setTitle("Player Unlinked - " + oldID)
                                .setDescription("UUID: " + player.getUniqueId() + "\n" + "IGN: " + player.getName())
                                .build()
                );
                break;
            }
            case "info": {
                DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
                if (databasePlayer.getDiscordID() == null) {
                    sender.sendMessage(ChatColor.RED + "Your account has not been linked! (/discord link) to link your account.");
                } else {
                    Warlords.newChain()
                            .async(() -> BotManager.jda.retrieveUserById(databasePlayer.getDiscordID()).queue(user -> {
                                if (user == null) {
                                    sender.sendMessage(ChatColor.GREEN + "Your account is linked to (" + databasePlayer.getDiscordID() + ").");
                                } else {
                                    sender.sendMessage(ChatColor.GREEN + "Your account is linked to " + user.getAsTag() + " (" + databasePlayer.getDiscordID() + ").");
                                }
                            }))
                            .execute();
                }
                break;
            }
            default: {
                sender.sendMessage(ChatColor.RED + "Invalid Arguments! /discord [link/unlink/info]");
                break;
            }
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("discord").setExecutor(this);
    }

    public Long getRandomNumber(int min, int max) {
        return min + (long) (Math.random() * (max - min));
    }

}
