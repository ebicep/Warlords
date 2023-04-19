package com.ebicep.warlords.pve.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.pve.mobs.Mobs;
import com.ebicep.warlords.pve.mobs.events.spidersburrow.EventEggSac;
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
            @Conditions("requireGame:gamemode=PVE") Player player,
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
                ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Spawned " + amount + " Mobs");
                return;
            }
        }
    }

    @Subcommand("spawntest")
    public void spawnTest(
            @Conditions("requireGame:gamemode=PVE") Player player,
            Mobs mobType
    ) {
        SPAWNED_MOBS.clear();
        for (Option option : Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get().getOptions()) {
            if (option instanceof PveOption pveOption) {
                AbstractMob<?> mob = mobType.createMob.apply(player.getLocation());
                pveOption.spawnNewMob(mob, Team.BLUE);
                SPAWNED_MOBS.add(mob);
                ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Spawned Test Mob - " + mob.getWarlordsNPC().getUuid());
                return;
            }
        }
    }

    @Subcommand("togglespawning")
    public void toggleSpawning(@Conditions("requireGame:gamemode=PVE") Player player) {
        for (Option option : Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get().getOptions()) {
            if (option instanceof PveOption pveOption) {
                pveOption.setPauseMobSpawn(!pveOption.isPauseMobSpawn());
                ChatChannels.sendDebugMessage(player, ChatColor.GREEN + (pveOption.isPauseMobSpawn() ? "Disabled" : "Enabled") + " mob spawning");
                return;
            }
        }
    }

    @Subcommand("togglearmorstandeggsac")
    public void toggleArmorStandEggSac(CommandIssuer issuer) {
        EventEggSac.ARMOR_STAND = !EventEggSac.ARMOR_STAND;
        if (EventEggSac.ARMOR_STAND) {
            ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Enabled armor stand egg sac");
        } else {
            ChatChannels.sendDebugMessage(issuer, ChatColor.RED + "Disabled armor stand egg sac");
        }
    }

    @Subcommand("speed")
    public void giveSpeed(@Conditions("requireGame:gamemode=PVE") Player player, Integer speed) {
        for (AbstractMob<?> spawnedMob : SPAWNED_MOBS) {
            spawnedMob.getWarlordsNPC().addSpeedModifier(null, "Test", speed, 30 * 20, "BASE");
        }
        ChatChannels.sendDebugMessage(player,
                ChatColor.GREEN + "Set Speed: " + ChatColor.YELLOW + speed + ChatColor.GREEN + " for " + SPAWNED_MOBS.size() + " mobs"
        );
    }

    @Subcommand("target")
    @CommandCompletion("@warlordsplayers")
    public void target(@Conditions("requireGame:gamemode=PVE") Player player, WarlordsPlayer target) {
        for (AbstractMob<?> spawnedMob : SPAWNED_MOBS) {
            spawnedMob.setTarget(target);
        }
        ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Set Target: " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + " for " + SPAWNED_MOBS.size() + " mobs");
    }

    @Subcommand("targetnpc")
    @CommandCompletion("@warlordsnpcs")
    public void target(@Conditions("requireGame:gamemode=PVE") Player player, WarlordsNPC target) {
        for (AbstractMob<?> spawnedMob : SPAWNED_MOBS) {
            spawnedMob.setTarget(target);
        }
        ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Set Target: " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + " for " + SPAWNED_MOBS.size() + " mobs");
    }

    @Subcommand("alltarget")
    public void allTarget(@Conditions("requireGame:gamemode=PVE") Player player, WarlordsPlayer target) {
        for (Option option : Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get().getOptions()) {
            if (option instanceof PveOption) {
                ((PveOption) option).getMobs().forEach(abstractMob -> abstractMob.setTarget(target));
                ChatChannels.sendDebugMessage(player,
                        ChatColor.GREEN + "Set All Mob Target: " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + " for " + SPAWNED_MOBS.size() + " mobs"
                );
                return;
            }
        }
    }

    @Subcommand("getmoblocations")
    @CommandCompletion("@gameids")
    public void getMobLocations(CommandIssuer issuer, @Conditions("filter:gamemode=PVE") Game game) {
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
                ChatChannels.sendDebugMessage(issuer, message);
                return;
            }
        }
    }

    @Subcommand("noai")
    public void noAi(@Conditions("requireGame:gamemode=PVE") Player player, Boolean ai) {
        for (Option option : Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get().getOptions()) {
            if (option instanceof PveOption pveOption) {
                pveOption.getMobs()
                         .stream()
                         .map(abstractMob -> abstractMob.getEntity().get())
                         .forEach(entityInsentient -> entityInsentient.setNoAi(ai));
                ChatChannels.sendDebugMessage(player,
                        ChatColor.GREEN + "Set All Mob NoAI to " + ChatColor.YELLOW + ai + ChatColor.GREEN + " for " + SPAWNED_MOBS.size() + " mobs"
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

    @Subcommand("drops")
    public class MobDropCommand extends BaseCommand {

        @Subcommand("add")
        public void add(Player player, MobDrops mobDrop, Integer amount) {
            DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
                databasePlayer.getPveStats().addMobDrops(mobDrop, amount);
            });
            ChatChannels.playerSendMessage(player,
                    ChatChannels.DEBUG,
                    ChatColor.GREEN + "Gave yourself " + mobDrop.getCostColoredName(amount)
            );
        }

    }

}
