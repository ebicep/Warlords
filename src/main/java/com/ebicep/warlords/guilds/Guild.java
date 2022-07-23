package com.ebicep.warlords.guilds;

import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Document(collection = "Guilds")
public class Guild {

    @Id
    private String id;
    private String name;
    @Field("date_created")
    private Instant creationDate = Instant.now();
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
    private boolean disbanded = false;
    @Field("player_limit")
    private int playerLimit = 10;
    private long coins = 0;

    public Guild() {
    }

    public Guild(Player player, String name) {
        this.name = name;
        this.createdBy = player.getUniqueId();
        this.currentMaster = player.getUniqueId();
        GuildRole masterRole = new GuildRole("Master", GuildPermissions.values());
        masterRole.addPlayer(player.getUniqueId());
        GuildRole officerRole = new GuildRole("Officer", GuildPermissions.INVITE, GuildPermissions.KICK);
        GuildRole memberRole = new GuildRole("Member");

        this.defaultRole = memberRole.getRoleName();
        this.roles.add(masterRole);
        this.roles.add(officerRole);
        this.roles.add(memberRole);
        addPlayer(player, masterRole);
    }

    public void join(Player player) {
        addPlayer(player, getDefaultRole());
        sendMessageToOnlinePlayers(ChatColor.AQUA + player.getName() + ChatColor.GREEN + " has joined the guild!", true);
    }

    public void leave(Player player) {
        this.players.removeIf(guildPlayer -> guildPlayer.getUUID().equals(player.getUniqueId()));
        sendMessageToOnlinePlayers(ChatColor.AQUA + player.getName() + ChatColor.RED + " has left the guild!", true);
        ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You left the guild!", ChatColor.GREEN, true);
    }

    public void transfer(GuildPlayer guildPlayer) {
        roles.get(0).getPlayers().remove(currentMaster);
        roles.get(1).getPlayers().add(currentMaster);

        roles.get(getRoleLevel(guildPlayer)).getPlayers().remove(guildPlayer.getUUID());
        roles.get(0).getPlayers().add(guildPlayer.getUUID());
        this.currentMaster = guildPlayer.getUUID();
        sendMessageToOnlinePlayers(ChatColor.GREEN + "The guild was transferred to " + ChatColor.AQUA + guildPlayer.getName(), true);
    }

    public void kick(GuildPlayer guildPlayer) {
        this.players.removeIf(player -> player.getUUID().equals(guildPlayer.getUUID()));
        sendMessageToOnlinePlayers(ChatColor.AQUA + guildPlayer.getName() + ChatColor.RED + " has been kicked from the guild!", true);
    }

    public void promote(GuildPlayer guildPlayer) {
        for (int i = 2; i < roles.size(); i++) {
            if (roles.get(i).getPlayers().contains(guildPlayer.getUUID())) {
                roles.get(i).getPlayers().remove(guildPlayer.getUUID());
                roles.get(i - 1).getPlayers().add(guildPlayer.getUUID());
                sendMessageToOnlinePlayers(ChatColor.AQUA + guildPlayer.getName() + ChatColor.GREEN + " has been promoted to " + roles.get(i - 1).getRoleName(), true);
                return;
            }
        }
    }

    public void demote(GuildPlayer guildPlayer) {
        for (int i = 1; i < roles.size(); i++) {
            if (roles.get(i).getPlayers().contains(guildPlayer.getUUID())) {
                roles.get(i).getPlayers().remove(guildPlayer.getUUID());
                roles.get(i + 1).getPlayers().add(guildPlayer.getUUID());
                sendMessageToOnlinePlayers(ChatColor.AQUA + guildPlayer.getName() + ChatColor.GREEN + " has been demoted to " + roles.get(i + 1).getRoleName(), true);
                return;
            }
        }
    }

    public void disband() {
        this.disbanded = true;
        sendMessageToOnlinePlayers(ChatColor.RED + "The guild has been disbanded!", true);
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

    public void sendMessageToOnlinePlayers(String message, boolean centered) {
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

    public String getName() {
        return name;
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

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
        sendMessageToOnlinePlayers((muted ? ChatColor.RED : ChatColor.GREEN) + "The guild is now " + (muted ? "muted" : "unmuted"), true);
    }

    public GuildRole getDefaultRole() {
        for (GuildRole role : roles) {
            if (role.getRoleName().equals(defaultRole)) {
                return role;
            }
        }
        return null;
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

    public boolean isDisbanded() {
        return disbanded;
    }

    public int getPlayerLimit() {
        return playerLimit;
    }
}
