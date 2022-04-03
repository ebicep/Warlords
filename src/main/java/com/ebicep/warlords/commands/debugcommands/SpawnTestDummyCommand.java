package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Optional;

public class SpawnTestDummyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        WarlordsPlayer player = BaseCommand.requireWarlordsPlayer(sender);

        if (!sender.hasPermission("warlords.game.spawndummy")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }

        if (player == null) {
            return true;
        }
        if (!player.getGame().getAddons().contains(GameAddon.PRIVATE_GAME)) {
            sender.sendMessage("§cDebug commands are disabled in public games!");
            return true;
        }
        if (args.length >= 1) {
            String teamString = args[0];
            Optional<Team> teamOpt = TeamMarker.getTeams(player.getGame()).stream().filter(e -> e.name().equalsIgnoreCase(teamString)).findAny();
            if (teamOpt.isPresent()) {
                Team team = teamOpt.get();
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer("testdummy");
                WarlordsPlayer testDummy = new WarlordsPlayer(offlinePlayer, player.getGameState(), team, new PlayerSettings());
                testDummy.setHealth(testDummy.getMaxHealth() / 2);
                Warlords.addPlayer(testDummy);
                player.getGame().addPlayer(offlinePlayer, false);
                if (args.length >= 2 && args[1].equalsIgnoreCase("false")) {
                    testDummy.setTakeDamage(false);
                } else if (args.length >= 2 && args[1].equalsIgnoreCase("true")) {
                    testDummy.setTakeDamage(true);
                } else {
                    sender.sendMessage("§cInvalid arguments! Valid arguments: [true, false]");
                }
                testDummy.teleport(player.getLocation());
                //SKULL
                ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
                skullMeta.setOwner(offlinePlayer.getName());
                playerSkull.setItemMeta(skullMeta);
                Warlords.getPlayerHeads().put(offlinePlayer.getUniqueId(), CraftItemStack.asNMSCopy(playerSkull));
            } else {
                sender.sendMessage("§cUnable to find team named " + teamString + ", valid options: " + TeamMarker.getTeams(player.getGame()));
                return true;
            }
        }
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("spawntestdummy").setExecutor(this);
    }
}
