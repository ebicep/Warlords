package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        //Warlords.addHologramLeaderboards();
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("test").setExecutor(this);
        //instance.getCommand("class").setTabCompleter(this);
    }

}
