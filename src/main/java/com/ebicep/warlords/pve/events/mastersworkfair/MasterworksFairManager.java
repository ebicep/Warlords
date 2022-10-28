package com.ebicep.warlords.pve.events.mastersworkfair;

import com.ebicep.customentities.npc.traits.MasterworksFairTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFairPlayerEntry;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.rewards.types.MasterworksFairReward;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu;
import com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
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
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            sendMasterworksFairMessage(onlinePlayer, ChatColor.GREEN + "Masterworks Fair #" + masterworksFair.getFairNumber() + " has just ended!");
        }
        ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Resetting fair");
        masterworksFair.setEnded(true);
        //give out rewards
        awardEntries(masterworksFair, throughRewardsInventory);
        //reset fair
        MasterworksFairTrait.startTime = Instant.now().plus(minutesTillStart, ChronoUnit.MINUTES);
        MasterworksFairTrait.PAUSED.set(false);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getOpenInventory().getTopInventory().getName().equals("Masterworks Fair")) {
                onlinePlayer.closeInventory();
            }
        }
    }

    public static void sendMasterworksFairMessage(Player player, String message) {
        player.sendMessage(ChatColor.GOLD + "Masterworks Fair" + ChatColor.DARK_GRAY + " > " + message);
    }

    public static void awardEntries(MasterworksFair masterworksFair, boolean throughRewardsInventory) {
        currentFair = null;

        Warlords.newChain()
                .async(() -> DatabaseManager.masterworksFairService.update(masterworksFair))
                .sync(() -> {
                    Instant now = Instant.now();
                    HashMap<UUID, List<MasterworksFairEntry>> playerFairResults = new HashMap<>();
                    int fairNumber = masterworksFair.getFairNumber();
                    for (WeaponsPvE rarity : WeaponsPvE.VALUES) {
                        if (rarity.getPlayerEntries == null) {
                            continue;
                        }

                        List<MasterworksFairPlayerEntry> playerEntries = rarity.getPlayerEntries.apply(masterworksFair);
                        playerEntries.sort(Comparator.comparingDouble(o -> ((WeaponScore) o.getWeapon()).getWeaponScore()));
                        Collections.reverse(playerEntries);

                        for (int i = 0; i < playerEntries.size(); i++) {
                            MasterworksFairPlayerEntry entry = playerEntries.get(i);
                            MasterworksFairEntry playerRecordEntry = new MasterworksFairEntry(now,
                                    rarity,
                                    i + 1,
                                    Float.parseFloat(NumberFormat.formatOptionalHundredths(((WeaponScore) entry.getWeapon()).getWeaponScore())),
                                    fairNumber
                            );
                            playerFairResults.computeIfAbsent(entry.getUuid(), k -> new ArrayList<>()).add(playerRecordEntry);
                        }
                    }
                    Warlords.newChain()
                            .async(() -> {
                                playerFairResults.forEach((uuid, masterworksFairEntries) -> {
                                    Warlords.newChain()
                                            .asyncFirst(() -> DatabaseManager.playerService.findByUUID(uuid))
                                            .syncLast(databasePlayer -> {
                                                if (databasePlayer == null) {
                                                    return;
                                                }
                                                DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                                                if (pveStats == null) {
                                                    return;
                                                }
                                                for (MasterworksFairEntry masterworksFairEntry : masterworksFairEntries) {
                                                    WeaponsPvE rarity = masterworksFairEntry.getRarity();
                                                    pveStats.addMasterworksFairEntry(masterworksFairEntry);
                                                    LinkedHashMap<Currencies, Long> rewards = getRewards(masterworksFair, masterworksFairEntry);
                                                    if (throughRewardsInventory) {
                                                        pveStats.addReward(new MasterworksFairReward(rewards, now, rarity));
                                                    } else {
                                                        rewards.forEach(pveStats::addCurrency);
                                                    }
                                                }

                                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                            })
                                            .execute();
                                });
                            })
                            .execute();
                    masterworksFair.sendResults(playerFairResults);
                    if (throughRewardsInventory) {
                        ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Awarded entries through reward inventory");
                    } else {
                        ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Awarded entries directly");
                    }
                }).
                execute();
    }

    public static LinkedHashMap<Currencies, Long> getRewards(MasterworksFair masterworksFair, MasterworksFairEntry masterworksFairEntry) {
        int fairNumber = masterworksFair.getFairNumber();
        int placement = masterworksFairEntry.getPlacement();
        LinkedHashMap<Currencies, Long> rewards = new LinkedHashMap<>();
        if (placement < 3) { //top three guaranteed Star Piece of the weapon rarity they submitted
            rewards.put(masterworksFairEntry.getRarity().starPieceCurrency, 1L);
            switch (placement) { //The top submission will get 10 Supply Drop roll opportunities, 2nd and 3rd place will get 7 Supply Drop roll opportunities
                case 0:
                    rewards.put(Currencies.SUPPLY_DROP_TOKEN, 10L);
                    break;
                case 1:
                case 2:
                    rewards.put(Currencies.SUPPLY_DROP_TOKEN, 7L);
                    break;
            }
        } else {
            if (placement < 10) { //4-10 will get 5 Supply Drop roll opportunities
                rewards.put(Currencies.SUPPLY_DROP_TOKEN, 5L);
            } else if (masterworksFairEntry.getScore() >= 85) { //Players who submit a 85%+ weapon
                // will be guaranteed at least 3 supply drop opportunities
                rewards.put(Currencies.SUPPLY_DROP_TOKEN, 3L);
            } else { //Players who submit any weapon will get a guaranteed supply drop roll as pity
                rewards.put(Currencies.SUPPLY_DROP_TOKEN, 1L);
            }
        }
        if (fairNumber != 0 && fairNumber % 10 == 0) {
            rewards.forEach((currency, amount) -> rewards.put(currency, amount * 10));
        }
        return rewards;
    }

    public static void sendMasterworksFairMessage(Player player, ComponentBuilder components) {
        BaseComponent[] baseComponents = new ComponentBuilder(ChatColor.GOLD + "Masterworks Fair" + ChatColor.DARK_GRAY + " > ")
                .create();
        player.spigot().sendMessage(components.prependAndCreate(baseComponents));
    }

    public static void createFair() {
        MasterworksFair newFair = new MasterworksFair();
        initializeFair(newFair);
        createFair(newFair);
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

    private static void createFair(MasterworksFair masterworksFair) {
        Warlords.newChain()
                .asyncFirst(() -> {
                    DatabaseManager.masterworksFairService.create(masterworksFair);
                    return DatabaseManager.masterworksFairService.count();
                })
                .syncLast(count -> {
                    int size = Math.toIntExact(count);
                    masterworksFair.setFairNumber(size);
                    DatabaseManager.masterworksFairService.update(masterworksFair);
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        sendMasterworksFairMessage(onlinePlayer, ChatColor.GREEN + "Masterworks Fair #" + size + " has just started!" +
                                (size % 10 == 0 ? ChatColor.RED + " 10x REWARDS!" : ""));
                    }
                })
                .execute();
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
        DatabaseManager.getPlayer(uuid, databasePlayer -> {
            DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();
            List<AbstractWeapon> weaponInventory = databasePlayerPvE.getWeaponInventory();

            WeaponsPvE[] values = WeaponsPvE.VALUES;
            int column = 2;
            for (WeaponsPvE value : values) {
                if (value.getPlayerEntries == null) {
                    continue;
                }
                List<MasterworksFairPlayerEntry> weaponPlayerEntries = value.getPlayerEntries.apply(currentFair);
                Optional<MasterworksFairPlayerEntry> playerEntry = weaponPlayerEntries.stream()
                        .filter(masterworksFairPlayerEntry -> masterworksFairPlayerEntry.getUuid().equals(uuid))
                        .findFirst();

                ItemBuilder itemBuilder;
                if (playerEntry.isEmpty()) {
                    itemBuilder = new ItemBuilder(value.glassItem);
                    itemBuilder.name(ChatColor.GREEN + "Click to submit a " + value.name + " weapon");
                } else {
                    itemBuilder = new ItemBuilder(playerEntry.get().getWeapon().generateItemStack(false));
                    itemBuilder.addLore(
                            "",
                            ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to change your submission"
                    );
                }
                menu.setItem(column, 2,
                        itemBuilder.get(),
                        (m, e) -> openSubmissionMenu(player, databasePlayer, value, 1)
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

                        }
                );
                column += 2;
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
        });
    }

    public static void openSubmissionMenu(Player player, DatabasePlayer databasePlayer, WeaponsPvE weaponType, int page) {
        Menu menu = new Menu("Choose a weapon", 9 * 6);
        UUID uuid = player.getUniqueId();
        List<AbstractWeapon> weaponInventory = databasePlayer.getPveStats().getWeaponInventory();
        List<AbstractWeapon> filteredWeaponInventory = new ArrayList<>(weaponInventory);
        filteredWeaponInventory.removeIf(weapon -> weapon.getRarity() != weaponType);
        filteredWeaponInventory.sort(WeaponManagerMenu.SortOptions.WEAPON_SCORE.comparator.reversed());

        List<MasterworksFairPlayerEntry> weaponPlayerEntries = weaponType.getPlayerEntries.apply(currentFair);
        Optional<MasterworksFairPlayerEntry> playerEntry = weaponPlayerEntries.stream()
                .filter(masterworksFairPlayerEntry -> masterworksFairPlayerEntry.getUuid().equals(uuid))
                .findFirst();

        for (int i = 0; i < 45; i++) {
            int weaponNumber = ((page - 1) * 45) + i;
            if (weaponNumber < filteredWeaponInventory.size()) {
                AbstractWeapon weapon = filteredWeaponInventory.get(weaponNumber);

                int column = i % 9;
                int row = i / 9;

                menu.setItem(
                        column,
                        row,
                        weapon.generateItemStack(false),
                        (m, e) -> {
                            //check bound
                            if (weapon.isBound()) {
                                sendMasterworksFairMessage(player, ChatColor.RED + "You cannot submit a bound weapon. Unbind it first!");
                                return;
                            }
                            Menu.openConfirmationMenu(
                                    player,
                                    "Submit Weapon",
                                    3,
                                    Arrays.asList(
                                            ChatColor.GRAY + "Submit " + weapon.getName(),
                                            ChatColor.GRAY + "to the Masterworks Fair?",
                                            "",
                                            ChatColor.RED + "WARNING: " + ChatColor.GRAY + "This will override any previous",
                                            ChatColor.GRAY + "weapon and you cannot get this weapon back!"
                                    ),
                                    Collections.singletonList(ChatColor.GRAY + "Go back"),
                                    (m2, e2) -> {
                                        //submit weapon to fair
                                        MasterworksFairPlayerEntry masterworksFairPlayerEntry = playerEntry.orElseGet(() -> new MasterworksFairPlayerEntry(uuid));
                                        if (playerEntry.isEmpty()) {
                                            //add new entry if there wasnt already one
                                            weaponPlayerEntries.add(masterworksFairPlayerEntry);
                                        }
                                        //remove new weapon
                                        weaponInventory.remove(weapon);
                                        //set new weapon
                                        masterworksFairPlayerEntry.setWeapon(weapon);

                                        //update database stuff
                                        updateFair.set(true);
                                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                                        sendMasterworksFairMessage(player,
                                                new ComponentBuilder(ChatColor.GRAY + "Submitted ")
                                                        .appendHoverItem(weapon.getName(), weapon.generateItemStack(false))
                                                        .append(ChatColor.GRAY + " to the Masterworks Fair!")
                                        );

                                        openMasterworksFairMenu(player);
                                    },
                                    (m2, e2) -> openSubmissionMenu(player, databasePlayer, weaponType, page),
                                    (m2) -> {
                                        m2.setItem(4, 1,
                                                weapon.generateItemStack(false),
                                                (m3, e3) -> {
                                                }
                                        );
                                    }
                            );
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
                        openSubmissionMenu(player, databasePlayer, weaponType, page - 1);
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
                        openSubmissionMenu(player, databasePlayer, weaponType, page + 1);
                    }
            );
        }

        menu.setItem(4, 5, Menu.MENU_BACK, (m, e) -> openMasterworksFairMenu(player));
        menu.openForPlayer(player);
    }
}
