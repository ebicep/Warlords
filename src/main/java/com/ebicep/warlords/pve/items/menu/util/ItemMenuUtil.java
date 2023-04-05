package com.ebicep.warlords.pve.items.menu.util;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemBucklerModifier;
import com.ebicep.warlords.pve.items.modifiers.ItemGauntletModifier;
import com.ebicep.warlords.pve.items.modifiers.ItemTomeModifier;
import com.ebicep.warlords.pve.items.statpool.ItemBucklerStatPool;
import com.ebicep.warlords.pve.items.statpool.ItemGauntletStatPool;
import com.ebicep.warlords.pve.items.statpool.ItemTomeStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.ItemBuckler;
import com.ebicep.warlords.pve.items.types.ItemGauntlet;
import com.ebicep.warlords.pve.items.types.ItemTome;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;
import java.util.function.BiConsumer;

public class ItemMenuUtil {

    public static String getRequirementMetString(boolean requirementMet, String requirement) {
        return (requirementMet ? ChatColor.GREEN + "✔" : ChatColor.RED + "✖") + ChatColor.GRAY + " " + requirement;
    }

    public static void addItemTierRequirement(
            Menu menu,
            ItemTier tier,
            AbstractItem<?, ?, ?> item,
            int x,
            int y,
            BiConsumer<Menu, InventoryClickEvent> onClick
    ) {
        ItemBuilder itemBuilder;
        if (item == null) {
            itemBuilder = new ItemBuilder(tier.clayBlock)
                    .name(ChatColor.GREEN + "Click to Select Item");
        } else {
            itemBuilder = item.generateItemBuilder()
                              .addLore(
                                      "",
                                      ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to swap this item"
                              );
        }
        menu.setItem(x, y,
                itemBuilder.get(),
                onClick
        );
        addPaneRequirement(menu, x + 1, y, item != null);
    }

    public static void addPaneRequirement(Menu menu, int x, int y, boolean requirementMet) {
        menu.setItem(x, y,
                new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) (requirementMet ? 5 : 14))
                        .name(" ")
                        .get(),
                (m, e) -> {
                }
        );
    }

    public static void addSpendableCostRequirement(
            DatabasePlayer databasePlayer,
            Menu menu,
            LinkedHashMap<Spendable, Long> cost,
            int x,
            int y
    ) {
        List<String> costLore = PvEUtils.getCostLore(cost);
        menu.setItem(x, y,
                new ItemBuilder(Material.BOOK)
                        .name(ChatColor.GREEN + "Loot")
                        .lore(costLore)
                        .get(),
                (m, e) -> {
                }
        );
        boolean hasRequiredCosts = cost
                .entrySet()
                .stream()
                .allMatch(spendableLongEntry -> spendableLongEntry.getKey().getFromPlayer(databasePlayer) >= spendableLongEntry.getValue());
        menu.setItem(x + 1, y,
                new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) (hasRequiredCosts ? 5 : 14))
                        .name(" ")
                        .get(),
                (m, e) -> {
                }
        );
    }

    public static void addItemConfirmation(
            Menu menu,
            Runnable onCenterClick
    ) {
        for (int i = 5; i < 8; i++) {
            for (int j = 1; j < 4; j++) {
                if (i == 6 && j == 2) {
                    onCenterClick.run();
                } else {
                    menu.setItem(i, j,
                            new ItemBuilder(Material.IRON_FENCE)
                                    .name(" ")
                                    .get(),
                            (m, e) -> {
                            }
                    );
                }
            }
        }
    }

    public static List<String> getTotalBonusLore(List<AbstractItem<?, ?, ?>> equippedItems) {
        HashMap<ItemGauntletStatPool, Integer> gauntletStatPool = new HashMap<>();
        HashMap<ItemTomeStatPool, Integer> tomeStatPool = new HashMap<>();
        HashMap<ItemBucklerStatPool, Integer> bucklerStatPool = new HashMap<>();
        float gauntletModifier = 0;
        float tomeModifier = 0;
        float bucklerModifier = 0;
        for (AbstractItem<?, ?, ?> equippedItem : equippedItems) {
            if (equippedItem instanceof ItemGauntlet) {
                ItemGauntlet itemGauntlet = (ItemGauntlet) equippedItem;
                itemGauntlet.getStatPool().forEach((stat, tier) -> gauntletStatPool.merge(stat, tier, Integer::sum));
                gauntletModifier += itemGauntlet.getModifierCalculated();
            } else if (equippedItem instanceof ItemTome) {
                ItemTome itemTome = (ItemTome) equippedItem;
                itemTome.getStatPool().forEach((stat, tier) -> tomeStatPool.merge(stat, tier, Integer::sum));
                tomeModifier += itemTome.getModifierCalculated();
            } else if (equippedItem instanceof ItemBuckler) {
                ItemBuckler itemBuckler = (ItemBuckler) equippedItem;
                itemBuckler.getStatPool().forEach((stat, tier) -> bucklerStatPool.merge(stat, tier, Integer::sum));
                bucklerModifier += itemBuckler.getModifierCalculated();
            }
        }
        List<String> gauntletBonusLore = AbstractItem.getStatPoolLore(gauntletStatPool, "   ");
        if (gauntletModifier != 0) {
            gauntletBonusLore.add("   " + AbstractItem.getModifierCalculatedLore(
                    ItemGauntletModifier.Blessings.VALUES,
                    ItemGauntletModifier.Curses.VALUES,
                    gauntletModifier
            ));
        }
        List<String> tomeBonusLore = AbstractItem.getStatPoolLore(tomeStatPool, "   ");
        if (tomeModifier != 0) {
            tomeBonusLore.add("   " + AbstractItem.getModifierCalculatedLore(
                    ItemTomeModifier.Blessings.VALUES,
                    ItemTomeModifier.Curses.VALUES,
                    tomeModifier
            ));
        }
        List<String> bucklerBonusLore = AbstractItem.getStatPoolLore(bucklerStatPool, "   ");
        if (bucklerModifier != 0) {
            bucklerBonusLore.add("   " + AbstractItem.getModifierCalculatedLore(
                    ItemBucklerModifier.Blessings.VALUES,
                    ItemBucklerModifier.Curses.VALUES,
                    bucklerModifier
            ));
        }
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.AQUA + "Gauntlets Bonuses");
        lore.addAll(gauntletBonusLore.isEmpty() ? Collections.singletonList(ChatColor.GRAY + "   None") : gauntletBonusLore);
        lore.add("");
        lore.add(ChatColor.AQUA + "Tome Bonuses");
        lore.addAll(tomeBonusLore.isEmpty() ? Collections.singletonList(ChatColor.GRAY + "   None") : tomeBonusLore);
        lore.add("");
        lore.add(ChatColor.AQUA + "Buckler Bonuses");
        lore.addAll(bucklerBonusLore.isEmpty() ? Collections.singletonList(ChatColor.GRAY + "   None") : bucklerBonusLore);
        return lore;
    }
}
