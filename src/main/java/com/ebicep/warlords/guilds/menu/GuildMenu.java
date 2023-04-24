package com.ebicep.warlords.guilds.menu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.guilds.GuildLeaderboardManager;
import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.guilds.*;
import com.ebicep.warlords.guilds.logs.AbstractGuildLog;
import com.ebicep.warlords.guilds.logs.types.oneplayer.GuildLogCoinsConverted;
import com.ebicep.warlords.guilds.upgrades.permanent.GuildUpgradesPermanent;
import com.ebicep.warlords.guilds.upgrades.temporary.GuildUpgradesTemporary;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import de.rapha149.signgui.SignGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public class GuildMenu {

    public static void openGuildMenu(Guild guild, Player player, int page) {
        List<GuildPlayer> guildPlayers = guild.getPlayers();
        int playerCount = guildPlayers.size();
        int rows = page == 1 ? Math.min(6, 4 + (playerCount - 1) / 9) : 6;
        Menu menu = new Menu("Guild Settings: " + guild.getName(), 9 * rows);

        int guildLevel = GuildExperienceUtils.getLevelFromExp(guild.getExperience(Timing.LIFETIME));
        GuildRole roleOfPlayer = guild.getRoleOfPlayer(player.getUniqueId());
        menu.setItem(0, 0,
                new ItemBuilder(Material.OAK_SIGN)
                        .name(ChatColor.GREEN + "Guild Information")
                        .loreLEGACY(
                                ChatColor.GRAY + "Name: " + ChatColor.YELLOW + guild.getName(),
                                ChatColor.GRAY + "Created: " + ChatColor.YELLOW + AbstractGuildLog.FORMATTER.format(guild.getCreationDate()),
                                ChatColor.GRAY + "Level: " + ChatColor.YELLOW + guildLevel,
                                ChatColor.GRAY + "Experience: " + ChatColor.YELLOW + NumberFormat.addCommas(guild.getExperience(Timing.LIFETIME)),
                                ChatColor.GRAY + "Coins: " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(guild.getCurrentCoins()),
                                ChatColor.GRAY + "Members: " + ChatColor.YELLOW + guild.getPlayers()
                                                                                       .size() + ChatColor.AQUA + "/" + ChatColor.YELLOW + guild.getPlayerLimit(),
                                ChatColor.GRAY + "Rank: " + ChatColor.YELLOW + (roleOfPlayer != null ? roleOfPlayer.getRoleName() : "None")
                        )
                        .get(),
                (m, e) -> {
                }
        );
        menu.setItem(2, 0,
                new ItemBuilder(Material.GOLDEN_HORSE_ARMOR)
                        .name(ChatColor.GREEN + "Temporary Blessings")
                        .loreLEGACY(ChatColor.GRAY + "These upgrades last 24 hours and " +
                                "\nwill only affect players in the guild.")
                        .get(),
                (m, e) -> GuildUpgradeMenu.openGuildUpgradeTypeMenu(player, guild, "Temporary Blessings", GuildUpgradesTemporary.VALUES)
        );
        menu.setItem(3, 0,
                new ItemBuilder(Material.DIAMOND_HORSE_ARMOR)
                        .name(ChatColor.GREEN + "Permanent Upgrades")
                        .loreLEGACY(ChatColor.GRAY + "These upgrades last permanently and " +
                                "\nwill only affect players in the guild."
                        ).get(),
                (m, e) -> GuildUpgradeMenu.openGuildUpgradeTypeMenu(player, guild, "Permanent Upgrades", GuildUpgradesPermanent.VALUES)
        );
        int conversionRatio = Guild.getConversionRatio(guild);
        menu.setItem(4, 0,
                new ItemBuilder(Material.EMERALD)
                        .name(ChatColor.GREEN + "Convert Coins")
                        .loreLEGACY(
                                ChatColor.GRAY + "Convert your Player Coins to Guild Coins",
                                "",
                                ChatColor.GOLD + "Conversion Ratios",
                                (conversionRatio == 100 ? ChatColor.GREEN : ChatColor.GRAY) + "Guild Level 1-5: " + ChatColor.GOLD + "100" + ChatColor.DARK_GRAY + ":" + ChatColor.GOLD + "1",
                                (conversionRatio == 40 ? ChatColor.GREEN : ChatColor.GRAY) + "Guild Level 6-10: " + ChatColor.GOLD + "40" + ChatColor.DARK_GRAY + ":" + ChatColor.GOLD + "1",
                                (conversionRatio == 10 ? ChatColor.GREEN : ChatColor.GRAY) + "Guild Level 11-15: " + ChatColor.GOLD + "10" + ChatColor.DARK_GRAY + ":" + ChatColor.GOLD + "1",
                                (conversionRatio == 5 ? ChatColor.GREEN : ChatColor.GRAY) + "Guild Level 16-20: " + ChatColor.GOLD + "5" + ChatColor.DARK_GRAY + ":" + ChatColor.GOLD + "1"
                        )
                        .get(),
                (m, e) -> onCoinConversion(guild, player)
        );

        /*
        guild.getPlayerMatchingUUID(player.getUniqueId()).ifPresent(guildPlayer -> {
            if (!guild.playerHasPermission(guildPlayer, GuildPermissions.MODIFY_TAG)) {
                return;
            }
            menu.setItem(7, 0,
                    new ItemBuilder(GuildPermissions.MODIFY_TAG.material)
                            .name(ChatColor.GREEN + "Modify Guild Tag")
                            .lore(ChatColor.GRAY + "Modify the tag of your guild")
                            .get(),
                    (m, e) -> {
                        openGuildTagMenu(guild, player);
                    }
            );
        });

         */

        if (player.getUniqueId().equals(guild.getCurrentMaster())) {
            menu.setItem(8, 0,
                    new ItemBuilder(Material.LEVER)
                            .name(ChatColor.GREEN + "Edit Permissions")
                            .get(),
                    (m, e) -> GuildRoleMenu.openRoleSelectorMenu(guild, player)
            );
        }

        int playerPerPage = 36;
        for (int i = 0; i < playerPerPage; i++) {
            int index = ((page - 1) * playerPerPage) + i;
            if (index < playerCount) {
                GuildPlayer guildPlayer = guildPlayers.get(index);
                menu.setItem(i % 9, i / 9 + 1,
                        new ItemBuilder(HeadUtils.getHead(guildPlayer.getUUID()))
                                .name(ChatColor.GREEN + guildPlayer.getName())
                                .loreLEGACY(
                                        ChatColor.GRAY + "Join Date: " + ChatColor.YELLOW + AbstractGuildLog.FORMATTER.format(guildPlayer.getJoinDate()),
                                        ChatColor.GRAY + "Role: " + ChatColor.AQUA + guild.getRoleOfPlayer(guildPlayer.getUUID()).getRoleName(),
                                        ChatColor.GRAY + "Coins: ",
                                        ChatColor.GRAY + " - Lifetime: " + ChatColor.YELLOW + NumberFormat.addCommas(guildPlayer.getCoins(Timing.LIFETIME)),
                                        ChatColor.GRAY + " - Weekly: " + ChatColor.YELLOW + NumberFormat.addCommas(guildPlayer.getCoins(Timing.WEEKLY)),
                                        ChatColor.GRAY + " - Daily: " + ChatColor.YELLOW + NumberFormat.addCommas(guildPlayer.getCoins(Timing.DAILY)),
                                        ChatColor.GRAY + "Coins Converted: ",
                                        ChatColor.GRAY + " - Lifetime: " + ChatColor.YELLOW + NumberFormat.addCommas(guildPlayer.getCoinsConverted()),
                                        ChatColor.GRAY + " - Daily: " + ChatColor.YELLOW + NumberFormat.addCommas(guildPlayer.getDailyCoinsConverted()),
                                        ChatColor.GRAY + "Experience: ",
                                        ChatColor.GRAY + " - Lifetime: " + ChatColor.YELLOW + NumberFormat.addCommas(guildPlayer.getExperience(Timing.LIFETIME)),
                                        ChatColor.GRAY + " - Weekly: " + ChatColor.YELLOW + NumberFormat.addCommas(guildPlayer.getExperience(Timing.WEEKLY)),
                                        ChatColor.GRAY + " - Daily: " + ChatColor.YELLOW + NumberFormat.addCommas(guildPlayer.getExperience(Timing.DAILY))
                                )
                                .get(),
                        (m, e) -> {

                        }
                );
            } else {
                break;
            }
        }

        if (page - 1 > 0) {
            menu.setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .loreLEGACY(ChatColor.YELLOW + "Page " + (page - 1))
                            .get(),
                    (m, e) -> openGuildMenu(guild, player, page - 1)
            );
        }
        if (guild.getPlayers().size() > (page * playerPerPage)) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .loreLEGACY(ChatColor.YELLOW + "Page " + (page + 1))
                            .get(),
                    (m, e) -> openGuildMenu(guild, player, page + 1)
            );
        }

        menu.setItem(4, rows - 1, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void onCoinConversion(Guild guild, Player player) {
        Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
        if (guildPlayerPair == null) {
            return;
        }
        GuildPlayer guildPlayer = guildPlayerPair.getB();
        if (!guildPlayer.getJoinDate().isBefore(Instant.now().minus(2, ChronoUnit.DAYS))) {
            player.sendMessage(ChatColor.RED + "You must be in the guild for at least 2 days to convert coins.");
            return;
        }
        long dailyCoinsConverted = guildPlayer.getDailyCoinsConverted();
        if (dailyCoinsConverted >= 10000) {
            player.sendMessage(ChatColor.RED + "You can only covert up to 10,000 guild coins per day.");
            return;
        }
        int coinConversionRatio = Guild.getConversionRatio(guild);
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            Long playerCoins = databasePlayer.getPveStats().getCurrencyValue(Currencies.COIN);

            int maxCoinsCanConvert = (int) ((10000 - dailyCoinsConverted) * coinConversionRatio);
            new SignGUI()
                    .lines("",
                            "Bal: " + NumberFormat.addCommaAndRound(playerCoins),
                            "Max: " + NumberFormat.addCommaAndRound(maxCoinsCanConvert),
                            "Ratio: " + coinConversionRatio + ":1"
                    )
                    .onFinish((p, lines) -> {
                        String amountString;
                        try {
                            amountString = lines[0];
                        } catch (NumberFormatException ex) {
                            player.sendMessage(ChatColor.RED + "Invalid Number!");
                            openGuildMenuAfterTick(guild, player);
                            return null;
                        }
                        int playerCoinsToConvert = Integer.parseInt(amountString);
                        if (playerCoinsToConvert <= 0) {
                            player.sendMessage(ChatColor.RED + "You must enter a positive number.");
                            openGuildMenuAfterTick(guild, player);
                            return null;
                        }
                        if (playerCoinsToConvert > playerCoins) {
                            player.sendMessage(ChatColor.RED + "You do not have enough player coins to convert to guild coins.");
                            openGuildMenuAfterTick(guild, player);
                            return null;
                        }
                        if (playerCoinsToConvert < coinConversionRatio) {
                            player.sendMessage(ChatColor.RED + "You must enter a number greater than or equal to " + coinConversionRatio + ".");
                            openGuildMenuAfterTick(guild, player);
                            return null;
                        }
                        if (playerCoinsToConvert > maxCoinsCanConvert) {
                            player.sendMessage(ChatColor.RED + "You cannot exceed the max convertable amount.");
                            openGuildMenuAfterTick(guild, player);
                            return null;
                        }
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                int guildCoinsGained = playerCoinsToConvert / coinConversionRatio;
                                Menu.openConfirmationMenu0(
                                        player,
                                        "Confirm Conversion",
                                        3,
                                        Arrays.asList(
                                                Component.text("+" + guildCoinsGained + " Guild Coins", NamedTextColor.GREEN),
                                                Component.text("-" + playerCoinsToConvert + " Player Coins", NamedTextColor.RED)
                                        ),
                                        Menu.GO_BACK,
                                        (m2, e2) -> {
                                            databasePlayer.getPveStats().subtractCurrency(Currencies.COIN, playerCoinsToConvert);
                                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                            guild.addCurrentCoins(guildCoinsGained);
                                            guildPlayer.addDailyCoinsConverted(guildCoinsGained);
                                            guild.log(new GuildLogCoinsConverted(player.getUniqueId(), playerCoinsToConvert, guildCoinsGained));
                                            guild.queueUpdate();
                                            guild.sendGuildMessageToOnlinePlayers(
                                                    ChatColor.AQUA + player.getName() + ChatColor.GRAY + " converted " +
                                                            ChatColor.GREEN + playerCoinsToConvert + ChatColor.GRAY + " player coins to " +
                                                            ChatColor.GREEN + guildCoinsGained + ChatColor.GRAY + " guild coins.",
                                                    true
                                            );
                                            GuildLeaderboardManager.recalculateAllLeaderboards();
                                            openGuildMenu(guild, player, 1);
                                        },
                                        (m2, e2) -> openGuildMenu(guild, player, 1),
                                        (m2) -> {
                                        }
                                );

                            }
                        }.runTaskLater(Warlords.getInstance(), 1);
                        return null;
                    }).open(player);
        });

    }

    private static void openGuildMenuAfterTick(Guild guild, Player player) {
        openGuildMenu(guild, player, 1);
    }

}
