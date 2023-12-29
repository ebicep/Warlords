package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class BountyUtils {

    public static final TextColor COLOR = TextColor.color(255, 140, 0);
    public static Map<String, BountyInfo> BOUNTY_COLLECTION_INFO = new HashMap<>() {{
        put(PlayersCollections.DAILY.name, new BountyInfo(Bounty.BountyGroup.DAILY_ALL.bounties, 2, 5));
        put(PlayersCollections.WEEKLY.name, new BountyInfo(Bounty.BountyGroup.WEEKLY_ALL.bounties, 2, 5));
        put(PlayersCollections.LIFETIME.name, new BountyInfo(Bounty.BountyGroup.LIFETIME_ALL.bounties, Integer.MAX_VALUE, 1));
        put("Garden of Hesperides", new BountyInfo(Bounty.BountyGroup.EVENT_GARDEN_OF_HESPERIDES_ALL.bounties, 5, 5));
    }};

    public static List<AbstractBounty> getNewBounties(String bountyInfoName) {
        BountyInfo bountyInfo = BOUNTY_COLLECTION_INFO.get(bountyInfoName);
        if (bountyInfo == null) {
//            ChatUtils.MessageType.BOUNTIES.sendMessage("Unknown bounty collection: " + playersCollections.name());
            return new ArrayList<>();
        }
        List<AbstractBounty> newBounties = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Bounty randomBounty = getRandomBounty(bountyInfoName, newBounties.stream().map(AbstractBounty::getBounty).collect(Collectors.toSet()));
            if (randomBounty == null) {
//                ChatUtils.MessageType.BOUNTIES.sendMessage("No bounties found for " + playersCollections.name());
                return newBounties;
            }
            newBounties.add(randomBounty.create.get());
        }
//        ChatUtils.MessageType.BOUNTIES.sendMessage("Gave new bounties (" + playersCollections.name() + ") - " + newBounties.stream().map(bounty -> bounty.getBounty().name()).collect(Collectors.joining(", ")));
        return newBounties;
    }

    @Nullable
    public static Bounty getRandomBounty(String bountyInfoName, Set<Bounty> excluding) {
        BountyInfo bountyInfo = BOUNTY_COLLECTION_INFO.get(bountyInfoName);
        if (bountyInfo == null) {
            ChatUtils.MessageType.BOUNTIES.sendMessage("Unknown bounty collection: " + bountyInfoName);
            return null;
        }
        List<Bounty> bountyList = Arrays.stream(bountyInfo.bounties)
                                        .filter(bounty -> !excluding.contains(bounty))
                                        .collect(Collectors.toList());
        Collections.shuffle(bountyList);
        if (bountyList.isEmpty()) {
            return null;
        }
        return bountyList.get(0);
    }

    public static void sendBountyMessage(Player player, Component component) {
        player.sendMessage(Component.textOfChildren(
                Component.text("Bounties", COLOR), // dark orange
                Component.text(" > ", NamedTextColor.DARK_GRAY),
                component
        ));
    }

    public static boolean waveDefenseMatchesDifficulty(Game game, DifficultyIndex difficulty) {
        return game.getOptions().stream().anyMatch(option -> option instanceof WaveDefenseOption waveDefenseOption && waveDefenseOption.getDifficulty() == difficulty);
    }

    public static boolean wonGame(WarlordsGameTriggerWinEvent event) {
        return event.getDeclaredWinner() == Team.BLUE;
    }

    public static <T> Optional<T> getPvEOptionFromGame(Game game, Class<T> optionClass) {
        return game.getOptions()
                   .stream()
                   .filter(option -> optionClass.isAssignableFrom(option.getClass()))
                   .map(option -> (T) option)
                   .findFirst();
    }

    public record BountyInfo(Bounty[] bounties, int maxBounties, int maxBountiesStarted) {
    }

}
