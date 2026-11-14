package com.ebicep.warlords.pve.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.DatabasePlayerFuture;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.option.pve.onslaught.PouchReward;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.rewards.RewardInventory;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

@CommandAlias("pouch|givepouch")
@CommandPermission("group.administrator")
public class PouchCommand extends BaseCommand {

    @Default
    @Subcommand("give")
    @CommandCompletion("@players synthetic|aspirant|ascendant")
    @Description("Roll a synthetic, aspirant, or ascendant pouch into a player's reward inventory")
    public CompletionStage<?> give(
            CommandIssuer issuer,
            DatabasePlayerFuture databasePlayerFuture,
            PouchReward.PouchType pouchType,
            @Default("1") @Conditions("limits:min=1,max=64") Integer amount
    ) {
        return databasePlayerFuture.future().handle((databasePlayer, throwable) -> {
            if (throwable != null) {
                Throwable cause = throwable instanceof CompletionException && throwable.getCause() != null ? throwable.getCause() : throwable;
                String message = cause.getMessage() == null ? "Could not find that player." : cause.getMessage();
                Bukkit.getScheduler().runTask(Warlords.getInstance(), () ->
                        ChatChannels.sendDebugMessage(issuer, Component.text(message, NamedTextColor.RED))
                );
                return null;
            }
            Bukkit.getScheduler().runTask(Warlords.getInstance(), () -> givePouches(issuer, databasePlayer, pouchType, amount));
            return null;
        });
    }

    private static void givePouches(CommandIssuer issuer, DatabasePlayer databasePlayer, PouchReward.PouchType pouchType, int amount) {
        List<PouchReward> given = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Pair<Spendable, Long> rolled = pouchType.getLootPool().next();
            if (rolled == null) {
                continue;
            }
            LinkedHashMap<Spendable, Long> rewards = new LinkedHashMap<>();
            rewards.put(rolled.getA(), rolled.getB());
            if (pouchType == PouchReward.PouchType.ASCENDANT) {
                PouchReward.addRolledAscendantItem(rewards);
            }
            PouchReward pouchReward = new PouchReward(rewards, pouchType);
            databasePlayer.getPveStats().getPouchRewards().add(pouchReward);
            given.add(pouchReward);
        }
        if (given.isEmpty()) {
            ChatChannels.sendDebugMessage(issuer, Component.text("Could not roll a " + pouchType.name + ".", NamedTextColor.RED));
            return;
        }
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

        Component hover = Component.empty();
        for (int i = 0; i < given.size(); i++) {
            PouchReward reward = given.get(i);
            if (i > 0) {
                hover = hover.append(Component.newline()).append(Component.newline());
            }
            hover = hover.append(Component.text(reward.getFrom(), reward.getNameColor()));
            for (Component line : reward.getLore()) {
                hover = hover.append(Component.newline()).append(line);
            }
        }

        String phrase = pouchPhrase(pouchType, given.size());
        ChatChannels.sendDebugMessage(issuer,
                Component.text("Gave ", NamedTextColor.GREEN)
                         .append(Component.text(databasePlayer.getName(), NamedTextColor.AQUA))
                         .append(Component.text(" " + phrase + " through the Rewards Inventory", NamedTextColor.GREEN))
                         .hoverEvent(HoverEvent.showText(hover))
        );
        RewardInventory.sendRewardMessage(
                databasePlayer.getUuid(),
                Component.text(Character.toUpperCase(phrase.charAt(0)) + phrase.substring(1) + (given.size() == 1 ? " was" : " were") + " added to your Reward Inventory!", NamedTextColor.GREEN)
                         .hoverEvent(HoverEvent.showText(hover))
        );
    }

    private static String pouchPhrase(PouchReward.PouchType pouchType, int amount) {
        if (amount == 1) {
            char first = Character.toLowerCase(pouchType.name.charAt(0));
            String article = first == 'a' || first == 'e' || first == 'i' || first == 'o' || first == 'u' ? "an" : "a";
            return article + " " + pouchType.name;
        }
        return amount + " " + pouchType.name.replace("Pouch", "Pouches");
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
