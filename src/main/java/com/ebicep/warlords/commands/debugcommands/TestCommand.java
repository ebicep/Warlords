package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.database.cache.MultipleCacheResolver;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.TextCooldown;
import com.github.benmanes.caffeine.cache.Cache;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
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
            System.out.println(!warlordsPlayer.getGameState().isForceEnd() && warlordsPlayer.getGameState().getStats(warlordsPlayer.getTeam()).points() > warlordsPlayer.getGameState().getStats(warlordsPlayer.getTeam().enemy()).points());
//            System.out.println(ExperienceManager.getExpFromGameStats(warlordsPlayer, true));
        }

        Player player = (Player) sender;

        TextCooldown textCooldown = new TextCooldown("TEST", "TRAP", TestCommand.class, null, warlordsPlayer, CooldownTypes.ABILITY, cooldownManager -> {
        }, "5");
        warlordsPlayer.getCooldownManager().addCooldown(textCooldown);
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                switch (counter++) {
                    case 1:
                        textCooldown.setText("4");
                        break;
                    case 2:
                        textCooldown.setText("3");
                        break;
                    case 3:
                        textCooldown.setText("2");
                        break;
                    case 4:
                        textCooldown.setText("1");
                        break;
                    case 5:
                        textCooldown.setText("READY");
                        break;
                    case 10:
                        textCooldown.setRemove(true);
                        break;
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 20);

        //new CooldownFilter<>(warlordsPlayer.getCooldownManager().getAbilityCooldowns(), RegularCooldown.class).findFirst().get().get

//        warlordsPlayer.getCooldownManager().getAbilityCooldowns().stream()
//                .filter(RegularCooldown.class::isInstance)
//                .map(RegularCooldown.class::cast)
//                .forEach(regularCooldown -> {
//                    regularCooldown.
//                });

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

    public void register(Warlords instance) {
        instance.getCommand("test").setExecutor(this);
        //instance.getCommand("class").setTabCompleter(this);
    }
}
