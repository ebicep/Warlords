package com.ebicep.warlords.commands2.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import org.bukkit.entity.Player;

import static com.ebicep.warlords.menu.generalmenu.WarlordsShopMenu.openMainMenu;

@CommandAlias("menu")
public class MenuCommand extends BaseCommand {

    @Default
    @Description("Opens the main menu")
    public void menu(@Conditions("outsideGame") Player player) {
        openMainMenu(player);
    }

}
