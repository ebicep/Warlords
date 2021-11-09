package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.database.DatabaseGamePlayer;
import com.ebicep.warlords.database.DatabasePlayer;
import com.ebicep.warlords.database.Leaderboards;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.ClassesGroup;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static com.ebicep.warlords.database.DatabaseManager.*;
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

        MongoCollection<Document> temp = warlordsGamesDatabase.getCollection("Temp");
//        for (Document document : gamesInformation.find()) {
//            temp.insertOne(document);
//        }
//        for (Document document : gamesInformation.find()) {
//            Document newDocument = new Document();
//            ArrayList<Document> playersBlue = new ArrayList<>((ArrayList<Document>) getDocumentInfoWithDotNotation(document, "players.blue"));
//            ArrayList<Document> playersRed = new ArrayList<>((ArrayList<Document>) getDocumentInfoWithDotNotation(document, "players.red"));
//
//            for (int i = 0; i < playersBlue.size(); i++) {
//                DatabaseGamePlayer databasePlayer = new DatabaseGamePlayer(playersBlue.get(i), ChatColor.BLUE);
//                newDocument.append("players.blue." + i + ".total_damage", databasePlayer.getTotalDamage());
//                newDocument.append("players.blue." + i + ".total_healing", databasePlayer.getTotalHealing());
//                newDocument.append("players.blue." + i + ".total_absorbed", databasePlayer.getTotalAbsorbed());
//                newDocument.append("players.blue." + i + ".total_kills", databasePlayer.getTotalKills());
//                newDocument.append("players.blue." + i + ".total_assists", databasePlayer.getTotalAssists());
//                newDocument.append("players.blue." + i + ".total_deaths", databasePlayer.getTotalDeaths());
//            }
//
//            for (int i = 0; i < playersRed.size(); i++) {
//                DatabaseGamePlayer databasePlayer = new DatabaseGamePlayer(playersRed.get(i), ChatColor.RED);
//                newDocument.append("players.red." + i + ".total_damage", databasePlayer.getTotalDamage());
//                newDocument.append("players.red." + i + ".total_healing", databasePlayer.getTotalHealing());
//                newDocument.append("players.red." + i + ".total_absorbed", databasePlayer.getTotalAbsorbed());
//                newDocument.append("players.red." + i + ".total_kills", databasePlayer.getTotalKills());
//                newDocument.append("players.red." + i + ".total_assists", databasePlayer.getTotalAssists());
//                newDocument.append("players.red." + i + ".total_deaths", databasePlayer.getTotalDeaths());
//            }
//
//            gamesInformation.updateOne(eq("date", document.getString("date")), new Document("$set", newDocument));
//        }

        MongoCollection<Document> test = warlordsPlayersDatabase.getCollection("Players_Information_Test");
        for (Document document : playersInformation.find()) {
            Document newDocument = new Document();
            for (ClassesGroup value : ClassesGroup.values()) {
                int cap = 0;
                int ret = 0;
                for (Classes subclass : value.subclasses) {
                    cap += document.getEmbedded(Arrays.asList(value.name.toLowerCase(), subclass.name.toLowerCase(), "flags_captured"), Integer.class);
                    ret += document.getEmbedded(Arrays.asList(value.name.toLowerCase(), subclass.name.toLowerCase(), "flags_returned"), Integer.class);
                }
                newDocument.put(value.name.toLowerCase() + ".flags_captured", cap);
                newDocument.put(value.name.toLowerCase() + ".flags_returned", ret);
            }
            playersInformation.updateOne(eq("uuid", document.getString("uuid")), new Document("$set", newDocument));
        }

//        DatabaseManager.warlordsGamesDatabase.createCollection("Games_Information_Backup");
//        for (Document document : DatabaseManager.playersInformation.find()) {
//            DatabaseManager.playersInformationWeekly.insertOne(document);
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
//                ArrayList<Document> playersRed = new ArrayList<>((ArrayList<Document>) getDocumentInfoWithDotNotation(document, "players.red"));
//                int counter = 0;
//                for (Document document1 : playersRed) {
//                    if(document1.get("seconds_in_respawn") instanceof Double) {
//                        Document doc = new Document();
//                        doc.put("players.red." + counter + ".seconds_in_respawn", (int)Math.round((Double) document1.get("seconds_in_respawn")));
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

//        System.out.println(HologramsAPI.getHolograms(Warlords.getInstance()).stream()
//                .filter(h -> h.getVisibilityManager().isVisibleTo((Player) sender) && h.getLocation().equals(Leaderboards.center))
//                .count());
//        Hologram center = HologramsAPI.getHolograms(Warlords.getInstance()).stream()
//                .filter(h -> h.getVisibilityManager().isVisibleTo((Player) sender) && h.getLocation() == Leaderboards.center)
//                .findAny()
//                .orElseGet(() -> HologramsAPI.createHologram(Warlords.getInstance(), Leaderboards.center));
//        System.out.println(center);
//        System.out.println(center.getLine(0));
//        center.clearLines();
//        center.appendTextLine("TEST");
//
//        center.getVisibilityManager().setVisibleByDefault(false);
//        center.getVisibilityManager().isVisibleTo((Player) sender);

//        Document document = new Document();
//        document.put("last_reset", new Date());
//        DatabaseManager.weeklyInfo.insertOne(document);
//        String string = "October 25, 2021 6:02:00";
//        try {
//            Date date = new SimpleDateFormat("MMMM d, yyyy hh:mm:ss").parse(string);
//            weeklyInfo.insertOne(new Document("last_reset", date));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        Date date = new Date();
//        Date oldDate = (Date) DatabaseManager.weeklyInfo.find().first().get("last_reset");
//        System.out.println(date);
//        System.out.println(oldDate);
//        System.out.println(date.getTime() - oldDate.getTime());
//

        weeklyLeaderboards.insertOne(Leaderboards.getTopPlayersOnLeaderboard());
        sender.sendMessage(ChatColor.GREEN + "DID THE THING");
        return true;
    }

    private static long testMethod(Player player) {
        long startTime = System.nanoTime();

//        String[] keys = "paladin.avenger.wins".split("\\.");
//        Document doc = cachedPlayerInfo.get(player.getUniqueId());
//        for (int i = 0; i < keys.length - 1; i++) {
//            Object o = doc.get(keys[i]);
//            if (!(o instanceof Document)) {
//                throw new MongoException(String.format(ChatColor.GREEN + "[Warlords] Field '%s' does not exist or is not a Document", keys[i]));
//            }
//            doc = (Document) o;
//        }
//        System.out.println(doc.get(keys[keys.length - 1]));
 //       System.out.println(doc.getEmbedded(Arrays.asList("paladin", "avenger", "wins"), Integer.class));
        System.out.println(getPlayerInfoWithDotNotation(player, "paladin.avenger.wins"));
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    public void register(Warlords instance) {
        instance.getCommand("test").setExecutor(this);
        //instance.getCommand("class").setTabCompleter(this);
    }

}
