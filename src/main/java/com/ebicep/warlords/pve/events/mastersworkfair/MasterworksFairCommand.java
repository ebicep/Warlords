package com.ebicep.warlords.pve.events.mastersworkfair;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFairPlayerEntry;
import com.ebicep.warlords.database.repositories.player.pojos.general.FutureMessage;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.rewards.types.MasterworksFairReward;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager.currentFair;
import static com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager.resetFair;

@CommandAlias("masterworksfair")
@CommandPermission("group.administrator")
public class MasterworksFairCommand extends BaseCommand {

    @Subcommand("end")
    @Description("Ends the current masterworks fair event")
    public void end(CommandIssuer issuer, Boolean awardThroughRewardsInventory, @Default("5") @Conditions("limits:min=1,max=5") Integer startMinuteDelay) {
        if (currentFair == null) {
            ChatChannels.sendDebugMessage(issuer, Component.text("No current masterworks fair event to end", NamedTextColor.RED));
            return;
        }
        ChatChannels.sendDebugMessage(issuer,
                Component.text("Ending current masterworks fair event with start delay of " + startMinuteDelay, NamedTextColor.GREEN)
        );
        currentFair.setEnded(true);
        resetFair(currentFair, awardThroughRewardsInventory, startMinuteDelay);
    }

    @Subcommand("resendresults")
    @Description("Resends the results of the selected masterworks fair, or the latest one if none is selected")
    public void resendResults(CommandIssuer issuer, @Optional Integer fairNumber) {
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.masterworksFairService.findAll())
                .syncLast(fairs -> {
                    if (fairNumber == null) {
                        fairs.get(fairs.size() - 2).sendResults(true);
                        ChatChannels.sendDebugMessage(issuer, Component.text("Resent Masterworks Fair before current results", NamedTextColor.GREEN));
                    } else {
                        java.util.Optional<MasterworksFair> fairOptional = fairs.stream()
                                                                                .filter(fair -> fair.getFairNumber() == fairNumber)
                                                                                .findFirst();
                        if (fairOptional.isPresent()) {
                            fairOptional.get().sendResults(true);
                            ChatChannels.sendDebugMessage(issuer, Component.text("Resent Masterworks Fair #" + fairNumber + " results", NamedTextColor.GREEN));
                        } else {
                            ChatChannels.sendDebugMessage(issuer, Component.text("Could not find fair #" + fairNumber, NamedTextColor.RED));
                        }
                    }
                })
                .execute();
    }

    @Subcommand("resendrewards")
    @Description("Resends the rewards of the selected masterworks fair, or the latest one if none is selected")
    public void resendRewards(CommandIssuer issuer, @Optional Integer fairNumber) {
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.masterworksFairService.findAll())
                .syncLast(fairs -> {
                    if (fairNumber == null) {
                        fairs.get(fairs.size() - 2).sendRewards(true);
                        ChatChannels.sendDebugMessage(issuer, Component.text("Resent Masterworks Fair before current rewards", NamedTextColor.GREEN));
                    } else {
                        java.util.Optional<MasterworksFair> fairOptional = fairs.stream()
                                                                                .filter(fair -> fair.getFairNumber() == fairNumber)
                                                                                .findFirst();
                        if (fairOptional.isPresent()) {
                            fairOptional.get().sendRewards(true);
                            ChatChannels.sendDebugMessage(issuer, Component.text("Resent Masterworks Fair #" + fairNumber + " rewards", NamedTextColor.GREEN));
                        } else {
                            ChatChannels.sendDebugMessage(issuer, Component.text("Could not find fair #" + fairNumber, NamedTextColor.RED));
                        }
                    }
                })
                .execute();
    }

    @Subcommand("validate")
    @Description("Validates the masterworks fair event, making sure players got their rewards")
    public void validate(CommandIssuer issuer, Instant instant) {
        ChatChannels.sendDebugMessage(issuer, Component.text("Locating fair with start date " + instant, NamedTextColor.GREEN));
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.masterworksFairService.findAll())
                .syncLast(fairs -> {
                    MasterworksFair masterworksFair = fairs.stream()
                                                           .filter(fair -> fair.getStartDate().equals(instant))
                                                           .findFirst()
                                                           .orElse(null);
                    if (masterworksFair == null) {
                        ChatChannels.sendDebugMessage(issuer, Component.text("Could not find fair with start date " + instant, NamedTextColor.RED));
                        return;
                    } else {
                        ChatChannels.sendDebugMessage(issuer, Component.text("Found fair with start date " + instant, NamedTextColor.GREEN));
                    }
                    int fairNumber = masterworksFair.getFairNumber();
                    HashMap<UUID, List<MasterworksFairEntry>> playerFairResults = new HashMap<>();
                    for (WeaponsPvE rarity : WeaponsPvE.VALUES) {
                        if (rarity.getPlayerEntries == null) {
                            continue;
                        }

                        List<MasterworksFairPlayerEntry> playerEntries = rarity.getPlayerEntries.apply(masterworksFair);
                        playerEntries.sort(Comparator.comparingDouble(o -> ((WeaponScore) o.getWeapon()).getWeaponScore()));
                        Collections.reverse(playerEntries);

                        for (int i = 0; i < playerEntries.size(); i++) {
                            MasterworksFairPlayerEntry entry = playerEntries.get(i);
                            MasterworksFairEntry playerRecordEntry = new MasterworksFairEntry(instant,
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
                                                boolean resent = false;
                                                for (MasterworksFairEntry masterworksFairEntry : masterworksFairEntries) {
                                                    WeaponsPvE rarity = masterworksFairEntry.getRarity();
                                                    if (pveStats.getMasterworksFairEntries()
                                                                .stream()
                                                                .anyMatch(entry -> entry.getFairNumber() == masterworksFairEntry.getFairNumber() && entry.getRarity() == rarity)
                                                    ) {
                                                        continue;
                                                    }
                                                    resent = true;
                                                    pveStats.addMasterworksFairEntry(masterworksFairEntry);
                                                    LinkedHashMap<Spendable, Long> rewards = masterworksFair.getRewards(masterworksFairEntry);
                                                    pveStats.addReward(new MasterworksFairReward(rewards, instant, rarity));
                                                    ChatChannels.sendDebugMessage(
                                                            (CommandIssuer) null,
                                                            Component.text("Validating Fair - Gave ", NamedTextColor.GREEN)
                                                                     .append(Component.text(databasePlayer.getName(), NamedTextColor.AQUA))
                                                                     .append(Component.text(" their ", NamedTextColor.GREEN))
                                                                     .append(rarity.getTextColoredName())
                                                                     .append(Component.text(" reward", NamedTextColor.GREEN))
                                                    );
                                                }
                                                if (resent) {
                                                    databasePlayer.addFutureMessage(FutureMessage.create(Arrays.asList(
                                                            Component.text("------------------------------------------------", NamedTextColor.GOLD),
                                                            Component.text("Hey! We noticed you didn't get all your previous Masterworks",
                                                                    NamedTextColor.GREEN
                                                            ),
                                                            Component.text("Fair rewards, so we've given them to you!", NamedTextColor.GREEN),
                                                            Component.text("------------------------------------------------", NamedTextColor.GOLD)
                                                    ), true));
                                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                                }
                                            })
                                            .execute();
                                });
                            })
                            .execute();
                })
                .execute();
    }

    @Subcommand("participants")
    @Description("Lists the participants of the masterworks fair event")
    public void participants(Player player, @Optional Integer fairNum) {
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.masterworksFairService.findAll())
                .syncLast(fairs -> {
                    MasterworksFair masterworksFair;
                    AtomicInteger fairNumber = new AtomicInteger();
                    if (fairNum == null) {
                        masterworksFair = fairs.get(fairs.size() - 1);
                        fairNumber.set(masterworksFair.getFairNumber());
                    } else {
                        java.util.Optional<MasterworksFair> fairOptional = fairs.stream()
                                                                                .filter(f -> f.getFairNumber() == fairNum)
                                                                                .findFirst();
                        if (fairOptional.isPresent()) {
                            masterworksFair = fairOptional.get();
                            fairNumber.set(masterworksFair.getFairNumber());
                        } else {
                            ChatChannels.sendDebugMessage(player, Component.text("Could not find fair #" + fairNum, NamedTextColor.RED));
                            return;
                        }
                    }
                    ChatChannels.sendDebugMessage(player, Component.text("Masterworks Fair #" + fairNumber.get() + " Participants:", NamedTextColor.GREEN));
                    ChatChannels.sendDebugMessage(player, Component.text(" - Common", NamedTextColor.GREEN));
                    sendFairEntry(player, masterworksFair.getCommonPlayerEntries());
                    ChatChannels.sendDebugMessage(player, Component.text(" - Rare", NamedTextColor.BLUE));
                    sendFairEntry(player, masterworksFair.getRarePlayerEntries());
                    ChatChannels.sendDebugMessage(player, Component.text(" - Epic", NamedTextColor.DARK_PURPLE));
                    sendFairEntry(player, masterworksFair.getEpicPlayerEntries());
                })
                .execute();
    }

    private void sendFairEntry(Player player, List<MasterworksFairPlayerEntry> entries) {
        entries.sort(Comparator.comparingDouble(o -> ((WeaponScore) o.getWeapon()).getWeaponScore()));
        Collections.reverse(entries);
        for (int i = 0; i < entries.size(); i++) {
            MasterworksFairPlayerEntry entry = entries.get(i);
            ChatChannels.playerSendMessage(player,
                    ChatChannels.DEBUG,
                    Component.text("   " + (i + 1), NamedTextColor.GRAY)
                             .append(Component.text(". " + Bukkit.getOfflinePlayer(entry.getUuid()).getName(), NamedTextColor.AQUA))
                             .append(Component.text(" (" + entry.getUuid() + ")")
                                              .clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand("" + entry.getUuid()))
                             )
            );
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
