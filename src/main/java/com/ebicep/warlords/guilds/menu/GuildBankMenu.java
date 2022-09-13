package com.ebicep.warlords.guilds.menu;

import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildPermissions;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.guilds.upgrades.GuildUpgrade;
import com.ebicep.warlords.guilds.upgrades.permanent.GuildUpgradesPermanent;
import com.ebicep.warlords.guilds.upgrades.temporary.GuildUpgradesTemporary;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;

public class GuildBankMenu {

    public static void openGuildBankMenu(Player player, Guild guild) {
        Menu menu = new Menu("Guild Bank", 9 * 5);

        menu.setItem(1, 1,
                new ItemBuilder(Material.ENCHANTMENT_TABLE)
                        .name(ChatColor.GOLD + "Guild Upgrades")
                        .get(),
                (m, e) -> {
                    openGuildUpgradesMenu(player, guild);
                }
        );

        menu.setItem(4, 4, MENU_BACK, (m, e) -> GuildMenu.openGuildMenu(guild, player, 1));
        menu.openForPlayer(player);
    }

    public static void openGuildUpgradesMenu(Player player, Guild guild) {
        Menu menu = new Menu("Guild Upgrades", 9 * 3);

        menu.setItem(2, 1,
                new ItemBuilder(Material.GOLD_BARDING)
                        .name(ChatColor.GREEN + "Temporary Upgrades")
                        .get(),
                (m, e) -> {
                    openGuildUpgradeTypeMenu(player, guild, GuildUpgradesTemporary.VALUES);

                }
        );
        menu.setItem(6, 1,
                new ItemBuilder(Material.DIAMOND_BARDING)
                        .name(ChatColor.GREEN + "Permanent Upgrades")
                        .get(),
                (m, e) -> {
                    openGuildUpgradeTypeMenu(player, guild, GuildUpgradesPermanent.VALUES);
                }
        );

        menu.setItem(4, 2, MENU_BACK, (m, e) -> openGuildBankMenu(player, guild));
        menu.openForPlayer(player);
    }

    public static <T extends Enum<T> & GuildUpgrade> void openGuildUpgradeTypeMenu(Player player, Guild guild, T[] values) {
        Menu menu = new Menu("Temporary Upgrades", 9 * 6);

        Optional<GuildPlayer> optionalGuildPlayer = guild.getPlayerMatchingUUID(player.getUniqueId());
        boolean canPurchaseUpgrades = optionalGuildPlayer.isPresent() && guild.playerHasPermission(optionalGuildPlayer.get(),
                GuildPermissions.PURCHASE_UPGRADES
        );

        List<AbstractGuildUpgrade<?>> upgrades = guild.getUpgrades();
        int index = 0;
        for (T value : values) {
            ItemBuilder itemBuilder = new ItemBuilder(value.getMaterial())
                    .name(ChatColor.GREEN + value.getName())
                    .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            for (AbstractGuildUpgrade<?> upgrade : upgrades) {
                if (upgrade.getUpgrade() == value) {
                    upgrade.modifyItem(itemBuilder);
                    break;
                }
            }
            if (canPurchaseUpgrades) {
                itemBuilder.addLore(ChatColor.GRAY + "\nClick to Purchase");
            }
            menu.setItem(index % 7 + 1, index / 7 + 1,
                    itemBuilder.get(),
                    (m, e) -> {
                        if (canPurchaseUpgrades) {
                            if (value instanceof GuildUpgradesTemporary) {
                                openGuildUpgradeTemporaryPurchaseMenu(player, guild, (GuildUpgradesTemporary) value);
                            }
                        }
                    }
            );
            index++;
        }

        menu.setItem(4, 5, MENU_BACK, (m, e) -> openGuildUpgradesMenu(player, guild));
        menu.openForPlayer(player);
    }

    public static void openGuildUpgradeTemporaryPurchaseMenu(Player player, Guild guild, GuildUpgradesTemporary upgrade) {
        Menu menu = new Menu(upgrade.name, 9 * 5);

        for (int i = 0; i < 9; i++) {
            int tier = i + 1;
            long upgradeCost = upgrade.getCost(tier);
            menu.setItem(i % 7 + 1, i / 7 + 1,
                    new ItemBuilder(Utils.getWoolFromIndex(i + 5))
                            .name(ChatColor.GREEN + "Tier " + tier)
                            .lore(
                                    ChatColor.GRAY + "Cost: " + ChatColor.GREEN + NumberFormat.addCommas(upgradeCost) +
                                            " Guild Coins",
                                    ChatColor.GRAY + "Effect Bonus: " + ChatColor.GREEN + upgrade.getEffectBonusFromTier(tier),
                                    "",
                                    ChatColor.RED + "WARNING: " + ChatColor.GRAY + "This will override the current upgrade."
                            )
                            .get(),
                    (m, e) -> {
                        Menu.openConfirmationMenu(player,
                                upgrade.name + " (T" + tier + ")",
                                3,
                                Collections.singletonList(ChatColor.GRAY + "Purchase Upgrade"),
                                Collections.singletonList(ChatColor.GRAY + "Go back"),
                                (m2, e2) -> {
                                    if (guild.getCoins(Timing.LIFETIME) >= upgradeCost) {
                                        guild.setCoins(Timing.LIFETIME, guild.getCoins(Timing.LIFETIME) - upgradeCost);
                                        guild.addUpgrade(upgrade.createUpgrade(tier));

                                        Instant now = Instant.now();
                                        Instant end = upgrade.expirationDate.apply(Instant.now());
                                        guild.sendGuildMessageToOnlinePlayers(ChatColor.YELLOW.toString() +
                                                        Duration.between(now, end).toHours() + " Hour Tier " +
                                                        tier + " " + upgrade.name + ChatColor.GREEN + " upgrade purchased!",
                                                true
                                        );
                                        openGuildUpgradeTypeMenu(player, guild, GuildUpgradesTemporary.VALUES);
                                    } else {
                                        player.sendMessage(ChatColor.RED + "You do not have enough guild coins to purchase this upgrade.");
                                    }
                                },
                                (m2, e2) -> openGuildUpgradeTemporaryPurchaseMenu(player, guild, upgrade),
                                (m2) -> {
                                }
                        );
                    }
            );
        }

        menu.setItem(4, 4, MENU_BACK, (m, e) -> openGuildUpgradeTypeMenu(player, guild, GuildUpgradesTemporary.VALUES));
        menu.openForPlayer(player);
    }

}
