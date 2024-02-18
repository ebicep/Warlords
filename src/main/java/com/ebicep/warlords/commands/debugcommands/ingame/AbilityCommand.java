package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.bosses.MagmaticOoze;
import org.bukkit.entity.Player;

import java.util.HashMap;

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
        int splitNumber = 0;
        warlordsPlayer.getSpec().getAbilities().add(new MagmaticOoze.FieryProjectile(600 - (splitNumber * 10), 700 - (splitNumber * 10)));
        warlordsPlayer.getSpec().getAbilities().add(new MagmaticOoze.FlamingSlam(1000 - (splitNumber * 100), 1500 - (splitNumber * 100)));
        warlordsPlayer.getSpec().getAbilities().add(new MagmaticOoze.HeatAura(100 - (splitNumber * 10), 10 - splitNumber));
        warlordsPlayer.getSpec().getAbilities().add(new MagmaticOoze.MoltenFissure(new HashMap<>()));
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
