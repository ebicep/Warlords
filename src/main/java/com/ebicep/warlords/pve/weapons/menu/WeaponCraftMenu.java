package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.weapons.events.LegendaryWeaponCraftEvent;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WeaponCraftMenu {

    public static final LinkedHashMap<Currencies, Long> LEGENDARY_COST = new LinkedHashMap<>() {{
        put(Currencies.COIN, 600_000L);
        put(Currencies.SYNTHETIC_SHARD, 6_000L);
    }};
    public static final LinkedHashMap<Currencies, Long> ASCENDANT_COST = new LinkedHashMap<>() {{
        put(Currencies.COIN, 2_500_000L);
        put(Currencies.SYNTHETIC_SHARD, 15_000L);
        put(Currencies.ASCENDANT_SCROLL, 1L);
        put(Currencies.ETHEREUM_CRYSTAL, 5L);
    }};

    public static final List<Component> LEGENDARY_COST_LORE = PvEUtils.getCostLore(LEGENDARY_COST, "Craft Cost", true);
    public static final List<Component> ASCENDANT_COST_LORE = PvEUtils.getCostLore(ASCENDANT_COST, "Craft Cost", true);

    public static void openWeaponLegendaryCraftMenu(Player player, DatabasePlayer databasePlayer) {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        for (Map.Entry<Currencies, Long> currenciesLongEntry : LEGENDARY_COST.entrySet()) {
            if (pveStats.getCurrencyValue(currenciesLongEntry.getKey()) < currenciesLongEntry.getValue()) {
                player.sendMessage(Component.text("You are not worthy of crafting a legendary weapon yet, bring me 6.000 Synthetic Shards and 600.000 Coins first!", NamedTextColor.RED));
                return;
            }
        }

        Menu menu = new Menu("Witchard the Weapon Specialist", 9 * 6);

        menu.setItem(4, 2,
                new ItemBuilder(Material.GUNPOWDER)
                        .name(Component.text("Craft Legendary Weapon", NamedTextColor.GREEN))
                        .lore(LEGENDARY_COST_LORE)
                        .get(),
                (m, e) -> {
                    List<Component> confirmLore = new ArrayList<>();
                    confirmLore.add(Component.text("Craft a Legendary Weapon", NamedTextColor.GRAY));
                    confirmLore.addAll(LEGENDARY_COST_LORE);
                    Menu.openConfirmationMenu(
                            player,
                            "Craft Legendary Weapon",
                            3,
                            confirmLore,
                            Menu.GO_BACK,
                            (m2, e2) -> {
                                LegendaryWeapon weapon = new LegendaryWeapon(player.getUniqueId());
                                LEGENDARY_COST.forEach(pveStats::subtractCurrency);
                                pveStats.getWeaponInventory().add(weapon);
                                Location loc = player.getLocation();
                                playCraftEffects(player, loc);
                                player.sendMessage(Component.text("Crafted Legendary Weapon: ", NamedTextColor.GRAY).append(weapon.getHoverComponent(false)));
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    onlinePlayer.sendMessage(Permissions.getPrefixWithColor(player, false)
                                                                        .append(Component.text(player.getName()))
                                                                        .append(Component.text(" crafted ", NamedTextColor.GRAY))
                                                                        .append(weapon.getHoverComponent(false))
                                    );
                                }
                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                Bukkit.getPluginManager().callEvent(new LegendaryWeaponCraftEvent(player.getUniqueId(), weapon));
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
                        .name(Component.text(" "))
                        .get(),
                (m, e) -> {
                }
        );

        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openWeaponAscendantCraftMenu(Player player, DatabasePlayer databasePlayer) {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        for (Map.Entry<Currencies, Long> currenciesLongEntry : ASCENDANT_COST.entrySet()) {
            if (pveStats.getCurrencyValue(currenciesLongEntry.getKey()) < currenciesLongEntry.getValue()) {
                player.sendMessage(Component.text("You are not worthy of crafting an ascendant weapon yet.", NamedTextColor.RED));
                return;
            }
        }

        Menu menu = new Menu("The Vaultkeeper", 9 * 6);

        menu.setItem(4, 2,
                new ItemBuilder(Material.GUNPOWDER)
                        .name(Component.text("Craft Ascendant Weapon", NamedTextColor.GREEN))
                        .lore(ASCENDANT_COST_LORE)
                        .get(),
                (m, e) -> {
                    List<Component> confirmLore = new ArrayList<>();
                    confirmLore.add(Component.text("Craft a Legendary Weapon", NamedTextColor.GRAY));
                    confirmLore.addAll(ASCENDANT_COST_LORE);
                    Menu.openConfirmationMenu(
                            player,
                            "Craft Ascendant Weapon",
                            3,
                            confirmLore,
                            Menu.GO_BACK,
                            (m2, e2) -> {
                                LegendaryWeapon weapon = new LegendaryWeapon(player.getUniqueId());
                                ASCENDANT_COST.forEach(pveStats::subtractCurrency);
                                pveStats.getWeaponInventory().add(weapon);
                                Location loc = player.getLocation();
                                playCraftEffects(player, loc);
                                player.sendMessage(Component.text("Crafted Ascendant Weapon: ", NamedTextColor.GRAY).append(weapon.getHoverComponent(false)));
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    onlinePlayer.sendMessage(Permissions.getPrefixWithColor(player, false)
                                            .append(Component.text(player.getName()))
                                            .append(Component.text(" crafted ", NamedTextColor.GRAY))
                                            .append(weapon.getHoverComponent(false))
                                    );
                                }
                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                // TODO: make ascendant craft event
                                //Bukkit.getPluginManager().callEvent(new LegendaryWeaponCraftEvent(player.getUniqueId(), weapon));
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
                        .name(Component.text(" "))
                        .get(),
                (m, e) -> {
                }
        );

        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    private static void playCraftEffects(Player player, Location loc) {
        player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 500, 2);
        player.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 500, 0.1f);
        Utils.playGlobalSound(loc, "legendaryfind", 500, 1);
        EffectUtils.strikeLightning(loc, false, 3);
    }

}
