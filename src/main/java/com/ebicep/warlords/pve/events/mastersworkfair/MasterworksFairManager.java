package com.ebicep.warlords.pve.events.mastersworkfair;

import com.ebicep.customentities.npc.traits.MasterworksFairTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFairPlayerEntry;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.rewards.Currencies;
import com.ebicep.warlords.pve.rewards.MasterworksFairReward;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu;
import com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MasterworksFairManager {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("LLL dd yyyy z")
            .withZone(ZoneId.of("America/New_York"));
    public static boolean enabled = true;
    public static MasterworksFair currentFair;
    public static AtomicBoolean updateFair = new AtomicBoolean(false);
    public static BukkitTask runnable;

    public static void resetFair(MasterworksFair masterworksFair) {
        resetFair(masterworksFair, true, 5);
    }

    public static void resetFair(MasterworksFair masterworksFair, boolean throughRewardsInventory, int minutesTillStart) {
        if (masterworksFair == null) {
            ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Supplied fair is null. Cannot reset fair.");
            return;
        }
        ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Resetting fair");
        //give out rewards
        awardEntries(masterworksFair, throughRewardsInventory);
        //reset fair
        MasterworksFairTrait.startTime = Instant.now().plus(minutesTillStart, ChronoUnit.MINUTES);
        MasterworksFairTrait.PAUSED.set(false);
    }

    public static void createFair(MasterworksFair masterworksFair) {
        Warlords.newChain()
                .async(() -> DatabaseManager.masterworksFairService.create(masterworksFair))
                .execute();
    }

    public static void initializeFair(MasterworksFair masterworksFair) {
        ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Initialize masterworks fair: " + masterworksFair.getStartDate());
        currentFair = masterworksFair;
        MasterworksFairTrait.PAUSED.set(false);
        //runnable that updates fair every 30 seconds if there has been a change
        if (runnable != null) {
            runnable.cancel();
        }
        runnable = new BukkitRunnable() {

            @Override
            public void run() {
                if (updateFair.get() && currentFair != null) {
                    updateFair.set(false);
                    Warlords.newChain()
                            .async(() -> DatabaseManager.masterworksFairService.update(currentFair))
                            .execute();
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 60, 20 * 30);
    }

    public static void awardEntries(MasterworksFair masterworksFair, boolean throughRewardsInventory) {
        Warlords.newChain()
                .async(() -> DatabaseManager.masterworksFairService.update(masterworksFair))
                .sync(() -> {
                    currentFair = null;

                    Instant now = Instant.now();
                    for (WeaponsPvE value : WeaponsPvE.values()) {
                        if (value.getPlayerEntries != null) {
                            List<MasterworksFairPlayerEntry> playerEntries = value.getPlayerEntries.apply(masterworksFair);
                            playerEntries.sort(Comparator.comparingDouble(o -> ((WeaponScore) o.getWeapon()).getWeaponScore()));
                            Collections.reverse(playerEntries);

                            for (int i = 0; i < playerEntries.size(); i++) {
                                MasterworksFairPlayerEntry entry = playerEntries.get(i);
                                MasterworksFairEntry playerRecordEntry = new MasterworksFairEntry(now, value, i + 1, Float.parseFloat(NumberFormat.formatOptionalHundredths(((WeaponScore) entry.getWeapon()).getWeaponScore())));
                                int finalI = i;
                                Warlords.newChain()
                                        .asyncFirst(() -> DatabaseManager.playerService.findByUUID(entry.getUuid()))
                                        .syncLast(databasePlayer -> {
                                            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                                            pveStats.addMasterworksFairEntry(playerRecordEntry);
                                            if (finalI < 3) { //top three guaranteed Star Piece of the weapon rarity they submitted
                                                if (throughRewardsInventory) {
                                                    pveStats.addReward(new MasterworksFairReward(value.starPieceCurrency, 1L, now));
                                                } else {
                                                    pveStats.addOneCurrency(value.starPieceCurrency);
                                                }
                                                switch (finalI) { //The top submission will get 10 Supply Drop roll opportunities, 2nd and 3rd place will get 7 Supply Drop roll opportunities
                                                    case 0:
                                                        if (throughRewardsInventory) {
                                                            pveStats.addReward(new MasterworksFairReward(Currencies.SUPPLY_DROP_TOKEN, 10L, now));
                                                        } else {
                                                            pveStats.addCurrency(Currencies.SUPPLY_DROP_TOKEN, 10);
                                                        }
                                                        break;
                                                    case 1:
                                                    case 2:
                                                        if (throughRewardsInventory) {
                                                            pveStats.addReward(new MasterworksFairReward(Currencies.SUPPLY_DROP_TOKEN, 7L, now));
                                                        } else {
                                                            pveStats.addCurrency(Currencies.SUPPLY_DROP_TOKEN, 7);
                                                        }
                                                        break;
                                                }
                                            } else {
                                                if (finalI < 10) { //4-10 will get 5 Supply Drop roll opportunities
                                                    if (throughRewardsInventory) {
                                                        pveStats.addReward(new MasterworksFairReward(Currencies.SUPPLY_DROP_TOKEN, 5L, now));
                                                    } else {
                                                        pveStats.addCurrency(Currencies.SUPPLY_DROP_TOKEN, 5);
                                                    }
                                                } else if (((WeaponScore) entry.getWeapon()).getWeaponScore() >= 85) { //Players who submit a 85%+ weapon will be guaranteed at least 3 supply drop opportunities
                                                    if (throughRewardsInventory) {
                                                        pveStats.addReward(new MasterworksFairReward(Currencies.SUPPLY_DROP_TOKEN, 3L, now));
                                                    } else {
                                                        pveStats.addCurrency(Currencies.SUPPLY_DROP_TOKEN, 3);
                                                    }
                                                } else { //Players who submit any weapon will get a guaranteed supply drop roll as pity
                                                    if (throughRewardsInventory) {
                                                        pveStats.addReward(new MasterworksFairReward(Currencies.SUPPLY_DROP_TOKEN, 1L, now));
                                                    } else {
                                                        pveStats.addCurrency(Currencies.SUPPLY_DROP_TOKEN, 1);
                                                    }
                                                }
                                            }
                                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                        })
                                        .execute();
                            }
                        }
                    }
                    if (throughRewardsInventory) {
                        ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Awarded entries through reward inventory");
                    } else {
                        ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Awarded entries directly");
                    }
                }).
                execute();
    }

    public static void openMasterworksFairMenu(Player player) {
        if (currentFair == null) {
            if (MasterworksFairTrait.startTime != null) {
                player.sendMessage(ChatColor.RED + "The Masterworks Fair is starting soon!");
            } else {
                player.sendMessage(ChatColor.RED + "The Masterworks Fair is currently closed!");
            }
            return;
        }

        Menu menu = new Menu("Masterworks Fair", 9 * 6);
        UUID uuid = player.getUniqueId();
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid);
        DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();
        List<AbstractWeapon> weaponInventory = databasePlayerPvE.getWeaponInventory();

        WeaponsPvE[] values = WeaponsPvE.values();
        int column = 2;
        for (WeaponsPvE value : values) {
            if (value.getPlayerEntries != null) {
                List<MasterworksFairPlayerEntry> weaponPlayerEntries = value.getPlayerEntries.apply(currentFair);
                Optional<MasterworksFairPlayerEntry> playerEntry = weaponPlayerEntries.stream()
                        .filter(masterworksFairPlayerEntry -> masterworksFairPlayerEntry.getUuid().equals(uuid))
                        .findFirst();

                ItemBuilder itemBuilder;
                if (!playerEntry.isPresent()) {
                    itemBuilder = new ItemBuilder(value.glassItem);
                    itemBuilder.name(ChatColor.GREEN + "Click to submit a " + value.name + " weapon");
                } else {
                    itemBuilder = new ItemBuilder(playerEntry.get().getWeapon().generateItemStack());
                    itemBuilder.addLore(
                            "",
                            ChatColor.YELLOW.toString() + ChatColor.BOLD + "LEFT-CLICK" + ChatColor.GREEN + " to change your submission",
                            ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK" + ChatColor.GREEN + " to remove your submission"
                    );
                }
                menu.setItem(column, 2,
                        itemBuilder.get(),
                        (m, e) -> {
                            if (!playerEntry.isPresent() || e.isLeftClick()) { //submit | change weapon
                                openSubmissionMenu(player, value, 1);
                            } else { //remove weapon
                                weaponInventory.add(playerEntry.get().getWeapon());
                                weaponPlayerEntries.remove(playerEntry.get());

                                updateFair.set(true);
                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                                openMasterworksFairMenu(player);
                            }
                        }
                );

                //last 10 placements
                List<MasterworksFairEntry> masterworksFairEntries = databasePlayerPvE.getMasterworksFairEntries();
                List<String> placementHistory = masterworksFairEntries
                        .stream()
                        .filter(masterworksFairEntry -> masterworksFairEntry.getRarity() == value)
                        .collect(Utils.lastN(10))
                        .stream()
                        .map(masterworksFairEntry -> ChatColor.GRAY + FORMATTER.format(masterworksFairEntry.getTime()) + ": " + value.chatColor + "#" + masterworksFairEntry.getPlacement() + ChatColor.GRAY + " - " + ChatColor.YELLOW + masterworksFairEntry.getScore() + "\n")
                        .collect(Collectors.toList());
                menu.setItem(column, 3,
                        new ItemBuilder(Material.BOOK)
                                .name(ChatColor.GREEN + "Your most recent placements")
                                .lore(IntStream.range(0, placementHistory.size())
                                        .mapToObj(index -> placementHistory.get(placementHistory.size() - index - 1))
                                        .collect(Collectors.toList()))
                                .get(), (m, e) -> {

                        });
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

        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
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
                .filter(masterworksFairPlayerEntry -> masterworksFairPlayerEntry.getUuid().equals(uuid))
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
                            MasterworksFairPlayerEntry masterworksFairPlayerEntry = playerEntry.orElseGet(() -> new MasterworksFairPlayerEntry(uuid));
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
                            updateFair.set(true);
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
