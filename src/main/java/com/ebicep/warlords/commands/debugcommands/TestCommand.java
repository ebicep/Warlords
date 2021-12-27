package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.commands.miscellaneouscommands.MessageCommand;
import com.ebicep.warlords.database.cache.MultipleCacheResolver;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseSpecialization;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.github.benmanes.caffeine.cache.Cache;
import com.mongodb.client.model.WriteModel;
import me.filoghost.holographicdisplays.api.beta.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.beta.hologram.Hologram;
import me.filoghost.holographicdisplays.api.beta.hologram.VisibilitySettings;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.cache.caffeine.CaffeineCache;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("warlords.game.test")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }
        WarlordsPlayer warlordsPlayer = BaseCommand.requireWarlordsPlayer(sender);
        if (warlordsPlayer != null) {
            System.out.println(!warlordsPlayer.getGameState().isForceEnd() && warlordsPlayer.getGameState().getStats(warlordsPlayer.getTeam()).points() > warlordsPlayer.getGameState().getStats(warlordsPlayer.getTeam().enemy()).points());
//            System.out.println(ExperienceManager.getExpFromGameStats(warlordsPlayer, true));
        }
        Player player = (Player) sender;
        MessageCommand.lastPlayerMessages.clear();
//        for (int i = 0; i < 9; i++) {
//            hologram.getLines().appendText("test text"); //ChatColor.YELLOW.toString() + (i + 1)+ ". " + ChatColor.AQUA + "Test Name");
//        }

//        hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
//        hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);

//        for (DatabasePlayer lifeTime : DatabaseManager.playerService.findAll(PlayersCollections.ALL_TIME)) {
//            DatabasePlayer season4 = DatabaseManager.playerService.findByUUID(UUID.fromString(lifeTime.getUuid()), PlayersCollections.SEASON_4);
//            if(season4 == null || lifeTime.getPlays() == season4.getPlays()) {
//                continue;
//            }
//            DatabasePlayer newPlayer = new DatabasePlayer(UUID.fromString(lifeTime.getUuid()), lifeTime.getName());
//            newPlayer.setKills(lifeTime.getKills() - season4.getKills());
//            newPlayer.setAssists(lifeTime.getAssists() - season4.getAssists());
//            newPlayer.setDeaths(lifeTime.getDeaths() - season4.getDeaths());
//            newPlayer.setWins(lifeTime.getWins() - season4.getWins());
//            newPlayer.setLosses(lifeTime.getLosses() - season4.getLosses());
//            newPlayer.setPlays(lifeTime.getPlays() - season4.getPlays());
//            newPlayer.setFlagsCaptured(lifeTime.getFlagsCaptured() - season4.getFlagsCaptured());
//            newPlayer.setFlagsReturned(lifeTime.getFlagsReturned() - season4.getFlagsReturned());
//            newPlayer.setDamage(lifeTime.getDamage() - season4.getDamage());
//            newPlayer.setHealing(lifeTime.getHealing() - season4.getHealing());
//            newPlayer.setAbsorbed(lifeTime.getAbsorbed() - season4.getAbsorbed());
//            subtractSpecs(newPlayer, lifeTime, season4);
//            newPlayer.setLastSpec(lifeTime.getLastSpec());
//            newPlayer.setHotkeyMode(lifeTime.getHotkeyMode());
//            newPlayer.setParticleQuality(lifeTime.getParticleQuality());
//            newPlayer.setExperience(lifeTime.getExperience());
//            DatabaseManager.playerService.create(newPlayer, PlayersCollections.SEASON_5);
//        }

//        QueueManager.queue.clear();
//
////        DatabaseManager.warlordsDatabase.getCollection("Weekly_Leaderboards").insertOne(LeaderboardManager.getTopPlayersOnLeaderboard());
//
//        int counter = 0;
//        List<DatabasePlayer> databasePlayers = DatabaseManager.playerService.findAll(PlayersCollections.WEEKLY);
////        MongoCollection<Document> collection = DatabaseManager.warlordsDatabase.getCollection("Temp");
////        System.out.println(databasePlayers.size());
//        for (int i = 0; i < 200 && i < databasePlayers.size(); i++) {
//            DatabasePlayer databasePlayer = databasePlayers.get(i);
//            System.out.println(i + " - " + databasePlayer.getName());
//
//            databasePlayer.setPlays(databasePlayer.getWins() + databasePlayer.getLosses());
//            databasePlayer.getMage().setPlays(databasePlayer.getMage().getWins() + databasePlayer.getMage().getLosses());
//            databasePlayer.getWarrior().setPlays(databasePlayer.getWarrior().getPlays() + databasePlayer.getWarrior().getLosses());
//            databasePlayer.getPaladin().setPlays(databasePlayer.getPaladin().getPlays() + databasePlayer.getPaladin().getLosses());
//            databasePlayer.getShaman().setPlays(databasePlayer.getShaman().getPlays() + databasePlayer.getShaman().getLosses());
//
//            for (DatabaseSpecialization spec : databasePlayer.getMage().getSpecs()) {
//                spec.setPlays(spec.getWins() + spec.getLosses());
//            }
//            for (DatabaseSpecialization spec : databasePlayer.getWarrior().getSpecs()) {
//                spec.setPlays(spec.getWins() + spec.getLosses());
//            }
//            for (DatabaseSpecialization spec : databasePlayer.getPaladin().getSpecs()) {
//                spec.setPlays(spec.getWins() + spec.getLosses());
//            }
//            for (DatabaseSpecialization spec : databasePlayer.getShaman().getSpecs()) {
//                spec.setPlays(spec.getWins() + spec.getLosses());
//            }
//
//            DatabaseManager.updatePlayerAsync(databasePlayer, PlayersCollections.WEEKLY);
//
//        }
//        for (DatabaseGame databaseGame : databaseGames) {
//            System.out.println(counter++);
//            System.out.println(databaseGame.getDate());
//            System.out.println(databaseGame.getPlayers());
//            for (DatabaseGamePlayers.GamePlayer gamePlayer : databaseGame.getPlayers().getBlue()) {
//                gamePlayer.setSpec(gamePlayer.getSpec().toUpperCase());
//            }
//            for (DatabaseGamePlayers.GamePlayer gamePlayer : databaseGame.getPlayers().getRed()) {
//                gamePlayer.setSpec(gamePlayer.getSpec().toUpperCase());
//            }
//            DatabaseManager.updateGameAsync(databaseGame);
//        }


//        for (DatabasePlayer databasePlayer : DatabaseManager.playerService.getPlayersSorted("", PlayersCollections.ALL_TIME)) {
//            System.out.println(databasePlayer.getName() + " - " + (databasePlayer.getWins() + databasePlayer.getLosses()));
//        }

//        printCache();
//        System.out.println(databasePlayer);
//        Utils.sendMessage(player, true, ChatColor.GREEN.toString() + ChatColor.BOLD + ChatColor.MAGIC + "   " + ChatColor.AQUA + ChatColor.BOLD + " LEVEL UP! " + ChatColor.DARK_GRAY + ChatColor.BOLD + "[" + ChatColor.GRAY + ChatColor.BOLD + "23" + ChatColor.DARK_GRAY + ChatColor.BOLD + "]" + ChatColor.GREEN + ChatColor.BOLD + " > " + ChatColor.DARK_GRAY + ChatColor.BOLD + "[" + ChatColor.GRAY + ChatColor.BOLD + "24" + ChatColor.DARK_GRAY + ChatColor.BOLD + "] " + ChatColor.GREEN + ChatColor.MAGIC + ChatColor.BOLD + "   ");

//        System.out.println(LeaderboardManager.leaderboards.get(0).getSortedAllTime().get(0));
//        System.out.println(LeaderboardManager.leaderboards.get(0).getSortedWeekly().get(0));
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
//        weeklyLeaderboards.insertOne(document);
        List<WriteModel<Document>> updates = new ArrayList<>();

//        playersInformation.find().forEach((Consumer<? super Document>) document -> {
//            ExperienceManager.giveExpFromCurrentStats(UUID.fromString(document.getString("uuid")));
//        });
//        DatabaseManager.warlordsGamesDatabase.createCollection("Games_Information_Test");
//        MongoCollection<Document> temp = warlordsGamesDatabase.getCollection("Temp");
//        for (Document document : gamesInformation.find().skip(640)) {
//            temp.insertOne(document);
//        }
//        Bukkit.getOnlinePlayers().forEach(p -> {
//            ExperienceManager.giveExpFromCurrentStats(p.getUniqueId());
//        });
//        MongoCollection<Document> test = warlordsPlayersDatabase.getCollection("Players_Information_Test");
//        long temp = (long) getPlayerInfoWithDotNotation(((Player) sender), "dots");
//        sender.sendMessage(ChatColor.BLUE + "---------------------------------------------------");
//        Utils.sendCenteredMessage((Player) sender, ChatColor.WHITE + "Experience Summary");
//        sender.sendMessage(ChatColor.BLUE + "---------------------------------------------------");
//
//        sender.sendMessage(ChatColor.BLUE + "---------------------------------------------------");
//        List<Document> documents = Lists.newArrayList(DatabaseManager.playersInformation.aggregate(Collections.singletonList(sort(descending("paladin.avenger.wins")))));
//        System.out.println(documents.get(0));
//        System.out.println(documents.get(1));
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

    private void subtractSpecs(DatabasePlayer databasePlayer, DatabasePlayer lifeTime, DatabasePlayer season4) {
        for (DatabaseWarlordsClass aClass : databasePlayer.getClasses()) {
            DatabaseWarlordsClass lifeTimeClass = lifeTime.getClass(aClass);
            DatabaseWarlordsClass season4Class = season4.getClass(aClass);
            for (int i = 0; i < aClass.getSpecs().length; i++) {
                DatabaseSpecialization spec = aClass.getSpecs()[i];
                DatabaseSpecialization lifeTimeSpec = lifeTimeClass.getSpecs()[i];
                DatabaseSpecialization season4Spec = season4Class.getSpecs()[i];
                spec.setKills(lifeTimeSpec.getKills() - season4Spec.getKills());
                spec.setAssists(lifeTimeSpec.getAssists() - season4Spec.getAssists());
                spec.setDeaths(lifeTimeSpec.getDeaths() - season4Spec.getDeaths());
                spec.setWins(lifeTimeSpec.getWins() - season4Spec.getWins());
                spec.setLosses(lifeTimeSpec.getLosses() - season4Spec.getLosses());
                spec.setPlays(lifeTimeSpec.getPlays() - season4Spec.getPlays());
                spec.setFlagsCaptured(lifeTimeSpec.getFlagsCaptured() - season4Spec.getFlagsCaptured());
                spec.setFlagsReturned(lifeTimeSpec.getFlagsReturned() - season4Spec.getFlagsReturned());
                spec.setDamage(lifeTimeSpec.getDamage() - season4Spec.getDamage());
                spec.setHealing(lifeTimeSpec.getHealing() - season4Spec.getHealing());
                spec.setAbsorbed(lifeTimeSpec.getAbsorbed() - season4Spec.getAbsorbed());
                spec.setWeapon(lifeTimeSpec.getWeapon());
                spec.setExperience(lifeTimeSpec.getExperience() - season4Spec.getExperience());
            }
            aClass.setKills(lifeTimeClass.getKills() - season4Class.getKills());
            aClass.setAssists(lifeTimeClass.getAssists() - season4Class.getAssists());
            aClass.setDeaths(lifeTimeClass.getDeaths() - season4Class.getDeaths());
            aClass.setWins(lifeTimeClass.getWins() - season4Class.getWins());
            aClass.setLosses(lifeTimeClass.getLosses() - season4Class.getLosses());
            aClass.setPlays(lifeTimeClass.getPlays() - season4Class.getPlays());
            aClass.setFlagsCaptured(lifeTimeClass.getFlagsCaptured() - season4Class.getFlagsCaptured());
            aClass.setFlagsReturned(lifeTimeClass.getFlagsReturned() - season4Class.getFlagsReturned());
            aClass.setDamage(lifeTimeClass.getDamage() - season4Class.getDamage());
            aClass.setHealing(lifeTimeClass.getHealing() - season4Class.getHealing());
            aClass.setAbsorbed(lifeTimeClass.getAbsorbed() - season4Class.getAbsorbed());
            aClass.setExperience(lifeTimeClass.getExperience() - season4Class.getExperience());
        }
    }

    public void register(Warlords instance) {
        instance.getCommand("test").setExecutor(this);
        //instance.getCommand("class").setTabCompleter(this);
    }

    private static void printCache() {
        Cache<Object, Object> cache = ((CaffeineCache) MultipleCacheResolver.playersCacheManager.getCache(PlayersCollections.ALL_TIME.cacheName)).getNativeCache();
        System.out.println("CACHE - " + cache.asMap());
    }
}
