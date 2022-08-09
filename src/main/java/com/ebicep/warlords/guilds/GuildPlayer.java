package com.ebicep.warlords.guilds;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class GuildPlayer {

    private UUID uuid;
    @Field("join_date")
    private Instant joinDate = Instant.now();
    private long experience = 0;

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

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public void addExperience(long experience) {
        this.experience += experience;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuildPlayer that = (GuildPlayer) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
