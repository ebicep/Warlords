package com.ebicep.warlords.commands2.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("shout")
public class ShoutCommand extends BaseCommand {

    @Default
    @Description("Shouts a message in game")
    public void shout(@Conditions("requireWarlordsPlayer") Player player, String message) {
        WarlordsEntity warlordsPlayer = Warlords.getPlayer(player);
        String shoutMessage = warlordsPlayer.getTeam().teamColor() + "[SHOUT] " + ChatColor.AQUA + player.getName() + ChatColor.WHITE + ": " + message;
        for (WarlordsEntity p : PlayerFilter.playingGame(warlordsPlayer.getGame())) {
            p.sendMessage(shoutMessage);
        }
    }

}
