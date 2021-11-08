package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;
import java.util.*;

import static com.ebicep.warlords.database.DatabaseManager.*;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.isOp()) {
            return true;
        }
        WarlordsPlayer player = BaseCommand.requireWarlordsPlayer(sender);
        if (player != null) {

        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
//        DatabaseManager.warlordsPlayersDatabase.createCollection("Players_Information_Test");
        MongoCollection<Document> test = warlordsPlayersDatabase.getCollection("Players_Information_Test");
        List<Document> documents = Lists.newArrayList(DatabaseManager.playersInformation.aggregate(Collections.singletonList(sort(descending("paladin.avenger.wins")))));
        System.out.println(documents.get(0));
        System.out.println(documents.get(1));
//        int counter = 0;
//        for (Document document : Leaderboards.cachedSortedPlayersLifeTime.get("wins")) {
//            System.out.println(document.get("name") + " - Games Played: " + (document.getInteger("wins") + document.getInteger("losses")));
//            for (String spec : DatabaseGame.specsOrdered) {
//                long exp = getCalculatedExp(document, Classes.getClassesGroup(spec).name.toLowerCase() + "." + spec.toLowerCase());
//                System.out.println(spec + " EXP: " + exp + " - Level: " +
//                        decimalFormat.format(calculateLevelFromExp(exp)) +
//                        " - Games Played: " + ((int) getDocumentInfoWithDotNotation(document, Classes.getClassesGroup(spec).name.toLowerCase() + "." + spec.toLowerCase() + ".wins") +
//                        (int) getDocumentInfoWithDotNotation(document,Classes.getClassesGroup(spec).name.toLowerCase() + "." + spec.toLowerCase() + ".losses")));
//            }
//            counter++;
//            if(counter == 10) {
//                break;
//            }
//        }

//        if(document.get("total_class_exp") == null) {
//            long calculated
//            Document expDocument = new Document("total_class_exp", 0);
//            test.updateOne(Filters.eq("uuid", document.get("uuid")), new Document("$set", document));
//        }
//
//        int totalDHP = 0;
//        int totalDamage = 0;
//        int totalHealing = 0;
//        int totalAbsorbed = 0;
//        for (String s1 : DatabaseGame.specsOrdered) {
//            Classes specName = Classes.getClass(s1);
//            String className = Classes.getClassesGroup(s1).name.toLowerCase();
//            if(specName.specType == SpecType.HEALER) {
//                int dhp = getTotalAverageDHP(className + "." + s1.toLowerCase());
//                int damage = getTotalAverageDHPSelected(className + "." + s1.toLowerCase(), "damage");
//                int healing = getTotalAverageDHPSelected(className + "." + s1.toLowerCase(), "healing");
//                int absorbed = getTotalAverageDHPSelected(className + "." + s1.toLowerCase(), "absorbed");
//                totalDHP += dhp;
//                totalDamage += damage;
//                totalHealing += healing;
//                totalAbsorbed += absorbed;
//                System.out.println(s1);
//                System.out.println("Average DHP: " + dhp);
//                System.out.println("Average Damage: " + damage);
//                System.out.println("Average Healing: " + healing);
//                System.out.println("Average Absorbed: " + absorbed);
//            }
//        }
//        System.out.println("DPS");
//        System.out.println("Average DHP: " + totalDHP / 4);
//        System.out.println("Average Damage: " + totalDamage / 4);
//        System.out.println("Average Healing: " + totalHealing / 4);
//        System.out.println("Average Absorbed: " + totalAbsorbed / 4);
//        System.out.println("Damage Ratio: " + (double) totalDamage / totalDHP);
//        System.out.println("Healing Ratio: " + (double) totalHealing / totalDHP);
//        System.out.println("Absorbed Ratio: " + (double) totalAbsorbed / totalDHP);

//        for (Document document : DatabaseManager.playersInformation.find().filter(eq("name", "sumSmash"))) {
//            test.insertOne(document);
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


        sender.sendMessage(ChatColor.GREEN + "DID THE THING");
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("test").setExecutor(this);
        //instance.getCommand("class").setTabCompleter(this);
    }

}
