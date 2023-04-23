package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public class WeaponLegendaryCraftMenu {

    public static final LinkedHashMap<Currencies, Long> COST = new LinkedHashMap<>() {{
        put(Currencies.COIN, 1000000L);
        put(Currencies.SYNTHETIC_SHARD, 10000L);
    }};
    public static final List<String> COST_LORE = PvEUtils.getCostLore(COST, "Craft Cost", true);

    public static void openWeaponLegendaryCraftMenu(Player player, DatabasePlayer databasePlayer) {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        for (Map.Entry<Currencies, Long> currenciesLongEntry : COST.entrySet()) {
            if (pveStats.getCurrencyValue(currenciesLongEntry.getKey()) < currenciesLongEntry.getValue()) {
                player.sendMessage(ChatColor.RED + "You are not worthy of crafting a legendary weapon yet, bring me enough Synthetic Shards and Coins first!");
                return;
            }
        }

        Menu menu = new Menu("Craft Legendary Weapon", 9 * 6);

        menu.setItem(4, 2,
                new ItemBuilder(Material.GUNPOWDER)
                        .name(ChatColor.GREEN + "Craft Legendary Weapon")
                        .loreLEGACY(COST_LORE)
                        .get(),
                (m, e) -> {
                    List<String> confirmLore = new ArrayList<>();
                    confirmLore.add(ChatColor.GRAY + "Craft a Legendary Weapon");
                    confirmLore.addAll(COST_LORE);
                    Menu.openConfirmationMenu(
                            player,
                            "Craft Legendary Weapon",
                            3,
                            confirmLore,
                            Collections.singletonList(ChatColor.GRAY + "Go back"),
                            (m2, e2) -> {
                                LegendaryWeapon weapon = new LegendaryWeapon(player.getUniqueId());
                                COST.forEach(pveStats::subtractCurrency);
                                pveStats.getWeaponInventory().add(weapon);
                                Location loc = player.getLocation();
                                player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 500, 2);
                                player.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 500, 0.1f);
                                Utils.playGlobalSound(loc, "legendaryfind", 500, 1);
                                EffectUtils.strikeLightning(loc, false, 3);
                                player.sendMessage(Component.text(ChatColor.GRAY + "Crafted Legendary Weapon: ")
                                                            .append(weapon.getHoverComponent(false))
                                );
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    onlinePlayer.sendMessage(Permissions.getPrefixWithColor(player)
                                                                        .append(Component.text(player.getName() + ChatColor.GRAY + " crafted "))
                                                                        .append(weapon.getHoverComponent(false))
                                    );
                                }
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
                new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                        .name(" ")
                        .get(),
                (m, e) -> {
                }
        );

        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

}
