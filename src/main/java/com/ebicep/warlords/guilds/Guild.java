package com.ebicep.warlords.guilds;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.guilds.logs.AbstractGuildLog;
import com.ebicep.warlords.guilds.logs.types.oneplayer.GuildLogJoin;
import com.ebicep.warlords.guilds.logs.types.oneplayer.GuildLogLeave;
import com.ebicep.warlords.guilds.logs.types.twoplayer.GuildLogDemote;
import com.ebicep.warlords.guilds.logs.types.twoplayer.GuildLogKick;
import com.ebicep.warlords.guilds.logs.types.twoplayer.GuildLogPromote;
import com.ebicep.warlords.guilds.logs.types.twoplayer.GuildLogTransfer;
import com.ebicep.warlords.guilds.upgrades.GuildUpgrade;
import com.ebicep.warlords.pve.rewards.Currencies;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ebicep.warlords.guilds.GuildManager.queueUpdateGuild;

@Document(collection = "Guilds")
public class Guild {

    public static final Predicate<DatabasePlayer> CAN_CREATE = databasePlayer -> {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        return pveStats.getCurrencyValue(Currencies.COIN) >= 100;
    };

    //TODO local cache of uuids for faster lookup

    @Id
    private String id;
    private String name;
    @Field("date_created")
    private Instant creationDate = Instant.now();
    @Field("date_disbanded")
    private Instant disbandDate;
    @Field("uuid_creator")
    private UUID createdBy;
    @Field("uuid_master")
    private UUID currentMaster;
    private boolean open = false;
    private boolean muted = false;
    @Field("default_role_name")
    private String defaultRole;
    private List<GuildRole> roles = new ArrayList<>();
    private List<GuildPlayer> players = new ArrayList<>();
    @Field("player_limit")
    private int playerLimit = 10;
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
    private List<GuildUpgrade> upgrades = new ArrayList<>();
    @Field("audit_log")
    private List<AbstractGuildLog> auditLog = new ArrayList<>();

    public Guild() {
    }

    public Guild(Player player, String name) {
        this.name = name;
        this.createdBy = player.getUniqueId();
        this.currentMaster = player.getUniqueId();
        GuildRole masterRole = new GuildRole("Master", GuildPermissions.VALUES);
        masterRole.addPlayer(player.getUniqueId());
        GuildRole officerRole = new GuildRole("Officer", GuildPermissions.INVITE, GuildPermissions.KICK);
        GuildRole memberRole = new GuildRole("Member");

        this.defaultRole = memberRole.getRoleName();
        this.roles.add(masterRole);
        this.roles.add(officerRole);
        this.roles.add(memberRole);
        addPlayer(player, masterRole);
    }

    public static void sendGuildMessage(Player player, String message) {
        ChatUtils.sendMessageToPlayer(player, message, ChatColor.GREEN, true);
    }

    public void queueUpdate() {
        queueUpdateGuild(this);
    }

    public void join(Player player) {
        addPlayer(player, getDefaultRole());
        sendGuildMessageToOnlinePlayers(ChatColor.AQUA + player.getName() + ChatColor.GREEN + " has joined the guild!", true);
        log(new GuildLogJoin(player.getUniqueId()));
        queueUpdate();
    }

    public void leave(Player player) {
        this.players.removeIf(guildPlayer -> guildPlayer.getUUID().equals(player.getUniqueId()));
        sendGuildMessageToOnlinePlayers(ChatColor.AQUA + player.getName() + ChatColor.RED + " has left the guild!", true);
        sendGuildMessage(player, ChatColor.RED + "You left the guild!");
        log(new GuildLogLeave(player.getUniqueId()));
        queueUpdate();
    }

    public void transfer(GuildPlayer guildPlayer) {
        UUID oldMaster = currentMaster;
        roles.get(0).getPlayers().remove(currentMaster);
        roles.get(1).getPlayers().add(currentMaster);

        roles.get(getRoleLevel(guildPlayer)).getPlayers().remove(guildPlayer.getUUID());
        roles.get(0).getPlayers().add(guildPlayer.getUUID());
        this.currentMaster = guildPlayer.getUUID();
        sendGuildMessageToOnlinePlayers(ChatColor.GREEN + "The guild was transferred to " + ChatColor.AQUA + guildPlayer.getName(), true);
        log(new GuildLogTransfer(oldMaster, currentMaster));
        queueUpdate();
    }

    public void kick(GuildPlayer sender, GuildPlayer target) {
        this.players.removeIf(player -> player.getUUID().equals(target.getUUID()));
        sendGuildMessageToOnlinePlayers(ChatColor.AQUA + target.getName() + ChatColor.RED + " was kicked from the guild!", true);
        log(new GuildLogKick(sender.getUUID(), target.getUUID()));
        queueUpdate();
    }

    public void promote(GuildPlayer sender, GuildPlayer target) {
        for (int i = 2; i < roles.size(); i++) {
            if (roles.get(i).getPlayers().contains(target.getUUID())) {
                roles.get(i).getPlayers().remove(target.getUUID());
                roles.get(i - 1).getPlayers().add(target.getUUID());
                sendGuildMessageToOnlinePlayers(ChatColor.AQUA + target.getName() + ChatColor.GREEN + " has been promoted to " + roles.get(i - 1).getRoleName(), true);
                log(new GuildLogPromote(sender.getUUID(), target.getUUID(), roles.get(i).getRoleName(), roles.get(i - 1).getRoleName(), i, i - 1));
                queueUpdate();
                return;
            }
        }
    }

    public void demote(GuildPlayer sender, GuildPlayer target) {
        for (int i = 1; i < roles.size(); i++) {
            if (roles.get(i).getPlayers().contains(target.getUUID())) {
                roles.get(i).getPlayers().remove(target.getUUID());
                roles.get(i + 1).getPlayers().add(target.getUUID());
                sendGuildMessageToOnlinePlayers(ChatColor.AQUA + target.getName() + ChatColor.GREEN + " has been demoted to " + roles.get(i + 1).getRoleName(), true);
                log(new GuildLogDemote(sender.getUUID(), target.getUUID(), roles.get(i).getRoleName(), roles.get(i + 1).getRoleName(), i, i + 1));
                queueUpdate();
                return;
            }
        }
    }

    public void disband() {
        this.disbandDate = Instant.now();
        GuildManager.removeGuild(this);
        sendGuildMessageToOnlinePlayers(ChatColor.RED + "The guild has been disbanded!", true);
        queueUpdate();
    }

    public String getList() {
        StringBuilder sb = new StringBuilder(ChatColor.GREEN + "------------------------------------------\n");
        sb.append(ChatColor.GOLD).append("Guild Name: ").append(this.name).append("\n \n");
        for (GuildRole role : this.roles) {
            sb.append(ChatColor.GREEN).append("= ").append(role.getRoleName()).append(" =\n");
            players.stream()
                    .filter(player -> role.getPlayers().contains(player.getUUID()))
                    .forEach(player -> sb.append(player.getListName()).append(" "));
            sb.append("\n \n");
        }
        sb.append(" \n");
        sb.append(ChatColor.YELLOW).append("Total Players: ").append(ChatColor.GREEN).append(this.players.size()).append("\n");
        sb.append(ChatColor.YELLOW).append("Online Players: ").append(ChatColor.GREEN).append(this.getOnlinePlayers().size()).append("\n");
        sb.append(ChatColor.GREEN).append("------------------------------------------\n");
        return sb.toString();
    }

    public List<Player> getOnlinePlayers() {
        List<UUID> guildPlayerUUIDs = players.stream().map(GuildPlayer::getUUID).collect(Collectors.toList());
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> guildPlayerUUIDs.contains(player.getUniqueId()))
                .collect(Collectors.toList());
    }

    public void sendGuildMessageToOnlinePlayers(String message, boolean centered) {
        for (Player onlinePlayer : getOnlinePlayers()) {
            ChatUtils.sendMessageToPlayer(onlinePlayer, message, ChatColor.GREEN, centered);
        }
    }

    public boolean playerHasPermission(GuildPlayer guildPlayer, GuildPermissions permission) {
        for (GuildRole role : roles) {
            if (role.getPlayers().contains(guildPlayer.getUUID())) {
                return role.getPermissions().contains(permission);
            }
        }
        return false;
    }

    public int getRoleLevel(GuildPlayer guildPlayer) {
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).getPlayers().contains(guildPlayer.getUUID())) {
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }

    public GuildRole getRoleOfPlayer(UUID uuid) {
        for (GuildRole role : roles) {
            if (role.getPlayers().contains(uuid)) {
                return role;
            }
        }
        return null;
    }

    public boolean hasUUID(UUID uuid) {
        for (GuildPlayer guildPlayer : players) {
            if (guildPlayer.getUUID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        sendGuildMessageToOnlinePlayers(ChatColor.GREEN + "The guild name was changed to " + ChatColor.GOLD + name, true);
    }

    public Instant getDisbandDate() {
        return disbandDate;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public UUID getCurrentMaster() {
        return currentMaster;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
        sendGuildMessageToOnlinePlayers((muted ? ChatColor.RED : ChatColor.GREEN) + "The guild is now " + (muted ? "muted" : "unmuted"), true);
        queueUpdate();
    }

    public GuildRole getDefaultRole() {
        for (GuildRole role : roles) {
            if (role.getRoleName().equals(defaultRole)) {
                return role;
            }
        }
        return null;
    }

    public void setDefaultRole(String defaultRole) {
        this.defaultRole = defaultRole;
    }

    public String getDefaultRoleName() {
        return defaultRole;
    }

    public List<GuildRole> getRoles() {
        return roles;
    }

    public List<GuildPlayer> getPlayers() {
        return players;
    }

    public void addPlayer(Player player, GuildRole role) {
        GuildPlayer guildPlayer = new GuildPlayer(player);
        role.addPlayer(player.getUniqueId());
        this.players.add(guildPlayer);
    }

    public Optional<GuildPlayer> getPlayerMatchingUUID(UUID uuid) {
        return players.stream().filter(player -> player.getUUID().equals(uuid)).findFirst();
    }

    public Optional<GuildPlayer> getPlayerMatchingName(String name) {
        return players.stream().filter(player -> player.getName().equalsIgnoreCase(name)).findFirst();
    }

    public int getPlayerLimit() {
        return playerLimit;
    }

    public long getCoins(Timing timing) {
        return coins.getOrDefault(timing, 0L);
    }

    public void setCoins(Timing timing, long coins) {
        this.coins.put(timing, coins);
    }

    public void addCoins(long coins) {
        this.coins.forEach((timing, amount) -> this.coins.put(timing, amount + coins));
    }

    public long getExperience(Timing timing) {
        return experience.getOrDefault(timing, 0L);
    }

    public void setExperience(Timing timing, long experience) {
        this.experience.put(timing, experience);
    }

    public void addExperience(long experience) {
        this.experience.forEach((timing, aLong) -> this.experience.put(timing, aLong + experience));
    }

    public List<GuildUpgrade> getUpgrades() {
        return upgrades;
    }

    public List<AbstractGuildLog> getAuditLog() {
        return auditLog;
    }

    public void log(AbstractGuildLog guildLog) {
        auditLog.add(guildLog);
    }

    @Override
    public String toString() {
        return "Guild{" +
                "name='" + name + '\'' +
                '}';
    }
}
