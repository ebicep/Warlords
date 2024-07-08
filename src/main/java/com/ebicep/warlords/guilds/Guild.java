package com.ebicep.warlords.guilds;

import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.guilds.logs.AbstractGuildLog;
import com.ebicep.warlords.guilds.logs.types.oneplayer.GuildLogJoin;
import com.ebicep.warlords.guilds.logs.types.oneplayer.GuildLogLeave;
import com.ebicep.warlords.guilds.logs.types.oneplayer.GuildLogMuteGuild;
import com.ebicep.warlords.guilds.logs.types.oneplayer.GuildLogUnmuteGuild;
import com.ebicep.warlords.guilds.logs.types.oneplayer.tag.GuildLogTagChangeName;
import com.ebicep.warlords.guilds.logs.types.oneplayer.tag.GuildLogTagCreateName;
import com.ebicep.warlords.guilds.logs.types.twoplayer.*;
import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ebicep.warlords.guilds.GuildManager.queueUpdateGuild;

@Document(collection = "Guilds")
public class Guild {

    public static final int LOG_PER_PAGE = 30;
    public static final int CREATE_COIN_COST = 500000;
    public static final Predicate<DatabasePlayer> CAN_CREATE = databasePlayer -> {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        return pveStats.getCurrencyValue(Currencies.COIN) >= CREATE_COIN_COST &&
                (pveStats.getNormalStats().getWins() + pveStats.getHardStats().getWins() + pveStats.getExtremeStats().getWins()) >= 10;
    };

    public static int getConversionRatio(Guild guild) {
        int guildLevel = guild.getLevel();

        int coinConversionRatio;
        if (guildLevel <= 5) {
            coinConversionRatio = 100;
        } else if (guildLevel <= 10) {
            coinConversionRatio = 40;
        } else if (guildLevel <= 15) {
            coinConversionRatio = 10;
        } else {
            coinConversionRatio = 5;
        }
        return coinConversionRatio;
    }

    public int getLevel() {
        return GuildExperienceUtils.getLevelFromExp(getExperience(Timing.LIFETIME));
    }

    public long getExperience(Timing timing) {
        return experience.getOrDefault(timing, 0L);
    }

    //Local cache of uuids for faster lookup
    @Transient
    public HashMap<UUID, GuildPlayer> guildPlayerUUIDCache = new HashMap<>();
    @Id
    private String id;
    @Nonnull
    private String name = "";
    private GuildTag tag;
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
    @Field("current_coins")
    private long currentCoins = 0;
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
    private List<AbstractGuildUpgrade<?>> upgrades = new ArrayList<>();
    @Field("audit_log")
    private List<AbstractGuildLog> auditLog = new ArrayList<>();
    private List<String> motd = new ArrayList<>();

    public Guild() {
    }

    public Guild(Player player, @Nonnull String name) {
        this.name = name;
        this.createdBy = player.getUniqueId();
        this.currentMaster = player.getUniqueId();
        GuildRole masterRole = new GuildRole("Master", GuildPermissions.VALUES);
        GuildRole officerRole = new GuildRole("Officer", GuildPermissions.INVITE, GuildPermissions.KICK);
        GuildRole memberRole = new GuildRole("Member");

        this.defaultRole = memberRole.getRoleName();
        this.roles.add(masterRole);
        this.roles.add(officerRole);
        this.roles.add(memberRole);
        addPlayer(player, masterRole);
    }

    public void addPlayer(Player player, GuildRole role) {
        GuildPlayer guildPlayer = new GuildPlayer(player);
        role.addPlayer(player.getUniqueId());
        this.players.add(guildPlayer);
        this.guildPlayerUUIDCache.put(player.getUniqueId(), guildPlayer);
    }

    public void reloadPlayerCache() {
        guildPlayerUUIDCache.clear();
        for (GuildPlayer guildPlayer : players) {
            guildPlayerUUIDCache.put(guildPlayer.getUUID(), guildPlayer);
        }
    }

    public void join(Player player) {
        addPlayer(player, getDefaultRole());
        sendGuildMessageToOnlinePlayers(Component.text(player.getName(), NamedTextColor.AQUA).append(Component.text(" has joined the guild!", NamedTextColor.GREEN)), true);
        log(new GuildLogJoin(player.getUniqueId()));
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

    public void sendGuildMessageToOnlinePlayers(Component message, boolean centered) {
        for (Player onlinePlayer : getOnlinePlayers()) {
            ChatUtils.sendMessageToPlayer(onlinePlayer, message, NamedTextColor.GREEN, centered);
        }
    }

    public void log(AbstractGuildLog guildLog) {
        auditLog.add(guildLog);
    }

    public void queueUpdate() {
        queueUpdateGuild(this);
    }

    public List<Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream()
                     .filter(player -> guildPlayerUUIDCache.containsKey(player.getUniqueId()))
                     .collect(Collectors.toList());
    }

    public void setDefaultRole(String defaultRole) {
        this.defaultRole = defaultRole;
    }

    public void sendGuildMessageToPlayer(Player player, Component message, boolean centered) {
        ChatUtils.sendMessageToPlayer(player, message, NamedTextColor.GREEN, centered);
    }

    public void leave(Player player) {
        this.players.removeIf(guildPlayer -> guildPlayer.getUUID().equals(player.getUniqueId()));
        this.guildPlayerUUIDCache.remove(player.getUniqueId());
        sendGuildMessageToOnlinePlayers(Component.text(player.getName(), NamedTextColor.AQUA).append(Component.text(" has left the guild!", NamedTextColor.RED)), true);
        sendGuildMessage(player, Component.text("You left the guild!", NamedTextColor.RED));
        log(new GuildLogLeave(player.getUniqueId()));
        queueUpdate();
    }

    public static void sendGuildMessage(Player player, Component message) {
        ChatUtils.sendMessageToPlayer(player, message, NamedTextColor.GREEN, true);
    }

    public void transfer(GuildPlayer guildPlayer) {
        UUID oldMaster = currentMaster;
        roles.get(0).getPlayers().remove(currentMaster);
        roles.get(1).getPlayers().add(currentMaster);

        roles.get(getRoleLevel(guildPlayer)).getPlayers().remove(guildPlayer.getUUID());
        roles.get(0).getPlayers().add(guildPlayer.getUUID());
        this.currentMaster = guildPlayer.getUUID();
        sendGuildMessageToOnlinePlayers(Component.text("The guild was transferred to ", NamedTextColor.GREEN).append(Component.text(guildPlayer.getName(), NamedTextColor.AQUA)),
                true
        );
        log(new GuildLogTransfer(oldMaster, currentMaster));
        queueUpdate();
    }

    public int getRoleLevel(GuildPlayer guildPlayer) {
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).getPlayers().contains(guildPlayer.getUUID())) {
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }

    public void kick(GuildPlayer sender, GuildPlayer target) {
        getRoleOfPlayer(target.getUUID()).getPlayers().remove(target.getUUID());
        this.players.removeIf(player -> player.getUUID().equals(target.getUUID()));
        this.guildPlayerUUIDCache.remove(target.getUUID());
        sendGuildMessageToOnlinePlayers(Component.text(target.getName(), NamedTextColor.AQUA).append(Component.text(" was kicked from the guild!", NamedTextColor.RED)), true);
        log(new GuildLogKick(sender.getUUID(), target.getUUID()));
        queueUpdate();
    }

    public GuildRole getRoleOfPlayer(UUID uuid) {
        for (GuildRole role : roles) {
            if (role.getPlayers().contains(uuid)) {
                return role;
            }
        }
        return null;
    }

    public void promote(GuildPlayer sender, GuildPlayer target) {
        for (int i = 2; i < roles.size(); i++) {
            if (roles.get(i).getPlayers().contains(target.getUUID())) {
                roles.get(i).getPlayers().remove(target.getUUID());
                roles.get(i - 1).getPlayers().add(target.getUUID());
                sendGuildMessageToOnlinePlayers(Component.text(target.getName(), NamedTextColor.AQUA)
                                                         .append(Component.text(" has been promoted to " + roles.get(i - 1).getRoleName(), NamedTextColor.GREEN)),
                        true
                );

                log(new GuildLogPromote(sender.getUUID(),
                        target.getUUID(),
                        roles.get(i).getRoleName(),
                        roles.get(i - 1).getRoleName(),
                        i,
                        i - 1
                ));
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
                sendGuildMessageToOnlinePlayers(Component.text(target.getName(), NamedTextColor.AQUA)
                                                         .append(Component.text(" has been demoted to " + roles.get(i + 1).getRoleName(), NamedTextColor.RED)),
                        true
                );
                log(new GuildLogDemote(sender.getUUID(),
                        target.getUUID(),
                        roles.get(i).getRoleName(),
                        roles.get(i + 1).getRoleName(),
                        i,
                        i + 1
                ));
                queueUpdate();
                return;
            }
        }
    }

    public void disband() {
        this.disbandDate = Instant.now();
        GuildManager.removeGuild(this);
        sendGuildMessageToOnlinePlayers(Component.text("The guild has been disbanded!", NamedTextColor.RED), true);
        queueUpdate();
    }

    public Component getList() {
        TextComponent.Builder list = Component.text("------------------------------------------", NamedTextColor.GREEN)
                                              .append(Component.newline())
                                              .toBuilder();
        list.append(Component.text(" Guild Name: " + this.name, NamedTextColor.GOLD))
            .append(Component.newline())
            .append(Component.newline())
            .append(Component.space());
        for (GuildRole role : this.roles) {
            if (role.getPlayers().isEmpty()) {
                continue;
            }
            list.append(Component.text(" = " + role.getRoleName() + " = ", NamedTextColor.GREEN, TextDecoration.BOLD));
            list.append(Component.newline());
            List<GuildPlayer> guildPlayers = players.stream()
                                                    .filter(player -> role.getPlayers().contains(player.getUUID()))
                                                    .toList();
            for (int i = 0, guildPlayersSize = guildPlayers.size(); i < guildPlayersSize; i++) {
                GuildPlayer player = guildPlayers.get(i);
                list.append(player.getListName()).append(Component.text(" "));
                if (i % 4 == 0 && i != 0) {
                    list.append(Component.newline())
                        .append(Component.space());
                }
            }
            list.append(Component.newline());
        }
        list.append(Component.newline());
        list.append(Component.text(" Total Players: ", NamedTextColor.YELLOW))
            .append(Component.text(this.players.size(), NamedTextColor.GREEN))
            .append(Component.newline());
        list.append(Component.text(" Online Players: ", NamedTextColor.YELLOW))
            .append(Component.text(this.getOnlinePlayers().size(), NamedTextColor.GREEN))
            .append(Component.newline());
        list.append(Component.text("------------------------------------------", NamedTextColor.GREEN));
        return list.build();
    }

    public List<Player> getOnlinePlayersWithPermission(GuildPermissions permission) {
        Set<UUID> uuidsWithPermission = players.stream()
                                               .filter(guildPlayer -> playerHasPermission(guildPlayer, permission))
                                               .map(GuildPlayer::getUUID)
                                               .collect(Collectors.toSet());
        return Bukkit.getOnlinePlayers().stream()
                     .filter(player -> uuidsWithPermission.contains(player.getUniqueId()))
                     .collect(Collectors.toList());
    }

    public boolean playerHasPermission(GuildPlayer guildPlayer, GuildPermissions permission) {
        UUID uuid = guildPlayer.getUUID();
        for (GuildRole role : roles) {
            if (role.getPlayers().contains(uuid)) {
                boolean hasPermission = role.getPermissions().contains(permission);
                if (uuid.equals(currentMaster)) {
                    if (!hasPermission) {
                        getRoleOfPlayer(uuid).getPermissions().add(permission);
                        queueUpdate();
                        return true;
                    }
                }
                return hasPermission;
            }
        }
        return false;
    }

    public boolean hasUUID(UUID uuid) {
        return guildPlayerUUIDCache.containsKey(uuid);
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        sendGuildMessageToOnlinePlayers(Component.text("The guild name was changed to ", NamedTextColor.GREEN).append(Component.text(name, NamedTextColor.GOLD)), true);
    }

    public GuildTag getTag() {
        return tag;
    }

    public void setTag(GuildTag tag) {
        this.tag = tag;
        CustomScoreboard.updateLobbyPlayerNames();
    }

    public void setTag(GuildPlayer sender, String tagName) {
        tagName = tagName.toUpperCase();
        if (this.tag == null) {
            this.tag = new GuildTag(tagName);
            log(new GuildLogTagCreateName(sender.getUUID(), tagName));
        } else {
            String oldName = this.tag.getName();
            this.tag.setName(tagName);
            log(new GuildLogTagChangeName(sender.getUUID(), oldName, tagName));
        }
        sendGuildMessageToOnlinePlayers(Component.text("The guild tag was changed to ", NamedTextColor.GREEN).append(Component.text(tagName, NamedTextColor.GOLD)), true);
        queueUpdate();
        CustomScoreboard.updateLobbyPlayerNames();
    }

    public Instant getCreationDate() {
        return creationDate;
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
        if (this.open != open) {
            this.open = open;
            sendGuildMessageToOnlinePlayers(Component.text("The guild is now " + (open ? "open" : "closed"), open ? NamedTextColor.GREEN : NamedTextColor.RED), true);
            queueUpdate();
        }
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(GuildPlayer sender, boolean muted) {
        if (this.muted != muted) {
            this.muted = muted;
            sendGuildMessageToOnlinePlayers(Component.text("The guild is now " + (muted ? "muted" : "unmuted"), (muted ? NamedTextColor.RED : NamedTextColor.GREEN)), true);
            if (muted) {
                log(new GuildLogMuteGuild(sender.getUUID()));
            } else {
                log(new GuildLogUnmuteGuild(sender.getUUID()));
            }
            queueUpdate();
        }
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

    public Optional<GuildPlayer> getPlayerMatchingUUID(UUID uuid) {
        return Optional.ofNullable(guildPlayerUUIDCache.get(uuid));
    }

    public Optional<GuildPlayer> getPlayerMatchingName(String name) {
        return players.stream().filter(player -> player.getName().equalsIgnoreCase(name)).findFirst();
    }

    public void mutePlayer(GuildPlayer from, GuildPlayer target) {
        target.mute();
        log(new GuildLogMute(from.getUUID(), target.getUUID(), GuildPlayerMuteEntry.TimeUnit.PERMANENT));
        queueUpdate();
    }

    public void mutePlayer(GuildPlayer from, GuildPlayer target, GuildPlayerMuteEntry.TimeUnit timeUnit, Integer duration) {
        target.mute(timeUnit, duration);
        log(new GuildLogMute(from.getUUID(), target.getUUID(), timeUnit, duration));
        queueUpdate();
    }

    public void unmutePlayer(GuildPlayer from, GuildPlayer target) {
        target.unmute();
        log(new GuildLogUnmute(from.getUUID(), target.getUUID()));
        queueUpdate();
    }

    public int getPlayerLimit() {
        return playerLimit;
    }

    public void setPlayerLimit(int playerLimit) {
        this.playerLimit = playerLimit;
    }

    public long getCurrentCoins() {
        return currentCoins;
    }

    public void setCurrentCoins(long currentCoins) {
        this.currentCoins = currentCoins;
    }

    public void addCurrentCoins(long coins) {
        this.currentCoins += coins;
        if (coins > 0) {
            this.coins.forEach((timing, amount) -> this.coins.put(timing, Math.max(amount + coins, 0)));
        }
    }

    public long getCoins(Timing timing) {
        return coins.getOrDefault(timing, 0L);
    }

    public void setCoins(Timing timing, long coins) {
        this.coins.put(timing, coins);
    }

    public void setExperience(Timing timing, long experience) {
        this.experience.put(timing, experience);
    }

    public void addExperience(long experience) {
        this.experience.forEach((timing, aLong) -> this.experience.put(timing, Math.max(aLong + experience, 0)));
    }

    public List<AbstractGuildUpgrade<?>> getUpgrades() {
        return upgrades;
    }

    public void addUpgrade(AbstractGuildUpgrade<?> upgrade) {
        this.upgrades.removeIf(guildUpgrade -> guildUpgrade.isMatchingUpgrade(upgrade));
        this.upgrades.add(upgrade);
    }

    public void printAuditLog(Player player, int page) {
        int maxLogPage = auditLog.size() / LOG_PER_PAGE + (auditLog.size() % LOG_PER_PAGE == 0 ? 0 : 1);
        if (page < 1) {
            page = 1;
        } else if (page > maxLogPage) {
            page = maxLogPage;

        }
        ChatUtils.sendMessageToPlayer(
                player,
                Component.text("Guild Log (" + page + "/" + maxLogPage + ")", NamedTextColor.GOLD)
                         .append(Component.newline())
                         .append(auditLog.stream()
                                         .skip((page - 1) * LOG_PER_PAGE)
                                         .limit(LOG_PER_PAGE)
                                         .map(AbstractGuildLog::getFormattedLog)
                                         .collect(Component.toComponent(Component.newline()))),
                NamedTextColor.GREEN,
                false
        );
    }

    public List<String> getMotd() {
        return motd;
    }

    public void sendMOTD(Player player) {
        if (motd == null) {
            return;
        }
        motd.removeIf(Objects::isNull);
        if (motd.isEmpty()) {
            return;
        }
        player.sendMessage(Component.text("---------- Guild Message of the Day ----------", NamedTextColor.GREEN, TextDecoration.BOLD));
        motd.forEach(player::sendMessage);
        player.sendMessage(Component.text("-------------------------------------------", NamedTextColor.GREEN, TextDecoration.BOLD));

    }

    public Map<GameEvents, Map<Long, Long>> getEventStats() {
        return eventStats;
    }

    public void addEventPoints(GameEvents event, Long eventStartEpochSecond, long amount) {
        eventStats.computeIfAbsent(event, gameEvents -> new HashMap<>())
                  .compute(eventStartEpochSecond, (date, previousPoints) -> previousPoints == null ? amount : previousPoints + amount);
    }

    public long getEventPoints(GameEvents event, long eventStartEpochSecond) {
        return eventStats.getOrDefault(event, new HashMap<>()).getOrDefault(eventStartEpochSecond, 0L);
    }

    @Override
    public String toString() {
        return "Guild{" +
                "name='" + name + '\'' +
                '}';
    }
}
