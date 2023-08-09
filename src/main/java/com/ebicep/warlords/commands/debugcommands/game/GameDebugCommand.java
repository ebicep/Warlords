package com.ebicep.warlords.commands.debugcommands.game;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.state.PreLobbyState;
import org.bukkit.entity.Player;

@CommandAlias("gamedebug|gd")
@CommandPermission("group.administrator")
public class GameDebugCommand extends BaseCommand {

    @Default
    @Description("Auto creates debugparty and starts game in sandbox with players with timer skipped")
    public void gameDebug(@Conditions("outsideGame") Player player) {
        player.performCommand("p debugcreate");
        GameStartCommand.startGame(player, false, queueEntryBuilder -> {
            queueEntryBuilder.setRequestedGameAddons(GameAddon.PRIVATE_GAME);
            queueEntryBuilder.setGameMode(GameMode.DEBUG);
            queueEntryBuilder.setMap(GameMap.DEBUG);
            queueEntryBuilder.setOnResult((queueResult, game) -> game.getState(PreLobbyState.class).ifPresent(PreLobbyState::skipTimer));
        });
    }

    @CommandAlias("gamedebug2|gd2")
    @Description("Auto starts game in wave defense with mobs not spawning")
    public void gameDebug2(@Conditions("outsideGame") Player player) {
        GameStartCommand.startGame(player, false, queueEntryBuilder -> {
            queueEntryBuilder.setRequestedGameAddons(GameAddon.PRIVATE_GAME);
            queueEntryBuilder.setGameMode(GameMode.WAVE_DEFENSE);
            queueEntryBuilder.setMap(GameMap.ILLUSION_RIFT);
            queueEntryBuilder.setOnResult((queueResult, game) -> {
                game.getState(PreLobbyState.class).ifPresent(PreLobbyState::skipTimer);
                for (Option option : game.getOptions()) {
                    if (option instanceof PveOption pveOption) {
                        pveOption.setPauseMobSpawn(true);
                    }
                }
            });
        });
    }

}
