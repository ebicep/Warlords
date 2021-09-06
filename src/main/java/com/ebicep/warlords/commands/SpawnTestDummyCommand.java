package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.player.ArmorManager;
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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Objects;

public class SpawnTestDummyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        WarlordsPlayer player = BaseCommand.requireWarlordsPlayer(sender);
        if (player != null) {
            if (args.length >= 1) {
                String teamString = args[0];
                if (teamString.equalsIgnoreCase("blue") || teamString.equalsIgnoreCase("red")) {
                    Team team = teamString.equalsIgnoreCase("blue") ? Team.BLUE : Team.RED;
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer("testdummy");
                    Warlords.addPlayer(new WarlordsPlayer(offlinePlayer, player.getGameState(), team, new PlayerSettings()));
                    WarlordsPlayer testDummy = Warlords.getPlayer(offlinePlayer);
                    if(args.length >= 2) {
                        if(args[1].equalsIgnoreCase("false")) {
                            assert testDummy != null;
                            testDummy.setTakeDamage(false);
                        }
                    }
                    Objects.requireNonNull(testDummy).teleport(player.getLocation());
                    //SKULL
                    ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                    SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
                    skullMeta.setOwner(offlinePlayer.getName());
                    playerSkull.setItemMeta(skullMeta);
                    Warlords.getPlayerHeads().put(offlinePlayer.getUniqueId(), CraftItemStack.asNMSCopy(playerSkull));



                } else {

                }
            }
        }
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("spawntestdummy").setExecutor(this);
    }
}
