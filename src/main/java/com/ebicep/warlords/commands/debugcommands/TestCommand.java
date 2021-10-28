package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.Leaderboards;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ebicep.warlords.database.DatabaseManager.getDocumentInfoWithDotNotation;
import static com.ebicep.warlords.database.DatabaseManager.warlordsGamesDatabase;
import static com.mongodb.client.model.Filters.eq;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.isOp()) {
            return true;
        }
        WarlordsPlayer player = BaseCommand.requireWarlordsPlayer(sender);
        if (player != null) {

        }

//        DatabaseManager.warlordsGamesDatabase.createCollection("Games_Information_Backup");
//        for (Document document : DatabaseManager.gamesInformation.find()) {
//            DatabaseManager.warlordsGamesDatabase.getCollection("Games_Information_Backup").insertOne(document);
//        }
//        for (Document document2 : DatabaseManager.gamesInformation.find()) {
//            if(document2.get("counted") == null) {
//                Document document = new Document();
//                document.put("counted", true);
//                DatabaseManager.gamesInformation.updateOne(Filters.eq("_id", document2.get("_id")), new Document("$set", document));
//            }
//        }
//        Warlords.newChain().async(() -> {
//            for (Document document : DatabaseManager.gamesInformation.find()) {
//                ArrayList<Document> playersRed = new ArrayList<>((ArrayList<Document>) getDocumentInfoWithDotNotation(document, "players.blue"));
//                int counter = 0;
//                for (Document document1 : playersRed) {
//                    if(document1.get("seconds_in_respawn") instanceof Double) {
//                        Document doc = new Document();
//                        doc.put("players.blue." + counter + ".seconds_in_respawn", (int)Math.round((Double) document1.get("seconds_in_respawn")));
//                        Warlords.newChain().async(()-> {
//                            DatabaseManager.gamesInformation.updateOne(eq("date", document.get("date")), new Document("$set", doc));
//                        }).execute();
//                        System.out.println(document1.get("name"));
//                        System.out.println("players.blue." + counter + ".seconds_in_respawn");
//                    }
//                    counter++;
//                }
//            }
//        }).execute();
//        MongoCollection<Document> temp = warlordsGamesDatabase.getCollection("Test");
//        Document document = new Document();
//        float[] array = {131232.8f,23122.32f,313.32f,4321.3123f,3125.1f,3129};
//        document.append("damage", IntStream.range(0,array.length).mapToLong(i -> (long) array[i]).boxed().collect(Collectors.toList()));
//
//        temp.insertOne(document);

        System.out.println(HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                .filter(h -> h.getVisibilityManager().isVisibleTo((Player) sender) && h.getLocation().equals(Leaderboards.center))
                .count());
        Hologram center = HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                .filter(h -> h.getVisibilityManager().isVisibleTo((Player) sender) && h.getLocation() == Leaderboards.center)
                .findAny()
                .orElseGet(() -> HologramsAPI.createHologram(Warlords.getInstance(), Leaderboards.center));
        System.out.println(center);
        System.out.println(center.getLine(0));
        center.clearLines();
        center.appendTextLine("TEST");

        center.getVisibilityManager().setVisibleByDefault(false);
        center.getVisibilityManager().isVisibleTo((Player) sender);

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("test").setExecutor(this);
        //instance.getCommand("class").setTabCompleter(this);
    }

}
