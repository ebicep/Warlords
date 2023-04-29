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
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
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
        TextComponent grayBase = Component.empty().color(NamedTextColor.GRAY);
        menu.setItem(0, 0,
                new ItemBuilder(Material.OAK_SIGN)
                        .name(Component.text("Guild Information", NamedTextColor.GREEN))
                        .lore(
                                grayBase.content("Name: ").append(Component.text(guild.getName(), NamedTextColor.YELLOW)),
                                grayBase.content("Created: ").append(Component.text(AbstractGuildLog.FORMATTER.format(guild.getCreationDate()), NamedTextColor.YELLOW)),
                                grayBase.content("Level: ").append(Component.text(guildLevel, NamedTextColor.YELLOW)),
                                grayBase.content("Experience: ").append(Component.text(NumberFormat.addCommas(guild.getExperience(Timing.LIFETIME)), NamedTextColor.YELLOW)),
                                grayBase.content("Coins: ").append(Component.text(NumberFormat.addCommaAndRound(guild.getCurrentCoins()), NamedTextColor.YELLOW)),
                                grayBase.content("Members: ").append(Component.text(guild.getPlayers().size(), NamedTextColor.YELLOW)
                                                                              .append(Component.text("/", NamedTextColor.AQUA))
                                                                              .append(Component.text(guild.getPlayerLimit()))),
                                grayBase.content("Rank: ").append(Component.text((roleOfPlayer != null ? roleOfPlayer.getRoleName() : "None"), NamedTextColor.YELLOW))
                        )
                        .get(),
                (m, e) -> {
                }
        );
        menu.setItem(2, 0,
                new ItemBuilder(Material.GOLDEN_HORSE_ARMOR)
                        .name(Component.text("Temporary Blessings", NamedTextColor.GREEN))
                        .lore(
                                grayBase.content("These upgrades last 24 hours and "),
                                grayBase.content("will only affect players in the guild.")
                        )
                        .get(),
                (m, e) -> GuildUpgradeMenu.openGuildUpgradeTypeMenu(player, guild, "Temporary Blessings", GuildUpgradesTemporary.VALUES)
        );
        menu.setItem(3, 0,
                new ItemBuilder(Material.DIAMOND_HORSE_ARMOR)
                        .name(Component.text("Permanent Upgrades", NamedTextColor.GREEN))
                        .lore(
                                grayBase.content("These upgrades last permanently and "),
                                grayBase.content("will only affect players in the guild.")
                        )
                        .get(),
                (m, e) -> GuildUpgradeMenu.openGuildUpgradeTypeMenu(player, guild, "Permanent Upgrades", GuildUpgradesPermanent.VALUES)
        );
        int conversionRatio = Guild.getConversionRatio(guild);
        menu.setItem(4, 0,
                new ItemBuilder(Material.EMERALD)
                        .name(Component.text("Convert Coins", NamedTextColor.GREEN))
                        .lore(
                                Component.text("Convert your Player Coins to Guild Coins", NamedTextColor.GRAY),
                                Component.empty(),
                                Component.text("Conversion Ratios", NamedTextColor.GOLD),
                                Component.textOfChildren(
                                        Component.text("Guild Level 1-5: ", conversionRatio == 100 ? NamedTextColor.GREEN : NamedTextColor.GRAY),
                                        Component.text("100", NamedTextColor.GOLD),
                                        Component.text(":", NamedTextColor.DARK_GRAY),
                                        Component.text("1", NamedTextColor.GOLD)
                                ),
                                Component.textOfChildren(
                                        Component.text("Guild Level 6-10: ", conversionRatio == 40 ? NamedTextColor.GREEN : NamedTextColor.GRAY),
                                        Component.text("40", NamedTextColor.GOLD),
                                        Component.text(":", NamedTextColor.DARK_GRAY),
                                        Component.text("1", NamedTextColor.GOLD)
                                ),
                                Component.textOfChildren(
                                        Component.text("Guild Level 11-15: ", conversionRatio == 10 ? NamedTextColor.GREEN : NamedTextColor.GRAY),
                                        Component.text("10", NamedTextColor.GOLD),
                                        Component.text(":", NamedTextColor.DARK_GRAY),
                                        Component.text("1", NamedTextColor.GOLD)
                                ),
                                Component.textOfChildren(
                                        Component.text("Guild Level 16-20: ", conversionRatio == 5 ? NamedTextColor.GREEN : NamedTextColor.GRAY),
                                        Component.text("5", NamedTextColor.GOLD),
                                        Component.text(":", NamedTextColor.DARK_GRAY),
                                        Component.text("1", NamedTextColor.GOLD)
                                )
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
                            .name(Component.text("Modify Guild Tag")
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
                            .name(Component.text("Edit Permissions", NamedTextColor.GREEN))
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
                                .name(Component.text(guildPlayer.getName(), NamedTextColor.GREEN))
                                .lore(
                                        grayBase.content("Join Date: ").append(Component.text(AbstractGuildLog.FORMATTER.format(guildPlayer.getJoinDate()), NamedTextColor.YELLOW)),
                                        grayBase.content("Role: ").append(Component.text(guild.getRoleOfPlayer(guildPlayer.getUUID()).getRoleName(), NamedTextColor.AQUA)),
                                        grayBase.content("Coins: "),
                                        grayBase.content(" - Lifetime: ")
                                                .append(Component.text(NumberFormat.addCommas(guildPlayer.getCoins(Timing.LIFETIME)), NamedTextColor.YELLOW)),
                                        grayBase.content(" - Weekly: ").append(Component.text(NumberFormat.addCommas(guildPlayer.getCoins(Timing.WEEKLY)), NamedTextColor.YELLOW)),
                                        grayBase.content(" - Daily: ").append(Component.text(NumberFormat.addCommas(guildPlayer.getCoins(Timing.DAILY)), NamedTextColor.YELLOW)),
                                        grayBase.content("Coins Converted: "),
                                        grayBase.content(" - Lifetime: ").append(Component.text(NumberFormat.addCommas(guildPlayer.getCoinsConverted()), NamedTextColor.YELLOW)),
                                        grayBase.content(" - Daily: ").append(Component.text(NumberFormat.addCommas(guildPlayer.getDailyCoinsConverted()), NamedTextColor.YELLOW)),
                                        grayBase.content("Experience: "),
                                        grayBase.content(" - Lifetime: ")
                                                .append(Component.text(NumberFormat.addCommas(guildPlayer.getExperience(Timing.LIFETIME)), NamedTextColor.YELLOW)),
                                        grayBase.content(" - Weekly: ")
                                                .append(Component.text(NumberFormat.addCommas(guildPlayer.getExperience(Timing.WEEKLY)), NamedTextColor.YELLOW)),
                                        grayBase.content(" - Daily: ")
                                                .append(Component.text(NumberFormat.addCommas(guildPlayer.getExperience(Timing.DAILY)), NamedTextColor.YELLOW))
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
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page - 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> openGuildMenu(guild, player, page - 1)
            );
        }
        if (guild.getPlayers().size() > (page * playerPerPage)) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page + 1), NamedTextColor.YELLOW))
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
            player.sendMessage(Component.text("You must be in the guild for at least 2 days to convert coins.", NamedTextColor.RED));
            return;
        }
        long dailyCoinsConverted = guildPlayer.getDailyCoinsConverted();
        if (dailyCoinsConverted >= 10000) {
            player.sendMessage(Component.text("You can only covert up to 10,000 guild coins per day.", NamedTextColor.RED));
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
                            player.sendMessage(Component.text("Invalid Number!", NamedTextColor.RED));
                            openGuildMenuAfterTick(guild, player);
                            return null;
                        }
                        int playerCoinsToConvert = Integer.parseInt(amountString);
                        if (playerCoinsToConvert <= 0) {
                            player.sendMessage(Component.text("You must enter a positive number.", NamedTextColor.RED));
                            openGuildMenuAfterTick(guild, player);
                            return null;
                        }
                        if (playerCoinsToConvert > playerCoins) {
                            player.sendMessage(Component.text("You do not have enough player coins to convert to guild coins.", NamedTextColor.RED));
                            openGuildMenuAfterTick(guild, player);
                            return null;
                        }
                        if (playerCoinsToConvert < coinConversionRatio) {
                            player.sendMessage(Component.text("You must enter a number greater than or equal to " + coinConversionRatio + ".", NamedTextColor.RED));
                            openGuildMenuAfterTick(guild, player);
                            return null;
                        }
                        if (playerCoinsToConvert > maxCoinsCanConvert) {
                            player.sendMessage(Component.text("You cannot exceed the max convertable amount.", NamedTextColor.RED));
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
                                                    Component.empty().color(NamedTextColor.GRAY)
                                                             .append(Component.text(player.getName(), NamedTextColor.AQUA))
                                                             .append(Component.text(" converted "))
                                                             .append(Component.text(playerCoinsToConvert, NamedTextColor.GREEN))
                                                             .append(Component.text(" player coins to "))
                                                             .append(Component.text(guildCoinsGained, NamedTextColor.GREEN))
                                                             .append(Component.text(" guild coins.")),
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
