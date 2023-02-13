package com.ebicep.warlords.game.option.wavedefense.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mobs;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.minecraft.world.entity.Mob;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@CommandAlias("mob")
@CommandPermission("group.administrator")
public class MobCommand extends BaseCommand {

    public static final Set<AbstractMob<?>> SPAWNED_MOBS = new HashSet<>();

    @Subcommand("spawn")
    @Description("Spawns mobs, amount is how many")
    @CommandCompletion("@pvemobs")
    public void spawn(
            @Conditions("requireGame:gamemode=WAVE_DEFENSE/EVENT_WAVE_DEFENSE") Player player,
            Mobs mobType,
            @Default("1") @Conditions("limits:min=0,max=25") Integer amount
    ) {
        SPAWNED_MOBS.clear();
        for (Option option : Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get().getOptions()) {
            if (option instanceof PveOption pveOption) {
                for (int i = 0; i < amount; i++) {
                    AbstractMob<?> mob = mobType.createMob.apply(player.getLocation());
                    pveOption.spawnNewMob(mob);
                    SPAWNED_MOBS.add(mob);
                }
                ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Spawned " + amount + " Mobs", true);
                return;
            }
        }
    }


    @Subcommand("speed")
    public void giveSpeed(@Conditions("requireGame:gamemode=WAVE_DEFENSE/EVENT_WAVE_DEFENSE") Player player, Integer speed) {
        for (AbstractMob<?> spawnedMob : SPAWNED_MOBS) {
            spawnedMob.getWarlordsNPC().addSpeedModifier(null, "Test", speed, 30 * 20, "BASE");
        }
        ChatChannels.sendDebugMessage(player,
                ChatColor.GREEN + "Set Speed: " + ChatColor.YELLOW + speed + ChatColor.GREEN + " for " + SPAWNED_MOBS.size() + " mobs",
                true
        );
    }

    @Subcommand("target")
    public void target(@Conditions("requireGame:gamemode=WAVE_DEFENSE/EVENT_WAVE_DEFENSE") Player player, WarlordsPlayer target) {
        for (AbstractMob<?> spawnedMob : SPAWNED_MOBS) {
            spawnedMob.setTarget(target);
        }
        ChatChannels.sendDebugMessage(player,
                ChatColor.GREEN + "Set Target: " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + " for " + SPAWNED_MOBS.size() + " mobs",
                true
        );
    }

    @Subcommand("alltarget")
    public void allTarget(@Conditions("requireGame:gamemode=WAVE_DEFENSE/EVENT_WAVE_DEFENSE") Player player, WarlordsPlayer target) {
        for (Option option : Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get().getOptions()) {
            if (option instanceof PveOption) {
                ((PveOption) option).getMobs().forEach(abstractMob -> abstractMob.setTarget(target));
                ChatChannels.sendDebugMessage(player,
                        ChatColor.GREEN + "Set All Mob Target: " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + " for " + SPAWNED_MOBS.size() + " mobs",
                        true
                );
                return;
            }
        }
    }

    @Subcommand("getmoblocations")
    @CommandCompletion("@gameids")
    public void getMobLocations(CommandIssuer issuer, @Conditions("filter:gamemode=WAVE_DEFENSE/EVENT_WAVE_DEFENSE") Game game) {
        //Ghoul Caller - @MainLobby | 10,-3,3
        for (Option option : game.getOptions()) {
            if (option instanceof PveOption) {
                String message = ((PveOption) option)
                        .getMobs()
                        .stream()
                        .map(abstractMob -> {
                            Mob mob = abstractMob.getMob();
                            return abstractMob.getWarlordsNPC().getColoredName() +
                                    ChatColor.GREEN + " @" + mob.getLevel().getWorld().getName() +
                                    ChatColor.GRAY + " | " + ChatColor.GREEN + mob.getX() + ChatColor.GRAY + "," + ChatColor.DARK_GREEN + mob.getY() + ChatColor.GRAY + "," + ChatColor.GREEN + mob.getZ();
                        })
                        .collect(Collectors.joining("\n"));
                ChatChannels.sendDebugMessage(issuer, message, true);
                return;
            }
        }
    }

    @Subcommand("noai")
    public void noAi(@Conditions("requireGame:gamemode=WAVE_DEFENSE/EVENT_WAVE_DEFENSE") Player player, Boolean ai) {
        for (Option option : Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get().getOptions()) {
            if (option instanceof PveOption pveOption) {
                pveOption.getMobs()
                         .stream()
                         .map(abstractMob -> abstractMob.getEntity().get())
                         .forEach(entityInsentient -> entityInsentient.setNoAi(ai));
                ChatChannels.sendDebugMessage(player,
                        ChatColor.GREEN + "Set All Mob NoAI to " + ChatColor.YELLOW + ai + ChatColor.GREEN + " for " + SPAWNED_MOBS.size() + " mobs",
                        true
                );
                return;
            }
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
