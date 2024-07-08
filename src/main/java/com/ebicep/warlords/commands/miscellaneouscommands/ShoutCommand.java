package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@CommandAlias("shout")
public class ShoutCommand extends BaseCommand {

    @Default
    @Description("Shouts a message in game")
    public void shout(WarlordsPlayer warlordsPlayer, String message) {
        Component shoutMessage = Component.empty()
                                          .append(Component.text("[SHOUT] ", warlordsPlayer.getTeam().getTeamColor()))
                                          .append(Component.text(warlordsPlayer.getName(), NamedTextColor.AQUA))
                                          .append(Component.text(": " + message, NamedTextColor.WHITE));
        for (WarlordsEntity p : PlayerFilter.playingGame(warlordsPlayer.getGame())) {
            p.sendMessage(shoutMessage);
        }
    }

}
