//package com.ebicep.warlords.database;
//
//import ch.qos.logback.classic.Level;
//import ch.qos.logback.classic.Logger;
//import com.github.benmanes.caffeine.cache.Cache;
//import org.slf4j.LoggerFactory;
//import org.springframework.cache.caffeine.CaffeineCache;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//import org.springframework.context.support.AbstractApplicationContext;
//
//import java.util.UUID;
//
//
//public class Test {
//
//    private static final AbstractApplicationContext context = new AnnotationConfigApplicationContext(com.ebicep.warlords.database.ApplicationConfiguration.class);
//    private static final com.ebicep.warlords.database.PlayerService playerService = context.getBean("playerService", com.ebicep.warlords.database.PlayerService.class);
//    private static final com.ebicep.warlords.database.GameService gameService = context.getBean("gameService", com.ebicep.warlords.database.GameService.class);
//
//    public static void main(String[] args) {
////        playerServicae.deleteAll();
////
//        //ADDING PLAYERS
////        Player heatran = new Player("Heatran", "4","Cryomancer");
////        playerService.create(heatran, "Players_Information");
////        Player chessking = new Player("Chessking345", "6","Defender");
////        playerService.create(chessking, "Players_Information");
////        Player sdumb = new Player("sdrawk", "8","Aquamancer");
////        playerService.create(sdumb, "Players_Information");
//        com.ebicep.warlords.database.Player player = playerService.findByUUID(UUID.fromString("8bb6dd7f-b3de-43ec-af7e-1be7975e7bca"));
//        System.out.println(player.getWins());
//        printCache();
//        player.setWins(10);
//        playerService.save(player, com.ebicep.warlords.database.PlayersCollections.ALL_TIME);
//        printCache();
//
//
////        playerService.find(new Query().addCriteria(Criteria.where("name").is("Heatran")), PlayersCollections.WEEKLY);
//
//
//
////        System.out.println(playerService.getPlayersSortedByPlays());
////        List<GamePlayers.GamePlayer> blue = Arrays.asList(new GamePlayers.GamePlayer("sumSmash"), new GamePlayers.GamePlayer("joe"));
////        List<GamePlayers.GamePlayer> red = Arrays.asList(new GamePlayers.GamePlayer("Heatran"), new GamePlayers.GamePlayer("mama"));
////        Game game = new Game("09/23/2021 14:59", "Crossfire", 97, "BLUE", 1000, 840, new GamePlayers(blue, red), "", false);
////        gameService.create(game);
//
////        long startTime = System.nanoTime();
////        Player players = playerService.findOne(Criteria.where("name").is("Heatran"), PlayersCollections.ALL_TIME);
////        System.out.println(players);
////        long endTime = System.nanoTime();
////        System.out.println((endTime - startTime) / 1000000);
//
////        Player player = playerService.findByName("Heatran");
////        System.out.println("Find One - " + player);
////
////        player.setLastSpec("Pyromancer");
////        Player player = playerService.findByName("Heatran");
////        playerService.update(player);
//
////        printCache();
////
////        startTime = System.nanoTime();
////        Player players2 = playerService.findOne(Criteria.where("name").is("Heatran"), PlayersCollections.ALL_TIME);
////        System.out.println(players2);
////        endTime = System.nanoTime();
////        System.out.println((endTime - startTime) / 1000000);
////
////        printCache();
////
////        startTime = System.nanoTime();
////        Player players3 = playerService.findOne(Criteria.where("name").is("Chessking345"), PlayersCollections.ALL_TIME);
////        System.out.println(players3);
////        endTime = System.nanoTime();
////        System.out.println((endTime - startTime) / 1000000);
////
////        printCache();
////
////        startTime = System.nanoTime();
////        Player players4 = playerService.findOne(Criteria.where("name").is("Heatran"), PlayersCollections.ALL_TIME);
////        System.out.println(players3);
////        endTime = System.nanoTime();
////        System.out.println((endTime - startTime) / 1000000);
//
////
////        startTime = System.nanoTime();
////        System.out.println(cachedPlayers.get("2").getName());
////        endTime = System.nanoTime();
////        System.out.println((endTime - startTime) / 1000000);
////        System.out.println("Find One - " + newPlayer);
//
//
//
//    }
//
//    private static void printCache() {
//        Cache<Object, Object> cache = ((CaffeineCache) com.ebicep.warlords.database.MultipleCacheResolver.playersCacheManager.getCache(com.ebicep.warlords.database.PlayersCollections.ALL_TIME.cacheName)).getNativeCache();
//        System.out.println("CACHE - " + cache.asMap());
//    }
//
//}
