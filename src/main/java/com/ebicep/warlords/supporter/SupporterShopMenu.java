package com.ebicep.warlords.supporter;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletionException;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public final class SupporterShopMenu {

    private SupporterShopMenu() {
    }

    public static void open(Player player) {
        Menu menu = new Menu("Support Warlords", 9 * 3);

        boolean supporter = Permissions.isSupporter(player);
        menu.setItem(4, 0,
                new ItemBuilder(supporter ? Material.GOLD_INGOT : Material.IRON_INGOT)
                        .name(Component.text(supporter ? "Supporter Active" : "Become a Supporter", supporter ? NamedTextColor.GOLD : NamedTextColor.AQUA))
                        .lore(
                                supporter
                                        ? Component.text("Thank you for supporting Warlords!", NamedTextColor.GRAY)
                                        : Component.text("Support the server and unlock Supporter perks.", NamedTextColor.GRAY),
                                Component.text("Purchases are completed securely through Tebex.", NamedTextColor.DARK_GRAY)
                        )
                        .get(),
                (m, e) -> {
                }
        );

        menu.setItem(2, 1,
                new ItemBuilder(Material.NETHER_STAR)
                        .name(Component.text("Supporter Subscription", NamedTextColor.GOLD))
                        .lore(
                                Component.text("Monthly recurring Supporter access.", NamedTextColor.GRAY),
                                Component.text("Your rank remains active while the subscription is active.", NamedTextColor.GRAY),
                                Component.empty(),
                                checkoutLore(TebexService.getSubscriptionPackageId())
                        )
                        .glow()
                        .get(),
                (m, e) -> openCheckout(player, TebexService.getSubscriptionPackageId(), "Supporter Subscription")
        );

        menu.setItem(6, 1,
                new ItemBuilder(Material.CLOCK)
                        .name(Component.text("Supporter - 30 Days", NamedTextColor.YELLOW))
                        .lore(
                                Component.text("One-time purchase with 30 days of Supporter access.", NamedTextColor.GRAY),
                                Component.text("The entitlement automatically expires after 30 days.", NamedTextColor.GRAY),
                                Component.empty(),
                                checkoutLore(TebexService.getThirtyDayPackageId())
                        )
                        .get(),
                (m, e) -> openCheckout(player, TebexService.getThirtyDayPackageId(), "Supporter - 30 Days")
        );

        menu.setItem(4, 2, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    private static Component checkoutLore(String packageId) {
        if (!TebexService.hasSecret() || packageId == null) {
            return Component.text("Tebex is not configured on this server.", NamedTextColor.RED);
        }
        return Component.text("Click to create your checkout link.", NamedTextColor.GREEN);
    }

    private static void openCheckout(Player player, String packageId, String packageName) {
        player.closeInventory();
        if (!TebexService.hasSecret() || packageId == null) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            player.sendMessage(Component.text("The Tebex shop is not configured yet. Please contact an administrator.", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.text("Creating your Tebex checkout...", NamedTextColor.GRAY));
        TebexService.createCheckout(player, packageId)
                .whenComplete((url, throwable) -> Bukkit.getScheduler().runTask(Warlords.getInstance(), () -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    if (throwable != null) {
                        Throwable cause = throwable instanceof CompletionException && throwable.getCause() != null
                                ? throwable.getCause()
                                : throwable;
                        Warlords.getInstance().getLogger().warning("Unable to create Tebex checkout for " + player.getUniqueId() + ": " + cause.getMessage());
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        player.sendMessage(Component.text("Unable to create a Tebex checkout right now. Please try again later.", NamedTextColor.RED));
                        return;
                    }

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    player.sendMessage(
                            Component.text("[Click here to purchase " + packageName + "]", NamedTextColor.GREEN, TextDecoration.BOLD)
                                    .clickEvent(ClickEvent.openUrl(url))
                    );
                    player.sendMessage(Component.text("The link opens the secure Tebex checkout in your browser.", NamedTextColor.GRAY));
                }));
    }
}
