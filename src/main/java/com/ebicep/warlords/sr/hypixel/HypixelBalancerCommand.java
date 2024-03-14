package com.ebicep.warlords.sr.hypixel;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;

@CommandAlias("hb|hypixelbalance")
public class HypixelBalancerCommand extends BaseCommand {

    @Default
    public void menu(Player player) {
        HypixelBalancerMenu.openMenu(player);
    }


}
