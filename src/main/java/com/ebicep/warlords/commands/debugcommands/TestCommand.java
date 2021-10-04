package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.LeaderboardRanking;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.flags.PlayerFlagLocation;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.WarlordsPlayer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.Document;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!sender.isOp()) {
            return true;
        }
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
        //LeaderboardRanking.addHologramLeaderboards();

//        System.out.println(BotManager.getCompGamesServer().getTextChannels());
//        Optional<TextChannel> botTeams = BotManager.getTextChannelByName("bot-teams");
//        botTeams.ifPresent(textChannel -> textChannel.sendMessage("test").queue());

//        System.out.println("0");
//        new BukkitRunnable() {
//
//            @Override
//            public void run() {
//                System.out.println("1");
//                new BukkitRunnable() {
//
//                    @Override
//                    public void run() {
//                        //Document document = DatabaseManager.playersInformation.find(eq("uuid", ((Player)sender).getUniqueId().toString())).first();
//                        //System.out.println(document);
//                        System.out.println("2");
//                    }
//                }.runTaskAsynchronously(Warlords.getInstance());
//                System.out.println("3");
//            }
//        }.runTaskAsynchronously(Warlords.getInstance());
//        System.out.println("4");

        //System.out.println(BotManager.getCompGamesServer().getTextChannels().get(6).sendMessage("HELLO"));
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("test").setExecutor(this);
        //instance.getCommand("class").setTabCompleter(this);
    }

}
