package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
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
import java.util.Set;

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
                .filter(w -> w.getBoundedToSpec() != null)
                .forEach(w -> boundWeapons.put(w, w.getBoundedToSpec()));
        Set<Specializations> boundSpecs = boundWeapons.values();

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
                    menu.setItem(
                            column + i,
                            row + 1,
                            boundWeapon == weapon ?
                                    boundWeapon.generateItemStackInLore(ChatColor.GREEN + "Click to unbind from " + spec.name) :
                                    boundWeapon.generateItemStackInLore(ChatColor.GREEN + "Click to replace binding from " + spec.name),
                            (m, e) -> {
                                if (boundWeapon == weapon) {
                                    //unbind weapon
                                    boundWeapon.setBoundedToSpec(null);
                                    player.spigot().sendMessage(
                                            new TextComponent(ChatColor.GRAY + "You unbounded "),
                                            new TextComponentBuilder(WeaponsPvE.getWeapon(boundWeapon).getGeneralName())
                                                    .setHoverItem(boundWeapon.generateItemStack())
                                                    .getTextComponent(),
                                            new TextComponent(ChatColor.GRAY + " from " + ChatColor.GREEN + spec.name));
                                } else {
                                    //unbind the old weapon
                                    boundWeapon.setBoundedToSpec(null);
                                    //bind the new weapon (auto unbind previous)
                                    weapon.setBoundedToSpec(spec);

                                    player.spigot().sendMessage(
                                            new TextComponent(ChatColor.GRAY + "You unbounded "),
                                            new TextComponentBuilder(WeaponsPvE.getWeapon(boundWeapon).getGeneralName())
                                                    .setHoverItem(boundWeapon.generateItemStack())
                                                    .getTextComponent(),
                                            new TextComponent(ChatColor.GRAY + " from " + ChatColor.GREEN + spec.name),
                                            new TextComponent(ChatColor.GRAY + " and changed "),
                                            new TextComponentBuilder(WeaponsPvE.getWeapon(weapon).getGeneralName())
                                                    .setHoverItem(weapon.generateItemStack())
                                                    .getTextComponent(),
                                            new TextComponent(ChatColor.GRAY + "'s binding to " + ChatColor.GREEN + spec.name));
                                }

                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                openWeaponBindMenu(player, weapon);
                            }
                    );
                } else {
                    menu.setItem(
                            column + i,
                            row + 1,
                            new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15)
                                    .name(ChatColor.GREEN + "Click to bind to " + spec.name)
                                    .get(),
                            (m, e) -> {
                                //bind the new weapon (auto unbind previous)
                                weapon.setBoundedToSpec(spec);
                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                openWeaponBindMenu(player, weapon);

                                player.spigot().sendMessage(
                                        new TextComponent(ChatColor.GRAY + "You changed "),
                                        new TextComponentBuilder(WeaponsPvE.getWeapon(weapon).getGeneralName())
                                                .setHoverItem(weapon.generateItemStack())
                                                .getTextComponent(),
                                        new TextComponent(ChatColor.GRAY + "'s binding to " + ChatColor.GREEN + spec.name));
                            }
                    );
                }
            }
        }

        menu.setItem(4, 5, Menu.MENU_BACK, (m, e) -> WeaponManagerMenu.openWeaponEditor(player, weapon));
        menu.openForPlayer(player);
    }
}
