package com.ebicep.warlords.guilds.menu;

import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.bounty.GuildBountyData;
import com.ebicep.warlords.guilds.bounty.GuildBountyManager;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;

public final class GuildShopMenu {

    private GuildShopMenu() {
    }

    public static void openGuildShopMenu(Guild guild, Player player) {
        Menu menu = new Menu("Guild Shop", 9 * 4);
        GuildBountyData data = GuildBountyManager.getOrCreateData(guild);
        boolean canPurchase = canPurchase(guild, player);

        menu.setItem(4, 0, new ItemBuilder(Material.CHEST)
                .name(Component.text("Guild Shop", NamedTextColor.GREEN))
                .lore(
                        Component.text("Purchase permanent guild features.", NamedTextColor.GRAY),
                        Component.text("Guild Coins: ", NamedTextColor.GRAY)
                                 .append(Component.text(NumberFormat.addCommas(guild.getCurrentCoins()), NamedTextColor.YELLOW))
                )
                .get(), (m, e) -> {
        });

        for (int i = 0; i < GuildBountyManager.MAX_SLOTS; i++) {
            int slot = i + 1;
            long cost = GuildBountyManager.SLOT_COSTS[i];
            boolean unlocked = data.getUnlockedSlots() >= slot;
            boolean previousUnlocked = slot == 1 || data.getUnlockedSlots() >= slot - 1;
            ItemBuilder itemBuilder = new ItemBuilder(unlocked ? Material.EMERALD_BLOCK : Material.GOLD_BLOCK)
                    .name(Component.text("Guild Bounty Slot " + slot, unlocked ? NamedTextColor.GREEN : NamedTextColor.GOLD))
                    .lore(
                            Component.text("Adds one weekly Guild Bounty slot.", NamedTextColor.GRAY),
                            Component.empty(),
                            unlocked
                                    ? Component.text("Unlocked", NamedTextColor.GREEN)
                                    : Component.text("Cost: ", NamedTextColor.GRAY)
                                               .append(Component.text(NumberFormat.addCommas(cost) + " Guild Coins", NamedTextColor.YELLOW))
                    );

            if (!unlocked) {
                if (!previousUnlocked) {
                    itemBuilder.addLore(Component.text("Unlock the previous slot first.", NamedTextColor.RED));
                } else if (canPurchase) {
                    itemBuilder.addLore(Component.text("Click to Purchase", NamedTextColor.YELLOW));
                } else {
                    itemBuilder.addLore(Component.text("Only guild officers and guildmasters can purchase this.", NamedTextColor.RED));
                }
            }

            int x = slot == 1 ? 3 : 5;
            menu.setItem(x, 1, itemBuilder.get(), (m, e) -> {
                if (!unlocked && previousUnlocked && canPurchase) {
                    openPurchaseConfirmation(guild, player, slot, cost);
                }
            });
        }

        menu.setItem(4, 3, MENU_BACK, (m, e) -> GuildMenu.openGuildMenu(guild, player, 1));
        menu.openForPlayer(player);
    }

    private static boolean canPurchase(Guild guild, Player player) {
        Optional<GuildPlayer> guildPlayer = guild.getPlayerMatchingUUID(player.getUniqueId());
        return guildPlayer.isPresent() && guild.getRoleLevel(guildPlayer.get()) <= 1;
    }

    private static void openPurchaseConfirmation(Guild guild, Player player, int slot, long cost) {
        Menu.openConfirmationMenu(
                player,
                "Guild Bounty Slot " + slot,
                3,
                Component.text("Purchase Guild Bounty Slot " + slot, NamedTextColor.GREEN),
                Arrays.asList(
                        Component.text("This permanently unlocks one weekly Guild Bounty slot.", NamedTextColor.GRAY),
                        Component.empty(),
                        Component.text("Cost: ", NamedTextColor.GRAY)
                                 .append(Component.text(NumberFormat.addCommas(cost) + " Guild Coins", NamedTextColor.YELLOW))
                ),
                Component.text("Cancel", NamedTextColor.RED),
                Menu.GO_BACK,
                (m, e) -> purchaseSlot(guild, player, slot, cost),
                (m, e) -> openGuildShopMenu(guild, player),
                m -> {
                }
        );
    }

    private static void purchaseSlot(Guild guild, Player player, int slot, long cost) {
        GuildBountyData data = GuildBountyManager.getOrCreateData(guild);
        if (!canPurchase(guild, player) || data.getUnlockedSlots() != slot - 1) {
            openGuildShopMenu(guild, player);
            return;
        }
        if (guild.getCurrentCoins() < cost) {
            player.sendMessage(Component.text("The guild does not have enough Guild Coins for this purchase.", NamedTextColor.RED));
            openGuildShopMenu(guild, player);
            return;
        }

        guild.addCurrentCoins(-cost);
        GuildBountyManager.unlockSlot(guild);
        guild.queueUpdate();
        guild.sendGuildMessageToOnlinePlayers(
                Component.text(player.getName(), NamedTextColor.AQUA)
                         .append(Component.text(" purchased Guild Bounty Slot " + slot + " for ", NamedTextColor.GREEN))
                         .append(Component.text(NumberFormat.addCommas(cost) + " Guild Coins", NamedTextColor.YELLOW))
                         .append(Component.text(".", NamedTextColor.GREEN)),
                true
        );
        openGuildShopMenu(guild, player);
    }
}
