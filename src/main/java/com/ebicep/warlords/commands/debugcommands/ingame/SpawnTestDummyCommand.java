package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsEntityFlag;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

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
            @Values("@boolean") Boolean takeDamage,
            @Default("1") @Conditions("limits:min=1,max=5") Integer amount
    ) {
        Game game = warlordsPlayer.getGame();
        Location location = warlordsPlayer.getLocation();
        game.getOption(PveOption.class)
            .stream()
            .findFirst()
            .ifPresentOrElse(
                    pveOption -> {
                        ChatChannels.sendDebugMessage(warlordsPlayer, Component.text("Spawned PvE TestDummy (" + amount + ")", NamedTextColor.RED));
                        for (int i = 0; i < amount; i++) {
                            pveOption.spawnNewMob(Mob.TEST_DUMMY.createMob(location.clone().add(
                                            ThreadLocalRandom.current().nextDouble(5) - 2.5,
                                            0,
                                            ThreadLocalRandom.current().nextDouble(5) - 2.5
                                    )), team
                            );
                        }
                    },
                    () -> {
                        ChatChannels.sendDebugMessage(warlordsPlayer, Component.text("Spawned PvP TestDummy (" + amount + ")", NamedTextColor.RED));
                        for (int i = 0; i < amount; i++) {
                            WarlordsEntity testDummy = game.addNPC(Mob.TEST_DUMMY.createMob(location.clone().add(
                                    amount == 1 ? 0 : ThreadLocalRandom.current().nextDouble(5) - 2.5,
                                    0,
                                    amount == 1 ? 0 : ThreadLocalRandom.current().nextDouble(5) - 2.5
                            )).toNPC(game, team, warlordsNPC -> warlordsNPC.getMob().onSpawn(null)));
                            testDummy.getEnergy().setBaseValue(200);
                            testDummy.setTakeDamage(true);
                            testDummy.updateHealth();
                            testDummy.setRegenTickTimer(Integer.MAX_VALUE);
                            testDummy.setTakeDamage(takeDamage);
                            testDummy.setName("TestDummy" + UUID.randomUUID().toString().substring(0, 5));
                            testDummy.setCurrentEnergy(0);
                            testDummy.setFlag(WarlordsEntityFlag.GAIN_ENERGY, false);
                        }
                    }
            );
    }


}
