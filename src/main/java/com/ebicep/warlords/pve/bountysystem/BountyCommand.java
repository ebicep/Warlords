package com.ebicep.warlords.pve.bountysystem;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("bounty")
@CommandPermission("group.administrator")
public class BountyCommand extends BaseCommand {

    @Default
    public void bounty(Player player) {
        BountyMenu.openBountyMenu(player);
    }

    @Subcommand("addrandomfromgroup")
    public void addRandom(Player player, PlayersCollections collection, @Conditions("limits:min=0,max=4") Integer index, Bounty.BountyGroup bountyGroup) {
        Bounty randomBounty = bountyGroup.bounties[(int) (Math.random() * bountyGroup.bounties.length)];
        add(player, collection, index, randomBounty);
    }

    @Subcommand("set")
    public void add(Player player, PlayersCollections collection, @Conditions("limits:min=0,max=4") Integer index, Bounty bounty) {
        DatabaseManager.getPlayer(player.getUniqueId(), collection, databasePlayer -> {
            List<AbstractBounty> activeBounties = databasePlayer.getPveStats().getActiveBounties();
            activeBounties.set(index, bounty.create.get());
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

    @Subcommand("forcecomplate")
    public void forceComplete(Player player, PlayersCollections collection, Integer index) {
        DatabaseManager.getPlayer(player.getUniqueId(), collection, databasePlayer -> {
            List<AbstractBounty> activeBounties = databasePlayer.getPveStats().getActiveBounties();
            try {
                AbstractBounty abstractBounty = activeBounties.get(index);
                abstractBounty.setValue(abstractBounty.getTarget());
                ChatChannels.sendDebugMessage(
                        player,
                        Component.text("Forced completion of ", NamedTextColor.GRAY)
                                 .append(Component.text(abstractBounty.getName(), NamedTextColor.GREEN)
                                                  .hoverEvent(abstractBounty.getItem().get().asHoverEvent()))
                                 .append(Component.text(" in ", NamedTextColor.GRAY))
                                 .append(Component.text(collection.name, NamedTextColor.GREEN))
                );
            } catch (IndexOutOfBoundsException e) {
                ChatChannels.sendDebugMessage(player, Component.text("Index out of bounds"));
            }
        });
    }

    @Subcommand("forcecompleteevent")
    public void forceComplete(Player player, Integer index) {
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        if (currentGameEvent == null) {
            ChatChannels.sendDebugMessage(player, Component.text("No event is currently active"));
            return;
        }
        DatabaseManager.getPlayer(player.getUniqueId(), PlayersCollections.LIFETIME, databasePlayer -> {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            EventMode eventMode = currentGameEvent.getEvent().eventsStatsFunction.apply(pveStats.getEventStats()).get(currentGameEvent.getStartDateSecond());
            if (eventMode == null) {
                ChatChannels.sendDebugMessage(player, Component.text("No event mode detected"));
                return;
            }
            List<AbstractBounty> activeBounties = eventMode.getActiveBounties();
            try {
                AbstractBounty abstractBounty = activeBounties.get(index);
                abstractBounty.setValue(abstractBounty.getTarget());
                ChatChannels.sendDebugMessage(
                        player,
                        Component.text("Forced completion of ", NamedTextColor.GRAY)
                                 .append(Component.text(abstractBounty.getName(), NamedTextColor.GREEN)
                                                  .hoverEvent(abstractBounty.getItem().get().asHoverEvent()))
                                 .append(Component.text(" in ", NamedTextColor.GRAY))
                                 .append(Component.text(currentGameEvent.getEvent().name, NamedTextColor.GREEN))
                );
            } catch (IndexOutOfBoundsException e) {
                ChatChannels.sendDebugMessage(player, Component.text("Index out of bounds"));
            }
        });
    }

    @Subcommand("clear")
    public void clear(Player player, PlayersCollections collection) {
        DatabaseManager.getPlayer(player.getUniqueId(), collection, databasePlayer -> {
            List<AbstractBounty> activeBounties = databasePlayer.getPveStats().getActiveBounties();
            for (AbstractBounty activeBounty : activeBounties) {
                ChatChannels.sendDebugMessage(player, Component.text("Cleared " + activeBounty.getName() + " in " + collection.name));
            }
            activeBounties.clear();
        });
    }

    @Subcommand("clearevent")
    public void clear(Player player) {
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        if (currentGameEvent == null) {
            ChatChannels.sendDebugMessage(player, Component.text("No event is currently active"));
            return;
        }
        DatabaseManager.getPlayer(player.getUniqueId(), PlayersCollections.LIFETIME, databasePlayer -> {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            EventMode eventMode = currentGameEvent.getEvent().eventsStatsFunction.apply(pveStats.getEventStats()).get(currentGameEvent.getStartDateSecond());
            if (eventMode == null) {
                ChatChannels.sendDebugMessage(player, Component.text("No event mode detected"));
                return;
            }
            List<AbstractBounty> activeBounties = eventMode.getActiveBounties();
            for (AbstractBounty activeBounty : activeBounties) {
                ChatChannels.sendDebugMessage(player, Component.text("Cleared " + activeBounty.getName() + " event bounty"));
            }
            for (int i = 0; i < 5; i++) {
                activeBounties.set(i, null);
            }
        });
    }

    @Subcommand("setevent")
    public void add(Player player, @Conditions("limits:min=0,max=4") Integer index, Bounty bounty) {
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        if (currentGameEvent == null) {
            ChatChannels.sendDebugMessage(player, Component.text("No event is currently active"));
            return;
        }
        DatabaseManager.getPlayer(player.getUniqueId(), PlayersCollections.LIFETIME, databasePlayer -> {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            EventMode eventMode = currentGameEvent.getEvent().eventsStatsFunction.apply(pveStats.getEventStats()).get(currentGameEvent.getStartDateSecond());
            if (eventMode == null) {
                ChatChannels.sendDebugMessage(player, Component.text("No event mode detected"));
                return;
            }
            List<AbstractBounty> activeBounties = eventMode.getActiveBounties();
            activeBounties.set(index, bounty.create.get());
            ChatChannels.sendDebugMessage(player, Component.text("Set bounty #" + index + " to " + bounty.name() + " in event bounties"));
        });
    }

}
