package com.ebicep.warlords.commands.debugcommands.misc;

import com.ebicep.warlords.pve.items.ItemTier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OldTestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (!player.isOp()) {
                return true;
            }
        }

        if (commandSender instanceof Player) {
//            DatabaseManager.getPlayer(((Player) commandSender).getUniqueId(), databasePlayer -> {
//                for (Currencies value : Currencies.VALUES) {
//                    System.out.println(value.name + ": " + databasePlayer.getPveStats().getCurrencyValue(value));
//                }
//            });
        }

        for (ItemTier tier : ItemTier.VALID_VALUES) {
            System.out.println("Tier: " + tier.name);
            System.out.println("Bottom to Mid Increment: " + getBottomToMidIncrement(tier.weightRange, tier.weightRange.getNormal()));
            System.out.println("Mid to Top Increment: " + getMidToTopIncrement(tier.weightRange, tier.weightRange.getNormal()));
            //int i = 89;
            for (int i = 0; i < 100; i += 5) {
                System.out.println(tier.name + " - " + i + " = " + getWeight(i, tier));
            }
            //break;
        }

//
//        for (GameManager.GameHolder game : Warlords.getGameManager().getGames()) {
//            System.out.println(game.getMap() + " - " + game.getGame());
//        }

//        Warlords.newChain()
//                .async(() -> {
//                    DatabaseManager.playerService.updateMany(new Query(),
//                            new Update().rename("pve_stats.event_statss", "pve_stats.event_stats"),
//                            DatabasePlayer.class,
//                            PlayersCollections.LIFETIME
//                    );
//                    DatabaseManager.playerService.updateMany(new Query(),
//                            new Update().rename("pve_stats.event_statss", "pve_stats.event_stats"),
//                            DatabasePlayer.class,
//                            PlayersCollections.SEASON_7
//                    );
//                    DatabaseManager.playerService.updateMany(new Query(),
//                            new Update().rename("pve_stats.event_statss", "pve_stats.event_stats"),
//                            DatabasePlayer.class,
//                            PlayersCollections.WEEKLY
//                    );
//                    DatabaseManager.playerService.updateMany(new Query(),
//                            new Update().rename("pve_stats.event_statss", "pve_stats.event_stats"),
//                            DatabasePlayer.class,
//                            PlayersCollections.DAILY
//                    );
//                }).execute();

//        Warlords.newChain()
//                .async(() -> {
//                    DatabaseManager.playerService.findAll()
//                    for (PlayersCollections activeCollection : PlayersCollections.ACTIVE_COLLECTIONS) {
//                        System.out.println("-----------------------------------------");
//                        System.out.println("Updating " + activeCollection.name);
//                        List<DatabasePlayer> databasePlayers = DatabaseManager.playerService.findAll(activeCollection);
//                        for (DatabasePlayer databasePlayer : databasePlayers) {
//                            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
//                            DatabasePlayerPvEEventStats eventStats = pveStats.getEventStats();
//                            if(eventStats.getPlays() == 0) {
//                                continue;
//                            }
//                            DatabasePlayerPvEEventBoltaroStats boltaroStats = eventStats.getBoltaroStats();
//                            DatabasePlayerPvEEventBoltaroDifficultyStats event = boltaroStats.getEvent(1672131600);
//                            boltaroStats.setLairStats(event.getLairStats());
//                            boltaroStats.setBonanzaStats(event.getBonanzaStats());
//
//                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer, activeCollection);
//                        }
//                        System.out.println("-----------------------------------------");
//                    }
//                }).execute();

//        for (Guild guild : GuildManager.GUILDS) {
//            Map<Long, Long> map = guild.getEventStats().get(GameEvents.BOLTARO);
//            if(map == null) {
//                continue;
//            }
//            System.out.println(guild.getName() + " - " + map.getOrDefault(1672131600L,0L));
//            long total = 0;
//            for (GuildPlayer player : guild.getPlayers()) {
//                Map<Long, Long> longMap = player.getEventStats().get(GameEvents.BOLTARO);
//                if(longMap == null) {
//                    continue;
//                }
//                System.out.println("  " + player.getName() + " - " + longMap.getOrDefault(1672131600L, 0L));
//                DatabasePlayer databasePlayer = DatabaseManager.CACHED_PLAYERS.get(PlayersCollections.LIFETIME).get(player.getUUID());
//                long cumulative = databasePlayer.getPveStats().getEventStats().getEventPointsCumulative();
//                longMap.put(1672131600L, cumulative);
//                System.out.println("  " + player.getName() + " - " + longMap.get(1672131600L));
//                total += cumulative;
//            }
//            map.put(1672131600L, total);
//            guild.queueUpdate();
//            System.out.println(guild.getName() + " - " + map.get(1672131600L));
//        }


//        Quests quest = Quests.DAILY_300_KA;
//        ChatUtils.sendCenteredMessageWithEvents(player, new ComponentBuilder()
//                .appendHoverText(ChatColor.GREEN + quest.name, quest.getHoverText())
//                .create()
//        );
//
//        QuestsMenu.openQuestMenu(player);

        //      MasterworksFairManager.currentFair.setStartDate(Instant.now().minus(7, ChronoUnit.DAYS).plus(1, ChronoUnit.MINUTES));
        //System.out.println(Instant.now().minus(7, ChronoUnit.DAYS).minus(2, ChronoUnit.MINUTES));


        return true;
    }

    private static double getWeight(float itemScore, ItemTier tier) {
        ItemTier.WeightRange weightRange = tier.weightRange;
        if (itemScore <= 10) {
            return weightRange.getMax();
        }
        if (45 <= itemScore && itemScore <= 55) {
            return weightRange.getNormal();
        }
        if (itemScore >= 90) {
            return weightRange.getMin();
        }
        int weight = weightRange.getMax();
        double midWeight = weightRange.getNormal();
        // 10-mid
        double bottomToMidIncrement = getBottomToMidIncrement(weightRange, midWeight);
        // 10-mid
        double midToTopIncrement = getMidToTopIncrement(weightRange, midWeight);
        for (double weightCheck = 10; weightCheck < 45; weightCheck += midToTopIncrement) {
            weight--;
//            System.out.println("Weight: " + weight);
//            System.out.println("WeightCheck: " + weightCheck + " - " + (weightCheck + midToTopIncrement));
            if (weightCheck <= itemScore && itemScore < weightCheck + midToTopIncrement) {
                return weight;
            }
            if (weight < 0) {
                return 100;
            }
        }
        weight--;
        for (double weightCheck = 55; weightCheck < 90; weightCheck += bottomToMidIncrement) {
            weight--;
//            System.out.println("Weight: " + weight);
//            System.out.println("WeightCheck: " + weightCheck + " - " + (weightCheck + bottomToMidIncrement));
            if (weightCheck <= itemScore && itemScore < weightCheck + bottomToMidIncrement) {
                return weight;
            }
            if (weight < 0) {
                return 100;
            }
        }
        return 100;
    }

    private static double getBottomToMidIncrement(ItemTier.WeightRange weightRange, double midWeight) {
//        System.out.println("35 / (" + midWeight + " - " + weightRange.getMin() + " - 1)");
//        System.out.println("35 / " + (midWeight - weightRange.getMin() - 1));
        return 35d / (midWeight - weightRange.getMin() - 1);
    }

    private static double getMidToTopIncrement(ItemTier.WeightRange weightRange, double midWeight) {
//        System.out.println("35 / (" + weightRange.getMax() + " - " + midWeight + " - 1)");
//        System.out.println("35 / " + (weightRange.getMax() - midWeight - 1));
        return 35d / (weightRange.getMax() - midWeight - 1);
    }


}
