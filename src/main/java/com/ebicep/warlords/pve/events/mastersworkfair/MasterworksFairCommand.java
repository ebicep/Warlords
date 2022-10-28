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
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.rewards.types.MasterworksFairReward;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

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
            ChatChannels.sendDebugMessage(issuer, ChatColor.RED + "No current masterworks fair event to end", true);
            return;
        }
        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Ending current masterworks fair event with start delay of " + startMinuteDelay, true);
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
                        fairs.get(fairs.size() - 1).sendResults();
                        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Resent latest Masterworks Fair results", true);
                    } else {
                        java.util.Optional<MasterworksFair> fairOptional = fairs.stream()
                                .filter(fair -> fair.getFairNumber() == fairNumber)
                                .findFirst();
                        if (fairOptional.isPresent()) {
                            fairOptional.get().sendResults();
                            ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Resent Masterworks Fair #" + fairNumber + " results", true);
                        } else {
                            ChatChannels.sendDebugMessage(issuer, ChatColor.RED + "Could not find fair #" + fairNumber, true);
                        }
                    }
                })
                .execute();
    }

    @Subcommand("validate")
    @Description("Validates the masterworks fair event, making sure players got their rewards")
    public void validate(CommandIssuer issuer, Instant instant, @Optional Integer fairNum) {
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
                            ChatChannels.sendDebugMessage(issuer, ChatColor.RED + "Could not find fair #" + fairNum, true);
                            return;
                        }
                    }
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
                                    fairNumber.get()
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
                                                    if (pveStats.getMasterworksFairEntries()
                                                            .stream()
                                                            .anyMatch(entry -> entry.getFairNumber() == masterworksFairEntry.getFairNumber() && entry.getRarity() == rarity)
                                                    ) {
                                                        return;
                                                    }
                                                    pveStats.addMasterworksFairEntry(masterworksFairEntry);
                                                    LinkedHashMap<Currencies, Long> rewards = MasterworksFairManager.getRewards(masterworksFair,
                                                            masterworksFairEntry
                                                    );
                                                    pveStats.addReward(new MasterworksFairReward(rewards, instant, rarity));
                                                    ChatChannels.sendDebugMessage(
                                                            (CommandIssuer) null,
                                                            ChatColor.GREEN + "Validating Fair - Gave " + ChatColor.AQUA + databasePlayer.getName() +
                                                                    ChatColor.GREEN + " their " + rarity.getChatColorName() + ChatColor.GREEN + " reward",
                                                            false
                                                    );
                                                }
                                                databasePlayer.addFutureMessage(new FutureMessage(Arrays.asList(
                                                        ChatColor.GOLD + "------------------------------------------------",
                                                        ChatColor.GREEN + "Hey! We noticed you didn't get all your previous Masterworks Fair rewards, " +
                                                                "so we've given them to you!",
                                                        ChatColor.GOLD + "------------------------------------------------"
                                                ), true));
                                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
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
    public void participants(CommandIssuer issuer, @Optional Integer fairNum) {
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
                            ChatChannels.sendDebugMessage(issuer, ChatColor.RED + "Could not find fair #" + fairNum, true);
                            return;
                        }
                    }
                    ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Masterworks Fair #" + fairNumber.get() + " Participants:", false);
                    ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + " - Common", false);
                    sendFairEntry(issuer, masterworksFair.getCommonPlayerEntries());
                    ChatChannels.sendDebugMessage(issuer, ChatColor.BLUE + " - Rare", false);
                    sendFairEntry(issuer, masterworksFair.getRarePlayerEntries());
                    ChatChannels.sendDebugMessage(issuer, ChatColor.DARK_PURPLE + " - Epic", false);
                    sendFairEntry(issuer, masterworksFair.getEpicPlayerEntries());
                })
                .execute();
    }

    private void sendFairEntry(CommandIssuer issuer, List<MasterworksFairPlayerEntry> entries) {
        for (MasterworksFairPlayerEntry entry : entries) {
            ChatChannels.sendDebugMessage(issuer,
                    ChatColor.GRAY + "   - " + ChatColor.AQUA + Bukkit.getOfflinePlayer(entry.getUuid())
                            .getName() + ChatColor.GRAY + " (" + entry.getUuid() + ")",
                    false
            );
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
