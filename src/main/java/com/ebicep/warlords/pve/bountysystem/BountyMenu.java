package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.bountysystem.events.BountyCancelEvent;
import com.ebicep.warlords.pve.bountysystem.events.BountyClaimEvent;
import com.ebicep.warlords.pve.bountysystem.events.BountyStartEvent;
import com.ebicep.warlords.pve.rewards.RewardInventory;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public class BountyMenu {

    public static void openBountyMenu(Player player) {
        DatabaseManager.getPlayer(player, databasePlayer -> {
            Menu menu = new Menu("Bounties", 9 * 6);

            addBountiesToMenu(player, databasePlayer, PlayersCollections.DAILY, menu, 1, true);
            addBountiesToMenu(player, databasePlayer, PlayersCollections.WEEKLY, menu, 2, true);
            addBountiesToMenu(player, databasePlayer, PlayersCollections.LIFETIME, menu, 3, false);
            if (DatabaseGameEvent.currentGameEvent != null) {
                addBountiesToMenu(player, databasePlayer, PlayersCollections.LIFETIME, menu, 4, true, DatabaseGameEvent.currentGameEvent.getEvent().name);
            }

            menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        });
    }

    private static void addBountiesToMenu(Player player, DatabasePlayer lifetimeDatabasePlayer, PlayersCollections collection, Menu menu, int y, boolean claimAll) {
        addBountiesToMenu(player, lifetimeDatabasePlayer, collection, menu, y, claimAll, collection.name);
    }

    private static void addBountiesToMenu(
            Player player,
            DatabasePlayer lifetimeDatabasePlayer,
            PlayersCollections collection,
            Menu menu,
            int y,
            boolean claimAll,
            String bountyInfoName
    ) {
        DatabaseManager.getPlayer(player.getUniqueId(), collection, databasePlayer -> {
            menu.setItem(1, y,
                    new ItemBuilder(Material.WRITABLE_BOOK)
                            .name(Component.text(bountyInfoName + " Bounties", BountyUtils.COLOR))
                            .get(),
                    (m, e) -> {}
            );

            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            List<AbstractBounty> activeBounties;
            DatabaseGameEvent gameEvent = DatabaseGameEvent.currentGameEvent;
            if (gameEvent != null && !bountyInfoName.equals(collection.name)) {
                BountyUtils.validateBounties(databasePlayer, bountyInfoName, true);
                EventMode eventMode = gameEvent.getEvent().eventsStatsFunction.apply(pveStats.getEventStats()).get(gameEvent.getStartDateSecond());
                if (eventMode == null) {
                    for (int i = 0; i < 5; i++) {
                        menu.setItem(i + 2, y,
                                new ItemBuilder(Material.MAP)
                                        .name(Component.text("Play an event game to unlock these bounties", NamedTextColor.RED))
                                        .get(),
                                (m, e) -> {}
                        );
                    }
                    return;
                }
                activeBounties = eventMode.getActiveEventBounties();
                if (activeBounties.isEmpty()) {
                    activeBounties.addAll(BountyUtils.getNewBounties(gameEvent.getEvent().name));
                }
            } else {
                BountyUtils.validateBounties(databasePlayer, bountyInfoName, false);
                activeBounties = pveStats.getActiveBounties();
            }
            int bountiesStarted = activeBounties.stream().mapToInt(bounty -> bounty != null && bounty.isStarted() ? 1 : 0).sum();
            boolean canBeClaimed = false;
            for (int i = 0; i < activeBounties.size(); i++) {
                AbstractBounty bounty = activeBounties.get(i);
                if (bounty == null) {
                    menu.setItem(i + 2, y,
                            new ItemBuilder(Material.BARRIER)
                                    .name(Component.text("Max bounties claimed!", NamedTextColor.RED))
                                    .get(),
                            (m, e) -> {}
                    );
                    continue;
                }
                if (bounty.isStarted() && bounty.getProgress() == null) {
                    canBeClaimed = true;
                }
                menu.setItem(i + 2, y,
                        bounty.getItemWithProgress().get(),
                        (m, e) -> {
                            onBountyClick(player, lifetimeDatabasePlayer, collection, bountyInfoName, databasePlayer, bountiesStarted, bounty);
                        }
                );
            }
            if (claimAll && canBeClaimed) {
                menu.setItem(7, y,
                        new ItemBuilder(Material.GOLD_BLOCK)
                                .name(Component.text("Click to claim all bounties!", NamedTextColor.GREEN))
                                .get(),
                        (m, e) -> {
                            for (AbstractBounty bounty : activeBounties) {
                                if (bounty != null && bounty.isStarted() && bounty.getProgress() == null) {
                                    claimBounty(player, collection, databasePlayer, bounty, bountyInfoName);
                                }
                            }
                            player.closeInventory();
                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer, collection);
                        }
                );
            }
        });
    }

    private static void onBountyClick(
            Player player,
            DatabasePlayer lifetimeDatabasePlayer,
            PlayersCollections collection,
            String bountyInfoName,
            DatabasePlayer databasePlayer,
            int bountiesStarted,
            AbstractBounty bounty
    ) {
        if (bounty.isStarted()) {
            if (bounty.getProgress() == null) {
                claimBounty(player, collection, databasePlayer, bounty, bountyInfoName);
                player.closeInventory();
            } else {
                Menu.openConfirmationMenu(
                        player,
                        "Cancel Bounty",
                        3,
                        Component.text("Cancel Bounty: ", NamedTextColor.RED).append(Component.text(bounty.getName(), NamedTextColor.GREEN)),
                        new ArrayList<>() {{
                            addAll(WordWrap.wrap(Component.text(bounty.getDescription(), NamedTextColor.GRAY), 160));
                            add(Component.empty());
                            add(Component.text("Rewards:", NamedTextColor.GRAY));
                            bounty.getCurrencyReward()
                                  .forEach((currencies, aLong) -> add(Component.text(" +", NamedTextColor.DARK_GRAY).append(currencies.getCostColoredName(aLong))));
                            add(Component.empty());
                            addAll(WordWrap.wrap(Component.text("Click to CANCEL this bounty. This will also reset its progress.", NamedTextColor.RED), 140));
                        }},
                        Component.text("Cancel", NamedTextColor.RED),
                        Collections.singletonList(Component.text("Go back", NamedTextColor.GRAY)),
                        (m2, e2) -> {
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                            bounty.setValue(0);
                            bounty.setStarted(false);
                            BountyUtils.sendBountyMessage(
                                    player,
                                    Component.text("You cancelled the " + bountyInfoName + " bounty ", NamedTextColor.GRAY)
                                             .append(Component.text(bounty.getName(), NamedTextColor.GREEN)
                                                              .hoverEvent(bounty.getItem().get().asHoverEvent()))
                                             .append(Component.text("!"))
                            );
                            Bukkit.getPluginManager().callEvent(new BountyCancelEvent(databasePlayer, bounty));
                            player.closeInventory();
                            DatabaseManager.queueUpdatePlayerAsync(lifetimeDatabasePlayer);
                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer, collection);
                        },
                        (m2, e2) -> openBountyMenu(player),
                        (m2) -> {
                        }
                );
            }
            return;
        }
        BountyUtils.BountyInfo bountyInfo = BountyUtils.BOUNTY_COLLECTION_INFO.get(bountyInfoName);
        if (bountiesStarted >= bountyInfo.maxBountiesStarted()) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
            player.sendMessage(Component.text("You can only accept " + bountyInfo.maxBountiesStarted() + " " + bountyInfoName + " bounties at a time!",
                    NamedTextColor.RED
            ));
            player.closeInventory();
            return;
        }
        LinkedHashMap<Currencies, Long> bountyCost = bounty.getCost();
        for (Map.Entry<Currencies, Long> currenciesLongEntry : bountyCost.entrySet()) {
            Currencies currency = currenciesLongEntry.getKey();
            Long cost = currenciesLongEntry.getValue();
            if (lifetimeDatabasePlayer.getPveStats().getCurrencyValue(currency) < cost) {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                            .append(currency.getCostColoredName(cost))
                                            .append(Component.text(" to start this bounty!"))
                );
                return;
            }
        }
        Menu.openConfirmationMenu(
                player,
                "Start Bounty",
                3,
                Component.text("Start Bounty: ", NamedTextColor.GRAY).append(Component.text(bounty.getName(), NamedTextColor.GREEN)),
                new ArrayList<>() {{
                    addAll(WordWrap.wrap(Component.text(bounty.getDescription(), NamedTextColor.GRAY), 160));
                    add(Component.empty());
                    add(Component.text("Rewards:", NamedTextColor.GRAY));
                    bounty.getCurrencyReward()
                          .forEach((currencies, aLong) -> add(Component.text(" +", NamedTextColor.DARK_GRAY).append(currencies.getCostColoredName(aLong))));
                    addAll(PvEUtils.getCostLore(bountyCost, true));
                    add(Component.empty());
                    addAll(WordWrap.wrap(Component.text("You can only have " + bountyInfo.maxBountiesStarted() + " " + bountyInfoName + " bounties active at a time. You can cancel a bounty at any time.",
                            NamedTextColor.GRAY
                    ), 160));
                }},
                Component.text("Cancel", NamedTextColor.RED),
                Collections.singletonList(Component.text("Go back", NamedTextColor.GRAY)),
                (m2, e2) -> {
                    for (Map.Entry<Currencies, Long> currenciesLongEntry : bountyCost.entrySet()) {
                        lifetimeDatabasePlayer.getPveStats().subtractCurrency(currenciesLongEntry.getKey(), currenciesLongEntry.getValue());
                    }
                    player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    bounty.setStarted(true);
                    BountyUtils.sendBountyMessage(
                            player,
                            Component.text("You started the " + bountyInfoName + " bounty ", NamedTextColor.GRAY)
                                     .append(Component.text(bounty.getName(), NamedTextColor.GREEN)
                                                      .hoverEvent(bounty.getItem().get().asHoverEvent()))
                                     .append(Component.text("!"))
                    );
                    Bukkit.getPluginManager().callEvent(new BountyStartEvent(databasePlayer, bounty));
                    player.closeInventory();
                    DatabaseManager.queueUpdatePlayerAsync(lifetimeDatabasePlayer);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer, collection);
                },
                (m2, e2) -> openBountyMenu(player),
                (m2) -> {
                }
        );
    }

    private static void claimBounty(Player player, PlayersCollections collection, DatabasePlayer databasePlayer, AbstractBounty bounty, String bountyInfoName) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2f);
        bounty.claim(databasePlayer, collection, bountyInfoName);
        BountyUtils.sendBountyMessage(
                player,
                Component.text("You claimed the " + collection.name.toLowerCase() + " bounty ", NamedTextColor.GRAY)
                         .append(Component.text(bounty.getName(), NamedTextColor.GREEN)
                                          .hoverEvent(bounty.getItem().get().asHoverEvent()))
                         .append(Component.text(". The reward has been added to your "))
                         .append(Component.text("Reward Inventory", NamedTextColor.YELLOW, TextDecoration.BOLD, TextDecoration.UNDERLINED)
                                          .clickEvent(ClickEvent.callback(audience -> RewardInventory.openRewardInventory(player, 1))))
                         .append(Component.text("!"))
        );
        Bukkit.getPluginManager().callEvent(new BountyClaimEvent(databasePlayer, bounty));
    }

}
