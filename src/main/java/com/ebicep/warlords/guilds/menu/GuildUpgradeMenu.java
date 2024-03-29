package com.ebicep.warlords.guilds.menu;

import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildPermissions;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.logs.types.oneplayer.upgrades.GuildLogUpgradePermanent;
import com.ebicep.warlords.guilds.logs.types.oneplayer.upgrades.GuildLogUpgradeTemporary;
import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.guilds.upgrades.GuildUpgrade;
import com.ebicep.warlords.guilds.upgrades.permanent.GuildUpgradePermanent;
import com.ebicep.warlords.guilds.upgrades.permanent.GuildUpgradesPermanent;
import com.ebicep.warlords.guilds.upgrades.temporary.GuildUpgradesTemporary;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;

public class GuildUpgradeMenu {

    public static <T extends Enum<T> & GuildUpgrade> void openGuildUpgradeTypeMenu(Player player, Guild guild, String name, T[] values) {
        Menu menu = new Menu(name, 9 * 4);

        Optional<GuildPlayer> optionalGuildPlayer = guild.getPlayerMatchingUUID(player.getUniqueId());
        boolean canPurchaseUpgrades = optionalGuildPlayer.isPresent() && guild.playerHasPermission(optionalGuildPlayer.get(),
                GuildPermissions.PURCHASE_UPGRADES
        );

        List<AbstractGuildUpgrade<?>> upgrades = guild.getUpgrades();
        int index = 0;
        for (T value : values) {
            ItemBuilder itemBuilder = new ItemBuilder(value.getMaterial())
                    .name(Component.text(value.getName(), NamedTextColor.GREEN))
                    .lore(WordWrap.wrap(Component.text(value.getDescription(), NamedTextColor.GRAY), 160));
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
            } else if (canPurchaseUpgrades) {
                itemBuilder.addLore(
                        Component.empty(),
                        Component.text("Click to Purchase", NamedTextColor.YELLOW)
                );
            }

            AbstractGuildUpgrade<?> finalUpgrade = upgrade;
            menu.setItem(index % 7 + 1, index / 7 + 1,
                    itemBuilder.get(),
                    (m, e) -> {
                        if (canPurchaseUpgrades) {
                            if (value instanceof GuildUpgradesTemporary) {
                                openGuildUpgradeTemporaryPurchaseMenu(player, guild, name, (GuildUpgradesTemporary) value);
                            } else if (value instanceof GuildUpgradesPermanent) {
                                openGuildUpgradePermanentPurchaseMenu(player, guild, name, (GuildUpgradesPermanent) value,
                                        (GuildUpgradePermanent) finalUpgrade
                                );
                            }
                        }
                    }
            );
            index++;
        }

        menu.setItem(4, 3, MENU_BACK, (m, e) -> GuildMenu.openGuildMenu(guild, player, 1));
        menu.openForPlayer(player);
    }

    public static void openGuildUpgradeTemporaryPurchaseMenu(Player player, Guild guild, String name, GuildUpgradesTemporary upgradesTemporary) {
        Menu menu = new Menu(upgradesTemporary.name, 9 * 4);

        for (int i = 0; i < 9; i++) {
            int tier = i + 1;
            long upgradeCost = upgradesTemporary.getCost(tier);
            menu.setItem(i % 7 + 1, i / 7 + 1,
                    new ItemBuilder(Utils.getWoolFromIndex(i + 5))
                            .name(Component.text("Tier " + tier, NamedTextColor.GREEN))
                            .lore(
                                    Component.text("Effect Bonus: ", NamedTextColor.YELLOW)
                                             .append(Component.text(upgradesTemporary.getEffectBonusFromTier(tier), NamedTextColor.GREEN)),
                                    Component.text("Cost: ", NamedTextColor.GRAY)
                                             .append(Component.text(NumberFormat.addCommas(upgradeCost) + " Guild Coins", NamedTextColor.GREEN)),
                                    Component.empty(),
                                    Component.text("WARNING: ", NamedTextColor.RED)
                                             .append(Component.text("This will override the current upgrade.", NamedTextColor.GRAY))

                            )
                            .get(),
                    (m, e) -> {
                        Menu.openConfirmationMenu(
                                player,
                                upgradesTemporary.name + " (T" + tier + ")",
                                3,
                                Component.text("Purchase Upgrade", NamedTextColor.GREEN),
                                Arrays.asList(
                                        Component.textOfChildren(
                                                Component.text("Tier: ", NamedTextColor.GRAY),
                                                Component.text(tier, NamedTextColor.GREEN)
                                        ),
                                        Component.textOfChildren(
                                                Component.text("Effect Bonus: ", NamedTextColor.YELLOW),
                                                Component.text(upgradesTemporary.getEffectBonusFromTier(tier), NamedTextColor.GREEN)
                                        ),
                                        Component.empty(),
                                        Component.textOfChildren(
                                                Component.text("Cost: ", NamedTextColor.GRAY),
                                                Component.text(NumberFormat.addCommas(upgradeCost) + " Guild Coins", NamedTextColor.GREEN)
                                        )
                                ),
                                Component.text("Cancel", NamedTextColor.RED),
                                Menu.GO_BACK,
                                (m2, e2) -> {
                                    if (guild.getCurrentCoins() >= upgradeCost) {
                                        guild.addCurrentCoins(-upgradeCost);
                                        guild.addUpgrade(upgradesTemporary.createUpgrade(tier));
                                        guild.log(new GuildLogUpgradeTemporary(player.getUniqueId(), upgradesTemporary, tier));
                                        guild.queueUpdate();

                                        Instant now = Instant.now();
                                        Instant end = upgradesTemporary.expirationDate.apply(Instant.now());
                                        guild.sendGuildMessageToOnlinePlayers(
                                                Component.text(Duration.between(now, end).toHours() + " Hour Tier " + tier + " " + upgradesTemporary.name, NamedTextColor.YELLOW)
                                                         .append(Component.text(" blessing purchased!", NamedTextColor.GREEN)),
                                                true
                                        );
                                        openGuildUpgradeTypeMenu(player, guild, name, GuildUpgradesTemporary.VALUES);
                                    } else {
                                        player.sendMessage(Component.text("You do not have enough guild coins to purchase this upgrade.", NamedTextColor.RED));
                                    }
                                },
                                (m2, e2) -> openGuildUpgradeTemporaryPurchaseMenu(player, guild, name, upgradesTemporary),
                                (m2) -> {
                                }
                        );
                    }
            );
        }

        menu.setItem(4, 3, MENU_BACK, (m, e) -> openGuildUpgradeTypeMenu(player, guild, name, GuildUpgradesTemporary.VALUES));
        menu.openForPlayer(player);
    }

    public static void openGuildUpgradePermanentPurchaseMenu(
            Player player,
            Guild guild,
            String name,
            GuildUpgradesPermanent upgradesPermanent,
            GuildUpgradePermanent upgrade
    ) {
        int nextTier = upgrade == null ? 1 : upgrade.getTier() + 1;
        if (nextTier > 9) {
            player.sendMessage(Component.text("You have reached the maximum tier for this upgrade.", NamedTextColor.RED));
            return;
        }
        long upgradeCost = upgradesPermanent.getCost(nextTier);
        Menu.openConfirmationMenu(player,
                upgradesPermanent.name + " (T" + nextTier + ")",
                3,
                Component.text("Purchase Upgrade", NamedTextColor.GREEN),
                Arrays.asList(
                        Component.textOfChildren(
                                Component.text("Tier: ", NamedTextColor.GRAY),
                                Component.text(nextTier, NamedTextColor.GREEN)
                        ),
                        Component.textOfChildren(
                                Component.text("Effect Bonus: ", NamedTextColor.YELLOW),
                                Component.text(upgradesPermanent.getEffectBonusFromTier(nextTier), NamedTextColor.GREEN)
                        ),
                        Component.empty(),
                        Component.textOfChildren(
                                Component.text("Cost: ", NamedTextColor.GRAY),
                                Component.text(NumberFormat.addCommas(upgradeCost) + " Guild Coins", NamedTextColor.GREEN)
                        )
                ),
                Component.text("Cancel", NamedTextColor.RED),
                Menu.GO_BACK,
                (m2, e2) -> {
                    if (guild.getCurrentCoins() >= upgradeCost) {
                        guild.addCurrentCoins(-upgradeCost);
                        guild.addUpgrade(upgradesPermanent.createUpgrade(nextTier));
                        guild.log(new GuildLogUpgradePermanent(player.getUniqueId(), upgradesPermanent, nextTier));
                        upgradesPermanent.onPurchase(guild, nextTier);
                        guild.queueUpdate();

                        guild.sendGuildMessageToOnlinePlayers(
                                Component.text("Permanent Tier " + nextTier + " " + upgradesPermanent.name, NamedTextColor.YELLOW)
                                         .append(Component.text(" upgrade purchased!", NamedTextColor.GREEN)),
                                true
                        );
                        openGuildUpgradeTypeMenu(player, guild, name, GuildUpgradesPermanent.VALUES);
                    } else {
                        player.sendMessage(Component.text("You do not have enough guild coins to purchase this upgrade.", NamedTextColor.RED));
                    }
                },
                (m2, e2) -> openGuildUpgradeTypeMenu(player, guild, name, GuildUpgradesPermanent.VALUES),
                (m2) -> {
                }
        );
    }
}
