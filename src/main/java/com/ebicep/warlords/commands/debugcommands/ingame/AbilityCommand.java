package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.bosses.Enavuris;
import org.bukkit.entity.Player;

@CommandAlias("ability")
@CommandPermission("group.administrator")
public class AbilityCommand extends BaseCommand {

    @Subcommand("forceactivate")
    @CommandCompletion("@warlordsplayers")
    @Description("Makes player activate their ability")
    public void respawn(CommandIssuer issuer, @Conditions("limits:min=0,max=4") Integer ability, @Optional WarlordsPlayer target) {
        if (target.getEntity() instanceof Player) {
            target.getSpec().onRightClick(target, (Player) target.getEntity(), ability, true);
        }
    }

    @Subcommand("test")
    public void test(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getSpec().getAbilities().clear();
        warlordsPlayer.getSpec().getAbilities().add(new Enavuris.EnderStones());
        warlordsPlayer.getSpec().getAbilities().add(new Enavuris.Imprisonment());
        warlordsPlayer.updateInventory(false);
    }

    @Subcommand("useall")
    public void useAll(WarlordsPlayer warlordsPlayer) {
        for (Ability value : Ability.VALUES) {
            AbstractAbility ability = value.create.get();
            ability.onActivate(warlordsPlayer);
        }
    }

}
