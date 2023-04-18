package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.entity.Player;

@CommandAlias("forceactivate")
@CommandPermission("group.administrator")
public class ForceActivateCommand extends BaseCommand {

    @Default
    @CommandCompletion("@warlordsplayers")
    @Description("Makes player activate their ability")
    public void respawn(CommandIssuer issuer, @Conditions("limits:min=0,max=4") Integer ability, @Optional WarlordsPlayer target) {
        if (target.getEntity() instanceof Player) {
            target.getSpec().onRightClick(target, (Player) target.getEntity(), ability, true);
        }
    }

}
