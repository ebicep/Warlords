package com.ebicep.warlords.pve.events;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFairPlayerEntry;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MasterworksFairManager {

    public static MasterworksFair currentFair;
    public static boolean updateFair = false;

    public static void init() {
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.masterworksFairService.findFirstByOrderByStartDateDesc())
                .abortIfNull()
                .syncLast(masterworksFair -> {
                    System.out.println("[MasterworksFairManager] Found masterworks fair: " + masterworksFair.getStartDate());
                    currentFair = masterworksFair;

                    NPCManager.createIndependentNPCs();

                    //runnable that updates fair every 20 seconds if there has been a change
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if (updateFair) {
                                updateFair = false;
                                DatabaseManager.masterworksFairService.update(currentFair);
                            }
                        }
                    }.runTaskTimer(Warlords.getInstance(), 20, 20 * 20);
                })
                .execute();
    }

    public static void openMasterworksFairMenu(Player player) {
        if (currentFair == null) {
            player.sendMessage(ChatColor.RED + "The Masterworks Fair is currently closed!");
            return;
        }

        Menu menu = new Menu("Masterworks Fair", 9 * 5);
        UUID uuid = player.getUniqueId();
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid);
        List<AbstractWeapon> weaponInventory = databasePlayer.getPveStats().getWeaponInventory();

        WeaponsPvE[] values = WeaponsPvE.values();
        int column = 2;
        for (WeaponsPvE value : values) {
            if (value.getPlayerEntries != null) {
                List<MasterworksFairPlayerEntry> weaponPlayerEntries = value.getPlayerEntries.apply(currentFair);
                Optional<MasterworksFairPlayerEntry> playerEntry = weaponPlayerEntries.stream()
                        .filter(masterworksFairPlayerEntry -> UUID.fromString(masterworksFairPlayerEntry.getUuid()).equals(uuid))
                        .findFirst();

                ItemBuilder itemBuilder;
                if (!playerEntry.isPresent()) {
                    itemBuilder = new ItemBuilder(value.glassItem);
                    itemBuilder.name(ChatColor.GREEN + "Click to submit a weapon");
                } else {
                    itemBuilder = new ItemBuilder(playerEntry.get().getWeapon().generateItemStack());
                    itemBuilder.addLore(
                            "",
                            ChatColor.YELLOW.toString() + ChatColor.BOLD + "LEFT-CLICK" + ChatColor.GREEN + " to change your submission",
                            ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK" + ChatColor.GREEN + " to remove your submission"
                    );
                }
                menu.setItem(
                        column,
                        2,
                        itemBuilder.get(),
                        (m, e) -> {
                            if (!playerEntry.isPresent() || e.isLeftClick()) { //submit | change weapon
                                openSubmissionMenu(player, value, 1);
                            } else { //remove weapon
                                weaponInventory.add(playerEntry.get().getWeapon());
                                weaponPlayerEntries.remove(playerEntry.get());

                                updateFair = true;
                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                                openMasterworksFairMenu(player);
                            }
                        }
                );
                column += 2;
            }
        }

        ItemBuilder infoItemBuilder = new ItemBuilder(Material.FIREWORK)
                .name(ChatColor.GREEN + "Current Submissions");
        List<String> infoLore = new ArrayList<>();
        for (WeaponsPvE value : values) {
            if (value.getPlayerEntries != null) {
                List<MasterworksFairPlayerEntry> weaponPlayerEntries = value.getPlayerEntries.apply(currentFair);
                infoLore.add(value.chatColor + value.name + ": " + ChatColor.AQUA + weaponPlayerEntries.size());
            }
        }
        infoItemBuilder.lore(infoLore);
        menu.setItem(4, 0, infoItemBuilder.get(), (m, e) -> {
        });


        menu.setItem(4, 4, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openSubmissionMenu(Player player, WeaponsPvE weaponType, int page) {
        Menu menu = new Menu("Your Weapons", 9 * 6);
        UUID uuid = player.getUniqueId();
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid);
        List<AbstractWeapon> weaponInventory = databasePlayer.getPveStats().getWeaponInventory();
        List<AbstractWeapon> filteredWeaponInventory = new ArrayList<>(weaponInventory);
        filteredWeaponInventory.removeIf(weapon -> WeaponsPvE.getWeapon(weapon) != weaponType);

        List<MasterworksFairPlayerEntry> weaponPlayerEntries = weaponType.getPlayerEntries.apply(currentFair);
        Optional<MasterworksFairPlayerEntry> playerEntry = weaponPlayerEntries.stream()
                .filter(masterworksFairPlayerEntry -> UUID.fromString(masterworksFairPlayerEntry.getUuid()).equals(uuid))
                .findFirst();

        for (int i = 0; i < 45; i++) {
            int weaponNumber = ((page - 1) * 45) + i;
            if (weaponNumber < filteredWeaponInventory.size()) {
                AbstractWeapon abstractWeapon = filteredWeaponInventory.get(weaponNumber);

                int column = i % 9;
                int row = i / 9;

                menu.setItem(
                        column,
                        row,
                        abstractWeapon.generateItemStack(),
                        (m, e) -> {
                            //submit weapon to fair
                            MasterworksFairPlayerEntry masterworksFairPlayerEntry = playerEntry.orElseGet(() -> new MasterworksFairPlayerEntry(uuid.toString()));
                            if (playerEntry.isPresent()) {
                                //remove old weapon
                                weaponInventory.add(masterworksFairPlayerEntry.getWeapon());
                            } else {
                                //add new entry if there wasnt already one
                                weaponPlayerEntries.add(masterworksFairPlayerEntry);
                            }
                            //remove new weapon
                            weaponInventory.remove(abstractWeapon);
                            //set new weapon
                            masterworksFairPlayerEntry.setWeapon(abstractWeapon);

                            //update database stuff
                            updateFair = true;
                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                            openMasterworksFairMenu(player);
                        }
                );
            }
        }

        if (page - 1 > 0) {
            menu.setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (page - 1))
                            .get(),
                    (m, e) -> {
                        openSubmissionMenu(player, weaponType, page - 1);
                    }
            );
        }
        if (filteredWeaponInventory.size() > (page * 45)) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (page + 1))
                            .get(),
                    (m, e) -> {
                        openSubmissionMenu(player, weaponType, page + 1);
                    }
            );
        }

        menu.setItem(4, 5, Menu.MENU_BACK, (m, e) -> openMasterworksFairMenu(player));
        menu.openForPlayer(player);
    }
}
