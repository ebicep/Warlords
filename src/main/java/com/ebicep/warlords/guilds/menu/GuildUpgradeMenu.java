package com.ebicep.warlords.guilds.menu;

import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildPermissions;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.guilds.upgrades.GuildUpgrade;
import com.ebicep.warlords.guilds.upgrades.permanent.GuildUpgradePermanent;
import com.ebicep.warlords.guilds.upgrades.permanent.GuildUpgradesPermanent;
import com.ebicep.warlords.guilds.upgrades.temporary.GuildUpgradesTemporary;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;

public class GuildUpgradeMenu {
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
            AbstractGuildUpgrade<?> upgrade = null;
            for (AbstractGuildUpgrade<?> abstractGuildUpgrade : upgrades) {
                if (abstractGuildUpgrade.getUpgrade() == value) {
                    upgrade = abstractGuildUpgrade;
                    break;
                }
            }
            if (upgrade != null) {
                upgrade.modifyItem(itemBuilder);
                if (canPurchaseUpgrades) {
                    upgrade.addItemClickLore(itemBuilder);
                }
            } else {
                itemBuilder.addLore(ChatColor.GRAY + "\nClick to Purchase");
            }

            AbstractGuildUpgrade<?> finalUpgrade = upgrade;
            menu.setItem(index % 7 + 1, index / 7 + 1,
                    itemBuilder.get(),
                    (m, e) -> {
                        if (canPurchaseUpgrades) {
                            if (value instanceof GuildUpgradesTemporary) {
                                openGuildUpgradeTemporaryPurchaseMenu(player, guild, (GuildUpgradesTemporary) value);
                            } else if (value instanceof GuildUpgradesPermanent) {
                                openGuildUpgradePermanentPurchaseMenu(player, guild, (GuildUpgradesPermanent) value, (GuildUpgradePermanent) finalUpgrade);
                            }
                        }
                    }
            );
            index++;
        }

        menu.setItem(4, 5, MENU_BACK, (m, e) -> GuildBankMenu.openGuildBankMenu(player, guild));
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
                                        guild.queueUpdate();

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

    public static void openGuildUpgradePermanentPurchaseMenu(
            Player player,
            Guild guild,
            GuildUpgradesPermanent upgradesPermanent,
            GuildUpgradePermanent upgrade
    ) {
        int nextTier = upgrade == null ? 1 : upgrade.getTier() + 1;
        if (nextTier > 9) {
            player.sendMessage(ChatColor.RED + "You have reached the maximum tier for this upgrade.");
            return;
        }
        Menu.openConfirmationMenu(player,
                upgradesPermanent.name + " (T" + nextTier + ")",
                3,
                Arrays.asList(
                        ChatColor.GRAY + "Purchase Upgrade",
                        "",
                        ChatColor.GRAY + "Cost: " + ChatColor.GREEN + NumberFormat.addCommas(upgradesPermanent.getCost(nextTier)) + " Guild Coins"
                ),
                Collections.singletonList(ChatColor.GRAY + "Go back"),
                (m2, e2) -> {
                    long upgradeCost = upgradesPermanent.getCost(nextTier);
                    if (guild.getCoins(Timing.LIFETIME) >= upgradeCost) {
                        guild.setCoins(Timing.LIFETIME, guild.getCoins(Timing.LIFETIME) - upgradeCost);
                        guild.addUpgrade(upgradesPermanent.createUpgrade(nextTier));
                        upgradesPermanent.onPurchase(guild, nextTier);
                        guild.queueUpdate();

                        guild.sendGuildMessageToOnlinePlayers(
                                ChatColor.YELLOW + "Permanent Tier " + nextTier + " " + upgradesPermanent.name + ChatColor.GREEN + " upgrade purchased!",
                                true
                        );
                        openGuildUpgradeTypeMenu(player, guild, GuildUpgradesPermanent.VALUES);
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have enough guild coins to purchase this upgrade.");
                    }
                },
                (m2, e2) -> openGuildUpgradeTypeMenu(player, guild, GuildUpgradesPermanent.VALUES),
                (m2) -> {
                }
        );
    }
}
