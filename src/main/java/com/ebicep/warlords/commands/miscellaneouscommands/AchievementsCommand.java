package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;

@CommandAlias("achievements|achievement")
public class AchievementsCommand extends BaseCommand {

    @Default
    public void achievements(Player player) {
//        AchievementsMenu.openAchievementsMenu(player);
    }
}
