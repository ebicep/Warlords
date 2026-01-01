package com.ebicep.warlords.pve.newitems.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.StarPieces;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemLoreCreator;
import com.ebicep.warlords.pve.newitems.NewItemRerollCost;
import com.ebicep.warlords.pve.newitems.NewItemsManager;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;

public class NewItemEditorMenu {

    private static Map<Spendable, Long> getStarPieceCost(StarPieces starPieceCurrency) {
        LinkedHashMap<Spendable, Long> map = new LinkedHashMap<>();
        map.put(Currencies.COIN, 50000L);
        map.put(Currencies.SYNTHETIC_SHARD, 500L);
        map.put(starPieceCurrency.currency, 1L);
        return map;
    }

    public static void open(Player player, NewItem item) {
        open(player, item, StarPieces.COMMON);
    }

    public static void open(Player player, NewItem item, StarPieces selectedStar) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        Menu menu = new Menu("Item Editor", 9 * 5);

        menu.setItem(4, 0, item.getItemBuilder().get(), Menu.ACTION_DO_NOTHING);

        menu.setItem(1, 2,
                new ItemBuilder(Material.END_CRYSTAL)
                        .name(Component.text("Set Bonuses", NamedTextColor.AQUA))
                        .lore(new NewItemLoreCreator.Builder(item.getSetBonus())
                                .addStarComponent()
                                .addBasicAttributes()
                                .addBonusAttributes()
                                .addSetBonus()
                                .build()
                        )
                        .get(),
                Menu.ACTION_DO_NOTHING
        );

        menu.setItem(3, 2,
                new ItemBuilder(Material.NAME_TAG)
                        .name(Component.text(item.isFavorite() ? "Unfavorite Item" : "Favorite Item", NamedTextColor.GREEN))
                        .lore(WordWrap.wrap(Component.text("Toggle your item's favorite status. You can filter by favorited items.", NamedTextColor.GRAY), 140))
                        .glow(item.isFavorite())
                        .get(),
                (m, e) -> {
                    item.setFavorite(!item.isFavorite());
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    NewItem.sendItemMessage(player, Component.text("You " + (item.isFavorite() ? "favorited " : "unfavorited "), NamedTextColor.GRAY)
                                                             .append(item.getHoverComponent())
                    );
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 2);
                    open(player, item);
                }
        );
        menu.setItem(4, 2,
                new ItemBuilder(Material.ENCHANTING_TABLE)
                        .name(Component.text("Reroll Item", NamedTextColor.GREEN))
                        .lore(WordWrap.wrap(Component.text(
                                                "Reroll your item's attributes at a certain cost. Each reroll increase the cost of you next attempt. Up to " + NewItemRerollCost.MAX_REROLLS + " attempts.",
                                                NamedTextColor.GRAY
                                        ), 140
                                )
                        )
                        .get(),
                (m, e) -> {
                    NewItemRerollMenu.open(player, item, rerollMenu -> {
                                rerollMenu.setItem(4, 4, MENU_BACK, (m2, e2) -> {
                                            open(player, item, selectedStar);
                                        }
                                );
                            }
                    );
                }
        );

        Map<Spendable, Long> starPieceCost = getStarPieceCost(selectedStar);
        List<Component> starLore = new ArrayList<>();
        starLore.addAll(WordWrap.wrap(
                Component.text("This star piece provides a ", NamedTextColor.GRAY)
                         .append(Component.text(selectedStar.starPieceBonusValue + "% ", selectedStar.currency.textColor))
                         .append(Component.text("stat boost to a random stat.", NamedTextColor.GRAY)),
                180
        ));
        starLore.addAll(PvEUtils.getCostLore(starPieceCost, true));
        starLore.add(Component.empty());
        starLore.add(Component.textOfChildren(
                Component.text("LEFT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                Component.text("to apply star piece", NamedTextColor.GRAY)
        ));
        starLore.add(Component.textOfChildren(
                Component.text("RIGHT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                Component.text("to change star piece selection", NamedTextColor.GRAY)
        ));

        menu.setItem(5, 2,
                new ItemBuilder(Material.NETHER_STAR)
                        .name(Component.text("Apply a " + selectedStar.currency.name, NamedTextColor.GREEN))
                        .lore(starLore)
                        .get(),
                (m, e) -> {
                    if (e.getClick().isLeftClick()) {
                        for (Map.Entry<Spendable, Long> currenciesLongEntry : starPieceCost.entrySet()) {
                            Spendable spendable = currenciesLongEntry.getKey();
                            Long cost = currenciesLongEntry.getValue();
                            if (spendable.getFromPlayer(databasePlayer) < cost) {
                                player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                            .append(spendable.getCostColoredName(cost))
                                                            .append(Component.text(" to apply this star piece!"))
                                );
                                return;
                            }
                        }
                        NewItemStarPieceMenu.openNewItemStarPieceMenu(player, databasePlayer, item, starPieceCost);
                    } else if (e.getClick().isRightClick()) {
                        open(player, item, selectedStar.next());
                    }
                }
        );

        menu.setItem(7, 2,
                new ItemBuilder(Material.FURNACE)
                        .name(Component.text("Salvage Item", NamedTextColor.GREEN))
                        .lore(Arrays.asList(
                                Component.text("Salvage this item and claim its materials.", NamedTextColor.GRAY),
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("Rewards: ", NamedTextColor.GREEN),
                                        Currencies.SCRAP_METAL.getCostColoredName(25)
                                ),
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("WARNING: ", NamedTextColor.RED),
                                        Component.text("This action cannot be undone.", NamedTextColor.GRAY)
                                )
                        ))
                        .get(),
                (m, e) -> {
                    Menu.openConfirmationMenu(player,
                            "Confirm Salvage",
                            3,
                            Arrays.asList(
                                    Component.text("Salvage this item and claim its materials.", NamedTextColor.GRAY),
                                    Component.empty(),
                                    Component.textOfChildren(
                                            Component.text("WARNING: ", NamedTextColor.RED),
                                            Component.text("This action cannot be undone.", NamedTextColor.GRAY)
                                    )
                            ),
                            Menu.GO_BACK,
                            (m2, e2) -> {
                                NewItemsManager itemsManager = databasePlayer.getPveStats().getNewItemsManager();
                                itemsManager.removeItem(item);
                                databasePlayer.getPveStats().addCurrency(Currencies.SCRAP_METAL, 25);
                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                NewItem.sendItemMessage(player, Component.text("You received 25 Scrap Metal from salvaging ", NamedTextColor.GRAY)
                                                                         .append(item.getHoverComponent())
                                );
                                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 2, 0.5f);
                                NewItemEquipMenu.openItemEquipMenuExternal(player, databasePlayer);
                            },
                            (m2, e2) -> NewItemEditorMenu.open(player, item),
                            (m2) -> {}
                    );
                }
        );

        menu.setItem(4, 4, Menu.MENU_BACK, (m, e) -> NewItemEquipMenu.openItemEquipMenuExternal(player, databasePlayer));
        menu.openForPlayer(player);
    }

}
