package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.LeaderboardRanking;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.flags.PlayerFlagLocation;
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

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        WarlordsPlayer player = BaseCommand.requireWarlordsPlayer(sender);
        if (player != null) {

            //do stuff
//            Document doc = DatabaseManager.getLastGame();
////            System.out.println(doc);
//            System.out.println(DatabaseManager.getDocumentInfoWithDotNotation(doc, "players.red"));
//            for (Document o : ((ArrayList<Document>) DatabaseManager.getDocumentInfoWithDotNotation(doc, "players.red"))) {
//                System.out.println(DatabaseManager.getDocumentInfoWithDotNotation(o, "kills"));
//                System.out.println(((ArrayList<Integer>)DatabaseManager.getDocumentInfoWithDotNotation(o, "kills")).stream().reduce(0, Integer::sum));
//                System.out.println("----------");
//            }
            //System.out.println(((Player) sender).getUniqueId());
            //System.out.println(DatabaseManager.getDocumentInfoWithDotNotation(doc, "players.blue" + ((Player) sender).getUniqueId()));

            //player.teleport(player.getLocation());
            //System.out.println("TELEPORTED");
        }
        LeaderboardRanking.addHologramLeaderboards();
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("test").setExecutor(this);
        //instance.getCommand("class").setTabCompleter(this);
    }

}
