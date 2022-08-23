package com.ebicep.warlords.game.option.wavedefense.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.AbstractMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.Mobs;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@CommandAlias("mob")
public class MobCommand extends BaseCommand {

    private static final Set<AbstractMob<?>> SPAWNED_MOBS = new HashSet<>();

    @Subcommand("spawn")
    @Description("Spawns mobs, amount is how many")
    @CommandCompletion("@pvemobs")
    public void spawn(@Conditions("requireGame:gamemode=WAVE_DEFENSE") WarlordsPlayer warlordsPlayer, Mobs mobType, @Default("1") @Conditions("limits:min=0,max=10") Integer amount) {
        SPAWNED_MOBS.clear();
        for (Option option : warlordsPlayer.getGame().getOptions()) {
            if (option instanceof WaveDefenseOption) {
                WaveDefenseOption waveDefenseOption = (WaveDefenseOption) option;
                for (int i = 0; i < amount; i++) {
                    AbstractMob<?> mob = mobType.createMob.apply(warlordsPlayer.getLocation());
                    waveDefenseOption.spawnNewMob(mob);
                    SPAWNED_MOBS.add(mob);
                }
                ChatChannels.sendDebugMessage(warlordsPlayer, ChatColor.GREEN + "Spawned " + amount + " Mobs", true);
                return;
            }
        }
    }


    @Subcommand("speed")
    public void giveSpeed(@Conditions("requireGame:gamemode=WAVE_DEFENSE") WarlordsPlayer warlordsPlayer, Integer speed) {
        for (AbstractMob<?> spawnedMob : SPAWNED_MOBS) {
            spawnedMob.getWarlordsNPC().getSpeed().reset();
            spawnedMob.getWarlordsNPC().getSpeed().addSpeedModifier("Test", speed, 30 * 20, "BASE");
        }
        ChatChannels.sendDebugMessage(warlordsPlayer, ChatColor.GREEN + "Set Speed: " + ChatColor.YELLOW + speed + ChatColor.GREEN + " for " + SPAWNED_MOBS.size() + " mobs", true);
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
