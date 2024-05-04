package com.ebicep.warlords.database.repositories.events.pojos;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.events.EventsLeaderboardManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.FutureMessage;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.events.player.DatabasePlayerFirstLoadEvent;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.logs.types.general.GuildLogGameEventReward;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Document(collection = "Game_Events")
public class DatabaseGameEvent implements Listener {

    public static final HashMap<GameEvents, DatabaseGameEvent> PREVIOUS_GAME_EVENTS = new HashMap<>();
    public static final HashMap<GameEvents, List<Long>> ALL_GAME_EVENT_TIMES = new HashMap<>();
    public static DatabaseGameEvent currentGameEvent = null;
    private static BukkitTask eventChecker = null;

    public static boolean eventIsActive() {
        return currentGameEvent != null && currentGameEvent.isActive();
    }

    public static void sendGameEventMessage(Player player, String message) {
        player.sendMessage(Component.text("Game Events", NamedTextColor.GOLD)
                                    .append(Component.text(" > " + message, NamedTextColor.DARK_GRAY)));
    }

    public static void startGameEvent() {
        long start = System.nanoTime();
        ChatUtils.MessageType.GAME_EVENTS.sendMessage("Scanning for game events...");
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.gameEventsService.findAll())
                .syncLast(gameEvents -> {
                    Instant now = Instant.now();
                    for (DatabaseGameEvent gameEvent : gameEvents) {
                        ALL_GAME_EVENT_TIMES.computeIfAbsent(gameEvent.getEvent(), k -> new ArrayList<>()).add(gameEvent.getStartDateSecond());
                        if (gameEvent.getStartDate().isAfter(now)) {
                            continue;
                        }
                        if (gameEvent.getEndDate().isBefore(now)) {
                            PREVIOUS_GAME_EVENTS.put(gameEvent.getEvent(), gameEvent);
                        } else if (gameEvent.isActive()) {
                            ChatUtils.MessageType.GAME_EVENTS.sendMessage("Found active game event: " + gameEvent.getEvent().name + " in " + (System.nanoTime() - start) / 1000000 + "ms");
                            ChatUtils.MessageType.GAME_EVENTS.sendMessage("Start: " + gameEvent.getStartDate());
                            ChatUtils.MessageType.GAME_EVENTS.sendMessage("End: " + gameEvent.getEndDate());
                            currentGameEvent = gameEvent;
                            if (!gameEvent.getStarted()) {
                                ChatUtils.MessageType.GAME_EVENTS.sendMessage("New Event Detected! Starting...");
                                gameEvent.setStarted(true);
                                Warlords.newChain().async(() -> DatabaseManager.gameEventsService.update(gameEvent)).execute();
                            }
                            gameEvent.start();
                            if (eventChecker != null) {
                                ChatUtils.MessageType.GAME_EVENTS.sendMessage("Cancelling event checker...");
                                eventChecker.cancel();
                            }
                            break;
                        }
                    }
                    if (currentGameEvent == null && !PREVIOUS_GAME_EVENTS.isEmpty()) {
                        DatabaseGameEvent gameEvent = PREVIOUS_GAME_EVENTS
                                .values()
                                .stream()
                                .min((o1, o2) -> o2.getEndDate().compareTo(o1.getEndDate()))
                                .get();
                        ChatUtils.MessageType.GAME_EVENTS.sendMessage("Days from last game event: " + gameEvent.getEndDate().until(Instant.now(), ChronoUnit.DAYS));
                        if (gameEvent.getEndDate().isAfter(Instant.now().minus(7, ChronoUnit.DAYS))) {
                            currentGameEvent = gameEvent;
                            currentGameEvent.start();
                            if (eventChecker != null) {
                                ChatUtils.MessageType.GAME_EVENTS.sendMessage("Cancelling event checker... but this shouldnt happen");
                                eventChecker.cancel();
                            }
                        } else {
                            ChatUtils.MessageType.GAME_EVENTS.sendMessage("Last game event was over 7 days ago, not starting");
                            if (eventChecker == null) {
                                ChatUtils.MessageType.GAME_EVENTS.sendMessage("Starting event checker...");
                                eventChecker = new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        startGameEvent();
                                    }
                                }.runTaskTimer(Warlords.getInstance(), 20 * 60 * 5, 20 * 60 * 5);
                            }
                        }
                    }
                    EventsLeaderboardManager.create();
                })
                .execute();
    }

    @EventHandler
    public void onDatabasePlayerLoad(DatabasePlayerFirstLoadEvent event) {
        if (!eventIsActive()) {
            return;
        }
        DatabasePlayer databasePlayer = event.getDatabasePlayer();
        GameEvents gameEvent = currentGameEvent.getEvent();
        EventMode eventMode = gameEvent.eventsStatsFunction.apply(databasePlayer.getPveStats().getEventStats()).get(currentGameEvent.getStartDateSecond());
        if (eventMode != null && eventMode.getEventPlays() != 0) {
            return;
        }
        Currencies currency = gameEvent.currency;
        Long currencyValue = databasePlayer.getPveStats().getCurrencyValue(currency);
        if (currencyValue <= 0) {
            return;
        }
        databasePlayer.getPveStats().subtractCurrency(currency, currencyValue);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        ChatUtils.MessageType.GAME_EVENTS.sendMessage("New event, cleared " + event.getPlayer().getName() + " " + currency.name + " currency.");
    }

    public GameEvents getEvent() {
        return event;
    }

    public long getStartDateSecond() {
        return startDate.getEpochSecond();
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return getEndDate().isAfter(Instant.now()); //getStartDate().isBefore(Instant.now()) &&
    }

    public Boolean getStarted() {
        return started;
    }

    public void setStarted(Boolean started) {
        this.started = started;
    }

    public void start() {
        if (GameEvents.npc != null) {
            GameEvents.npc.destroy();
        }
        if (!gaveRewards) {
            getEvent().initialize();
        }
        ChatUtils.MessageType.GAME_EVENTS.sendMessage("Creating new Game Event NPC...");
        getEvent().createNPC();
    }

    @Id
    protected String id;
    private GameEvents event;
    @Field("start_date")
    private Instant startDate;
    @Field("end_date")
    private Instant endDate;
    private boolean started;
    @Field("gave_rewards")
    private boolean gaveRewards;

    public void giveRewards() {
        //player rewards
        List<DatabasePlayer> databasePlayers = DatabaseManager.CACHED_PLAYERS
                .get(PlayersCollections.LIFETIME)
                .values()
                .stream()
                .filter(databasePlayer -> event.eventsStatsFunction.apply(databasePlayer.getPveStats().getEventStats())
                                                                   .getOrDefault(getStartDateSecond(), null) != null &&
                        event.eventsStatsFunction.apply(databasePlayer.getPveStats().getEventStats())
                                                 .get(getStartDateSecond())
                                                 .getEventPointsCumulative() > 0)
                .sorted((o1, o2) -> Long.compare(
                        event.eventsStatsFunction.apply(o2.getPveStats().getEventStats()).get(getStartDateSecond()).getEventPointsCumulative(),
                        event.eventsStatsFunction.apply(o1.getPveStats().getEventStats()).get(getStartDateSecond()).getEventPointsCumulative()
                ))
                .toList();
        ChatUtils.MessageType.GAME_EVENTS.sendMessage("Giving rewards for " + event.name + " (" + getStartDateSecond() + ") (" + databasePlayers.size() + " players)");
        HashMap<DatabasePlayer, List<Component>> playerMessages = new HashMap<>();
        for (int i = 0; i < databasePlayers.size(); i++) {
            int position = i + 1;
            DatabasePlayer databasePlayer = databasePlayers.get(i);
            databasePlayer.getPveStats()
                          .getGameEventRewards()
                          .add(new GameEventReward(event.getRewards(position), event.name + " Event", getStartDateSecond()));

            List<Component> messages = new ArrayList<>();
            messages.add(Component.text("------------------------------------------------", NamedTextColor.GOLD));
            messages.add(Component.text(event.name + " Event has Ended!", NamedTextColor.RED));
            messages.add(Component.empty());
            messages.add(Component.textOfChildren(
                            Component.text("#" + position + ". ", NamedTextColor.YELLOW),
                            Component.text(databasePlayer.getName(), NamedTextColor.AQUA),
                            Component.text(" - ", NamedTextColor.GRAY),
                            Component.text(NumberFormat.addCommas(event.eventsStatsFunction.apply(databasePlayer.getPveStats().getEventStats())
                                                                                           .get(getStartDateSecond())
                                                                                           .getEventPointsCumulative()) + " Points", NamedTextColor.YELLOW)
                    )
            );
            playerMessages.put(databasePlayer, messages);
        }
        //guild rewards
        List<Guild> guilds = GuildManager.GUILDS
                .stream()
                .filter(guild -> guild.getEventPoints(event, getStartDateSecond()) > 0)
                .sorted((o1, o2) -> Long.compare(
                        o2.getEventPoints(event, getStartDateSecond()),
                        o1.getEventPoints(event, getStartDateSecond())
                ))
                .toList();
        for (int i = 0; i < guilds.size(); i++) {
            int position = i + 1;
            Guild guild = guilds.get(i);
            LinkedHashMap<String, Long> guildRewards = event.getGuildRewards(position);
            guild.addCurrentCoins(guildRewards.getOrDefault("Coins", 0L));
            guild.addExperience(guildRewards.getOrDefault("Experience", 0L));
            guild.log(new GuildLogGameEventReward(event, getStartDateSecond(), position, guildRewards));

            for (GuildPlayer player : guild.getPlayers()) {
                for (DatabasePlayer databasePlayer : playerMessages.keySet()) {
                    if (databasePlayer.getUuid().equals(player.getUUID())) {
                        playerMessages.get(databasePlayer).add(
                                Component.textOfChildren(
                                        Component.text("#" + position + ". ", NamedTextColor.YELLOW),
                                        Component.text(guild.getName(), NamedTextColor.AQUA),
                                        Component.text(" - ", NamedTextColor.GRAY),
                                        Component.text(NumberFormat.addCommas(guild.getEventPoints(event, getStartDateSecond())) + " Points", NamedTextColor.GOLD)
                                )
                        );
                        break;
                    }
                }
            }

            guild.queueUpdate();
        }
        //player reward message
        playerMessages.forEach((databasePlayer, messages) -> {
            messages.add(Component.empty());
            messages.add(Component.text("Claim your rewards through your", NamedTextColor.GREEN));
            messages.add(Component.text("Reward Inventory!", NamedTextColor.GREEN));
            messages.add(Component.text("------------------------------------------------", NamedTextColor.GOLD));
            databasePlayer.addFutureMessage(FutureMessage.create(messages, true));
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        });
    }

    public boolean isGaveRewards() {
        return gaveRewards;
    }

    public void setGaveRewards(boolean gaveRewards) {
        this.gaveRewards = gaveRewards;
    }

    @Override
    public String toString() {
        return "DatabaseGameEvent{" +
                "event=" + event +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", started=" + started +
                ", startDateSecond=" + getStartDateSecond() +
                '}';
    }
}
