package com.ebicep.warlords.pve.consumables.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.consumables.GuildConsumableManager;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.generalmenu.WarlordsNewHotbarMenu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.consumables.ActiveConsumable;
import com.ebicep.warlords.pve.consumables.Consumable;
import com.ebicep.warlords.pve.consumables.ConsumableManager;
import com.ebicep.warlords.pve.consumables.ConsumablePurchaseLimit;
import com.ebicep.warlords.pve.consumables.ConsumableRegistry;
import com.ebicep.warlords.pve.consumables.FairyEssencePouch;
import com.ebicep.warlords.pve.consumables.vials.Vial;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.DateUtil;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;
import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;

public final class ConsumableMenu {

    private ConsumableMenu() {
    }

    public static void openVialInventory(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        ConsumableManager manager = databasePlayer.getPveStats().getConsumableManager();
        manager.cleanupExpired();

        Menu menu = new Menu("Vial Inventory", 9 * 6);
        int index = 0;
        for (Vial vial : Vial.VALUES) {
            int amount = manager.getAmount(vial);
            if (amount <= 0) {
                continue;
            }
            ActiveConsumable active = manager.getActiveConsumable(vial.getActiveGroup());
            boolean thisActive = active != null && vial.getId().equals(active.getConsumableId());
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(vial.getDescription(), NamedTextColor.GRAY));
            lore.add(Component.empty());
            lore.add(Component.text("Effect: ", NamedTextColor.GRAY).append(Component.text(vial.getEffectDescription(), NamedTextColor.GREEN)));
            lore.add(Component.text("Duration: ", NamedTextColor.GRAY).append(Component.text(formatDuration(vial), NamedTextColor.YELLOW)));
            lore.add(Component.text("Owned: ", NamedTextColor.GRAY).append(Component.text(amount, NamedTextColor.GREEN)));
            if (thisActive) {
                lore.add(Component.empty());
                lore.add(Component.text("ACTIVE", NamedTextColor.GREEN));
                lore.add(Component.text("Time Remaining: ", NamedTextColor.GRAY)
                                  .append(Component.text(DateUtil.getTimeTill(active.getExpiresAt(), true, true, true, false), NamedTextColor.YELLOW)));
            } else if (active != null) {
                Consumable activeDefinition = manager.getActiveDefinition(vial.getActiveGroup());
                if (activeDefinition != null) {
                    lore.add(Component.empty());
                    lore.add(Component.text("Active in this category: ", NamedTextColor.GRAY)
                                      .append(Component.text(activeDefinition.getName(), NamedTextColor.YELLOW)));
                }
            }
            lore.add(Component.empty());
            lore.add(Component.text("Click to Consume", NamedTextColor.YELLOW));

            menu.setItem(index % 7 + 1, index / 7 + 1,
                    new ItemBuilder(vial.getMaterial())
                            .name(Component.text(vial.getName(), NamedTextColor.GREEN))
                            .lore(lore)
                            .get(),
                    (m, e) -> {
                        if (manager.getAmount(vial) > 0) {
                            confirmConsume(player, databasePlayer, vial);
                        }
                    }
            );
            index++;
        }

        menu.setItem(3, 5,
                new ItemBuilder(Material.EMERALD)
                        .name(Component.text("Vial Shop", NamedTextColor.GREEN))
                        .lore(
                                Component.text("Purchase Vials unlocked by your guild.", NamedTextColor.GRAY),
                                Component.empty(),
                                Component.text("Click to View", NamedTextColor.YELLOW)
                        )
                        .get(),
                (m, e) -> openPersonalShop(player)
        );
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.setItem(5, 5, MENU_BACK, (m, e) -> WarlordsNewHotbarMenu.PvEMenu.openPvEMenu(player));
        menu.openForPlayer(player);
    }

    public static void openPersonalShop(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        Pair<Guild, GuildPlayer> guildPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
        Menu menu = new Menu("Vial Shop", 9 * 6);

        if (guildPair == null) {
            menu.setItem(4, 2,
                    new ItemBuilder(Material.BARRIER)
                            .name(Component.text("No Guild", NamedTextColor.RED))
                            .lore(Component.text("Join a guild to access its unlocked consumables.", NamedTextColor.GRAY))
                            .get(),
                    (m, e) -> {}
            );
        } else {
            Guild guild = guildPair.getA();
            int index = 0;
            for (Consumable consumable : ConsumableRegistry.values()) {
                boolean unlocked = GuildConsumableManager.isUnlocked(guild, consumable);
                boolean weeklyPurchased = consumable.getPurchaseLimit() == ConsumablePurchaseLimit.WEEKLY
                        && databasePlayer.getPveStats().getConsumableManager().hasPurchasedThisWeek(consumable);
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text(consumable.getDescription(), NamedTextColor.GRAY));
                lore.add(Component.empty());
                lore.add(Component.text("Effect: ", NamedTextColor.GRAY).append(Component.text(consumable.getEffectDescription(), NamedTextColor.GREEN)));
                if (consumable.isTimed()) {
                    lore.add(Component.text("Duration: ", NamedTextColor.GRAY).append(Component.text(formatDuration(consumable), NamedTextColor.YELLOW)));
                }
                lore.add(Component.text("Cost: ", NamedTextColor.GRAY)
                                  .append(Component.text(NumberFormat.addCommas(consumable.getPlayerCost()) + " Coins", NamedTextColor.YELLOW)));
                lore.add(Component.empty());
                if (!unlocked) {
                    lore.add(Component.text("Locked by Guild", NamedTextColor.RED));
                } else if (weeklyPurchased) {
                    lore.add(Component.text("Already purchased this week", NamedTextColor.RED));
                } else {
                    lore.add(Component.text("Click to Purchase", NamedTextColor.YELLOW));
                }

                menu.setItem(index % 7 + 1, index / 7 + 1,
                        new ItemBuilder(unlocked ? consumable.getMaterial() : Material.GRAY_DYE)
                                .name(Component.text(consumable.getName(), unlocked ? NamedTextColor.GREEN : NamedTextColor.GRAY))
                                .lore(lore)
                                .get(),
                        (m, e) -> {
                            if (GuildConsumableManager.isUnlocked(guild, consumable)) {
                                confirmPurchase(player, databasePlayer, guild, consumable);
                            }
                        }
                );
                index++;
            }
        }

        menu.setItem(4, 5, MENU_BACK, (m, e) -> openVialInventory(player));
        menu.openForPlayer(player);
    }

    private static void confirmConsume(Player player, DatabasePlayer databasePlayer, Vial vial) {
        ConsumableManager manager = databasePlayer.getPveStats().getConsumableManager();
        ActiveConsumable active = manager.getActiveConsumable(vial.getActiveGroup());
        List<Component> lore = new ArrayList<>(Arrays.asList(
                Component.text("Effect: ", NamedTextColor.GRAY).append(Component.text(vial.getEffectDescription(), NamedTextColor.GREEN)),
                Component.text("Duration: ", NamedTextColor.GRAY).append(Component.text(formatDuration(vial), NamedTextColor.YELLOW))
        ));
        if (active != null) {
            Consumable activeDefinition = manager.getActiveDefinition(vial.getActiveGroup());
            if (activeDefinition != null) {
                lore.add(Component.empty());
                lore.add(Component.text("WARNING: ", NamedTextColor.RED)
                                  .append(Component.text("This replaces your active " + activeDefinition.getName() + ".", NamedTextColor.GRAY)));
            }
        }
        Menu.openConfirmationMenu(
                player,
                "Consume " + vial.getName(),
                3,
                Component.text("Consume Vial", NamedTextColor.GREEN),
                lore,
                Component.text("Cancel", NamedTextColor.RED),
                Menu.GO_BACK,
                (m, e) -> {
                    ConsumableManager currentManager = databasePlayer.getPveStats().getConsumableManager();
                    if (!currentManager.remove(vial, 1)) {
                        openVialInventory(player);
                        return;
                    }
                    currentManager.activate(vial);
                    vial.onConsume(databasePlayer, player);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    player.sendMessage(Component.text("Activated ", NamedTextColor.GREEN)
                                                .append(Component.text(vial.getName(), NamedTextColor.YELLOW))
                                                .append(Component.text(" for " + formatDuration(vial) + ".", NamedTextColor.GREEN)));
                    openVialInventory(player);
                },
                (m, e) -> openVialInventory(player),
                m -> {}
        );
    }

    private static void confirmPurchase(Player player, DatabasePlayer databasePlayer, Guild guild, Consumable consumable) {
        ConsumableManager manager = databasePlayer.getPveStats().getConsumableManager();
        if (!GuildConsumableManager.isUnlocked(guild, consumable)) {
            openPersonalShop(player);
            return;
        }
        if (consumable.getPurchaseLimit() == ConsumablePurchaseLimit.WEEKLY && manager.hasPurchasedThisWeek(consumable)) {
            player.sendMessage(Component.text("You have already purchased this consumable this week.", NamedTextColor.RED));
            openPersonalShop(player);
            return;
        }

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Effect: ", NamedTextColor.GRAY).append(Component.text(consumable.getEffectDescription(), NamedTextColor.GREEN)));
        if (consumable.isTimed()) {
            lore.add(Component.text("Duration: ", NamedTextColor.GRAY).append(Component.text(formatDuration(consumable), NamedTextColor.YELLOW)));
        }
        lore.add(Component.empty());
        lore.add(Component.text("Cost: ", NamedTextColor.GRAY)
                          .append(Component.text(NumberFormat.addCommas(consumable.getPlayerCost()) + " Coins", NamedTextColor.YELLOW)));

        Menu.openConfirmationMenu(
                player,
                "Purchase " + consumable.getName(),
                3,
                Component.text("Purchase Consumable", NamedTextColor.GREEN),
                lore,
                Component.text("Cancel", NamedTextColor.RED),
                Menu.GO_BACK,
                (m, e) -> purchase(player, databasePlayer, guild, consumable),
                (m, e) -> openPersonalShop(player),
                m -> {}
        );
    }

    private static void purchase(Player player, DatabasePlayer databasePlayer, Guild guild, Consumable consumable) {
        ConsumableManager manager = databasePlayer.getPveStats().getConsumableManager();
        if (!GuildConsumableManager.isUnlocked(guild, consumable)) {
            openPersonalShop(player);
            return;
        }
        if (consumable.getPurchaseLimit() == ConsumablePurchaseLimit.WEEKLY && manager.hasPurchasedThisWeek(consumable)) {
            player.sendMessage(Component.text("You have already purchased this consumable this week.", NamedTextColor.RED));
            openPersonalShop(player);
            return;
        }
        long coins = databasePlayer.getPveStats().getCurrencyValue(Currencies.COIN);
        if (coins < consumable.getPlayerCost()) {
            player.sendMessage(Component.text("You do not have enough Coins for this purchase.", NamedTextColor.RED));
            openPersonalShop(player);
            return;
        }

        databasePlayer.getPveStats().subtractCurrency(Currencies.COIN, consumable.getPlayerCost());
        if (consumable == FairyEssencePouch.INSTANCE) {
            consumable.onConsume(databasePlayer, player);
        } else {
            manager.add(consumable, 1);
        }
        if (consumable.getPurchaseLimit() == ConsumablePurchaseLimit.WEEKLY) {
            manager.markPurchasedThisWeek(consumable);
        }
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        player.sendMessage(Component.text("Purchased ", NamedTextColor.GREEN)
                                    .append(Component.text(consumable.getName(), NamedTextColor.YELLOW))
                                    .append(Component.text(".", NamedTextColor.GREEN)));
        openPersonalShop(player);
    }

    private static String formatDuration(Consumable consumable) {
        long hours = consumable.getDuration().toHours();
        if (hours % 24 == 0) {
            long days = hours / 24;
            return days + " day" + (days == 1 ? "" : "s");
        }
        return hours + " hour" + (hours == 1 ? "" : "s");
    }
}
