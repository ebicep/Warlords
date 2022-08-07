package com.ebicep.warlords.game.option.wavedefense.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.commands.miscellaneouscommands.ChatCommand;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.berserkzombie.AbstractBerserkZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.berserkzombie.BasicBerserkZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.berserkzombie.EliteBerserkZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.berserkzombie.EnvoyBerserkZombie;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.UUID;

@CommandAlias("spawnmob")
public class SpawnMobCommand extends BaseCommand {

    private AbstractBerserkZombie berserkZombie;

    @Subcommand("test")
    public void spawn(@Conditions("requireGame:gamemode=WAVE_DEFENSE") WarlordsPlayer warlordsPlayer) {
        for (Option option : warlordsPlayer.getGame().getOptions()) {
            if (option instanceof WaveDefenseOption) {
                WaveDefenseOption waveDefenseOption = (WaveDefenseOption) option;
                berserkZombie = new BasicBerserkZombie(warlordsPlayer.getLocation());
                berserkZombie.toNPC(warlordsPlayer.getGame(), Team.RED, UUID.randomUUID());
                waveDefenseOption.getMobs().add(berserkZombie);
                waveDefenseOption.setSpawnCount(waveDefenseOption.getSpawnCount() + 1);
                ChatCommand.sendDebugMessage(warlordsPlayer, "Spawned Mob", true);
            }
        }
    }

    @Subcommand("test2")
    public void spawn2(@Conditions("requireGame:gamemode=WAVE_DEFENSE") WarlordsPlayer warlordsPlayer) {
        for (Option option : warlordsPlayer.getGame().getOptions()) {
            if (option instanceof WaveDefenseOption) {
                WaveDefenseOption waveDefenseOption = (WaveDefenseOption) option;
                berserkZombie = new EliteBerserkZombie(warlordsPlayer.getLocation());
                berserkZombie.toNPC(warlordsPlayer.getGame(), Team.RED, UUID.randomUUID());
                waveDefenseOption.getMobs().add(berserkZombie);
                waveDefenseOption.setSpawnCount(waveDefenseOption.getSpawnCount() + 1);
                ChatCommand.sendDebugMessage(warlordsPlayer, "Spawned Mob", true);
            }
        }
    }

    @Subcommand("test3")
    public void spawn3(@Conditions("requireGame:gamemode=WAVE_DEFENSE") WarlordsPlayer warlordsPlayer) {
        for (Option option : warlordsPlayer.getGame().getOptions()) {
            if (option instanceof WaveDefenseOption) {
                WaveDefenseOption waveDefenseOption = (WaveDefenseOption) option;
                berserkZombie = new EnvoyBerserkZombie(warlordsPlayer.getLocation());
                berserkZombie.toNPC(warlordsPlayer.getGame(), Team.RED, UUID.randomUUID());
                waveDefenseOption.getMobs().add(berserkZombie);
                waveDefenseOption.setSpawnCount(waveDefenseOption.getSpawnCount() + 1);
                ChatCommand.sendDebugMessage(warlordsPlayer, "Spawned Mob", true);
            }
        }
    }

    @Subcommand("speed")
    public void giveSpeed(@Conditions("requireGame:gamemode=WAVE_DEFENSE") WarlordsPlayer warlordsPlayer, Integer speed) {
        berserkZombie.getWarlordsNPC().getSpeed().reset();
        berserkZombie.getWarlordsNPC().getSpeed().addSpeedModifier("Test", speed, 30 * 20, "BASE");
        ChatCommand.sendDebugMessage(warlordsPlayer, "Set Speed: " + speed, true);
    }

}
