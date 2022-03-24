package com.ebicep.warlords.database;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.cache.MultipleCacheResolver;
import com.ebicep.warlords.database.configuration.ApplicationConfiguration;
import com.ebicep.warlords.database.leaderboards.LeaderboardManager;
import com.ebicep.warlords.database.repositories.games.GameService;
import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.player.PlayerService;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase.previousGames;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;


public class DatabaseManager {

    public static MongoClient mongoClient;
    public static MongoDatabase warlordsDatabase;
    public static MongoCollection<Document> gamesInformation;

    public static PlayerService playerService;
    public static GameService gameService;

    public static String lastWarlordsPlusString = "";

    public static boolean enabled = true;

    public static void init() {
        if (!enabled) {
            NPCManager.createGameNPC();
            return;
        }
        if (!LeaderboardManager.enabled) {
            NPCManager.createGameNPC();
        }

        AbstractApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);

        try {
            playerService = context.getBean("playerService", PlayerService.class);
            gameService = context.getBean("gameService", GameService.class);
        } catch (Exception e) {
            playerService = null;
            gameService = null;
            NPCManager.createGameNPC();
            e.printStackTrace();
            return;
        }

        try {
            System.out.println("CACHES");
            for (String cacheName : MultipleCacheResolver.playersCacheManager.getCacheNames()) {
                System.out.println(Objects.requireNonNull(((CaffeineCache) MultipleCacheResolver.playersCacheManager.getCache(cacheName)).getNativeCache()).asMap());
                Objects.requireNonNull(MultipleCacheResolver.playersCacheManager.getCache(cacheName)).clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Loading all online players
        Bukkit.getOnlinePlayers().forEach(player -> {
            loadPlayer(player.getUniqueId(), PlayersCollections.LIFETIME, () -> {
                updateName(player.getUniqueId());
            });
        });


        //Loading last 5 games
        Warlords.newChain()
                .asyncFirst(() -> gameService.getLastGames(10))
                .syncLast((games) -> {
                    previousGames.addAll(games);
                    LeaderboardManager.addHologramLeaderboards(UUID.randomUUID().toString(), true);
                })
                .execute();

        MongoCollection<Document> resetTimings = warlordsDatabase.getCollection("Reset_Timings");
        //checking weekly date, if over 10,000 minutes (10080 == 1 week) reset weekly
        Document weeklyDocumentInfo = resetTimings.find().filter(eq("time", "weekly")).first();
        Date current = new Date();
        if (weeklyDocumentInfo != null && weeklyDocumentInfo.get("last_reset") != null) {
            Date lastReset = weeklyDocumentInfo.getDate("last_reset");
            long timeDiff = current.getTime() - lastReset.getTime();

            System.out.println("Reset Time: " + timeDiff / 60000);
            if (timeDiff > 0 && timeDiff / (1000 * 60) > 10000) {
                Warlords.newSharedChain(UUID.randomUUID().toString())
                        .delay(20 * 10) // to make sure leaderboards are cached
                        .async(() -> {
                            //adding new document with top weekly players
                            Document topPlayers;
                            try {
                                topPlayers = LeaderboardManager.getTopPlayersOnLeaderboard();
                            } catch (Exception e) {
                                //updating date to current
                                resetTimings.updateOne(and(eq("time", "weekly"), eq("last_reset", lastReset)),
                                        new Document("$set", new Document("time", "weekly").append("last_reset", current))
                                );
                                System.out.println("ERROR DOING WEEKLY EXP THINGY - COMPS DIDNT HAPPEN?");
                                return;
                            }

                            MongoCollection<Document> weeklyLeaderboards = warlordsDatabase.getCollection("Weekly_Leaderboards");
                            weeklyLeaderboards.insertOne(topPlayers);

                            ExperienceManager.awardWeeklyExperience(topPlayers);
                            //clearing weekly
                            playerService.deleteAll(PlayersCollections.WEEKLY);
                            //reloading boards
                            LeaderboardManager.addHologramLeaderboards(UUID.randomUUID().toString(), false);
                            //updating date to current
                            resetTimings.updateOne(and(eq("time", "weekly"), eq("last_reset", lastReset)),
                                    new Document("$set", new Document("time", "weekly").append("last_reset", current))
                            );
                        }).sync(() -> Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Weekly player information reset"))
                        .execute();
            }
        }
        //checking daily date, if over 1,400 minutes (1440 == 1 day) reset daily
        Document dailyDocumentInfo = resetTimings.find().filter(eq("time", "daily")).first();
        if (dailyDocumentInfo != null && dailyDocumentInfo.get("last_reset") != null) {
            Date lastReset = dailyDocumentInfo.getDate("last_reset");
            long timeDiff = current.getTime() - lastReset.getTime();

            if (timeDiff > 0 && timeDiff / (1000 * 60) > 1400) {
                Warlords.newSharedChain(UUID.randomUUID().toString())
                        .async(() -> {
                            //clearing daily
                            playerService.deleteAll(PlayersCollections.DAILY);
                            //updating date to current
                            resetTimings.updateOne(and(eq("time", "daily"), eq("last_reset", lastReset)),
                                    new Document("$set", new Document("time", "daily").append("last_reset", current))
                            );
                        }).sync(() -> Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Daily player information reset"))
                        .execute();
            }
        }


    }

    public static void loadPlayer(UUID uuid, PlayersCollections collections, Runnable callback) {
        if (playerService == null || !enabled) return;
        if (playerService.findByUUID(uuid, collections) == null) {
            Warlords.newChain()
                    .syncFirst(() -> {
                        String name = Bukkit.getOfflinePlayer(uuid).getName();
                        if (name == null) {
                            System.out.println("NULL NAME ERROR !!!!! " + uuid);
                        }
                        return name;
                    })
                    .asyncLast((name) -> playerService.create(new DatabasePlayer(uuid, name), collections))
                    .sync(() -> {
                        if (collections == PlayersCollections.LIFETIME) {
                            loadPlayerInfo(Bukkit.getPlayer(uuid));
                            callback.run();
                        }
                    }).execute();
        } else {
            if (collections == PlayersCollections.LIFETIME) {
                Warlords.newChain()
                        .sync(() -> {
                            loadPlayerInfo(Bukkit.getPlayer(uuid));
                            callback.run();
                            System.out.println("Loaded Player " + uuid);
                        }).execute();
            }
        }
    }

    private static void loadPlayerInfo(Player player) {
        DatabasePlayer databasePlayer = playerService.findByUUID(player.getUniqueId());
        Warlords.getPlayerSettings(player.getUniqueId()).setSelectedSpec(databasePlayer.getLastSpec());

        ArmorManager.Helmets.setSelectedMage(player, databasePlayer.getMage().getHelmet());
        ArmorManager.ArmorSets.setSelectedMage(player, databasePlayer.getMage().getArmor());
        ArmorManager.Helmets.setSelectedWarrior(player, databasePlayer.getWarrior().getHelmet());
        ArmorManager.ArmorSets.setSelectedWarrior(player, databasePlayer.getWarrior().getArmor());
        ArmorManager.Helmets.setSelectedPaladin(player, databasePlayer.getPaladin().getHelmet());
        ArmorManager.ArmorSets.setSelectedPaladin(player, databasePlayer.getPaladin().getArmor());
        ArmorManager.Helmets.setSelectedShaman(player, databasePlayer.getShaman().getHelmet());
        ArmorManager.ArmorSets.setSelectedShaman(player, databasePlayer.getShaman().getArmor());
        ArmorManager.Helmets.setSelectedRogue(player, databasePlayer.getRogue().getHelmet());
        ArmorManager.ArmorSets.setSelectedRogue(player, databasePlayer.getRogue().getArmor());

        HashMap<Specializations, Weapons> weaponSkins = new HashMap<>();
        weaponSkins.put(Specializations.PYROMANCER, databasePlayer.getMage().getPyromancer().getWeapon());
        weaponSkins.put(Specializations.CRYOMANCER, databasePlayer.getMage().getCryomancer().getWeapon());
        weaponSkins.put(Specializations.AQUAMANCER, databasePlayer.getMage().getAquamancer().getWeapon());
        weaponSkins.put(Specializations.BERSERKER, databasePlayer.getWarrior().getBerserker().getWeapon());
        weaponSkins.put(Specializations.DEFENDER, databasePlayer.getWarrior().getDefender().getWeapon());
        weaponSkins.put(Specializations.REVENANT, databasePlayer.getWarrior().getRevenant().getWeapon());
        weaponSkins.put(Specializations.AVENGER, databasePlayer.getPaladin().getAvenger().getWeapon());
        weaponSkins.put(Specializations.CRUSADER, databasePlayer.getPaladin().getCrusader().getWeapon());
        weaponSkins.put(Specializations.PROTECTOR, databasePlayer.getPaladin().getProtector().getWeapon());
        weaponSkins.put(Specializations.THUNDERLORD, databasePlayer.getShaman().getThunderlord().getWeapon());
        weaponSkins.put(Specializations.SPIRITGUARD, databasePlayer.getShaman().getSpiritguard().getWeapon());
        weaponSkins.put(Specializations.EARTHWARDEN, databasePlayer.getShaman().getEarthwarden().getWeapon());
        weaponSkins.put(Specializations.ASSASSIN, databasePlayer.getRogue().getAssassin().getWeapon());
        weaponSkins.put(Specializations.VINDICATOR, databasePlayer.getRogue().getVindicator().getWeapon());
        weaponSkins.put(Specializations.APOTHECARY, databasePlayer.getRogue().getApothecary().getWeapon());
        Warlords.getPlayerSettings(player.getUniqueId()).setWeaponSkins(weaponSkins);

        HashMap<Specializations, SkillBoosts> classesSkillBoosts = new HashMap<>();
        classesSkillBoosts.put(Specializations.PYROMANCER, databasePlayer.getMage().getPyromancer().getSkillBoost());
        classesSkillBoosts.put(Specializations.CRYOMANCER, databasePlayer.getMage().getCryomancer().getSkillBoost());
        classesSkillBoosts.put(Specializations.AQUAMANCER, databasePlayer.getMage().getAquamancer().getSkillBoost());
        classesSkillBoosts.put(Specializations.BERSERKER, databasePlayer.getWarrior().getBerserker().getSkillBoost());
        classesSkillBoosts.put(Specializations.DEFENDER, databasePlayer.getWarrior().getDefender().getSkillBoost());
        classesSkillBoosts.put(Specializations.REVENANT, databasePlayer.getWarrior().getRevenant().getSkillBoost());
        classesSkillBoosts.put(Specializations.AVENGER, databasePlayer.getPaladin().getAvenger().getSkillBoost());
        classesSkillBoosts.put(Specializations.CRUSADER, databasePlayer.getPaladin().getCrusader().getSkillBoost());
        classesSkillBoosts.put(Specializations.PROTECTOR, databasePlayer.getPaladin().getProtector().getSkillBoost());
        classesSkillBoosts.put(Specializations.THUNDERLORD, databasePlayer.getShaman().getThunderlord().getSkillBoost());
        classesSkillBoosts.put(Specializations.SPIRITGUARD, databasePlayer.getShaman().getSpiritguard().getSkillBoost());
        classesSkillBoosts.put(Specializations.EARTHWARDEN, databasePlayer.getShaman().getEarthwarden().getSkillBoost());
        classesSkillBoosts.put(Specializations.ASSASSIN, databasePlayer.getRogue().getAssassin().getSkillBoost());
        classesSkillBoosts.put(Specializations.VINDICATOR, databasePlayer.getRogue().getVindicator().getSkillBoost());
        classesSkillBoosts.put(Specializations.APOTHECARY, databasePlayer.getRogue().getApothecary().getSkillBoost());
        classesSkillBoosts.values().removeAll(Collections.singleton(null));
        Warlords.getPlayerSettings(player.getUniqueId()).setSpecsSkillBoosts(classesSkillBoosts);

        Settings.HotkeyMode.setSelected(player, databasePlayer.getHotkeyMode());
        Settings.ParticleQuality.setSelected(player, databasePlayer.getParticleQuality());
    }

    public static void updateName(UUID uuid) {
        AtomicReference<String> currentName = new AtomicReference<>(Bukkit.getOfflinePlayer(uuid).getName());
        Warlords.newChain().asyncFirst(() -> playerService.findByUUID(uuid))
                .sync((player) -> {
                    if (currentName.get() == null) {
                        currentName.set(getName(uuid.toString()));
                    } else if (player.getName().equals(currentName.get())) {
                        return null;
                    }
                    return player;
                })
                .abortIfNull()
                .asyncLast((player) -> {
                    System.out.println("Changing " + player.getName() + "'s name to " + currentName);
                    player.setName(currentName.get());
                    playerService.update(player);
                }).execute();

    }

    public static String getName(String uuid) {
        String url = "https://api.mojang.com/user/profiles/" + uuid.replace("-", "") + "/names";
        try {
            String nameJson = IOUtils.toString(new URL(url));
            JSONArray nameValue = (JSONArray) JSONValue.parseWithException(nameJson);
            String playerSlot = nameValue.get(nameValue.size() - 1).toString();
            JSONObject nameObject = (JSONObject) JSONValue.parseWithException(playerSlot);
            return nameObject.get("name").toString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updatePlayerAsync(DatabasePlayer databasePlayer) {
        if (playerService == null || !enabled) return;
        Warlords.newChain().async(() -> playerService.update(databasePlayer)).execute();
    }

    public static void updatePlayerAsync(DatabasePlayer databasePlayer, PlayersCollections collections) {
        if (playerService == null || !enabled) return;
        Warlords.newChain().async(() -> playerService.update(databasePlayer, collections)).execute();
    }

    public static void updateGameAsync(DatabaseGameBase databaseGame, GamesCollections collection) {
        if (playerService == null || !enabled) return;
        Warlords.newChain().async(() -> gameService.save(databaseGame, collection)).execute();
    }

}
