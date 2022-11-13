package com.ebicep.warlords.commands.debugcommands.misc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OldTestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        if (!player.isOp()) {
            return true;
        }

//        Quests quest = Quests.DAILY_300_KA;
//        ChatUtils.sendCenteredMessageWithEvents(player, new ComponentBuilder()
//                .appendHoverText(ChatColor.GREEN + quest.name, quest.getHoverText())
//                .create()
//        );
//
//        QuestsMenu.openQuestMenu(player);

        //      MasterworksFairManager.currentFair.setStartDate(Instant.now().minus(7, ChronoUnit.DAYS).plus(1, ChronoUnit.MINUTES));
        //System.out.println(Instant.now().minus(7, ChronoUnit.DAYS).minus(2, ChronoUnit.MINUTES));


        return true;
    }

}
