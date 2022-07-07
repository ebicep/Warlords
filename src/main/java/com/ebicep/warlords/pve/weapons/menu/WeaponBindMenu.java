package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import com.ebicep.warlords.util.java.Pair;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class WeaponBindMenu {

    private static final HashMap<Classes, Pair<Integer, Integer>> CLASSES_MENU_LOCATION = new HashMap<Classes, Pair<Integer, Integer>>() {{
        put(Classes.MAGE, new Pair<>(1, 1));
        put(Classes.WARRIOR, new Pair<>(4, 1));
        put(Classes.PALADIN, new Pair<>(7, 1));
        put(Classes.SHAMAN, new Pair<>(2, 3));
        put(Classes.ROGUE, new Pair<>(6, 3));
    }};

    public static void openWeaponBindMenu(Player player, AbstractWeapon weapon) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        List<AbstractWeapon> weaponInventory = databasePlayer.getPveStats().getWeaponInventory();
        BidiMap<AbstractWeapon, Specializations> boundWeapons = new DualHashBidiMap<>();
        weaponInventory.stream()
                .filter(AbstractWeapon::isBound)
                .forEach(w -> boundWeapons.put(w, w.getSpecializations()));
        Specializations weaponSpec = weapon.getSpecializations();

        Menu menu = new Menu("Bind Weapons", 9 * 6);

        menu.setItem(
                4,
                0,
                weapon.generateItemStack(),
                (m, e) -> {
                }
        );

        for (Classes value : Classes.values()) {
            Pair<Integer, Integer> menuLocation = CLASSES_MENU_LOCATION.get(value);

            int column = menuLocation.getA();
            int row = menuLocation.getB();
            menu.setItem(
                    column,
                    row,
                    new ItemBuilder(value.item)
                            .name(ChatColor.GREEN + value.name)
                            .get(),
                    (m, e) -> {
                    }
            );

            List<Specializations> specializations = value.subclasses;
            for (int i = -1; i < 2; i++) {
                Specializations spec = specializations.get(i + 1);
                AbstractWeapon boundWeapon = boundWeapons.getKey(spec);
                if (boundWeapon != null) {
                    // Same spec bound
                    if (boundWeapon.getSpecializations() == weaponSpec) {
                        menu.setItem(
                                column + i,
                                row + 1,
                                boundWeapon == weapon ?
                                        boundWeapon.generateItemStackInLore(ChatColor.GREEN + "Click to unbind") :
                                        boundWeapon.generateItemStackInLore(ChatColor.GREEN + "Click to replace binding"),
                                (m, e) -> {
                                    //unbind already bound weapon
                                    boundWeapon.setBound(false);
                                    if (boundWeapon == weapon) {
                                        player.spigot().sendMessage(
                                                new TextComponent(ChatColor.AQUA + "You unbounded "),
                                                new TextComponentBuilder(boundWeapon.getTitle())
                                                        .setHoverItem(boundWeapon.generateItemStack())
                                                        .getTextComponent());
                                    } else {
                                        //bind the new weapon
                                        weapon.setBound(true);

                                        player.spigot().sendMessage(
                                                new TextComponent(ChatColor.AQUA + "You unbounded "),
                                                new TextComponentBuilder(boundWeapon.getTitle())
                                                        .setHoverItem(boundWeapon.generateItemStack())
                                                        .getTextComponent(),
                                                new TextComponent(ChatColor.AQUA + " and bound "),
                                                new TextComponentBuilder(weapon.getTitle())
                                                        .setHoverItem(weapon.generateItemStack())
                                                        .getTextComponent());
                                    }

                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                    openWeaponBindMenu(player, weapon);
                                }
                        );
                    } else {
                        menu.setItem(
                                column + i,
                                row + 1,
                                boundWeapon.generateItemStack(),
                                (m, e) -> {
                                }
                        );
                    }
                } else {
                    menu.setItem(
                            column + i,
                            row + 1,
                            spec == weaponSpec ?
                                    new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 13)
                                            .name(ChatColor.GREEN + "Click to bind")
                                            .get() :
                                    new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 14)
                                            .name(ChatColor.RED + "You cannot bind this weapon to " + spec.name)
                                            .get(),
                            (m, e) -> {
                                if (spec == weaponSpec) {
                                    //bind the new weapon
                                    weapon.setBound(true);
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                    openWeaponBindMenu(player, weapon);

                                    player.spigot().sendMessage(
                                            new TextComponent(ChatColor.AQUA + "You bound "),
                                            new TextComponentBuilder(weapon.getTitle())
                                                    .setHoverItem(weapon.generateItemStack())
                                                    .getTextComponent());
                                }
                            }
                    );
                }
            }
        }

        menu.setItem(4, 5, Menu.MENU_BACK, (m, e) -> WeaponManagerMenu.openWeaponEditor(player, weapon));
        menu.openForPlayer(player);
    }
}
