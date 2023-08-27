package com.ebicep.warlords.pve.bountysystem;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@CommandAlias("bounty")
@CommandPermission("group.administrator")
public class BountyCommand extends BaseCommand {

    @Default
    public void bounty(Player player) {
        BountyMenu.openBountyMenu(player);
    }

    @Subcommand("addrandomfromgroup")
    public void addRandom(Player player, PlayersCollections collection, Bounty.BountyGroup bountyGroup) {
        Bounty randomBounty = bountyGroup.bounties[(int) (Math.random() * bountyGroup.bounties.length)];
        add(player, collection, randomBounty);
    }

    @Subcommand("add")
    public void add(Player player, PlayersCollections collection, Bounty bounty) {
        DatabaseManager.getPlayer(player.getUniqueId(), collection, databasePlayer -> {
            List<AbstractBounty> activeBounties = databasePlayer.getPveStats().getActiveBounties();
            activeBounties.add(bounty.create.get());
            ChatChannels.sendDebugMessage(player, Component.text("Added " + bounty.name() + " to " + collection.name));
        });
    }

    @Subcommand("printall")
    public void printAll(CommandIssuer issuer, @Optional Bounty.BountyGroup bountyGroup) {
        Bounty[] bounties = bountyGroup == null ? Bounty.VALUES : bountyGroup.bounties;
        for (Bounty bounty : bounties) {
            AbstractBounty createdBounty = bounty.create.get();
            ChatChannels.sendDebugMessage(issuer, Component.text(createdBounty.getName())
                                                           .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                                                           .append(Component.text(createdBounty.getDescription(), NamedTextColor.GRAY)));
        }
    }

    @Subcommand("printtrackoutsidegame")
    public void printTrackOutsideGame(CommandIssuer issuer) {
        for (Map.Entry<UUID, Set<TracksOutsideGame>> uuidSetEntry : TracksOutsideGame.CACHED_ONLINE_PLAYER_TRACKERS.entrySet()) {
            ChatChannels.sendDebugMessage(issuer, Component.text(uuidSetEntry.getKey() + ":"));
            for (TracksOutsideGame tracksOutsideGame : uuidSetEntry.getValue()) {
                ChatChannels.sendDebugMessage(issuer, Component.text("   - " + tracksOutsideGame.toString()));
            }
        }
    }


}
