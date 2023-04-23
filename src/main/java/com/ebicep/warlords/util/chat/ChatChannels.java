package com.ebicep.warlords.util.chat;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.misc.SeeAllChatsCommand;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.guilds.*;
import com.ebicep.warlords.guilds.logs.AbstractGuildLog;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.java.Pair;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public enum ChatChannels {

    DEBUG("Debug",
            ChatColor.RED
    ) {
        @Override
        public String getFormat(Player player) {
            return getColoredName() + CHAT_ARROW;
        }

        @Override
        public void setRecipients(Player player, Set<Audience> audience) {
            audience.removeIf(a -> a instanceof Player p && !Permissions.isAdmin(p));
        }

        @Override
        public void onPlayerChatEvent(AsyncChatEvent e, Component prefixWithColor) {
            Player player = e.getPlayer();
            e.renderer((source, sourceDisplayName, message, viewer) ->
                    Component.text()
                             .append(Component.text(getFormat(player)))
                             .append(prefixWithColor)
                             .append(sourceDisplayName.color(prefixWithColor.color()))
                             .append(Component.text(": ")
                                              .append(message))
                             .build()
            );
            setRecipients(player, e.viewers());
            SeeAllChatsCommand.addPlayerSeeAllChats(e.viewers());
        }
    },
    ALL("All",
            null
    ) {
        @Override
        public String getFormat(Player player) {
            UUID uuid = player.getUniqueId();
            WarlordsEntity wp = Warlords.getPlayer(player);
            PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(uuid);
            int level = ExperienceManager.getLevelForSpec(uuid, playerSettings.getSelectedSpec());

            if (wp != null) {
                return wp.getTeam().teamColor() + "[" +
                        wp.getTeam().prefix() + "]" +
                        ChatColor.DARK_GRAY + "[" +
                        ChatColor.GOLD + wp.getSpec().getClassNameShort() +
                        ChatColor.DARK_GRAY + "][" +
                        ChatColor.GRAY + (level < 10 ? "0" : "") + level +
                        ChatColor.DARK_GRAY + "]" +
                        ExperienceManager.getPrestigeLevelString(player.getUniqueId(), playerSettings.getSelectedSpec()) +
                        ChatColor.DARK_GRAY + "[" +
                        playerSettings.getSelectedSpec().specType.getColoredSymbol() +
                        ChatColor.DARK_GRAY + "] " +
                        (wp.isDead() ? ChatColor.GRAY + "[SPECTATOR] " : "");
            } else {
                return ChatColor.DARK_GRAY + "[" +
                        ChatColor.GOLD + Specializations.getClass(playerSettings.getSelectedSpec()).name.toUpperCase().substring(0, 3) +
                        ChatColor.DARK_GRAY + "][" +
                        ChatColor.GRAY + (level < 10 ? "0" : "") + level +
                        ChatColor.DARK_GRAY + "]" +
                        ExperienceManager.getPrestigeLevelString(player.getUniqueId(), playerSettings.getSelectedSpec()) +
                        ChatColor.DARK_GRAY + "[" +
                        playerSettings.getSelectedSpec().specType.getColoredSymbol() +
                        ChatColor.DARK_GRAY + "] ";
            }
        }

        @Override
        public void setRecipients(Player player, Set<Audience> audience) {
            WarlordsEntity wp = Warlords.getPlayer(player);

            if (wp != null) {
                if (!(wp.getGame().getState() instanceof EndState)) {
                    audience.removeIf(a -> a instanceof Player p && wp.getGame().getPlayerTeam(p.getUniqueId()) != wp.getTeam());
                }
            } else {
                audience.removeIf(a -> a instanceof Player p && Warlords.hasPlayer(p));
            }
        }

        @Override
        public void onPlayerChatEvent(AsyncChatEvent e, Component prefixWithColor) {
            Player player = e.getPlayer();
            e.renderer((source, sourceDisplayName, message, viewer) ->
                    Component.text()
                             .append(Component.text(getFormat(player)))
                             .append(prefixWithColor)
                             .append(sourceDisplayName.color(prefixWithColor.color()))
                             .append(Component.text(": ")
                                              .append(message))
                             .build()
            );
            setRecipients(player, e.viewers());
            SeeAllChatsCommand.addPlayerSeeAllChats(e.viewers());
        }
    },
    PARTY("Party",
            ChatColor.BLUE
    ) {
        @Override
        public String getFormat(Player player) {
            return getColoredName() + CHAT_ARROW;
        }

        @Override
        public void setRecipients(Player player, Set<Audience> audience) {
            Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(player.getUniqueId());
            if (partyPlayerPair != null) {
                audience.removeIf(a -> a instanceof Player p && !partyPlayerPair.getA().getAllPartyPeoplePlayerOnline().contains(p));
            }
        }

        @Override
        public void onPlayerChatEvent(AsyncChatEvent e, Component prefixWithColor) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();

            Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(uuid);
            if (partyPlayerPair != null) {
                e.renderer((source, sourceDisplayName, message, viewer) ->
                        Component.text()
                                 .append(Component.text(getFormat(player)))
                                 .append(prefixWithColor)
                                 .append(sourceDisplayName.color(prefixWithColor.color()))
                                 .append(Component.text(": ")
                                                  .append(message))
                                 .build()
                );
                setRecipients(player, e.viewers());
                SeeAllChatsCommand.addPlayerSeeAllChats(e.viewers());
            } else {
                PLAYER_CHAT_CHANNELS.put(uuid, ChatChannels.ALL);
                player.sendMessage(ChatColor.RED + "You are not in a party and were moved to the ALL channel.");
                e.setCancelled(true);
            }
        }
    },
    GUILD("Guild",
            ChatColor.GREEN
    ) {
        @Override
        public String getFormat(Player player) {
            return getColoredName() + CHAT_ARROW;
        }

        @Override
        public void setRecipients(Player player, Set<Audience> audience) {
            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
            if (guildPlayerPair != null) {
                audience.removeIf(a -> a instanceof Player p && !guildPlayerPair.getA().getOnlinePlayers().contains(p));
            }
        }

        @Override
        public void onPlayerChatEvent(AsyncChatEvent e, Component prefixWithColor) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();

            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
            if (guildPlayerPair != null) {
                Guild guild = guildPlayerPair.getA();
                GuildPlayer guildPlayer = guildPlayerPair.getB();
                if (guild.isMuted() && !guild.playerHasPermission(guildPlayerPair.getB(), GuildPermissions.BYPASS_MUTE)) {
                    Guild.sendGuildMessage(player, ChatColor.RED + "The guild is currently muted.");
                    e.setCancelled(true);
                    return;
                }
                if (guildPlayer.isMuted()) {
                    GuildPlayerMuteEntry muteEntry = guildPlayer.getMuteEntry();
                    if (muteEntry.getTimeUnit() == GuildPlayerMuteEntry.TimeUnit.PERMANENT) {
                        Guild.sendGuildMessage(player, ChatColor.RED + "You are currently permanently muted in the guild.");
                    } else {
                        Guild.sendGuildMessage(player,
                                ChatColor.RED + "You are currently muted in the guild.\n" + ChatColor.GRAY + "Expires at " + AbstractGuildLog.FORMATTER.format(
                                        muteEntry.getEnd())
                        );
                    }
                    e.setCancelled(true);
                    return;
                }
                e.renderer((source, sourceDisplayName, message, viewer) ->
                        Component.text()
                                 .append(Component.text(getFormat(player)))
                                 .append(prefixWithColor)
                                 .append(sourceDisplayName.color(prefixWithColor.color()))
                                 .append(Component.text(": ")
                                                  .append(message))
                                 .build()
                );
                setRecipients(player, e.viewers());
                SeeAllChatsCommand.addPlayerSeeAllChats(e.viewers());
            } else {
                PLAYER_CHAT_CHANNELS.put(uuid, ChatChannels.ALL);
                player.sendMessage(ChatColor.RED + "You are not in a guild and were moved to the ALL channel.");
                e.setCancelled(true);
            }
        }
    },
    GUILD_OFFICER("Guild Officer",
            ChatColor.GREEN
    ) {
        @Override
        public String getFormat(Player player) {
            return getColoredName() + CHAT_ARROW;
        }

        @Override
        public void setRecipients(Player player, Set<Audience> audience) {
            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
            if (guildPlayerPair != null) {
                audience.retainAll(guildPlayerPair.getA().getOnlinePlayersWithPermission(GuildPermissions.OFFICER_CHAT));
            }
        }

        @Override
        public void onPlayerChatEvent(AsyncChatEvent e, Component prefixWithColor) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();

            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
            if (guildPlayerPair != null) {
                Guild guild = guildPlayerPair.getA();
                GuildPlayer guildPlayer = guildPlayerPair.getB();
                if (guildPlayer.isMuted()) {
                    GuildPlayerMuteEntry muteEntry = guildPlayer.getMuteEntry();
                    if (muteEntry.getTimeUnit() == GuildPlayerMuteEntry.TimeUnit.PERMANENT) {
                        Guild.sendGuildMessage(player, ChatColor.RED + "You are currently permanently muted in the guild.");
                    } else {
                        Guild.sendGuildMessage(player,
                                ChatColor.RED + "You are currently muted in the guild.\n" + ChatColor.GRAY + "Expires at " + AbstractGuildLog.FORMATTER.format(
                                        muteEntry.getEnd())
                        );
                    }
                    e.setCancelled(true);
                    return;
                }
                e.renderer((source, sourceDisplayName, message, viewer) ->
                        Component.text()
                                 .append(Component.text(getFormat(player)))
                                 .append(prefixWithColor)
                                 .append(sourceDisplayName.color(prefixWithColor.color()))
                                 .append(Component.text(": ")
                                                  .append(message))
                                 .build()
                );
                setRecipients(player, e.viewers());
                SeeAllChatsCommand.addPlayerSeeAllChats(e.viewers());
            } else {
                PLAYER_CHAT_CHANNELS.put(uuid, ChatChannels.ALL);
                player.sendMessage(ChatColor.RED + "You are not in a guild and were moved to the ALL channel.");
                e.setCancelled(true);
            }
        }
    },

    ;

    public static final ConcurrentHashMap<UUID, ChatChannels> PLAYER_CHAT_CHANNELS = new ConcurrentHashMap<>();

    public static final String CHAT_ARROW = ChatColor.DARK_GRAY + " > ";

    public static void playerSendMessage(Player player, String message) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.chat(message);
            }
        }.runTaskAsynchronously(Warlords.getInstance());
    }

    public static void switchChannels(Player player, ChatChannels chatChannel) {
        PLAYER_CHAT_CHANNELS.put(player.getUniqueId(), chatChannel);
        player.sendMessage(ChatColor.GREEN + "You are now in the " + ChatColor.GOLD + chatChannel.name.toUpperCase() + ChatColor.GREEN + " channel");
    }

    public static void sendDebugMessage(WarlordsPlayer warlordsPlayer, String message) {
        if (warlordsPlayer.getEntity() instanceof Player) {
            ChatChannels.playerSendMessage((Player) warlordsPlayer.getEntity(), ChatChannels.DEBUG, message);
        }
    }

    public static void playerSendMessage(Player player, ChatChannels chatChannel, String message) {
        ChatChannels.playerSendMessage(player, chatChannel, Component.text(message));
    }

    public static void sendDebugMessage(WarlordsPlayer warlordsPlayer, Component message) {
        if (warlordsPlayer.getEntity() instanceof Player) {
            ChatChannels.playerSendMessage((Player) warlordsPlayer.getEntity(), ChatChannels.DEBUG, message);
        }
    }

    /**
     * @param player      Sender of the message
     * @param chatChannel Channel to send the message to
     * @param message     Message to send
     */
    public static void playerSendMessage(Player player, ChatChannels chatChannel, Component message) {
        try {
            Component prefixWithColor = Permissions.getPrefixWithColor(player);
            Component component = Component.text(chatChannel.getFormat(player))
                                           .append(prefixWithColor.append(Component.text(player.getName())))
                                           .append(Component.text(": ").append(message));
            Set<Audience> viewers = new HashSet<>(Bukkit.getOnlinePlayers());
            chatChannel.setRecipients(player, viewers);
            viewers.add(Bukkit.getConsoleSender());
            for (Audience audience : viewers) {
                audience.sendMessage(component);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract String getFormat(Player player);

    public abstract void setRecipients(Player player, Set<Audience> audience);

    public static void sendDebugMessage(CommandIssuer commandIssuer, String message) {
        if (commandIssuer != null && commandIssuer.getIssuer() instanceof Player) {
            sendDebugMessage((Player) commandIssuer.getIssuer(), message);
        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (Permissions.isAdmin(onlinePlayer)) {
                    onlinePlayer.sendMessage(DEBUG.getColoredName() + CHAT_ARROW + ChatColor.YELLOW + "Console: " + ChatColor.WHITE + message);
                }
            }
            if (commandIssuer != null) {
                commandIssuer.sendMessage(DEBUG.getColoredName() + CHAT_ARROW + message);
            }
            Bukkit.getServer().getConsoleSender().sendMessage(DEBUG.getColoredName() + CHAT_ARROW + ChatColor.YELLOW + "Console: " + ChatColor.WHITE + message);
        }
    }

    public static void sendDebugMessage(CommandIssuer commandIssuer, Component message) {
        if (commandIssuer != null && commandIssuer.getIssuer() instanceof Player) {
            sendDebugMessage((Player) commandIssuer.getIssuer(), message);
        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (Permissions.isAdmin(onlinePlayer)) {
                    onlinePlayer.sendMessage(DEBUG.getColoredName() + CHAT_ARROW + ChatColor.YELLOW + "Console: " + ChatColor.WHITE + message);
                }
            }
            if (commandIssuer != null) {
                commandIssuer.sendMessage(DEBUG.getColoredName() + CHAT_ARROW + message);
            }
            Bukkit.getServer().getConsoleSender().sendMessage(DEBUG.getColoredName() + CHAT_ARROW + ChatColor.YELLOW + "Console: " + ChatColor.WHITE + message);
        }
    }

    public static void sendDebugMessage(Player player, String message) {
        ChatChannels.playerSendMessage(player, ChatChannels.DEBUG, message);
    }

    public static void sendDebugMessage(Player player, Component message) {
        ChatChannels.playerSendMessage(player, ChatChannels.DEBUG, message);
    }

    public String getColoredName() {
        return chatColor + name;
    }

    public final String name;
    public final ChatColor chatColor;

    ChatChannels(String name, ChatColor chatColor) {
        this.name = name;
        this.chatColor = chatColor;
    }

    public abstract void onPlayerChatEvent(AsyncChatEvent e, Component prefixWithColor);

}
