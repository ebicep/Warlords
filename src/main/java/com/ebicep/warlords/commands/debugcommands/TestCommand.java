package com.ebicep.warlords.commands.debugcommands;

import co.aikar.taskchain.TaskChain;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.isOp()) {
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

//        Warlords.newChain()
//                .async(() -> {
//                    System.out.println("1");
//                    Warlords.newChain()
//                            .async(() -> {
//                                System.out.println("2");
//                            }).execute();
//                    System.out.println("3");
//                }).execute();

//        Warlords.newSharedChain("test")
//                        .sync(() -> {
//                            System.out.println("1");
//                            Warlords.newSharedChain("test")
//                                    .sync(() -> {
//                                        System.out.println("2");
//                                    }).execute();
//                            System.out.println("3");
//                        }).execute();
        System.out.println("1");
        Warlords.newChain()
                .async(() -> {
                    System.out.println("2");
                    run((Player) sender);
                    //runAsync((Player) sender);
                    System.out.println("5");
                }).sync(() -> {
                    System.out.println("6");
                }).execute();
        System.out.println("7");
//        System.out.println(Thread.currentThread());
//        new BukkitRunnable() {
//
//            @Override
//            public void run() {
//                System.out.println(Thread.currentThread());
//                new BukkitRunnable() {
//
//                    @Override
//                    public void run() {
//                        System.out.println(Thread.currentThread());
//
//                        new BukkitRunnable() {
//
//                            @Override
//                            public void run() {
//                                System.out.println(Thread.currentThread());
//                            }
//                        }.runTask(Warlords.getInstance());
//                    }
//                }.runTaskAsynchronously(Warlords.getInstance());
//            }
//        }.runTaskAsynchronously(Warlords.getInstance());
//        System.out.println("here");
//        Warlords.newSharedChain("test")
//                .sync(() -> {
//                    System.out.println("1");
//                    DatabaseManager.loadPlayer("test", (Player) sender);
//                    System.out.println("2");
//                    DatabaseManager.loadPlayer("test", (Player) sender);
//                    System.out.println("3");
//                }).execute();
//        System.out.println("HERE");
//        Warlords.newSharedChain("test")
//                .sync(() -> {
//                    System.out.println("4");
//                }).execute();
//        Warlords.newChain()
//                .async(() -> {
//                    System.out.println("1");
//                    Warlords.newChain()
//                            .async(() -> {
//                                System.out.println("2");
//                            }).sync(() -> {
//                                System.out.println("3");
//                            }).execute();
//                    System.out.println("4");
//                }).sync(() -> {
//                    System.out.println("5");
//                }).execute();

        //System.out.println(BotManager.getCompGamesServer().getTextChannels().get(6).sendMessage("HELLO"));
        return true;
    }

    private void runAsync(Player sender) {
        Warlords.newChain()
            .async(() -> {
                DatabaseManager.playersInformation.find(eq("uuid", sender.getUniqueId().toString())).first();
                System.out.println("3");
            }).sync(() -> {
                System.out.println("4");
            }).execute();
    }

    private void run(Player sender) {
        DatabaseManager.playersInformation.find(eq("uuid", sender.getUniqueId().toString())).first();
        System.out.println("3");
        System.out.println("4");
    }

    public void register(Warlords instance) {
        instance.getCommand("test").setExecutor(this);
        //instance.getCommand("class").setTabCompleter(this);
    }

}
