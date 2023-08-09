package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@CommandAlias("discord")
@Conditions("database:player|bot")
public class DiscordCommand extends BaseCommand {

    public static BidiMap<UUID, Long> playerLinkKeys = new DualHashBidiMap<>();

    @Subcommand("link")
    @Description("Links your discord account to your minecraft account")
    public void link(Player player) {
        if (playerLinkKeys.containsKey(player.getUniqueId())) {
            player.sendMessage(Component.text("There is already an active key for your account, wait until it expires to link again.", NamedTextColor.RED));
            return;
        }
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        if (databasePlayer == null) {
            player.sendMessage(Component.text("Contact an Administrator.", NamedTextColor.RED));
            return;
        }
        if (databasePlayer.getDiscordID() != null) {
            player.sendMessage(Component.text("Your account is already linked! (/discord unlink) to unlink your account.", NamedTextColor.RED));
            return;
        }
        //random 5 digit key
        Long key = getRandomNumber(10000, 100000);
        while (playerLinkKeys.containsValue(key)) {
            key = getRandomNumber(10000, 100000);
        }
        playerLinkKeys.put(player.getUniqueId(), key);
        player.sendMessage(Component.text("Your discord link key is ", NamedTextColor.GRAY)
                                    .append(Component.text(key, NamedTextColor.GREEN))
                                    .append(Component.text(". Direct message (Balancer Bot) this key to link your account. This key will expire in 1 minute."))
        );
        Warlords.newChain().delay(1, TimeUnit.MINUTES).sync(() -> playerLinkKeys.remove(player.getUniqueId())).execute();

        BotManager.sendDebugMessage(
                new EmbedBuilder()
                        .setColor(16776960)
                        .setTitle("Link Key Created - " + key)
                        .setDescription("UUID: " + player.getUniqueId() + "\n" + "IGN: " + player.getName())
                        .build()
        );
    }

    public static Long getRandomNumber(int min, int max) {
        return min + (long) (Math.random() * (max - min));
    }

    @Subcommand("unlink")
    @Description("Unlinks your discord account from your minecraft account")
    public void unlink(Player player) {
        DatabaseManager.updatePlayer(player, databasePlayer -> {
            if (databasePlayer.getDiscordID() == null) {
                player.sendMessage(Component.text("Your account has not been linked! (/discord link) to link your account.", NamedTextColor.RED));
                return;
            }
            Long oldID = databasePlayer.getDiscordID();
            databasePlayer.setDiscordID(null);
            player.sendMessage(Component.text("Your account has been unlinked. You will need to relink it /discord link", NamedTextColor.GRAY));

            BotManager.sendDebugMessage(
                    new EmbedBuilder()
                            .setColor(15158332)
                            .setTitle("Player Unlinked - " + oldID)
                            .setDescription("UUID: " + player.getUniqueId() + "\n" + "IGN: " + player.getName())
                            .build()
            );
        });
    }

    @Subcommand("info")
    @Description("Shows information about your linked discord account")
    public void info(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        if (databasePlayer == null) {
            player.sendMessage(Component.text("Contact an Administrator.", NamedTextColor.RED));
            return;
        }
        if (databasePlayer.getDiscordID() == null) {
            player.sendMessage(Component.text("Your account has not been linked! (/discord link) to link your account.", NamedTextColor.RED));
        } else {
            Warlords.newChain()
                    .async(() -> BotManager.jda.retrieveUserById(databasePlayer.getDiscordID()).queue(user -> {
                        if (user == null) {
                            player.sendMessage(Component.text("Your account is linked to (" + databasePlayer.getDiscordID() + ").", NamedTextColor.GREEN));
                        } else {
                            player.sendMessage(Component.text("Your account is linked to " + user.getAsTag() + " (" + databasePlayer.getDiscordID() + ").", NamedTextColor.GREEN));
                        }
                    }))
                    .execute();
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }
}
