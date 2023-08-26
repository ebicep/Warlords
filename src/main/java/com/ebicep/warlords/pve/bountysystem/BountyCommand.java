package com.ebicep.warlords.pve.bountysystem;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("bounty")
@CommandPermission("group.administrator")
public class BountyCommand extends BaseCommand {

    @Default
    public void bounty(Player player) {
        BountyMenu.openBountyMenu(player);
    }

    @Subcommand("add")
    public void add(Player player, PlayersCollections collection, Bounties bounty) {
        DatabaseManager.getPlayer(player.getUniqueId(), collection, databasePlayer -> {
            List<AbstractBounty> activeBounties = databasePlayer.getPveStats().getActiveBounties();
            activeBounties.add(bounty.create.get());
            ChatChannels.sendDebugMessage(player, Component.text("Added " + bounty.name() + " to " + collection.name));
        });
    }


}
