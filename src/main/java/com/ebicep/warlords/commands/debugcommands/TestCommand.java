package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.cache.MultipleCacheResolver;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.SpecType;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.github.benmanes.caffeine.cache.Cache;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.cache.caffeine.CaffeineCache;


public class TestCommand implements CommandExecutor {

    private static void printCache() {
        Cache<Object, Object> cache = ((CaffeineCache) MultipleCacheResolver.playersCacheManager.getCache(PlayersCollections.LIFETIME.cacheName)).getNativeCache();
        System.out.println("CACHE - " + cache.asMap());
    }

//    private void subtractSpecs(DatabasePlayer databasePlayer, DatabasePlayer lifeTime, DatabasePlayer season4) {
//        for (DatabaseWarlordsClass aClass : databasePlayer.getClasses()) {
//            DatabaseWarlordsClass lifeTimeClass = lifeTime.getClass(aClass);
//            DatabaseWarlordsClass season4Class = season4.getClass(aClass);
//            for (int i = 0; i < aClass.getSpecs().length; i++) {
//                DatabaseSpecialization spec = aClass.getSpecs()[i];
//                DatabaseSpecialization lifeTimeSpec = lifeTimeClass.getSpecs()[i];
//                DatabaseSpecialization season4Spec = season4Class.getSpecs()[i];
//                spec.setKills(lifeTimeSpec.getKills() - season4Spec.getKills());
//                spec.setAssists(lifeTimeSpec.getAssists() - season4Spec.getAssists());
//                spec.setDeaths(lifeTimeSpec.getDeaths() - season4Spec.getDeaths());
//                spec.setWins(lifeTimeSpec.getWins() - season4Spec.getWins());
//                spec.setLosses(lifeTimeSpec.getLosses() - season4Spec.getLosses());
//                spec.setPlays(lifeTimeSpec.getPlays() - season4Spec.getPlays());
//                spec.setFlagsCaptured(lifeTimeSpec.getFlagsCaptured() - season4Spec.getFlagsCaptured());
//                spec.setFlagsReturned(lifeTimeSpec.getFlagsReturned() - season4Spec.getFlagsReturned());
//                spec.setDamage(lifeTimeSpec.getDamage() - season4Spec.getDamage());
//                spec.setHealing(lifeTimeSpec.getHealing() - season4Spec.getHealing());
//                spec.setAbsorbed(lifeTimeSpec.getAbsorbed() - season4Spec.getAbsorbed());
////                spec.setWeapon(lifeTimeSpec.getWeapon());
//                spec.setExperience(lifeTimeSpec.getExperience() - season4Spec.getExperience());
//            }
//            aClass.setKills(lifeTimeClass.getKills() - season4Class.getKills());
//            aClass.setAssists(lifeTimeClass.getAssists() - season4Class.getAssists());
//            aClass.setDeaths(lifeTimeClass.getDeaths() - season4Class.getDeaths());
//            aClass.setWins(lifeTimeClass.getWins() - season4Class.getWins());
//            aClass.setLosses(lifeTimeClass.getLosses() - season4Class.getLosses());
//            aClass.setPlays(lifeTimeClass.getPlays() - season4Class.getPlays());
//            aClass.setFlagsCaptured(lifeTimeClass.getFlagsCaptured() - season4Class.getFlagsCaptured());
//            aClass.setFlagsReturned(lifeTimeClass.getFlagsReturned() - season4Class.getFlagsReturned());
//            aClass.setDamage(lifeTimeClass.getDamage() - season4Class.getDamage());
//            aClass.setHealing(lifeTimeClass.getHealing() - season4Class.getHealing());
//            aClass.setAbsorbed(lifeTimeClass.getAbsorbed() - season4Class.getAbsorbed());
//            aClass.setExperience(lifeTimeClass.getExperience() - season4Class.getExperience());
//        }
//    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("warlords.game.test")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }
        WarlordsPlayer warlordsPlayer = BaseCommand.requireWarlordsPlayer(sender);
        if (warlordsPlayer != null) {
//            System.out.println(!warlordsPlayer.getGameState().isForceEnd() && warlordsPlayer.getGameState().getStats(warlordsPlayer.getTeam()).points() > warlordsPlayer.getGameState().getStats(warlordsPlayer.getTeam().enemy()).points());
//            System.out.println(ExperienceManager.getExpFromGameStats(warlordsPlayer, true));

//            warlordsPlayer.sendMessage(WarlordsPlayer.RECEIVE_ARROW_RED);
//            warlordsPlayer.sendMessage(WarlordsPlayer.RECEIVE_ARROW_GREEN);
//            warlordsPlayer.sendMessage(WarlordsPlayer.GIVE_ARROW_GREEN);
//            warlordsPlayer.sendMessage(WarlordsPlayer.GIVE_ARROW_RED);

        }


        //SRCalculator.recalculateSR();

        Player player = (Player) sender;


//        DatabaseManager.warlordsDatabase.getCollection("Temp").find().forEach(document -> {
//            document.put("map", document.getString("map").toUpperCase());
//            if(document.get("map").equals("WARSONG REMASTERED")) {
//                document.put("map", "WARSONG");
//            }
//            if(document.getString("winner").equals("DRAW")) {
//                document.put("winner", null);
//            }
//            DatabaseGameCTF databaseGameCTF = DatabaseManager.gameService.convertDocumentToClass(document, DatabaseGameCTF.class);
//            databaseGameCTF.setExactDate(DatabaseGameBase.convertToDateFrom(document.getObjectId("_id").toString()));
//            databaseGameCTF.setGameMode(GameMode.CAPTURE_THE_FLAG);
//            if((boolean) document.getOrDefault("private", true)) {
//                databaseGameCTF.getGameAddons().add(GameAddon.PRIVATE_GAME);
//            }
//            databaseGameCTF.setStatInfo((String) document.getOrDefault("statInfo", ""));
//            DatabaseManager.gameService.create(databaseGameCTF);
//        });
//
//
//        int assassinWins = 0;
//        int assassinLosses = 0;
//        int vindicatorWins = 0;
//        int vindicatorLosses = 0;
//        int apothecaryWins = 0;
//        int apothecaryLosses = 0;
//        for (DatabasePlayer databasePlayer : DatabaseManager.playerService.findAll(PlayersCollections.SEASON_5)) {
//            assassinWins += databasePlayer.getRogue().getAssassin().getWins();
//            assassinLosses += databasePlayer.getRogue().getAssassin().getLosses();
//            vindicatorWins += databasePlayer.getRogue().getVindicator().getWins();
//            vindicatorLosses += databasePlayer.getRogue().getVindicator().getLosses();
//            apothecaryWins += databasePlayer.getRogue().getApothecary().getWins();
//            apothecaryLosses += databasePlayer.getRogue().getApothecary().getLosses();
//        }
//        System.out.println("Assassin Wins: " + assassinWins);
//        System.out.println("Assassin Losses: " + assassinLosses);
//        System.out.println(((double) assassinWins / (assassinWins + assassinLosses) * 10) / 10 + "%");
//        System.out.println("Vindicator Wins: " + vindicatorWins);
//        System.out.println("Vindicator Losses: " + vindicatorLosses);
//        System.out.println(((double) vindicatorWins / (vindicatorWins + vindicatorLosses) * 10) / 10 + "%");
//        System.out.println("Apothecary Wins: " + apothecaryWins);
//        System.out.println("Apothecary Losses: " + apothecaryLosses);
//        System.out.println(((double) apothecaryWins / (apothecaryWins + apothecaryLosses) * 10) / 10 + "%");

//        for (Map.Entry<UUID, WarlordsPlayer> uuidWarlordsPlayerEntry : Warlords.getPlayers().entrySet()) {
//            System.out.println(uuidWarlordsPlayerEntry.getValue().getName() + " - " + uuidWarlordsPlayerEntry.getValue().getEntity());
//        }
//
//        Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get().warlordsPlayers().forEach(warlordsPlayer1 -> {
//            System.out.println(warlordsPlayer1.getName());
//            System.out.println(warlordsPlayer1.getEntity().getVehicle() != null);
//        });

//        DatabaseManager.gameService.create(new DatabaseGameCTF(), GamesCollections.ALL);
//        DatabaseManager.gameService.create(new DatabaseGameTDM(), GamesCollections.ALL);
//        for (DatabaseGameBase databaseGameBase : DatabaseManager.gameService.findAll(GamesCollections.ALL)) {
//            System.out.println(databaseGameBase);
//        }

//        PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
//        System.out.println(ArmorManager.ArmorSets.getSelected(player.getUniqueId()));
//        System.out.println(ArmorManager.Helmets.getSelected(player.getUniqueId()));

//        ArmorManager.resetArmor(player, playerSettings.getSelectedClass(), playerSettings.getWantedTeam());

//        SRCalculator.totalValues.clear();
//        List<DatabasePlayer> databasePlayers = DatabaseManager.playerService.findAll(PlayersCollections.SEASON_5);
//        HashMap<DatabasePlayer, Integer> playerSR = new HashMap<>();
//        for (DatabasePlayer databasePlayer : databasePlayers) {
//            if (databasePlayer.getPlays() > 5) {
//                playerSR.put(databasePlayer, SRCalculator.getSR(databasePlayer));
//            }
//        }
//        playerSR.entrySet().stream()
//                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
//                .forEachOrdered(databasePlayerIntegerEntry -> System.out.println(databasePlayerIntegerEntry.getKey().getName() + " - " + databasePlayerIntegerEntry.getValue()));
//
//        System.out.println(playerSR.size());

//        SRCalculator.playersSR.entrySet().stream()
//                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
//                .forEachOrdered(databasePlayerIntegerEntry -> {
//                    System.out.println(databasePlayerIntegerEntry.getKey().getName() + " - " + databasePlayerIntegerEntry.getValue());
//                });

//        Warlords.newChain()
//                .asyncFirst(() -> DatabaseManager.playerService.findAll(PlayersCollections.SEASON_5))
//                .asyncLast(databasePlayerList -> {
//                    databasePlayerList.stream().filter(databasePlayer -> databasePlayer.getPlays() == 0).forEach(databasePlayer -> {
//                        //System.out.println(databasePlayer.getName());
//                        DatabaseManager.playerService.delete(databasePlayer, PlayersCollections.SEASON_5);
//                    });
//                })
//                .execute();


//        List<DatabasePlayer> databasePlayersLifeTime = DatabaseManager.playerService.findAll(PlayersCollections.SEASON_4);
//        for (DatabasePlayer databasePlayer : databasePlayersLifeTime) {
//            AbstractDatabaseStatInformation cryo = databasePlayer.getCompStats().getCtfStats().getMage().getCryomancer();
//            DatabaseBaseSpec databaseBaseSpec = databasePlayer.getCompStats().getMage().getCryomancer();
//            databaseBaseSpec.setKills(cryo.getKills());
//            databaseBaseSpec.setAssists(cryo.getAssists());
//            databaseBaseSpec.setDeaths(cryo.getDeaths());
//            databaseBaseSpec.setWins(cryo.getWins());
//            databaseBaseSpec.setLosses(cryo.getLosses());
//            databaseBaseSpec.setPlays(cryo.getPlays());
//            databaseBaseSpec.setDamage(cryo.getDamage());
//            databaseBaseSpec.setHealing(cryo.getHealing());
//            databaseBaseSpec.setAbsorbed(cryo.getAbsorbed());
//            databaseBaseSpec.setExperience(cryo.getExperience());
//
//            DatabaseManager.updatePlayerAsync(databasePlayer, PlayersCollections.SEASON_4);
//        }


//        List<DatabasePlayer> databasePlayersLifeTime = DatabaseManager.playerService.findAll(PlayersCollections.LIFETIME);
//        List<DatabasePlayer> databasePlayersS5 = DatabaseManager.playerService.findAll(PlayersCollections.SEASON_5);
//        List<DatabasePlayer> databasePlayersWeekly = DatabaseManager.playerService.findAll(PlayersCollections.WEEKLY);
//        List<DatabasePlayer> databasePlayersDaily = DatabaseManager.playerService.findAll(PlayersCollections.DAILY);
//
//        for (DatabasePlayer databasePlayer : databasePlayersDaily) {
//            DatabasePlayerCTF databasePlayerCTF = databasePlayer.getCtfStats();
//            DatabasePlayerPubStats pubStats = databasePlayer.getPubStats();
//            DatabasePlayerCTF ctfStats = pubStats.getCtfStats();
//            DatabaseBaseCTF damage = ctfStats.getShaman().getThunderlord();
//            DatabaseBaseCTF tank = ctfStats.getShaman().getSpiritguard();
//            DatabaseBaseCTF healer = ctfStats.getShaman().getEarthwarden();
//            long damageExp = getExp(damage, SpecType.DAMAGE, damage.getFlagsCaptured(), damage.getFlagsReturned());
//            long tankEXP = getExp(tank, SpecType.TANK, tank.getFlagsCaptured(), tank.getFlagsReturned());
//            long healerExp = getExp(healer, SpecType.HEALER, healer.getFlagsCaptured(), healer.getFlagsReturned());
//            long totalExp = damageExp + tankEXP + healerExp;
//            //general
//            databasePlayer.setExperience(databasePlayer.getExperience() + totalExp);
//            databasePlayer.getShaman().setExperience(databasePlayer.getShaman().getExperience() + totalExp);
//            databasePlayer.getShaman().getThunderlord().setExperience(databasePlayer.getShaman().getThunderlord().getExperience() + damageExp);
//            databasePlayer.getShaman().getSpiritguard().setExperience(databasePlayer.getShaman().getSpiritguard().getExperience() + tankEXP);
//            databasePlayer.getShaman().getEarthwarden().setExperience(databasePlayer.getShaman().getEarthwarden().getExperience() + healerExp);
//            //ctf
//            databasePlayerCTF.setExperience(databasePlayerCTF.getExperience() + totalExp);
//            databasePlayerCTF.getShaman().setExperience(databasePlayerCTF.getShaman().getExperience() + totalExp);
//            databasePlayerCTF.getShaman().getThunderlord().setExperience(databasePlayerCTF.getShaman().getThunderlord().getExperience() + damageExp);
//            databasePlayerCTF.getShaman().getSpiritguard().setExperience(databasePlayerCTF.getShaman().getSpiritguard().getExperience() + tankEXP);
//            databasePlayerCTF.getShaman().getEarthwarden().setExperience(databasePlayerCTF.getShaman().getEarthwarden().getExperience() + healerExp);
//            //pub
//            pubStats.setExperience(pubStats.getExperience() + totalExp);
//            pubStats.getShaman().setExperience(pubStats.getShaman().getExperience() + totalExp);
//            pubStats.getShaman().getThunderlord().setExperience(pubStats.getShaman().getThunderlord().getExperience() + damageExp);
//            pubStats.getShaman().getSpiritguard().setExperience(pubStats.getShaman().getSpiritguard().getExperience() + tankEXP);
//            pubStats.getShaman().getEarthwarden().setExperience(pubStats.getShaman().getEarthwarden().getExperience() + healerExp);
//            //pub ctf
//            ctfStats.setExperience(ctfStats.getExperience() + totalExp);
//            ctfStats.getShaman().setExperience(ctfStats.getShaman().getExperience() + totalExp);
//            ctfStats.getShaman().getThunderlord().setExperience(ctfStats.getShaman().getThunderlord().getExperience() + damageExp);
//            ctfStats.getShaman().getSpiritguard().setExperience(ctfStats.getShaman().getSpiritguard().getExperience() + tankEXP);
//            ctfStats.getShaman().getEarthwarden().setExperience(ctfStats.getShaman().getEarthwarden().getExperience() + healerExp);
//
//            DatabaseManager.updatePlayerAsync(databasePlayer, PlayersCollections.DAILY);
//            updateStats(
//                    databasePlayersLifeTime.stream().filter(db -> db.getUuid().equalsIgnoreCase(databasePlayer.getUuid())).findAny().get(),
//                    damageExp, tankEXP, healerExp, totalExp,
//                    PlayersCollections.LIFETIME
//            );
//            updateStats(
//                    databasePlayersS5.stream().filter(db -> db.getUuid().equalsIgnoreCase(databasePlayer.getUuid())).findAny().get(),
//                    damageExp, tankEXP, healerExp, totalExp,
//                    PlayersCollections.SEASON_5
//            );
//            updateStats(
//                    databasePlayersWeekly.stream().filter(db -> db.getUuid().equalsIgnoreCase(databasePlayer.getUuid())).findAny().get(),
//                    damageExp, tankEXP, healerExp, totalExp,
//                    PlayersCollections.WEEKLY
//            );
//
//        }

//        boolean s5 = false;
//        List<DatabaseGame> gameList = DatabaseManager.gameService.findAll().stream()
//                .filter(databaseGame -> databaseGame.isCounted() && databaseGame.isPrivate())
//                .collect(Collectors.toList());
//        List<DatabasePlayer> databasePlayers = DatabaseManager.playerService.findAll(PlayersCollections.TEMP2);
//        for (DatabaseGame databaseGame : gameList) {
//            if(databaseGame.getDate().startsWith("12/17") && !s5) {
//                s5 = true;
//                System.out.println("S5 DETECTED");
//            }
//            if(!s5) {
//                continue;
//            }
//            List<DatabaseGamePlayers.GamePlayer> gamePlayers = new ArrayList<>();
//            gamePlayers.addAll(databaseGame.getPlayers().getBlue());
//            gamePlayers.addAll(databaseGame.getPlayers().getRed());
//
//            int timePlayed = 900 - databaseGame.getTimeLeft();
//            for (DatabaseGamePlayers.GamePlayer gamePlayer : gamePlayers) {
//                databasePlayers.stream()
//                        .filter(databasePlayer -> databasePlayer.getUuid().equalsIgnoreCase(gamePlayer.getUuid()))
//                        .findFirst()
//                        .ifPresent(databasePlayer -> {
//                            int blocksTravelled = gamePlayer.getBlocksTravelled();
//                            int secondsInRespawn = gamePlayer.getSecondsInRespawn();
//                            //ctf_stats
//                            databasePlayer.getCtfStats().addTotalBlocksTravelled(blocksTravelled);
//                            if (databasePlayer.getCtfStats().getMostBlocksTravelled() < blocksTravelled) {
//                                databasePlayer.getCtfStats().setMostBlocksTravelled(blocksTravelled);
//                            }
//                            databasePlayer.getCtfStats().addTotalTimeInRespawn(secondsInRespawn);
//                            databasePlayer.getCtfStats().addTotalTimePlayed(timePlayed);
//                            //ctf_stats class
//                            DatabaseBaseCTF databaseBaseCTFClass = databasePlayer.getCtfStats().getClass(Classes.getClassesGroup(gamePlayer.getSpec()));
//                            databaseBaseCTFClass.addTotalBlocksTravelled(blocksTravelled);
//                            if (databaseBaseCTFClass.getMostBlocksTravelled() < blocksTravelled) {
//                                databaseBaseCTFClass.setMostBlocksTravelled(blocksTravelled);
//                            }
//                            databaseBaseCTFClass.addTotalTimeInRespawn(secondsInRespawn);
//                            databaseBaseCTFClass.addTotalTimePlayed(timePlayed);
//                            //ctf_stats class spec
//                            DatabaseBaseCTF databaseBaseCTFClassSpec = databasePlayer.getCtfStats().getSpec(gamePlayer.getSpec());
//                            databaseBaseCTFClassSpec.addTotalBlocksTravelled(blocksTravelled);
//                            if (databaseBaseCTFClassSpec.getMostBlocksTravelled() < blocksTravelled) {
//                                databaseBaseCTFClassSpec.setMostBlocksTravelled(blocksTravelled);
//                            }
//                            databaseBaseCTFClassSpec.addTotalTimeInRespawn(secondsInRespawn);
//                            databaseBaseCTFClassSpec.addTotalTimePlayed(timePlayed);
//                            //comp_stats ctf_stats
//                            databasePlayer.getCompStats().getCtfStats().addTotalBlocksTravelled(blocksTravelled);
//                            if (databasePlayer.getCompStats().getCtfStats().getMostBlocksTravelled() < blocksTravelled) {
//                                databasePlayer.getCompStats().getCtfStats().setMostBlocksTravelled(blocksTravelled);
//                            }
//                            databasePlayer.getCompStats().getCtfStats().addTotalTimeInRespawn(secondsInRespawn);
//                            databasePlayer.getCompStats().getCtfStats().addTotalTimePlayed(timePlayed);
//                            //comp_stats ctf_stats class
//                            DatabaseBaseCTF databaseBaseCTF2Class = databasePlayer.getCompStats().getCtfStats().getClass(Classes.getClassesGroup(gamePlayer.getSpec()));
//                            databaseBaseCTF2Class.addTotalBlocksTravelled(blocksTravelled);
//                            if (databaseBaseCTF2Class.getMostBlocksTravelled() < blocksTravelled) {
//                                databaseBaseCTF2Class.setMostBlocksTravelled(blocksTravelled);
//                            }
//                            databaseBaseCTF2Class.addTotalTimeInRespawn(secondsInRespawn);
//                            databaseBaseCTF2Class.addTotalTimePlayed(timePlayed);
//                            //comp_stats ctf_stats class spec
//                            DatabaseBaseCTF databaseBaseCTF2ClassSpec = databasePlayer.getCompStats().getCtfStats().getSpec(gamePlayer.getSpec());
//                            databaseBaseCTF2ClassSpec.addTotalBlocksTravelled(blocksTravelled);
//                            if (databaseBaseCTF2ClassSpec.getMostBlocksTravelled() < blocksTravelled) {
//                                databaseBaseCTF2ClassSpec.setMostBlocksTravelled(blocksTravelled);
//                            }
//                            databaseBaseCTF2ClassSpec.addTotalTimeInRespawn(secondsInRespawn);
//                            databaseBaseCTF2ClassSpec.addTotalTimePlayed(timePlayed);
//                        });
//            }
//        }
//        for (DatabasePlayer databasePlayer : databasePlayers) {
//            DatabaseManager.updatePlayerAsync(databasePlayer, PlayersCollections.TEMP2);
//        }
//
        sender.sendMessage(ChatColor.GREEN + "DID THE THING");
        return true;
    }

    private void updateStats(DatabasePlayer databasePlayer, long damageExp, long tankEXP, long healerExp, long totalExp, PlayersCollections playersCollections) {
//        DatabasePlayerCTF databasePlayerCTF = databasePlayer.getCtfStats();
//        DatabasePlayerPubStats pubStats = databasePlayer.getPubStats();
//        DatabasePlayerCTF ctfStats = pubStats.getCtfStats();
//
//        //general
//        databasePlayer.setExperience(databasePlayer.getExperience() + totalExp);
//        databasePlayer.getShaman().setExperience(databasePlayer.getShaman().getExperience() + totalExp);
//        databasePlayer.getShaman().getThunderlord().setExperience(databasePlayer.getShaman().getThunderlord().getExperience() + damageExp);
//        databasePlayer.getShaman().getSpiritguard().setExperience(databasePlayer.getShaman().getSpiritguard().getExperience() + tankEXP);
//        databasePlayer.getShaman().getEarthwarden().setExperience(databasePlayer.getShaman().getEarthwarden().getExperience() + healerExp);
//        //ctf
//        databasePlayerCTF.setExperience(databasePlayerCTF.getExperience() + totalExp);
//        databasePlayerCTF.getShaman().setExperience(databasePlayerCTF.getShaman().getExperience() + totalExp);
//        databasePlayerCTF.getShaman().getThunderlord().setExperience(databasePlayerCTF.getShaman().getThunderlord().getExperience() + damageExp);
//        databasePlayerCTF.getShaman().getSpiritguard().setExperience(databasePlayerCTF.getShaman().getSpiritguard().getExperience() + tankEXP);
//        databasePlayerCTF.getShaman().getEarthwarden().setExperience(databasePlayerCTF.getShaman().getEarthwarden().getExperience() + healerExp);
//        //pub
//        pubStats.setExperience(pubStats.getExperience() + totalExp);
//        pubStats.getShaman().setExperience(pubStats.getShaman().getExperience() + totalExp);
//        pubStats.getShaman().getThunderlord().setExperience(pubStats.getShaman().getThunderlord().getExperience() + damageExp);
//        pubStats.getShaman().getSpiritguard().setExperience(pubStats.getShaman().getSpiritguard().getExperience() + tankEXP);
//        pubStats.getShaman().getEarthwarden().setExperience(pubStats.getShaman().getEarthwarden().getExperience() + healerExp);
//        //pub ctf
//        ctfStats.setExperience(ctfStats.getExperience() + totalExp);
//        ctfStats.getShaman().setExperience(ctfStats.getShaman().getExperience() + totalExp);
//        ctfStats.getShaman().getThunderlord().setExperience(ctfStats.getShaman().getThunderlord().getExperience() + damageExp);
//        ctfStats.getShaman().getSpiritguard().setExperience(ctfStats.getShaman().getSpiritguard().getExperience() + tankEXP);
//        ctfStats.getShaman().getEarthwarden().setExperience(ctfStats.getShaman().getEarthwarden().getExperience() + healerExp);

        DatabaseManager.updatePlayerAsync(databasePlayer, playersCollections);
    }

    private long getExp(AbstractDatabaseStatInformation information, SpecType specType, long caps, long rets) {
        double damageMultiplier;
        double healingMultiplier;
        double absorbedMultiplier;
        if (specType == SpecType.DAMAGE) {
            damageMultiplier = .80;
            healingMultiplier = .10;
            absorbedMultiplier = .10;
        } else if (specType == SpecType.HEALER) {
            damageMultiplier = .275;
            healingMultiplier = .65;
            absorbedMultiplier = .75;
        } else { //tank
            damageMultiplier = .575;
            healingMultiplier = .1;
            absorbedMultiplier = .325;
        }
        double calculatedDHP = information.getDamage() * damageMultiplier + information.getHealing() * healingMultiplier + information.getAbsorbed() * absorbedMultiplier;

        long exp = 0;
        exp += information.getWins() * 50L + information.getLosses() * 25L;
        exp += (information.getKills() + information.getAssists()) * 5L;
        exp += calculatedDHP / 5000L;
        exp += caps * 15L;
        exp += rets * 5L;
        return exp;
    }

    public void register(Warlords instance) {
        instance.getCommand("test").setExecutor(this);
        //instance.getCommand("class").setTabCompleter(this);
    }
}
