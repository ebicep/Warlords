package com.ebicep.warlords.guilds;

import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

public class GuildPlayer {

    private UUID uuid;
    @Field("join_date")
    private Instant joinDate = Instant.now();
    private Map<Timing, Long> coins = new HashMap<>() {{
        for (Timing value : Timing.VALUES) {
            put(value, 0L);
        }
    }};
    private Map<Timing, Long> experience = new HashMap<>() {{
        for (Timing value : Timing.VALUES) {
            put(value, 0L);
        }
    }};
    @Field("event_stats")
    private Map<GameEvents, Map<Long, Long>> eventStats = new LinkedHashMap<>();
    @Field("coins_converted")
    private long coinsConverted = 0;
    @Field("daily_coin_bonus_received")
    private boolean dailyCoinBonusReceived = false;
    @Field("daily_coins_converted")
    private long dailyCoinsConverted = 0;
    @Field("mute_entry")
    private GuildPlayerMuteEntry muteEntry;

    public GuildPlayer() {
    }

    public GuildPlayer(Player player) {
        this.uuid = player.getUniqueId();
    }

    public void doOnPlayer(Consumer<Player> playerConsumer) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.getPlayer() != null) {
            playerConsumer.accept(offlinePlayer.getPlayer());
        }
    }

    public String getName() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer != null) {
            return offlinePlayer.getName();
        }
        return "UNKNOWN";
    }

    public String getListName() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        String name = offlinePlayer.getName();
        if (offlinePlayer.isOnline()) {
            return ChatColor.AQUA + name + ChatColor.GREEN + " ● ";
        }
        return ChatColor.AQUA + name + ChatColor.RED + " ● ";
    }


    public UUID getUUID() {
        return uuid;
    }

    public Instant getJoinDate() {
        return joinDate;
    }

    public long getExperience(Timing timing) {
        return experience.getOrDefault(timing, 0L);
    }

    public void addExperience(long experience) {
        this.experience.forEach((timing, amount) -> this.experience.put(timing, Math.max(amount + experience, 0)));
    }

    public void setExperience(Timing timing, long experience) {
        this.experience.put(timing, experience);
    }

    public long getCoins(Timing timing) {
        return coins.getOrDefault(timing, 0L);
    }

    public void addCoins(long coins) {
        this.coins.forEach((timing, amount) -> this.coins.put(timing, Math.max(amount + coins, 0)));
    }

    public void setCoins(Timing timing, long coins) {
        this.coins.put(timing, coins);
    }

    public long getCoinsConverted() {
        return coinsConverted;
    }

    public void addCoinsConverted(long coinsConverted) {
        this.coinsConverted += coinsConverted;
    }

    public boolean isDailyCoinBonusReceived() {
        return dailyCoinBonusReceived;
    }

    public void setDailyCoinBonusReceived(boolean dailyCoinBonusReceived) {
        this.dailyCoinBonusReceived = dailyCoinBonusReceived;
    }

    public long getDailyCoinsConverted() {
        return dailyCoinsConverted;
    }

    public void setDailyCoinsConverted(long dailyCoinsConverted) {
        this.dailyCoinsConverted = dailyCoinsConverted;
    }

    public void addDailyCoinsConverted(long dailyCoinsConverted) {
        this.coinsConverted += dailyCoinsConverted;
        this.dailyCoinsConverted += dailyCoinsConverted;
    }

    public GuildPlayerMuteEntry getMuteEntry() {
        return muteEntry;
    }

    public void mute() {
        this.muteEntry = new GuildPlayerMuteEntry();
    }

    public void mute(GuildPlayerMuteEntry.TimeUnit timeUnit, int duration) {
        this.muteEntry = new GuildPlayerMuteEntry(timeUnit, duration);
    }

    public void unmute() {
        this.muteEntry = null;
    }

    public boolean isMuted() {
        if (muteEntry != null) {
            if (muteEntry.getEnd() == null || muteEntry.getEnd().isAfter(Instant.now())) {
                return true;
            } else {
                unmute();
            }
        }
        return false;
    }

    public Map<GameEvents, Map<Long, Long>> getEventStats() {
        return eventStats;
    }

    public void addEventPoints(GameEvents event, Long eventStartEpochSecond, long amount) {
        eventStats.computeIfAbsent(event, gameEvents -> new HashMap<>())
                  .compute(eventStartEpochSecond, (date, previousPoints) -> previousPoints == null ? amount : previousPoints + amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GuildPlayer that = (GuildPlayer) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
