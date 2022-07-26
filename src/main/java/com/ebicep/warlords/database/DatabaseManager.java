package com.ebicep.warlords.database;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.cache.MultipleCacheResolver;
import com.ebicep.warlords.database.configuration.ApplicationConfiguration;
import com.ebicep.warlords.database.leaderboards.LeaderboardManager;
import com.ebicep.warlords.database.repositories.games.GameService;
import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.guild.GuildService;
import com.ebicep.warlords.database.repositories.masterworksfair.MasterworksFairService;
import com.ebicep.warlords.database.repositories.player.PlayerService;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.timings.TimingsService;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase.previousGames;


public class DatabaseManager {

    public static MongoClient mongoClient;
    public static MongoDatabase warlordsDatabase;

    public static PlayerService playerService;
    public static GameService gameService;
    public static TimingsService timingsService;
    public static MasterworksFairService masterworksFairService;
    public static GuildService guildService;

    public static String lastWarlordsPlusString = "";

    public static boolean enabled = true;
    private static final ConcurrentHashMap<PlayersCollections, Set<DatabasePlayer>> playersToUpdate = new ConcurrentHashMap<PlayersCollections, Set<DatabasePlayer>>() {{
        for (PlayersCollections value : PlayersCollections.values()) {
            put(value, new HashSet<>());
        }
    }};

    public static void init() {
        if (!enabled) {
            NPCManager.createGameNPCs();
            return;
        }
        if (!LeaderboardManager.enabled) {
            NPCManager.createGameNPCs();
        }

        AbstractApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);

        try {
            playerService = context.getBean("playerService", PlayerService.class);
            gameService = context.getBean("gameService", GameService.class);
            timingsService = context.getBean("timingsService", TimingsService.class);
            masterworksFairService = context.getBean("masterworksFairService", MasterworksFairService.class);
            guildService = context.getBean("guildService", GuildService.class);
        } catch (Exception e) {
            NPCManager.createGameNPCs();
            e.printStackTrace();
            return;
        }

        try {
            for (String cacheName : MultipleCacheResolver.playersCacheManager.getCacheNames()) {
                Objects.requireNonNull(MultipleCacheResolver.playersCacheManager.getCache(cacheName)).clear();
            }
            System.out.println("[Warlords] Cleared all players cache");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Loading all online players
        Bukkit.getOnlinePlayers().forEach(player -> {
            loadPlayer(player.getUniqueId(), PlayersCollections.LIFETIME, () -> {
            });
        });

        System.out.println("[Warlords] Storing all guilds");
        long guildStart = System.nanoTime();
        Warlords.newChain()
                .asyncFirst(() -> guildService.findAll())
                .syncLast(GuildManager.GUILDS::addAll)
                .sync(() -> System.out.println("[Warlords] Stored " + GuildManager.GUILDS.size() + " guilds in " + (System.nanoTime() - guildStart) / 1000000 + "ms"))
                .execute();

        MasterworksFairManager.init();

        //runnable that updates all player that need updating every 10 seconds (prevents spam update)
        new BukkitRunnable() {

            @Override
            public void run() {
                Warlords.newChain()
                        .async(DatabaseManager::updateQueue)
                        .sync(() -> playersToUpdate.forEach((playersCollections, databasePlayers) -> databasePlayers.clear()))
                        .execute();
            }
        }.runTaskTimer(Warlords.getInstance(), 20, 20 * 10);

        System.out.println("[Warlords] Loading Leaderboard Holograms - " + LeaderboardManager.enabled);
        Warlords.newChain()
                .async(() -> LeaderboardManager.addHologramLeaderboards(UUID.randomUUID().toString(), true))
                .execute();

        //Loading last 5 games
        System.out.println("[Warlords] Loading Last Games");
        Warlords.newChain()
                .asyncFirst(() -> gameService.getLastGames(10))
                .syncLast((games) -> {
                    System.out.println("Loaded Last Games");
                    previousGames.addAll(games);
                    LeaderboardManager.playerGameHolograms.forEach((uuid, integer) -> LeaderboardManager.playerGameHolograms.put(uuid, previousGames.size() - 1));
                    Bukkit.getOnlinePlayers().forEach(DatabaseGameBase::setGameHologramVisibility);
                    System.out.println("Set Game Hologram Visibility");
                })
                .execute();
    }

    public static void loadPlayer(UUID uuid, PlayersCollections collections, Runnable callback) {
        if (playerService == null || !enabled) return;
        if (playerService.findByUUID(uuid, collections) == null) {
            Warlords.newChain()
                    .syncFirst(() -> {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player == null) {
                            System.out.println("[WARNING] Player " + uuid + " name was not found");
                            return null;
                        }
                        return player.getName();
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

        if (!Objects.equals(databasePlayer.getName(), player.getName())) {
            databasePlayer.setName(player.getName());
            queueUpdatePlayerAsync(databasePlayer);
        }

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
        weaponSkins.values().removeAll(Collections.singleton(null));
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
        classesSkillBoosts.forEach((specializations, skillBoosts) -> {
            if (!specializations.skillBoosts.contains(skillBoosts)) {
                classesSkillBoosts.put(specializations, specializations.skillBoosts.get(0));
            }
        });
        Warlords.getPlayerSettings(player.getUniqueId()).setSpecsSkillBoosts(classesSkillBoosts);

        Settings.HotkeyMode.setSelected(player, databasePlayer.getHotkeyMode());
        Settings.ParticleQuality.setSelected(player, databasePlayer.getParticleQuality());
    }

    public static void updateQueue() {
        playersToUpdate.forEach((playersCollections, databasePlayers) -> databasePlayers.forEach(databasePlayer -> playerService.update(databasePlayer, playersCollections)));
    }

    public static void queueUpdatePlayerAsync(DatabasePlayer databasePlayer) {
        if (playerService == null || !enabled) return;
        playersToUpdate.get(PlayersCollections.LIFETIME).add(databasePlayer);
        //Warlords.newChain().async(() -> playerService.update(databasePlayer)).execute();
    }

    public static void queueUpdatePlayerAsync(DatabasePlayer databasePlayer, PlayersCollections collections) {
        if (playerService == null || !enabled) return;
        playersToUpdate.get(collections).add(databasePlayer);
        //Warlords.newChain().async(() -> playerService.update(databasePlayer, collections)).execute();
    }

    public static void updateGameAsync(DatabaseGameBase databaseGame) {
        if (playerService == null || !enabled) return;
        Warlords.newChain().async(() -> gameService.save(databaseGame, GamesCollections.ALL)).execute();
        Warlords.newChain().async(() -> gameService.save(databaseGame, databaseGame.getGameMode().gamesCollections)).execute();
    }

}
