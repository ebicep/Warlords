package com.ebicep.warlords.guilds.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildExperienceUtils;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.logs.types.oneplayer.GuildLogCoinsConverted;
import com.ebicep.warlords.guilds.logs.types.oneplayer.upgrades.GuildLogUpgradeTemporary;
import com.ebicep.warlords.guilds.upgrades.permanent.GuildUpgradesPermanent;
import com.ebicep.warlords.guilds.upgrades.temporary.GuildUpgradesTemporary;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.rewards.Currencies;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.signgui.SignGUI;
import com.ebicep.warlords.util.java.Pair;
import net.dv8tion.jda.internal.entities.DataMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;

public class GuildBankMenu {

    public static void openGuildBankMenu(Player player, Guild guild) {
        Menu menu = new Menu("Guild Bank", 9 * 4);

        menu.setItem(1, 1,
                new ItemBuilder(Material.GOLD_BARDING)
                        .name(ChatColor.GREEN + "Temporary Blessings")
                        .get(),
                (m, e) -> {
                    GuildUpgradeMenu.openGuildUpgradeTypeMenu(player, guild, "Temporary Blessings", GuildUpgradesTemporary.VALUES);

                }
        );
        menu.setItem(2, 1,
                new ItemBuilder(Material.DIAMOND_BARDING)
                        .name(ChatColor.GREEN + "Permanent Upgrades")
                        .get(),
                (m, e) -> {
                    GuildUpgradeMenu.openGuildUpgradeTypeMenu(player, guild, "Permanent Upgrades", GuildUpgradesPermanent.VALUES);
                }
        );
        menu.setItem(3, 1,
                new ItemBuilder(Material.EMERALD)
                        .name(ChatColor.GREEN + "Convert Coins")
                        .lore(
                                ChatColor.GRAY + "Convert your Player Coins to Guild Coins",
                                ChatColor.GRAY + "Guild Level 1-5: " + ChatColor.GOLD + "100:1",
                                ChatColor.GRAY + "Guild Level 6-10: " + ChatColor.GOLD + "40:1",
                                ChatColor.GRAY + "Guild Level 11-15: " + ChatColor.GOLD + "10:1",
                                ChatColor.GRAY + "Guild Level 16-20: " + ChatColor.GOLD + "5:1"
                        )
                        .get(),
                (m, e) -> {
                    if (DatabaseManager.playerService == null) {
                        return;
                    }
                    Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
                    if (guildPlayerPair == null) {
                        return;
                    }
                    GuildPlayer guildPlayer = guildPlayerPair.getB();
                    long dailyCoinsConverted = guildPlayer.getDailyCoinsConverted();
                    if (dailyCoinsConverted >= 10000) {
                        player.sendMessage(ChatColor.RED + "You can only covert up to 10,000 guild coins per day.");
                        return;
                    }
                    int coinConversionRatio;
                    int guildLevel = GuildExperienceUtils.getLevelFromExp(guild.getExperience(Timing.LIFETIME));
                    if (guildLevel <= 5) {
                        coinConversionRatio = 100;
                    } else if (guildLevel <= 10) {
                        coinConversionRatio = 40;
                    } else if (guildLevel <= 15) {
                        coinConversionRatio = 10;
                    } else {
                        coinConversionRatio = 5;
                    }
                    DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
                    DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                    Long playerCoins = pveStats.getCurrencyValue(Currencies.COIN);

                    int maxCoinsCanConvert = (int) ((10000 - dailyCoinsConverted) / coinConversionRatio);
                    SignGUI.open(
                            player,
                            new String[]{
                                    "",
                                    "Max Convertable: " + maxCoinsCanConvert,
                                    "Conversion Rate",
                                    coinConversionRatio + ":1"
                            },
                            (p, lines) -> {
                                String amountString = lines[0];
                                try {
                                    int playerCoinsToConvert = Integer.parseInt(amountString);
                                    if (playerCoinsToConvert <= 0) {
                                        player.sendMessage(ChatColor.RED + "You must enter a positive number.");
                                        openGuildBankMenu(player, guild);
                                        return;
                                    }
                                    if (playerCoinsToConvert > playerCoins) {
                                        player.sendMessage(ChatColor.RED + "You do not have enough player coins to convert to guild coins.");
                                        openGuildBankMenu(player, guild);
                                        return;
                                    }
                                    if (playerCoinsToConvert > maxCoinsCanConvert) {
                                        player.sendMessage(ChatColor.RED + "You cannot exceed the max convertable amount.");
                                        openGuildBankMenu(player, guild);
                                        return;
                                    }
                                    int guildCoinsGained = playerCoinsToConvert / coinConversionRatio;
                                    Menu.openConfirmationMenu(
                                            player,
                                            "Confirm Conversion",
                                            3,
                                            Arrays.asList(
                                                    ChatColor.GRAY + "Convert",
                                                    ChatColor.GREEN + "+" + guildCoinsGained + " Guild Coins",
                                                    ChatColor.RED + "-" + playerCoinsToConvert + " Player Coins"
                                            ),
                                            Collections.singletonList(ChatColor.GRAY + "Go back"),
                                            (m2, e2) -> {
                                                guild.addCoins(guildCoinsGained);
                                                guildPlayer.addDailyCoinsConverted(guildCoinsGained);
                                                guild.log(new GuildLogCoinsConverted(player.getUniqueId(), playerCoinsToConvert, guildCoinsGained));
                                                guild.queueUpdate();
                                                openGuildBankMenu(player, guild);
                                            },
                                            (m2, e2) -> openGuildBankMenu(player, guild),
                                            (m2) -> {
                                            }
                                    );
                                } catch (NumberFormatException ex) {
                                    player.sendMessage(ChatColor.RED + "Invalid Number!");
                                    openGuildBankMenu(player, guild);
                                }
                            }
                    );
                }
        );

        menu.setItem(4, 3, MENU_BACK, (m, e) -> GuildMenu.openGuildMenu(guild, player, 1));
        menu.openForPlayer(player);
    }


    public static void openGuildCoinConversionMenu(Player player, Guild guild) {
        Menu menu = new Menu("Guild Coin Conversion", 9 * 6);

        menu.openForPlayer(player);
    }


}
