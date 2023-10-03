package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.items.pojos.WeeklyBlessings;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.menu.ItemMichaelMenu;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.*;

@CommandAlias("patch")
@CommandPermission("group.administrator")
public class PatchCommand extends BaseCommand {

    private static Map<UUID, LinkedHashMap<Spendable, Long>> cachedPlayerBlessPatch = new HashMap<>();

    @Subcommand("blessingscheck")
    @Description("Found/Bought blessing compensation")
    public void blessingCheck(CommandIssuer issuer) {
        if (issuer.isPlayer()) {
            return;
        }
        ChatChannels.sendDebugMessage(issuer, Component.text("Compensating for blessings", NamedTextColor.GREEN));
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.weeklyBlessingsService.findAll())
                .syncLast(weeklyBlessings -> {
                    ChatChannels.sendDebugMessage(issuer, Component.text("Found: " + weeklyBlessings.size() + " blessing entries", NamedTextColor.GREEN));
                    Map<UUID, Map<Integer, Integer>> playerBoughtBlessings = new HashMap<>();
                    for (WeeklyBlessings weeklyBlessing : weeklyBlessings) {
                        weeklyBlessing.getPlayerOrders().forEach((uuid, order) -> {
                            playerBoughtBlessings.merge(uuid, order, (oldValue, newValue) -> {
                                for (Map.Entry<Integer, Integer> entry : newValue.entrySet()) {
                                    oldValue.merge(entry.getKey(), entry.getValue(), Integer::sum);
                                }
                                return oldValue;
                            });
                        });
                    }
                    ChatChannels.sendDebugMessage(issuer, Component.text("Found: " + playerBoughtBlessings.size() + " players with bought blessings", NamedTextColor.GREEN));
                    Map<UUID, LinkedHashMap<Spendable, Long>> playerRewards = new HashMap<>();
                    playerBoughtBlessings.forEach((uuid, integerIntegerMap) -> {
                        StringBuilder blessings = new StringBuilder();
                        for (int i = 1; i <= 5; i++) {
                            blessings.append(i).append(":").append(integerIntegerMap.getOrDefault(i, 0)).append(", ");
                        }
                        blessings.setLength(blessings.length() - 2);
                        ChatChannels.sendDebugMessage(issuer, Component.text(uuid + " : " + blessings, NamedTextColor.GREEN));
                        LinkedHashMap<Spendable, Long> rewards = new LinkedHashMap<>();
                        integerIntegerMap.forEach((tier, amount) -> {
                            LinkedHashMap<Spendable, Long> cost = ItemMichaelMenu.BuyABlessingMenu.COSTS.get(tier);
                            cost.forEach((spendable, aLong) -> rewards.merge(spendable, aLong * amount, Long::sum));
                        });
                        rewards.forEach((spendable, aLong) -> {
                            ChatChannels.sendDebugMessage(issuer, Component.text("    ").append(spendable.getCostColoredName(aLong)));
                        });
                        playerRewards.put(uuid, rewards);
                    });
                    cachedPlayerBlessPatch = playerRewards;
                })
                .execute();
    }

    @Subcommand("blessingsconfirm")
    @Description("Found/Bought blessing compensation")
    public void blessingConfirm(CommandIssuer issuer) {
        if (issuer.isPlayer()) {
            return;
        }
//        cachedPlayerBlessPatch.forEach((uuid, rewards) -> {
//            Warlords.newChain()
//                    .async(() -> {
//                        DatabaseManager.getPlayer(uuid, databasePlayer -> {
//                            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
//                            List<CompensationReward> compensationRewards = pveStats.getCompensationRewards();
//                            if (compensationRewards.stream().noneMatch(compensationReward -> compensationReward instanceof CompensationReward.BlessingPatch)) {
//                                ItemsManager itemsManager = pveStats.getItemsManager();
//                                rewards.merge(Currencies.LEGEND_FRAGMENTS, itemsManager.getBlessingsFound() * 25L, Long::sum);
//                                compensationRewards.add(new CompensationReward.BlessingPatch(rewards));
//                            }
//                        }, () -> {
//                            ChatChannels.sendDebugMessage(issuer, Component.text("Player not found: " + uuid, NamedTextColor.RED));
//                        });
//                    })
//                    .execute();
//        });
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
