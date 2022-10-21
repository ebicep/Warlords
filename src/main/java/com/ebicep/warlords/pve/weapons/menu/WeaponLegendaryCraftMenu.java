package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryWeapon;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class WeaponLegendaryCraftMenu {

    public static LinkedHashMap<Currencies, Long> cost = new LinkedHashMap<>() {{
        put(Currencies.COIN, 1000000L);
        put(Currencies.SYNTHETIC_SHARD, 10000L);
    }};
    public static List<String> costLore = new ArrayList<>() {{
        add("");
        add(ChatColor.AQUA + "Craft Cost: ");
        cost.forEach((currencies, amount) -> add(ChatColor.GRAY + " - " + currencies.getCostColoredName(amount)));
    }};

    public static void openWeaponLegendaryCraftMenu(Player player, DatabasePlayer databasePlayer) {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        for (Map.Entry<Currencies, Long> currenciesLongEntry : cost.entrySet()) {
            if (pveStats.getCurrencyValue(currenciesLongEntry.getKey()) < currenciesLongEntry.getValue()) {
                player.sendMessage(ChatColor.RED + "You are not worthy of crafting a legendary weapon.");
                return;
            }
        }

        Menu menu = new Menu("Craft Legendary Weapon", 9 * 6);

        menu.setItem(4, 2,
                new ItemBuilder(Material.SULPHUR)
                        .name(ChatColor.GREEN + "Craft Legendary Weapon")
                        .lore(costLore)
                        .get(),
                (m, e) -> {
                    List<String> confirmLore = new ArrayList<>();
                    confirmLore.add(ChatColor.GRAY + "Craft a Legendary Weapon");
                    confirmLore.addAll(costLore);
                    Menu.openConfirmationMenu(
                            player,
                            "Craft Legendary Weapon",
                            3,
                            confirmLore,
                            Collections.singletonList(ChatColor.GRAY + "Go back"),
                            (m2, e2) -> {
                                LegendaryWeapon weapon = new LegendaryWeapon(player.getUniqueId());
                                cost.forEach(pveStats::subtractCurrency);
                                pveStats.getWeaponInventory().add(weapon);
                                Location loc = player.getLocation();
                                player.playSound(loc, Sound.NOTE_PLING, 500, 2);
                                player.playSound(loc, Sound.AMBIENCE_THUNDER, 500, 0.1f);
                                player.playSound(loc, "legendaryfind", 500, 1);
                                EffectUtils.strikeLightning(loc, false, 2);
                                player.getPlayer().spigot().sendMessage(
                                        new ComponentBuilder(ChatColor.GRAY + "Crafted Legendary Weapon: ")
                                                .appendHoverItem(weapon.getName(), weapon.generateItemStack())
                                                .create()
                                );
                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                                player.closeInventory();
                            },
                            (m2, e2) -> openWeaponLegendaryCraftMenu(player, databasePlayer),
                            (m2) -> {
                            }
                    );
                }
        );

        menu.fillEmptySlots(
                new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 7)
                        .name(" ")
                        .get(),
                (m, e) -> {
                }
        );

        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

}
