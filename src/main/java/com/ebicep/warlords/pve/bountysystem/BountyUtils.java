package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.DatabasePlayerPvEEventStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
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
        put(PlayersCollections.DAILY.name, new BountyInfo(Bounty.BountyGroup.DAILY_ALL.bounties, 5, 5));
        put(PlayersCollections.WEEKLY.name, new BountyInfo(Bounty.BountyGroup.WEEKLY_ALL.bounties, 5, 5));
        put(PlayersCollections.LIFETIME.name, new BountyInfo(Bounty.BountyGroup.LIFETIME_ALL.bounties, Integer.MAX_VALUE, 1));
        put("Garden of Hesperides", new BountyInfo(Bounty.BountyGroup.EVENT_GARDEN_OF_HESPERIDES_ALL.bounties, 5, 5));
        put("Library Archives", new BountyInfo(Bounty.BountyGroup.EVENT_LIBRARY_ARCHIVES_ALL.bounties, 5, 5));
    }};

    public static void validateBounties(DatabasePlayer databasePlayer, String bountyInfoName, boolean isEventBounty) {
        ChatUtils.MessageType.BOUNTIES.sendMessage("Fixing bounty for " + databasePlayer.getUuid() + " - " + bountyInfoName);
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        if (isEventBounty) {
            DatabaseGameEvent gameEvent = DatabaseGameEvent.currentGameEvent;
            GameEvents event = gameEvent.getEvent();
            DatabasePlayerPvEEventStats eventStats = pveStats.getEventStats();
            EventMode eventMode = event.eventsStatsFunction.apply(eventStats).get(gameEvent.getStartDateSecond());
            validateBounties(bountyInfoName, eventMode.getActiveEventBounties(), eventMode.getCompletedBounties(), eventMode.getBountiesCompleted());
        } else {
            validateBounties(bountyInfoName, pveStats.getActiveBounties(), pveStats.getCompletedBounties(), pveStats.getBountiesCompleted());
        }
    }

    private static void validateBounties(String bountyInfoName, List<AbstractBounty> activeBounties, Map<Bounty, Long> completedBounties, int bountiesCompleted) {
        int maxBounties = BOUNTY_COLLECTION_INFO.get(bountyInfoName).maxBounties() + 5;
        Set<Bounty> excludeBounties = new HashSet<>(completedBounties.keySet());
        excludeBounties.addAll(activeBounties.stream().filter(Objects::nonNull).map(AbstractBounty::getBounty).collect(Collectors.toSet()));
        for (int i = 0; i < maxBounties - bountiesCompleted; i++) {
            Bounty randomBounty = getRandomBounty(bountyInfoName, excludeBounties);
            if (randomBounty == null) {
                break;
            }
            for (int i1 = 0; i1 < activeBounties.size(); i1++) {
                AbstractBounty bounty = activeBounties.get(i1);
                if (bounty == null) {
                    ChatUtils.MessageType.BOUNTIES.sendMessage("Replaced bounty " + i1 + " with " + randomBounty);
                    activeBounties.set(i1, randomBounty.create.get());
                    break;
                }
            }
            excludeBounties.add(randomBounty);
        }
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

    public static boolean lostGame(WarlordsGameTriggerWinEvent event) {
        return event.getDeclaredWinner() != Team.BLUE;
    }

    public static <T> Optional<T> getOptionFromGame(Game game, Class<T> optionClass) {
        return game.getOptions()
                   .stream()
                   .filter(option -> optionClass.isAssignableFrom(option.getClass()))
                   .map(option -> (T) option)
                   .findFirst();
    }

    // maxBounties = # of new bounties that you can get, 2 = total of 7, since u start with 5
    public record BountyInfo(Bounty[] bounties, int maxBounties, int maxBountiesStarted) {
    }

}
