package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
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

            menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        });
    }

    private static void addBountiesToMenu(Player player, DatabasePlayer lifetimeDatabasePlayer, PlayersCollections collection, Menu menu, int y, boolean claimAll) {
        DatabaseManager.getPlayer(player.getUniqueId(), collection, databasePlayer -> {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            List<AbstractBounty> bounties = pveStats.getActiveBounties();
            menu.setItem(1, y,
                    new ItemBuilder(Material.WRITABLE_BOOK)
                            .name(Component.text(collection.name + " Bounties", BountyUtils.COLOR))
                            .get(),
                    (m, e) -> {}
            );
            int bountiesStarted = bounties.stream().mapToInt(bounty -> bounty != null && bounty.isStarted() ? 1 : 0).sum();
            boolean canBeClaimed = false;
            for (int i = 0; i < bounties.size(); i++) {
                AbstractBounty bounty = bounties.get(i);
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
                            if (bounty.isStarted()) {
                                if (bounty.getProgress() == null) {
                                    claimBounty(player, collection, databasePlayer, bounty);
                                    player.closeInventory();
                                }
                            } else {
                                BountyUtils.BountyInfo bountyInfo = BountyUtils.BOUNTY_COLLECTION_INFO.get(collection);
                                if (bountiesStarted >= bountyInfo.maxBountiesStarted()) {
                                    player.sendMessage(Component.text("You can only accept " + bountyInfo.maxBountiesStarted() + " " + collection.name + " bounties at a time!",
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
                                        Component.text("Start Bounty: ", NamedTextColor.GRAY)
                                                 .append(Component.text(bounty.getName(), NamedTextColor.GREEN)),
                                        new ArrayList<>() {{
                                            addAll(WordWrap.wrap(Component.text(bounty.getDescription(), NamedTextColor.GRAY), 160));
                                            add(Component.empty());
                                            add(Component.text("Rewards:", NamedTextColor.GRAY));
                                            bounty.getCurrencyReward()
                                                  .forEach((currencies, aLong) -> add(Component.text(" +", NamedTextColor.DARK_GRAY).append(currencies.getCostColoredName(aLong))));
                                            addAll(PvEUtils.getCostLore(bountyCost, true));
                                        }},
                                        Component.text("Cancel", NamedTextColor.RED),
                                        Collections.singletonList(Component.text("Go back", NamedTextColor.GRAY)),
                                        (m2, e2) -> {
                                            for (Map.Entry<Currencies, Long> currenciesLongEntry : bountyCost.entrySet()) {
                                                lifetimeDatabasePlayer.getPveStats().subtractCurrency(currenciesLongEntry.getKey(), currenciesLongEntry.getValue());
                                            }
                                            bounty.setStarted(true);
                                            BountyUtils.sendBountyMessage(
                                                    player,
                                                    Component.text("You started the " + collection.name.toLowerCase() + " bounty ", NamedTextColor.GRAY)
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
                        }
                );
            }
            if (claimAll && canBeClaimed) {
                menu.setItem(7, y,
                        new ItemBuilder(Material.GOLD_BLOCK)
                                .name(Component.text("Click to claim all bounties!", NamedTextColor.GREEN))
                                .get(),
                        (m, e) -> {
                            for (AbstractBounty bounty : bounties) {
                                if (bounty.isStarted() && bounty.getProgress() == null) {
                                    claimBounty(player, collection, databasePlayer, bounty);
                                }
                            }
                            player.closeInventory();
                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer, collection);
                        }
                );
            }
        });
    }

    private static void claimBounty(Player player, PlayersCollections collection, DatabasePlayer databasePlayer, AbstractBounty bounty) {
        bounty.claim(databasePlayer, collection);
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
