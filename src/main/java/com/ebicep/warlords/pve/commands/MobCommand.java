package com.ebicep.warlords.pve.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.pve.mobs.Mobs;
import com.ebicep.warlords.pve.mobs.events.spidersburrow.EventEggSac;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
            if (option instanceof PveOption) {
                PveOption pveOption = (PveOption) option;
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

    @Subcommand("spawntest")
    public void spawnTest(
            @Conditions("requireGame:gamemode=WAVE_DEFENSE/EVENT_WAVE_DEFENSE") Player player,
            Mobs mobType
    ) {
        SPAWNED_MOBS.clear();
        for (Option option : Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get().getOptions()) {
            if (option instanceof PveOption) {
                PveOption pveOption = (PveOption) option;
                AbstractMob<?> mob = mobType.createMob.apply(player.getLocation());
                pveOption.spawnNewMob(mob);
                WarlordsNPC warlordsNPC = mob.getWarlordsNPC();
                warlordsNPC.getEntity().remove();
                ArmorStand armorStand = warlordsNPC.getWorld().spawn(warlordsNPC.getLocation(), ArmorStand.class);
                armorStand.setGravity(false);
                armorStand.setVisible(false);
                armorStand.setHelmet(new ItemStack(Material.DRAGON_EGG));
                armorStand.setCustomName("Test Mob");
                warlordsNPC.setEntity(armorStand);
                warlordsNPC.updateEntity();
                SPAWNED_MOBS.add(mob);
                ChatChannels.sendDebugMessage(player, ChatColor.GREEN + "Spawned Test Mob", true);
                return;
            }
        }
    }

    @Subcommand("togglearmorstandeggsac")
    public void toggleArmorStandEggSac(CommandIssuer issuer) {
        EventEggSac.ARMOR_STAND = !EventEggSac.ARMOR_STAND;
        if (EventEggSac.ARMOR_STAND) {
            ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Enabled armor stand egg sac", true);
        } else {
            ChatChannels.sendDebugMessage(issuer, ChatColor.RED + "Disabled armor stand egg sac", true);
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
                            EntityInsentient entity = abstractMob.getEntityInsentient();
                            return abstractMob.getWarlordsNPC().getColoredName() +
                                    ChatColor.GREEN + " @" + entity.getWorld().getWorld().getName() +
                                    ChatColor.GRAY + " | " + ChatColor.GREEN + entity.locX + ChatColor.GRAY + "," + ChatColor.DARK_GREEN + entity.locY + ChatColor.GRAY + "," + ChatColor.GREEN + entity.locZ;
                        })
                        .collect(Collectors.joining("\n"));
                ChatChannels.sendDebugMessage(issuer, message, true);
                return;
            }
        }
    }

    @Subcommand("ai")
    public void ai(@Conditions("requireGame:gamemode=WAVE_DEFENSE/EVENT_WAVE_DEFENSE") Player player, @Conditions("limits:min=0,max=1") Integer ai) {
        for (Option option : Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get().getOptions()) {
            if (option instanceof PveOption) {
                ((PveOption) option).getMobs().forEach(abstractMob -> {
                    EntityInsentient entityInsentient = abstractMob.getEntity().get();
                    NBTTagCompound tag = entityInsentient.getNBTTag();
                    if (tag == null) {
                        tag = new NBTTagCompound();
                    }
                    entityInsentient.c(tag);
                    tag.setByte("NoAI", ai.byteValue());
                    entityInsentient.f(tag);
                });
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

    @Subcommand("drops")
    public class MobDropCommand extends BaseCommand {

        @Subcommand("add")
        public void add(Player player, MobDrops mobDrop, Integer amount) {
            DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
                databasePlayer.getPveStats().addMobDrops(mobDrop, amount);
            });
            ChatChannels.playerSendMessage(player,
                    ChatColor.GREEN + "Gave yourself " + mobDrop.getCostColoredName(amount),
                    ChatChannels.DEBUG,
                    true
            );
        }

    }

}
