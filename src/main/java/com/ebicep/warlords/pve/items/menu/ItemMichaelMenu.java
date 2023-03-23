package com.ebicep.warlords.pve.items.menu;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.items.modifiers.ItemModifier;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemMichaelMenu {

    public static void openMichaelItemMenu(Player player, DatabasePlayer databasePlayer) {
        Menu menu = new Menu("Michael", 9 * 4);

        menu.setItem(1, 1,
                new ItemBuilder(Material.BOOK)
                        .name(ChatColor.GREEN + "Your Blessings")
                        .get(),
                (m, e) -> {
                }
        );

        menu.setItem(3, 1,
                new ItemBuilder(Material.PAPER)
                        .name(ChatColor.GREEN + "Buy a Blessing")
                        .get(),
                (m, e) -> {
                }
        );
        menu.setItem(5, 1,
                new ItemBuilder(Material.ANVIL)
                        .name(ChatColor.GREEN + "Apply a Blessing")
                        .get(),
                (m, e) -> {
                    ApplyBlessingMenu.openApplyBlessingMenu(player, databasePlayer, new ApplyBlessingMenu.ApplyBlessingMenuData());

                }
        );
        menu.setItem(7, 1,
                new ItemBuilder(Material.FIREBALL)
                        .name(ChatColor.GREEN + "Remove a Curse")
                        .get(),
                (m, e) -> {
                }
        );

        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    static class ApplyBlessingMenu {

        private static final LinkedHashMap<MobDrops, Long> cost = new LinkedHashMap<>();

        public static void openApplyBlessingMenu(Player player, DatabasePlayer databasePlayer, ApplyBlessingMenuData menuData) {
            AbstractItem<?, ?, ?> item = menuData.getItem();
            Integer blessing = menuData.getBlessing();
            ItemStack selectedItem;
            ItemStack selectedBlessing;
            if (item != null) {
                selectedItem = new ItemBuilder(item.generateItemStack())
                        .addLore(
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select a different item"
                        )
                        .get();
                if (blessing != null) {
                    ItemModifier<?> itemBlessing = item.getBlessings()[blessing];
                    selectedBlessing = new ItemBuilder(Material.PAPER)
                            .name(itemBlessing.getName())
                            .addLore(
                                    ChatColor.GREEN + itemBlessing.getDescription(),
                                    "",
                                    ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select a different blessing"
                            )
                            .get();
                } else {
                    selectedBlessing = new ItemBuilder(Material.PAPER)
                            .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select a blessing")
                            .get();
                }
            } else {
                selectedItem = new ItemBuilder(Material.SKULL_ITEM)
                        .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select an item")
                        .get();
                selectedBlessing = new ItemBuilder(Material.EMPTY_MAP)
                        .name(ChatColor.RED + "Select an item first")
                        .get();
            }


            Menu menu = new Menu("Apply a Blessing", 9 * 6);
            menu.setItem(2, 0,
                    selectedItem,
                    (m, e) -> {
                        openApplyBlessingItemSelectMenu(
                                player,
                                databasePlayer,
                                new ItemMenu.PlayerItemMenuSettings(databasePlayer),
                                menuData
                        );
                    }
            );
            ItemMenuUtil.addPaneRequirement(menu, 3, 0, item != null);
            menu.setItem(2, 1,
                    selectedBlessing,
                    (m, e) -> {
                        openApplyBlessingBlessingSelectMenu(player, databasePlayer, menuData);
                    }
            );
            ItemMenuUtil.addPaneRequirement(menu, 3, 1, blessing != null);
            ItemMenuUtil.addMobDropRequirement(databasePlayer, menu, new LinkedHashMap<>(), 2, 2);

            ItemMenuUtil.addItemConfirmation(menu, () -> {
                addCraftItemConfirmation(player, databasePlayer, menuData, menu);
            });

            menu.openForPlayer(player);
        }

        private static void openApplyBlessingItemSelectMenu(
                Player player,
                DatabasePlayer databasePlayer,
                ItemMenu.PlayerItemMenuSettings menuSettings,
                ApplyBlessingMenuData menuData
        ) {
            ItemMenu menu = new ItemMenu(
                    player, "Select an Item",
                    (i, m, e) -> {
                        menuData.setItem(i);
                        openApplyBlessingMenu(player, databasePlayer, menuData);
                    },
                    itemBuilder -> itemBuilder.addLore(
                            "",
                            ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select"
                    ),
                    menuSettings,
                    databasePlayer,
                    m -> m.setItem(4, 5,
                            Menu.MENU_BACK,
                            (m2, e2) -> {
                                openApplyBlessingMenu(player, databasePlayer, menuData);
                            }
                    )
            );

            menu.open();
        }

        private static void openApplyBlessingBlessingSelectMenu(
                Player player,
                DatabasePlayer databasePlayer,
                ApplyBlessingMenuData menuData
        ) {
            Menu menu = new Menu("Select a Blessing Tier", 9 * 4);
            for (int i = 0; i < 5; i++) {
                int finalI = i;
                Integer blessingAmount = databasePlayer.getPveStats().getItemsManager().getBlessingAmount(i + 1);
                menu.setItem(i + 2, 1,
                        new ItemBuilder(Material.PAPER)
                                .name(ChatColor.GREEN + "Tier " + (i + 1))
                                .lore(
                                        ChatColor.GRAY + "Amount: " + blessingAmount,
                                        "",
                                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select"
                                )
                                .get(),
                        (m, e) -> {
                            if (true || blessingAmount > 0) {
                                menuData.setBlessing(finalI + 1);
                                openApplyBlessingMenu(player, databasePlayer, menuData);
                            }
                        }
                );
            }
            menu.setItem(4, 3,
                    Menu.MENU_BACK,
                    (m, e) -> {
                        openApplyBlessingMenu(player, databasePlayer, menuData);
                    }
            );
        }

        private static void addCraftItemConfirmation(
                Player player,
                DatabasePlayer databasePlayer,
                ApplyBlessingMenuData menuData,
                Menu menu
        ) {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            AbstractItem<?, ?, ?> item = menuData.getItem();
            menu.setItem(7, 1,
                    new ItemBuilder(item.generateItemStack()) //TODO CLONE
                                                              //.name(ChatColor.GREEN + "Click to Craft Item")
                                                              .get(),
                    (m, e) -> {

                        for (Map.Entry<MobDrops, Long> currenciesLongEntry : cost.entrySet()) {
                            MobDrops mobDrop = currenciesLongEntry.getKey();
                            Long cost = currenciesLongEntry.getValue();
                            if (pveStats.getMobDrops(mobDrop) < cost) {
                                player.sendMessage(ChatColor.RED + "You need " + mobDrop.getCostColoredName(cost) + ChatColor.RED + " to craft this item!");
                                return;
                            }
                        }

                        Menu.openConfirmationMenu(player,
                                "Confirm Item Craft",
                                3,
                                Collections.singletonList(ChatColor.GRAY + "Are you sure you want to bless?"),
                                Collections.singletonList(ChatColor.GRAY + "Go back"),
                                (m2, e2) -> {
                                    for (Map.Entry<MobDrops, Long> currenciesLongEntry : cost.entrySet()) {
                                        currenciesLongEntry.getKey().subtractFromPlayer(databasePlayer, currenciesLongEntry.getValue());
                                    }


                                    AbstractItem.sendItemMessage(player,
                                            new ComponentBuilder(ChatColor.GRAY + "You blessed ")
                                                    .appendHoverItem(item.getName(), item.generateItemStack())
                                    );
                                    player.closeInventory();
                                },
                                (m2, e2) -> openApplyBlessingMenu(player, databasePlayer, menuData),
                                (m2) -> {
                                }
                        );
                    }
            );
        }

        private static class ApplyBlessingMenuData {
            private AbstractItem<?, ?, ?> item;
            private Integer blessing;

            public ApplyBlessingMenuData() {
            }

            public ApplyBlessingMenuData(AbstractItem<?, ?, ?> item, Integer blessing) {
                this.item = item;
                this.blessing = blessing;
            }

            public AbstractItem<?, ?, ?> getItem() {
                return item;
            }

            public void setItem(AbstractItem<?, ?, ?> item) {
                this.item = item;
            }

            public Integer getBlessing() {
                return blessing;
            }

            public void setBlessing(Integer blessing) {
                this.blessing = blessing;
            }
        }
    }

    static class RemoveCurseMenu {

        public static void removeACurseMenu(Player player, DatabasePlayer databasePlayer, AbstractItem<?, ?, ?> item) {
            ItemStack selectedItem;
            if (item != null) {
                selectedItem = new ItemBuilder(item.generateItemStack())
                        .addLore(
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select a different item"
                        )
                        .get();
            } else {
                selectedItem = new ItemBuilder(Material.SKULL_ITEM)
                        .name(ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to select an item")
                        .get();
            }

            Menu menu = new Menu("Remove a Curse", 9 * 6);
            menu.setItem(1, 1,
                    selectedItem,
                    (m, e) -> {
                    }
            );
            //TODO APPLY ITEM

            menu.openForPlayer(player);
        }
    }
}
