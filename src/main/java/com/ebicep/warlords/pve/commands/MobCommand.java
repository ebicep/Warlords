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
import com.ebicep.warlords.player.ingame.WarlordsPlayerDisguised;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.mobs.events.spidersburrow.EventEggSac;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@CommandAlias("mob")
@CommandPermission("group.administrator")
public class MobCommand extends BaseCommand {

    public static final Set<AbstractMob<?>> SPAWNED_MOBS = new HashSet<>();

    @Subcommand("spawn")
    @Description("Spawns mobs, amount is how many")
    public void spawn(
            @Conditions("requireGame:gamemode=PVE") Player player,
            Mob mobType,
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
                ChatChannels.sendDebugMessage(player, Component.text("Spawned " + amount + " Mobs", NamedTextColor.GREEN));
                return;
            }
        }
    }

    @Subcommand("spawngroup")
    @Description("Spawns all mobs in a group")
    public void spawnGroup(
            @Conditions("requireGame:gamemode=PVE") Player player,
            Mob.MobGroup mobGroup
    ) {
        SPAWNED_MOBS.clear();
        for (Option option : Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get().getOptions()) {
            if (option instanceof PveOption pveOption) {
                ChatChannels.sendDebugMessage(player, Component.text("Spawning " + mobGroup.name() + " mobs", NamedTextColor.GREEN));
                for (Mob mob : mobGroup.mobs) {
                    AbstractMob<?> abstractMob = mob.createMob.apply(player.getLocation());
                    pveOption.spawnNewMob(abstractMob);
                    SPAWNED_MOBS.add(abstractMob);
                    ChatChannels.sendDebugMessage(player, Component.text("Spawned " + abstractMob.getName(), NamedTextColor.GREEN));
                }
                return;
            }
        }
    }

    @Subcommand("spawntest")
    public void spawnTest(
            @Conditions("requireGame:gamemode=PVE") Player player,
            Mob mobType
    ) {
        SPAWNED_MOBS.clear();
        for (Option option : Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get().getOptions()) {
            if (option instanceof PveOption pveOption) {
                AbstractMob<?> mob = mobType.createMob.apply(player.getLocation());
                pveOption.spawnNewMob(mob, Team.BLUE);
                SPAWNED_MOBS.add(mob);
                ChatChannels.sendDebugMessage(player, Component.text("Spawned Test Mob - " + mob.getWarlordsNPC().getUuid(), NamedTextColor.GREEN));
                return;
            }
        }
    }

    @Subcommand("togglespawning")
    public void toggleSpawning(@Conditions("requireGame:gamemode=PVE") Player player) {
        for (Option option : Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get().getOptions()) {
            if (option instanceof PveOption pveOption) {
                pveOption.setPauseMobSpawn(!pveOption.isPauseMobSpawn());
                ChatChannels.sendDebugMessage(player, Component.text((pveOption.isPauseMobSpawn() ? "Disabled" : "Enabled") + " mob spawning", NamedTextColor.GREEN));
                return;
            }
        }
    }

    @Subcommand("togglearmorstandeggsac")
    public void toggleArmorStandEggSac(CommandIssuer issuer) {
        EventEggSac.ARMOR_STAND = !EventEggSac.ARMOR_STAND;
        if (EventEggSac.ARMOR_STAND) {
            ChatChannels.sendDebugMessage(issuer, Component.text("Enabled armor stand egg sac", NamedTextColor.GREEN));
        } else {
            ChatChannels.sendDebugMessage(issuer, Component.text("Disabled armor stand egg sac", NamedTextColor.RED));
        }
    }

    @Subcommand("speed")
    public void giveSpeed(@Conditions("requireGame:gamemode=PVE") Player player, Integer speed) {
        for (AbstractMob<?> spawnedMob : SPAWNED_MOBS) {
            spawnedMob.getWarlordsNPC().addSpeedModifier(null, "Test", speed, 30 * 20, "BASE");
        }
        ChatChannels.sendDebugMessage(player,
                Component.text("Set Speed: ", NamedTextColor.GREEN)
                         .append(Component.text(speed, NamedTextColor.YELLOW))
                         .append(Component.text(" for " + SPAWNED_MOBS.size() + " mobs"))
        );
    }

    @Subcommand("target")
    @CommandCompletion("@warlordsplayers")
    public void target(@Conditions("requireGame:gamemode=PVE") Player player, WarlordsPlayer target) {
        for (AbstractMob<?> spawnedMob : SPAWNED_MOBS) {
            spawnedMob.setTarget(target);
        }
        ChatChannels.sendDebugMessage(player, Component.text("Set Target: ", NamedTextColor.GREEN)
                                                       .append(Component.text(target.getName(), NamedTextColor.AQUA))
                                                       .append(Component.text(" for " + SPAWNED_MOBS.size() + " mobs")));
    }

    @Subcommand("targetnpc")
    @CommandCompletion("@warlordsnpcs")
    public void target(@Conditions("requireGame:gamemode=PVE") Player player, WarlordsNPC target) {
        for (AbstractMob<?> spawnedMob : SPAWNED_MOBS) {
            spawnedMob.setTarget(target);
        }
        ChatChannels.sendDebugMessage(player,
                Component.text("Set Target: ", NamedTextColor.GREEN)
                         .append(Component.text(target.getName(), NamedTextColor.AQUA))
                         .append(Component.text(" for " + SPAWNED_MOBS.size() + " mobs"))
        );
    }

    @Subcommand("alltarget")
    public void allTarget(@Conditions("requireGame:gamemode=PVE") Player player, WarlordsPlayer target) {
        for (Option option : Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get().getOptions()) {
            if (option instanceof PveOption) {
                ((PveOption) option).getMobs().forEach(abstractMob -> abstractMob.setTarget(target));
                ChatChannels.sendDebugMessage(player,
                        Component.text("Set All Mob Target: ", NamedTextColor.GREEN)
                                 .append(Component.text(target.getName(), NamedTextColor.AQUA))
                                 .append(Component.text(" for " + SPAWNED_MOBS.size() + " mobs"))
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
//                Component message = ((PveOption) option)
//                        .getMobs()
//                        .stream()
//                        .map(abstractMob -> {
//                            Mob mob = abstractMob.getMob();
//                            return abstractMob.getWarlordsNPC().getColoredName() +
//                                    Component.text(" @" + mob.getLevel().getWorld().getName() +
//                                    ChatColor.GRAY + " | " + Component.text(mob.getX() + ChatColor.GRAY + "," + ChatColor.DARK_GREEN + mob.getY() + ChatColor.GRAY + "," + Component.text(mob.getZ();
//                        })
//                        .collect(Collectors.joining("\n"));
//                ChatChannels.sendDebugMessage(issuer, message);
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
                        Component.text("Set All Mob NoAI to ", NamedTextColor.GREEN)
                                 .append(Component.text(ai, NamedTextColor.YELLOW))
                                 .append(Component.text(" for " + SPAWNED_MOBS.size() + " mobs"))
                );
                return;
            }
        }
    }

    @Subcommand("fakeplay")
    public void fakePlayer(@Conditions("requireGame:gamemode=PVE") Player player) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        for (Option option : game.getOptions()) {
            if (option instanceof PveOption pveOption) {
                pveOption.getMobs()
                         .stream()
                         .map(AbstractMob::getWarlordsNPC)
                         .findFirst()
                         .ifPresent(warlordsNPC -> {
                             ChatChannels.sendDebugMessage(player, Component.text(warlordsNPC + " - " + warlordsNPC.getLocation(), NamedTextColor.GREEN));
                             AbstractMob<?> mob = warlordsNPC.getMob();
                             mob.getLivingEntity().remove();
                             WarlordsPlayerDisguised playerDisguised = new WarlordsPlayerDisguised(player, warlordsNPC);
                             Warlords.getPlayers().put(player.getUniqueId(), playerDisguised);
                             game.getPlayers().put(player.getUniqueId(), warlordsNPC.getTeam());
                             player.setGameMode(GameMode.ADVENTURE);
                             player.teleport(warlordsNPC.getLocation());
                         });
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
        public void add(Player player, MobDrop mobDrop, Integer amount) {
            DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
                databasePlayer.getPveStats().addMobDrops(mobDrop, amount);
            });
            ChatChannels.playerSendMessage(player,
                    ChatChannels.DEBUG,
                    Component.text("Gave yourself ", NamedTextColor.GREEN).append(mobDrop.getCostColoredName(amount))
            );
        }

    }

}
