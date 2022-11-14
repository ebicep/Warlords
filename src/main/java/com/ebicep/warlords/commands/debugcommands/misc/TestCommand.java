package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.cache.MultipleCacheResolver;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairEntry;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.github.benmanes.caffeine.cache.Cache;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.springframework.cache.caffeine.CaffeineCache;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CommandAlias("test")
@CommandPermission("warlords.game.test")
public class TestCommand extends BaseCommand {

    @Default
    @Description("Universal test command")
    public void test(CommandIssuer issuer) {
        doTest(issuer);
        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Test executed", true);
    }

    public static void doTest(CommandIssuer issuer) {
        System.out.println("--------------");
        long start = System.nanoTime();
        System.out.println(DatabaseManager.playerService.findByUUID(issuer.getUniqueId()));
        System.out.println("Time: " + (System.nanoTime() - start) / 1000000 + "ms");
        printCache();
        System.out.println("--------------");
    }

    public static boolean inCache(UUID uuid, PlayersCollections collection) {
        return ((CaffeineCache) MultipleCacheResolver.playersCacheManager.getCache(collection.cacheName)).getNativeCache().asMap().containsKey(uuid);
    }

    public static void printCache() {
        for (PlayersCollections value : PlayersCollections.ACTIVE_COLLECTIONS) {
            System.out.println(value.name);
            Cache<Object, Object> cache = ((CaffeineCache) MultipleCacheResolver.playersCacheManager.getCache(value.cacheName)).getNativeCache();

            System.out.println("CACHE - " + cache.asMap());
            System.out.println(cache.stats());
        }

    }

    @CommandAlias("testdatabase")
    @Description("Database test command")
    public void testDatabase(CommandIssuer issuer) {

        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.playerService.findAll(PlayersCollections.LIFETIME))
                .asyncLast(databasePlayers -> {
                    for (DatabasePlayer databasePlayer : databasePlayers) {
                        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
//            pveStats.getPlayerCountStats().forEach((integer, databasePlayerPvEPlayerCountStats) -> {
//                databasePlayerPvEPlayerCountStats.merge(pveStats.getEasyStats().getPlayerCountStats().get(integer));
//                databasePlayerPvEPlayerCountStats.merge(pveStats.getNormalStats().getPlayerCountStats().get(integer));
//                databasePlayerPvEPlayerCountStats.merge(pveStats.getHardStats().getPlayerCountStats().get(integer));
//                databasePlayerPvEPlayerCountStats.merge(pveStats.getEndlessStats().getPlayerCountStats().get(integer));
//            });
                        for (WeaponsPvE value : WeaponsPvE.VALUES) {
                            if (value.getPlayerEntries == null) {
                                continue;
                            }
                            List<MasterworksFairEntry> entries = pveStats.getMasterworksFairEntries()
                                    .stream()
                                    .filter(masterworksFairEntry -> masterworksFairEntry.getFairNumber() == 2)
                                    .filter(masterworksFairEntry -> masterworksFairEntry.getRarity() == value)
                                    .collect(Collectors.toList());
                            if (entries.size() > 1) {
                                pveStats.getMasterworksFairEntries().remove(entries.get(1));
                                System.out.println("Removed duplicate for " + databasePlayer.getName());
                            }
                        }
                        databasePlayer.getAchievements()
                                .removeIf(abstractAchievementRecord -> abstractAchievementRecord instanceof ChallengeAchievements.ChallengeAchievementRecord && abstractAchievementRecord.getAchievement() == ChallengeAchievements.SERIAL_KILLER || abstractAchievementRecord.getAchievement() == ChallengeAchievements.LIFELEECHER);
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    }
                }).execute();

        //DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(issuer.getUniqueId());


//        List<DatabasePlayer> all = DatabaseManager.playerService.findAll(PlayersCollections.LIFETIME);
//        for (DatabasePlayer databasePlayer : all) {
//            for (AbstractWeapon weapon : databasePlayer.getPveStats().getWeaponInventory()) {
//                if(weapon instanceof Upgradeable) {
//                    System.out.println(databasePlayer.getName());
//                    System.out.println(weapon.getUUID());
//                    System.out.println(((Upgradeable) weapon).getUpgradeLevel());
//                }
//            }
//        }
//
//        List<DatabasePlayer> databasePlayers = DatabaseManager.playerService.findAll(PlayersCollections.TEMP);
//        for (DatabasePlayer databasePlayer : databasePlayers) {
//            if (databasePlayer.getPveStats().getPlays() > 0) {
//                DatabasePlayer dp = new DatabasePlayer(databasePlayer.getUuid(), databasePlayer.getName());
//                DatabaseManager.playerService.create(dp, PlayersCollections.TEMP2);
//            }
//        }

//        List<DatabasePlayer> temp = DatabaseManager.playerService.findAll(PlayersCollections.TEMP);
//        for (DatabasePlayer databasePlayer : temp) {
//            for (DatabaseBaseGeneral aClass : databasePlayer.getClasses()) {
//                ArmorManager.ArmorSets armor = aClass.getArmor();
//                switch (armor) {
//                    case SIMPLE_CHESTPLATE_MAGE:
//                    case SIMPLE_CHESTPLATE_WARRIOR:
//                    case SIMPLE_CHESTPLATE_PALADIN:
//                    case SIMPLE_CHESTPLATE_SHAMAN:
//                    case SIMPLE_CHESTPLATE_ROGUE:
//                        aClass.setArmor(ArmorManager.ArmorSets.SIMPLE_CHESTPLATE);
//                        break;
//                    case GREATER_CHESTPLATE_MAGE:
//                    case GREATER_CHESTPLATE_WARRIOR:
//                    case GREATER_CHESTPLATE_PALADIN:
//                    case GREATER_CHESTPLATE_SHAMAN:
//                    case GREATER_CHESTPLATE_ROGUE:
//                        aClass.setArmor(ArmorManager.ArmorSets.GREATER_CHESTPLATE);
//                        break;
//                    default:
//                        aClass.setArmor(ArmorManager.ArmorSets.MASTERWORK_CHESTPLATE);
//                        break;
//                }
//            }
//            DatabaseManager.playerService.update(databasePlayer, PlayersCollections.TEMP2);
//        }

//        Warlords.newChain()
//                .asyncFirst(() -> DatabaseManager.gameService.findAll(GamesCollections.TEMP2))
//                .asyncLast((games) -> {
//                    int counter = 0;
//                    for (DatabaseGameBase game : games) {
////                        if(game instanceof DatabaseGameCTF) {
////                            DatabaseGameCTF databaseGameCTF = (DatabaseGameCTF) game;
////                            databaseGameCTF.getTeamPlayers().put(Team.BLUE, databaseGameCTF.getPlayers().getBlue());
////                            databaseGameCTF.getTeamPlayers().put(Team.RED, databaseGameCTF.getPlayers().getRed());
////                        }
////                        DatabaseManager.gameService.delete(game, GamesCollections.TEMP);
//                        DatabaseManager.gameService.save(game, GamesCollections.TEMP2);
//                        System.out.println(counter++);
//                    }
//                }).execute();

//        Warlords.newChain()
//                        .async(() -> {
//                            DatabaseManager.gameService.updateMany(new Query(), new Update().rename("team_players", "players"), DatabaseGameCTF.class, GamesCollections.TEMP2);
//                        }).execute();


        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Database Test executed", true);
    }

    @CommandAlias("testgame")
    @Description("In game test command")
    public void testGame(WarlordsPlayer warlordsPlayer) {
        for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
            ability.updateDescription((Player) warlordsPlayer.getEntity());
        }
        ChatChannels.sendDebugMessage(warlordsPlayer, ChatColor.GREEN + "In Game Test executed", true);
    }

    @CommandAlias("testplayer")
    public void testPlayer(Player player) {
        ChallengeAchievements.LAWNMOWER.sendAchievementUnlockMessage(player);
//        CustomScoreboard playerScoreboard = CustomScoreboard.getPlayerScoreboard(player);
//        Scoreboard scoreboard = playerScoreboard.getScoreboard();
//        System.out.println(scoreboard.getTeams());
//        //scoreboard.getTeam("sumSmash").unregister();
//        Team team = scoreboard.registerNewTeam("sumSmash");
//        team.setPrefix("1");
//        team.addEntry("sumSmash");
//        team.setSuffix("2");
//        Instant now = Instant.now();
//        System.out.println(now);
//        for (PlayersCollections activeCollection : PlayersCollections.ACTIVE_COLLECTIONS) {
//            System.out.println(activeCollection.name);
//            System.out.println(activeCollection.shouldUpdate(now));
//        }
//        System.out.println(DateUtil.getResetDateToday());
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }
}

/*


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
        WarlordsEntity warlordsPlayer = BaseCommand.requireWarlordsPlayer(sender);
        if (warlordsPlayer != null) {
//            System.out.println(!warlordsPlayer.getGameState().isForceEnd() && warlordsPlayer.getGameState().getStats(warlordsPlayer.getTeam()).points() > warlordsPlayer.getGameState().getStats(warlordsPlayer.getTeam().enemy()).points());
//            System.out.println(ExperienceManager.getExpFromGameStats(warlordsPlayer, true));

//            warlordsPlayer.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED);
//            warlordsPlayer.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN);
//            warlordsPlayer.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN);
//            warlordsPlayer.sendMessage(WarlordsEntity.GIVE_ARROW_RED);

//            ((WarlordsPlayer) warlordsPlayer).getAbilityTree().openAbilityTree();
//            warlordsPlayer.addCurrency(10000000);

        }


        //MasterworksFairManager.awardEntriesThroughRewardInventory(MasterworksFairManager.currentFair);

//        testWeaponScore((Player) sender);

        //AbstractWeapon.giveTestItem((Player) sender);


//        for (int i = 0; i < 20; i++) {
//            AbstractWeapon.giveTestItem((Player) sender);
//        }

//        DatabaseManager.masterworksFairService.create(new MasterworksFair());

//       // QueueManager.sendNewQueue();
//        BotManager.getCompGamesServer().upsertCommand("queue", "Join, Leave, or Refresh the queue")
//                .addSubcommands(new SubcommandData("refresh", "Refresh the queue"))
//                .addSubcommands(new SubcommandData("join", "Join the queue")
//                        .addOption(OptionType.STRING, "time", "Future time in EST PM format (e.g. '4:15')", false)
//                )
//                .addSubcommands(new SubcommandData("leave", "Leave the queue"))
//                .queue();
//        BotManager.getCompGamesServer().retrieveCommands().queue(commands ->
//            for (net.dv8tion.jda.api.interactions.commands.Command command1 : commands) {
//                System.out.println(command1.getName() + " - " + command1.getId());
//            }
//        });

        //SRCalculator.recalculateSR();

        Player player = (Player) sender;

        long start = System.nanoTime();

//        player.getInventory().addItem(playerSkull);
//        player.getInventory().addItem(HeadUtils.getHead(UUID.fromString("e57c1a67-c163-45c6-ad47-91e903f7af51")));

//        OfflinePlayer offlinePlayer1 = Bukkit.getOfflinePlayer(UUID.fromString("b2e53e60-61c8-4298-a9bd-f400818075ec"));
//        System.out.println(offlinePlayer1.getName());
        long end = System.nanoTime();
        System.out.println((end - start) / 1000000 + "ms");
//        long start2 = System.nanoTime();
//        OfflinePlayer offlinePlayer2 = Bukkit.getOfflinePlayer(UUID.fromString("b2e53e60-61c8-4298-a9bd-f400818075ec"));
//        System.out.println(offlinePlayer2.getName());
//        long end2 = System.nanoTime();
//        System.out.println((end2 - start2) / 1000000 + "ms");

//        Set<String> testSet = new HashSet<>();
//        testSet.add("test1");
//        testSet.add("test2");
        //DatabaseManager.mongoClient.getDatabase("Warlords").getCollection("Test").insertOne(new Document("test", UUID.randomUUID()));
        // DatabaseManager.guildService.create(new Guild(player, "test"));
        //Guild test = DatabaseManager.guildService.findByName("fuck");
//       // DatabaseManager.guildService.delete(test);
        //   DatabaseManager.guildService.update(test);

        //  DatabaseManager.guildService.update(GuildManager.GUILDS.get(0));

//        PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
//        System.out.println(playerSettings.getSelectedSpec());
//        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
//        System.out.println(databasePlayer.getLastSpec());

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

//        for (Map.Entry<UUID, WarlordsEntity> uuidWarlordsPlayerEntry : Warlords.getPlayers().entrySet()) {
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

        DatabaseManager.queueUpdatePlayerAsync(databasePlayer, playersCollections);
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

 */
