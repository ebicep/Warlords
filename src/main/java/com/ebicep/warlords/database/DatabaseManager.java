package com.ebicep.warlords.database;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.leaderboards.LeaderboardManager;
import com.ebicep.warlords.database.configuration.ApplicationConfiguration;
import com.ebicep.warlords.database.repositories.games.GameService;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.player.PlayerService;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.*;
import com.ebicep.warlords.player.ArmorManager;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.Settings;
import com.ebicep.warlords.player.Weapons;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame.previousGames;

public class DatabaseManager {

    public static MongoClient mongoClient;
    public static MongoDatabase warlordsDatabase;
    public static MongoCollection<Document> gamesInformation;

    public static PlayerService playerService;
    public static GameService gameService;

    public static String lastWarlordsPlusString = "";

    public static void init() {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);

        playerService = context.getBean("playerService", PlayerService.class);
        gameService = context.getBean("gameService", GameService.class);

        //Loading all online players
        Bukkit.getOnlinePlayers().forEach(player -> {
            loadPlayer(player.getUniqueId());
            updateName(player.getUniqueId());
            Warlords.playerScoreboards.get(player.getUniqueId()).giveMainLobbyScoreboard();
        });

        //Loading last 5 games
        Warlords.newChain()
                .asyncFirst(() -> gameService.getLastGames(5))
                .syncLast((games) -> {
                    previousGames.addAll(games);
                    LeaderboardManager.addHologramLeaderboards(UUID.randomUUID().toString());
                })
                .execute();

    }

    public static void loadPlayer(UUID uuid) {
        if (playerService.findByUUID(uuid) == null) {
            Warlords.newChain().syncFirst(() -> {
                        String name = Bukkit.getOfflinePlayer(uuid).getName();
                        if (name == null) {
                            System.out.println("NULL NAME ERROR !!!!! " + uuid.toString());
                        }
                        return name;
                    }).asyncLast((name) -> playerService.create(new DatabasePlayer(uuid, name)))
                    .sync(() -> loadPlayerInfo(Bukkit.getPlayer(uuid))).execute();
        } else {
            System.out.println("Loaded Player " + uuid);
            loadPlayerInfo(Bukkit.getPlayer(uuid));
        }
    }

    private static void loadPlayerInfo(Player player) {
        DatabasePlayer databasePlayer = playerService.findByUUID(player.getUniqueId());
        Warlords.getPlayerSettings(player.getUniqueId()).setSelectedClass(databasePlayer.getLastSpec());

        ArmorManager.Helmets.setSelectedMage(player, databasePlayer.getMage().getHelmet());
        ArmorManager.ArmorSets.setSelectedMage(player, databasePlayer.getMage().getArmor());
        ArmorManager.Helmets.setSelectedWarrior(player, databasePlayer.getWarrior().getHelmet());
        ArmorManager.ArmorSets.setSelectedWarrior(player, databasePlayer.getWarrior().getArmor());
        ArmorManager.Helmets.setSelectedPaladin(player, databasePlayer.getPaladin().getHelmet());
        ArmorManager.ArmorSets.setSelectedPaladin(player, databasePlayer.getPaladin().getArmor());
        ArmorManager.Helmets.setSelectedShaman(player, databasePlayer.getShaman().getHelmet());
        ArmorManager.ArmorSets.setSelectedShaman(player, databasePlayer.getShaman().getArmor());

        HashMap<Classes, Weapons> weaponSkins = new HashMap<>();
        weaponSkins.put(Classes.PYROMANCER, databasePlayer.getMage().getPyromancer().getWeapon());
        weaponSkins.put(Classes.CRYOMANCER, databasePlayer.getMage().getCryomancer().getWeapon());
        weaponSkins.put(Classes.AQUAMANCER, databasePlayer.getMage().getAquamancer().getWeapon());
        weaponSkins.put(Classes.BERSERKER, databasePlayer.getWarrior().getBerserker().getWeapon());
        weaponSkins.put(Classes.DEFENDER, databasePlayer.getWarrior().getDefender().getWeapon());
        weaponSkins.put(Classes.REVENANT, databasePlayer.getWarrior().getRevenant().getWeapon());
        weaponSkins.put(Classes.AVENGER, databasePlayer.getPaladin().getAvenger().getWeapon());
        weaponSkins.put(Classes.CRUSADER, databasePlayer.getPaladin().getCrusader().getWeapon());
        weaponSkins.put(Classes.PROTECTOR, databasePlayer.getPaladin().getProtector().getWeapon());
        weaponSkins.put(Classes.THUNDERLORD, databasePlayer.getShaman().getThunderlord().getWeapon());
        weaponSkins.put(Classes.SPIRITGUARD, databasePlayer.getShaman().getSpiritguard().getWeapon());
        weaponSkins.put(Classes.EARTHWARDEN, databasePlayer.getShaman().getEarthwarden().getWeapon());
        Warlords.getPlayerSettings(player.getUniqueId()).setWeaponSkins(weaponSkins);

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
        Warlords.newChain().async(() -> playerService.update(databasePlayer)).execute();
    }

    public static void updatePlayerAsync(DatabasePlayer databasePlayer, PlayersCollections collections) {
        Warlords.newChain().async(() -> playerService.update(databasePlayer, collections)).execute();
    }

    public static void updateGameAsync(DatabaseGame databaseGame) {
        Warlords.newChain().async(() -> gameService.update(databaseGame)).execute();
    }

}
