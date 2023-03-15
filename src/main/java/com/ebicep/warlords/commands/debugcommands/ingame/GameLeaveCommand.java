package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

@CommandAlias("gameleave")
@CommandPermission("group.administrator")
public class GameLeaveCommand extends BaseCommand {

    @Default
    @Description("Leave your current game if an in game player")
    public void leaveGame(@Conditions("requireGame") WarlordsPlayer warlordsPlayer) {
        Game game = warlordsPlayer.getGame();
        game.removePlayer(warlordsPlayer.getUuid());
    }

}
