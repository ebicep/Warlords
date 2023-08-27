package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.pve.Currencies;
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
    public static final LinkedHashMap<Currencies, Long> COST = new LinkedHashMap<>() {{
        put(Currencies.COIN, 5000L);
    }};
    public static Map<PlayersCollections, BountyInfo> BOUNTY_COLLECTION_INFO = new HashMap<>() {{
        put(PlayersCollections.DAILY, new BountyInfo(Bounty.BountyGroup.DAILY_ALL.bounties, 2));
        put(PlayersCollections.WEEKLY, new BountyInfo(Bounty.BountyGroup.WEEKLY_ALL.bounties, 2));
        put(PlayersCollections.LIFETIME, new BountyInfo(Bounty.BountyGroup.LIFETIME_ALL.bounties, Integer.MAX_VALUE));
    }};

    public static void giveNewBounties(DatabasePlayerPvE databasePlayerPvE, PlayersCollections playersCollections) {
        BountyInfo bountyInfo = BOUNTY_COLLECTION_INFO.get(playersCollections);
        if (bountyInfo == null) {
//            ChatUtils.MessageType.BOUNTIES.sendMessage("Unknown bounty collection: " + playersCollections.name());
            return;
        }
        List<AbstractBounty> activeBounties = databasePlayerPvE.getActiveBounties();
        activeBounties.clear(); // precaution
        for (int i = 0; i < 5; i++) {
            Bounty randomBounty = getRandomBounty(playersCollections, activeBounties.stream().map(AbstractBounty::getBounty).collect(Collectors.toList()));
            if (randomBounty == null) {
//                ChatUtils.MessageType.BOUNTIES.sendMessage("No bounties found for " + playersCollections.name());
                return;
            }
            activeBounties.add(randomBounty.create.get());
        }
    }

    @Nullable
    public static Bounty getRandomBounty(PlayersCollections collection, List<Bounty> excluding) {
        BountyInfo bountyInfo = BOUNTY_COLLECTION_INFO.get(collection);
        if (bountyInfo == null) {
            ChatUtils.MessageType.BOUNTIES.sendMessage("Unknown bounty collection: " + collection.name());
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

    public static <T> Optional<T> getPvEOptionFromGame(Game game, Class<T> optionClass) {
        return game.getOptions()
                   .stream()
                   .filter(option -> optionClass.isAssignableFrom(option.getClass()))
                   .map(option -> (T) option)
                   .findFirst();
    }

    public record BountyInfo(Bounty[] bounties, int maxBounties) {
    }

}
