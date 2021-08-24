package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = BaseCommand.requirePlayerOutsideGame(sender);
        if (player != null) {
            //do stuff
//            Document doc = DatabaseManager.getLastGame();
//            System.out.println(doc);
//            System.out.println(DatabaseManager.getDocumentInfoWithDotNotation(doc, "players.blue"));
//            for (Document o : ((ArrayList<Document>) DatabaseManager.getDocumentInfoWithDotNotation(doc, "players.blue"))) {
//                System.out.println(o.entrySet().stream().iterator().next().getKey());
//            }
            //System.out.println(((Player) sender).getUniqueId());
            //System.out.println(DatabaseManager.getDocumentInfoWithDotNotation(doc, "players.blue" + ((Player) sender).getUniqueId()));
            Warlords.addHologramLeaderboards();
        }
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("test").setExecutor(this);
        //instance.getCommand("class").setTabCompleter(this);
    }

}
