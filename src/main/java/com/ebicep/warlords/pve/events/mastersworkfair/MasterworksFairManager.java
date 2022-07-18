package com.ebicep.warlords.pve.events.mastersworkfair;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.customentities.npc.traits.MasterworksFairTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFairPlayerEntry;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.rewards.MasterworksFairReward;
import com.ebicep.warlords.pve.rewards.RewardTypes;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu;
import com.ebicep.warlords.pve.weapons.weapontypes.WeaponScore;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.DateUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MasterworksFairManager {

    public static MasterworksFair currentFair;
    public static boolean updateFair = false;

    public static void init() {
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.masterworksFairService.findFirstByOrderByStartDateDesc())
                .asyncLast(masterworksFair -> {
                    if (masterworksFair == null) {
                        System.out.println("[MasterworksFairManager] Could not find masterworks fair in database");
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(new Date());
                        boolean monday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;
                        if (monday) {
                            System.out.println("[MasterworksFairManager] Monday. Creating new masterworks fair.");
                            MasterworksFair newFair = new MasterworksFair();
                            initializeFair(newFair);
                            DatabaseManager.masterworksFairService.create(newFair);
                        } else {
                            System.out.println("[MasterworksFairManager] Not Monday. Skipping creation of new masterworks fair.");
                        }
                        Warlords.newChain()
                                .asyncFirst(() -> DatabaseManager.timingsService.findByTitle("Masterworks Fair"))
                                .asyncLast(timing -> {
                                    if (timing == null) {
                                        System.out.println("[MasterworksFairManager] Could not find masterworks fair timing in database. Creating new timing.");
                                        DatabaseManager.timingsService.create(new DatabaseTiming("Masterworks Fair", DateUtil.getResetDateLatestMonday(), Timing.WEEKLY));
                                    }
                                }).execute();
                    } else {
                        //check for reset
                        Warlords.newChain()
                                .asyncFirst(() -> DatabaseManager.timingsService.findByTitle("Masterworks Fair"))
                                .asyncLast(timing -> {
                                    if (timing == null) {
                                        System.out.println("[MasterworksFairManager] Could not find masterworks fair timing in database. Creating new timing.");
                                        DatabaseManager.timingsService.create(new DatabaseTiming("Masterworks Fair", DateUtil.getResetDateLatestMonday(), Timing.WEEKLY));
                                    } else {
                                        //check if week past
                                        long minutesBetween = ChronoUnit.MINUTES.between(timing.getLastReset(), Instant.now());
                                        System.out.println("Masterworks Fair Reset Time Minute: " + minutesBetween + " > " + (timing.getTiming().minuteDuration - 30));
                                        //30 min buffer
                                        if (minutesBetween > 0 && minutesBetween > timing.getTiming().minuteDuration - 30) {
                                            System.out.println("[MasterworksFairManager] Masterworks Fair reset time has passed. Resetting.");
                                            //give out rewards
                                            awardEntriesThroughRewardInventory(masterworksFair);

                                            //reset fair
                                            currentFair = null;
                                            MasterworksFairTrait masterworksFairTrait = NPCManager.masterworksFairNPC.getTraitNullable(MasterworksFairTrait.class);
                                            if (masterworksFairTrait != null) {
                                                masterworksFairTrait.updateHologram();
                                            }

                                            Warlords.newChain()
                                                    .delay(20 * 60 * 5)
                                                    .async(() -> {
                                                        //create new fair
                                                        MasterworksFair newFair = new MasterworksFair();
                                                        DatabaseManager.masterworksFairService.create(newFair);
                                                        initializeFair(newFair);
                                                    }).execute();
                                        } else {
                                            initializeFair(masterworksFair);
                                        }
                                    }

                                }).execute();
                    }
                })
                .execute();
    }

    private static void initializeFair(MasterworksFair masterworksFair) {
        System.out.println("[MasterworksFairManager] Initialize masterworks fair: " + masterworksFair.getStartDate());
        currentFair = masterworksFair;

        if (Warlords.citizensEnabled) {
            Warlords.newChain()
                    .sync(NPCManager::createMasterworksFairNPC)
                    .execute();
        }

        //runnable that updates fair every 20 seconds if there has been a change
        new BukkitRunnable() {

            @Override
            public void run() {
                if (updateFair) {
                    updateFair = false;
                    Warlords.newChain()
                            .async(() -> DatabaseManager.masterworksFairService.update(currentFair))
                            .execute();
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 20, 20 * 20);
    }

    public static void awardEntriesDirectly(MasterworksFair masterworksFair) {
        Instant now = Instant.now();
        for (WeaponsPvE value : WeaponsPvE.values()) {
            if (value.getPlayerEntries != null) {
                List<MasterworksFairPlayerEntry> playerEntries = value.getPlayerEntries.apply(masterworksFair);
                playerEntries.sort(Comparator.comparingDouble(o -> ((WeaponScore) o.getWeapon()).getWeaponScore()));
                for (int i = 0; i < playerEntries.size(); i++) {
                    MasterworksFairPlayerEntry entry = playerEntries.get(i);
                    MasterworksFairEntry playerRecordEntry = new MasterworksFairEntry(value, i + 1, now);
                    int finalI = i;
                    if (i < 3) { //top three guaranteed Star Piece of the weapon rarity they submitted
                        Warlords.newChain()
                                .asyncFirst(() -> DatabaseManager.playerService.findByUUID(UUID.fromString(entry.getUuid())))
                                .syncLast(databasePlayer -> {
                                    DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                                    pveStats.addMasterworksFairEntry(playerRecordEntry);
                                    value.addStarPiece.accept(pveStats);
                                    switch (finalI) { //The top submission will get 10 Supply Drop roll opportunities, 2nd and 3rd place will get 7 Supply Drop roll opportunities
                                        case 0:
                                            pveStats.addSupplyDropToken(10);
                                            break;
                                        case 1:
                                        case 2:
                                            pveStats.addSupplyDropToken(7);
                                            break;
                                    }
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                }).execute();
                    } else {
                        Warlords.newChain()
                                .asyncFirst(() -> DatabaseManager.playerService.findByUUID(UUID.fromString(entry.getUuid())))
                                .syncLast(databasePlayer -> {
                                    DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                                    pveStats.addMasterworksFairEntry(playerRecordEntry);
                                    if (finalI < 10) { //4-10 will get 5 Supply Drop roll opportunities
                                        pveStats.addSupplyDropToken(5);
                                    } else if (((WeaponScore) entry.getWeapon()).getWeaponScore() >= 85) { //Players who submit a 85%+ weapon will be guaranteed at least 3 supply drop opportunities
                                        pveStats.addSupplyDropToken(3);
                                    } else { //Players who submit any weapon will get a guaranteed supply drop roll as pity
                                        pveStats.addSupplyDropToken(1);
                                    }
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                }).execute();
                    }

                }
            }
        }
    }

    public static void awardEntriesThroughRewardInventory(MasterworksFair masterworksFair) {
        Instant now = Instant.now();
        for (WeaponsPvE value : WeaponsPvE.values()) {
            if (value.getPlayerEntries != null) {
                List<MasterworksFairPlayerEntry> playerEntries = value.getPlayerEntries.apply(masterworksFair);
                playerEntries.sort(Comparator.comparingDouble(o -> ((WeaponScore) o.getWeapon()).getWeaponScore()));
                for (int i = 0; i < playerEntries.size(); i++) {
                    MasterworksFairPlayerEntry entry = playerEntries.get(i);
                    MasterworksFairEntry playerRecordEntry = new MasterworksFairEntry(value, i + 1, now);
                    int finalI = i;
                    if (i < 3) { //top three guaranteed Star Piece of the weapon rarity they submitted
                        Warlords.newChain()
                                .asyncFirst(() -> DatabaseManager.playerService.findByUUID(UUID.fromString(entry.getUuid())))
                                .syncLast(databasePlayer -> {
                                    DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                                    pveStats.addMasterworksFairEntry(playerRecordEntry);
                                    pveStats.getRewards().add(new MasterworksFairReward(value.starPieceRewardType, 1));
                                    switch (finalI) { //The top submission will get 10 Supply Drop roll opportunities, 2nd and 3rd place will get 7 Supply Drop roll opportunities
                                        case 0:
                                            pveStats.getRewards().add(new MasterworksFairReward(RewardTypes.SUPPLY_DROP_TOKEN, 10));
                                            break;
                                        case 1:
                                        case 2:
                                            pveStats.getRewards().add(new MasterworksFairReward(RewardTypes.SUPPLY_DROP_TOKEN, 7));
                                            break;
                                    }
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                }).execute();
                    } else {
                        Warlords.newChain()
                                .asyncFirst(() -> DatabaseManager.playerService.findByUUID(UUID.fromString(entry.getUuid())))
                                .syncLast(databasePlayer -> {
                                    DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                                    pveStats.addMasterworksFairEntry(playerRecordEntry);
                                    if (finalI < 10) { //4-10 will get 5 Supply Drop roll opportunities
                                        pveStats.getRewards().add(new MasterworksFairReward(RewardTypes.SUPPLY_DROP_TOKEN, 5));
                                    } else if (((WeaponScore) entry.getWeapon()).getWeaponScore() >= 85) { //Players who submit a 85%+ weapon will be guaranteed at least 3 supply drop opportunities
                                        pveStats.getRewards().add(new MasterworksFairReward(RewardTypes.SUPPLY_DROP_TOKEN, 3));
                                    } else { //Players who submit any weapon will get a guaranteed supply drop roll as pity
                                        pveStats.getRewards().add(new MasterworksFairReward(RewardTypes.SUPPLY_DROP_TOKEN, 1));
                                    }
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                }).execute();
                    }

                }
            }
        }
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
                infoLore.add(value.getChatColorName() + ": " + ChatColor.AQUA + weaponPlayerEntries.size());
            }
        }
        infoItemBuilder.lore(infoLore);
        menu.setItem(4, 0, infoItemBuilder.get(), (m, e) -> {
        });


        menu.setItem(4, 4, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openSubmissionMenu(Player player, WeaponsPvE weaponType, int page) {
        Menu menu = new Menu("Choose a weapon", 9 * 6);
        UUID uuid = player.getUniqueId();
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid);
        List<AbstractWeapon> weaponInventory = databasePlayer.getPveStats().getWeaponInventory();
        List<AbstractWeapon> filteredWeaponInventory = new ArrayList<>(weaponInventory);
        filteredWeaponInventory.removeIf(weapon -> WeaponsPvE.getWeapon(weapon) != weaponType);
        filteredWeaponInventory.sort(WeaponManagerMenu.SortOptions.WEAPON_SCORE.comparator.reversed());

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
                            //check bound
                            if (abstractWeapon.isBound()) {
                                player.sendMessage(ChatColor.RED + "You cannot submit a bound weapon. Unbind it first!");
                                return;
                            }
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
