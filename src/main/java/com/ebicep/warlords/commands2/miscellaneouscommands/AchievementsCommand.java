package com.ebicep.warlords.commands2.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.ebicep.warlords.achievements.AchievementsMenu;
import org.bukkit.entity.Player;

@CommandAlias("achievements")
public class AchievementsCommand extends BaseCommand {

    @Default
    public void achievements(Player player) {
        AchievementsMenu.openAchievementsMenu(player);
    }
}
