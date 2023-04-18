package com.ebicep.warlords.game.option.pve.onslaught;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import org.bukkit.entity.Player;

@CommandAlias("onslaught")
@CommandPermission("group.administrator")
public class OnslaughtCommand extends BaseCommand {

    @Subcommand("setintegrity|setsoulenergy")
    @Description("Set the integrity counter")
    public void setIntegrity(@Conditions("requireGame:gamemode=ONSLAUGHT") Player player, @Conditions("limits:min=1,max=100") Integer amount) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        OnslaughtOption onslaughtOption = (OnslaughtOption) game.getOptions().stream()
                                                                .filter(option -> option instanceof OnslaughtOption)
                                                                .findFirst()
                                                                .get();
        onslaughtOption.setIntegrityCounter(amount);
    }
}
