package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@CommandAlias("spawntestdummy")
@CommandPermission("warlords.game.spawndummy")
public class SpawnTestDummyCommand extends BaseCommand {

    @Default
    @CommandCompletion("@gameteams @boolean")
    @Syntax("<team> <takeDamage>")
    @Description("Spawns a test dummy on the specified team and true/false for whether it will take damage")
    public void spawnTestDummy(
            @Conditions("requireGame:withAddon=PRIVATE_GAME") WarlordsPlayer warlordsPlayer,
            @Values("@gameteams") Team team,
            @Values("@boolean") Boolean takeDamage
    ) {
        Game game = warlordsPlayer.getGame();
        for (Option option : game.getOptions()) {
            if (option instanceof PveOption pveOption) {
                ChatChannels.sendDebugMessage(warlordsPlayer, Component.text("Spawned PvE TestDummy", NamedTextColor.RED));
                pveOption.spawnNewMob(Mob.TEST_DUMMY.createMob(warlordsPlayer.getLocation()), team);
                return;
            }
        }
        WarlordsEntity testDummy = game.addNPC(Mob.TEST_DUMMY.createMob(warlordsPlayer.getLocation()).toNPC(game, team, warlordsNPC -> warlordsNPC.getMob().onSpawn(null)));
        testDummy.setTakeDamage(true);
        testDummy.updateHealth();
        testDummy.setRegenTickTimer(Integer.MAX_VALUE);
        testDummy.setTakeDamage(takeDamage);
        ChatChannels.sendDebugMessage(warlordsPlayer, Component.text("Spawned PvP TestDummy", NamedTextColor.RED));
    }

}
