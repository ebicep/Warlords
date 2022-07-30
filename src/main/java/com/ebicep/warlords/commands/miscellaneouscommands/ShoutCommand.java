package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.ChatColor;

@CommandAlias("shout")
public class ShoutCommand extends BaseCommand {

    @Default
    @Description("Shouts a message in game")
    public void shout(WarlordsPlayer warlordsPlayer, String message) {
        String shoutMessage = warlordsPlayer.getTeam().teamColor() + "[SHOUT] " + ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.WHITE + ": " + message;
        for (WarlordsEntity p : PlayerFilter.playingGame(warlordsPlayer.getGame())) {
            p.sendMessage(shoutMessage);
        }
    }

}
