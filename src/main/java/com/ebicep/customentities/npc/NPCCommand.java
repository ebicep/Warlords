package com.ebicep.customentities.npc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("npcwl")
@CommandPermission("group.administrator")
public class NPCCommand extends BaseCommand {

    @Subcommand("createdbnpcs")
    public void createDBNPCs(CommandIssuer commandIssuer) {
        NPCManager.createDatabaseRequiredNPCs();
    }

}
