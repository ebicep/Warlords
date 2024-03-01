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
import com.ebicep.warlords.pve.mobs.*;
import com.ebicep.warlords.pve.mobs.events.spidersburrow.EventEggSac;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.ConfigUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@CommandAlias("mob")
@CommandPermission("group.administrator")
public class MobCommand extends BaseCommand {

    public static final Set<AbstractMob> SPAWNED_MOBS = new HashSet<>();

    @Subcommand("menu")
    public void menu(Player player) {
        MobMenu.openMobMenu(player);
    }

    @Subcommand("spawn")
    @Description("Spawns mobs, amount is how many")
    public void spawn(
            @Conditions("requireGame:gamemode=PVE") Player player,
            Mob mobType,
            @Default("1") @Conditions("limits:min=0,max=25") Integer amount,
            @Optional Aspect aspect
    ) {
        SPAWNED_MOBS.clear();
        for (Option option : Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get().getOptions()) {
            if (option instanceof PveOption pveOption) {
                for (int i = 0; i < amount; i++) {
                    AbstractMob mob = mobType.createMob(player.getLocation());
                    if (aspect != null) {
                        mob.setAspect(aspect);
                    }
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
            Mob.MobGroup mobGroup,
            @Optional Aspect aspect
    ) {
        SPAWNED_MOBS.clear();
        for (Option option : Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get().getOptions()) {
            if (option instanceof PveOption pveOption) {
                ChatChannels.sendDebugMessage(player, Component.text("Spawning " + mobGroup.name() + " mobs", NamedTextColor.GREEN));
                for (Mob mob : mobGroup.mobs) {
                    AbstractMob abstractMob = mob.createMob(player.getLocation());
                    if (aspect != null) {
                        abstractMob.setAspect(aspect);
                    }
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
                AbstractMob mob = mobType.createMob(player.getLocation());
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
        for (AbstractMob spawnedMob : SPAWNED_MOBS) {
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
        for (AbstractMob spawnedMob : SPAWNED_MOBS) {
            spawnedMob.setTarget(target);
        }
        ChatChannels.sendDebugMessage(player, Component.text("Set Target: ", NamedTextColor.GREEN)
                                                       .append(Component.text(target.getName(), NamedTextColor.AQUA))
                                                       .append(Component.text(" for " + SPAWNED_MOBS.size() + " mobs")));
    }

    @Subcommand("targetnpc")
    @CommandCompletion("@warlordsnpcs")
    public void target(@Conditions("requireGame:gamemode=PVE") Player player, WarlordsNPC target) {
        for (AbstractMob spawnedMob : SPAWNED_MOBS) {
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
        SPAWNED_MOBS.forEach(mob -> mob.getNpc().getNavigator().setPaused(ai));
        ChatChannels.sendDebugMessage(player,
                Component.text("Set All Mob NoAI to ", NamedTextColor.GREEN)
                         .append(Component.text(ai, NamedTextColor.YELLOW))
                         .append(Component.text(" for " + SPAWNED_MOBS.size() + " mobs"))
        );
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
                             AbstractMob mob = warlordsNPC.getMob();
                             mob.getNpc().destroy();
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

    @Subcommand("tojson")
    public void toJson(Player player, @Optional String fileName) {
        JsonObject jsonObject = new JsonObject();
        Location location = player.getLocation();
        for (Mob value : Mob.VALUES) {
            JsonObject mobObject = new JsonObject();
            AbstractMob mob = value.createMobLegacy.apply(location.add(200, 100, 200));
            mobObject.addProperty("name", mob.getName());
            mobObject.addProperty("max_health", mob.getMaxHealth());
            mobObject.addProperty("walk_speed", mob.getWalkSpeed());
            mobObject.addProperty("damage_resistance", mob.getPlayerClass().getDamageResistance());
            mobObject.addProperty("min_melee_damage", mob.getMinMeleeDamage());
            mobObject.addProperty("max_melee_damage", mob.getMaxMeleeDamage());
            jsonObject.add(value.name(), mobObject);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(jsonObject));
        try {
            FileWriter writer = new FileWriter(new File(Warlords.getInstance().getDataFolder(), (fileName == null ? "mobs" : fileName) + ".json"));
            gson.toJson(jsonObject, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
            ChatChannels.sendDebugMessage(player, Component.text("There was an error writing file - " + e.getMessage(), NamedTextColor.RED));
        }
    }

    @Subcommand("reloadconfig")
    public void reloadConfig(CommandIssuer issuer) {
        ChatChannels.sendDebugMessage(issuer, Component.text("Reloading mob values", NamedTextColor.GREEN));
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    ConfigUtil.readMobConfig(Warlords.getInstance());
                    ChatChannels.sendDebugMessage(issuer, Component.text("Reloaded mob values", NamedTextColor.GREEN));
                } catch (FileNotFoundException e) {
                    ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
                    ChatChannels.sendDebugMessage(issuer, Component.text("There was an error reloading mob values - " + e.getMessage(), NamedTextColor.RED));
                }
            }
        }.runTaskAsynchronously(Warlords.getInstance());
    }

    @Subcommand("printvalues")
    public void printValues(CommandIssuer issuer) {
        for (Mob value : Mob.VALUES) {
            ChatChannels.sendDebugMessage(issuer, Component.text(
                    value.name() + " - " +
                            value.name + " - " +
                            value.maxHealth + " - " +
                            value.walkSpeed + " - " +
                            value.damageResistance + " - " +
                            value.minMeleeDamage + " - " +
                            value.maxMeleeDamage
                    , NamedTextColor.GREEN));
        }
    }

    @Subcommand("moveto")
    public void moveTo(Player player) {
        for (AbstractMob spawnedMob : SPAWNED_MOBS) {
            spawnedMob.getNpc().getNavigator().setTarget(player.getLocation());
        }
    }

    @Subcommand("getcodenames")
    public void getCodeNames(CommandIssuer issuer) {
        for (Mob value : Mob.VALUES) {
            ChatChannels.sendDebugMessage(issuer, Component.text(value.name() + "\t" + value.name, NamedTextColor.GREEN));
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
