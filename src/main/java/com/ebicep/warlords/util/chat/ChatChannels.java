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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public enum ChatChannels {

    DEBUG("Debug",
            NamedTextColor.RED
    ) {
        @Override
        public void setRecipients(Player player, Set<Audience> audience) {
            audience.removeIf(a -> a instanceof Player p && !Permissions.isAdmin(p));
        }

        @Override
        public void onPlayerChatEvent(AsyncChatEvent e, Component prefixWithColor) {
            Player player = e.getPlayer();
            e.renderer((source, sourceDisplayName, message, viewer) ->
                    Component.textOfChildren(
                            getFormat(player),
                            prefixWithColor,
                            sourceDisplayName.color(prefixWithColor.color()),
                            Component.text(": ").append(message)
                    )
            );
            setRecipients(player, e.viewers());
            SeeAllChatsCommand.addPlayerSeeAllChats(e.viewers());
        }
    },
    ALL("All",
            null
    ) {
        @Override
        public Component getFormat(Player player) {
            UUID uuid = player.getUniqueId();
            WarlordsEntity wp = Warlords.getPlayer(player);
            PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(uuid);
            int level = ExperienceManager.getLevelForSpec(uuid, playerSettings.getSelectedSpec());

            if (wp != null) {
                return Component.empty().color(NamedTextColor.DARK_GRAY)
                                .append(Component.text("[" + wp.getTeam().prefix() + "]", wp.getTeam().teamColor()))
                                .append(Component.text("["))
                                .append(Component.text(wp.getSpec().getClassNameShort(), NamedTextColor.GOLD))
                                .append(Component.text("]["))
                                .append(Component.text((level < 10 ? "0" : "") + level, NamedTextColor.GRAY))
                                .append(Component.text("]"))
                                .append(ExperienceManager.getPrestigeLevelString(player.getUniqueId(), playerSettings.getSelectedSpec()))
                                .append(Component.text("["))
                                .append(playerSettings.getSelectedSpec().specType.getColoredSymbol())
                                .append(Component.text("] "))
                                .append(Component.text(wp.isDead() ? "[SPECTATOR] " : "", NamedTextColor.GRAY));
            } else {
                return Component.text("[", NamedTextColor.DARK_GRAY)
                                .append(Component.text(Specializations.getClass(playerSettings.getSelectedSpec()).name.toUpperCase().substring(0, 3), NamedTextColor.GOLD))
                                .append(Component.text("]["))
                                .append(Component.text((level < 10 ? "0" : "") + level, NamedTextColor.GRAY))
                                .append(Component.text("]"))
                                .append(ExperienceManager.getPrestigeLevelString(player.getUniqueId(), playerSettings.getSelectedSpec()))
                                .append(Component.text("["))
                                .append(playerSettings.getSelectedSpec().specType.getColoredSymbol())
                                .append(Component.text("] "));
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
                    Component.textOfChildren(
                            getFormat(player),
                            prefixWithColor,
                            sourceDisplayName.color(prefixWithColor.color()),
                            Component.text(": ").append(message)
                    )
            );
            setRecipients(player, e.viewers());
            SeeAllChatsCommand.addPlayerSeeAllChats(e.viewers());
        }
    },
    PARTY("Party",
            NamedTextColor.BLUE
    ) {
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
                        Component.textOfChildren(
                                getFormat(player),
                                prefixWithColor,
                                sourceDisplayName.color(prefixWithColor.color()),
                                Component.text(": ").append(message)
                        )
                );
                setRecipients(player, e.viewers());
                SeeAllChatsCommand.addPlayerSeeAllChats(e.viewers());
            } else {
                PLAYER_CHAT_CHANNELS.put(uuid, ChatChannels.ALL);
                player.sendMessage(Component.text("You are not in a party and were moved to the ALL channel.", NamedTextColor.RED));
                e.setCancelled(true);
            }
        }
    },
    GUILD("Guild",
            NamedTextColor.GREEN
    ) {
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
                    Guild.sendGuildMessage(player, Component.text("The guild is currently muted.", NamedTextColor.RED));
                    e.setCancelled(true);
                    return;
                }
                if (guildPlayer.isMuted()) {
                    GuildPlayerMuteEntry muteEntry = guildPlayer.getMuteEntry();
                    if (muteEntry.getTimeUnit() == GuildPlayerMuteEntry.TimeUnit.PERMANENT) {
                        Guild.sendGuildMessage(player, Component.text("You are currently permanently muted in the guild.", NamedTextColor.RED));
                    } else {
                        Guild.sendGuildMessage(player,
                                Component.text("You are currently muted in the guild.", NamedTextColor.RED)
                                         .append(Component.newline())
                                         .append(Component.text("Expires at " + AbstractGuildLog.FORMATTER.format(muteEntry.getEnd()), NamedTextColor.GRAY))
                        );
                    }
                    e.setCancelled(true);
                    return;
                }
                e.renderer((source, sourceDisplayName, message, viewer) ->
                        Component.textOfChildren(
                                getFormat(player),
                                prefixWithColor,
                                sourceDisplayName.color(prefixWithColor.color()),
                                Component.text(": ").append(message)
                        )
                );
                setRecipients(player, e.viewers());
                SeeAllChatsCommand.addPlayerSeeAllChats(e.viewers());
            } else {
                PLAYER_CHAT_CHANNELS.put(uuid, ChatChannels.ALL);
                player.sendMessage(Component.text("You are not in a guild and were moved to the ALL channel.", NamedTextColor.RED));
                e.setCancelled(true);
            }
        }
    },
    GUILD_OFFICER("Guild Officer",
            NamedTextColor.GREEN
    ) {
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
                        Guild.sendGuildMessage(player, Component.text("You are currently permanently muted in the guild.", NamedTextColor.RED));
                    } else {
                        Guild.sendGuildMessage(player,
                                Component.text("You are currently muted in the guild.", NamedTextColor.RED)
                                         .append(Component.newline())
                                         .append(Component.text("Expires at " + AbstractGuildLog.FORMATTER.format(muteEntry.getEnd()), NamedTextColor.GRAY))
                        );
                    }
                    e.setCancelled(true);
                    return;
                }
                e.renderer((source, sourceDisplayName, message, viewer) ->
                        Component.textOfChildren(
                                getFormat(player),
                                prefixWithColor,
                                sourceDisplayName.color(prefixWithColor.color()),
                                Component.text(": ").append(message)
                        )
                );
                setRecipients(player, e.viewers());
                SeeAllChatsCommand.addPlayerSeeAllChats(e.viewers());
            } else {
                PLAYER_CHAT_CHANNELS.put(uuid, ChatChannels.ALL);
                player.sendMessage(Component.text("You are not in a guild and were moved to the ALL channel.", NamedTextColor.RED));
                e.setCancelled(true);
            }
        }
    },

    ;

    public static final ConcurrentHashMap<UUID, ChatChannels> PLAYER_CHAT_CHANNELS = new ConcurrentHashMap<>();

    public static final Component CHAT_ARROW = Component.text(" > ", NamedTextColor.DARK_GRAY);

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
        player.sendMessage(Component.text("You are now in the ", NamedTextColor.GREEN)
                                    .append(Component.text(chatChannel.name.toUpperCase(), NamedTextColor.GOLD))
                                    .append(Component.text(" channel"))
        );
    }

    public static void sendDebugMessage(WarlordsPlayer warlordsPlayer, String message) {
        if (warlordsPlayer.getEntity() instanceof Player) {
            ChatChannels.playerSendMessage((Player) warlordsPlayer.getEntity(), ChatChannels.DEBUG, message);
        }
    }

    public static void playerSendMessage(Player player, ChatChannels chatChannel, String message) {
        ChatChannels.playerSendMessage(player, chatChannel, Component.text(message));
    }

    /**
     * @param player      Sender of the message
     * @param chatChannel Channel to send the message to
     * @param message     Message to send
     */
    public static void playerSendMessage(Player player, ChatChannels chatChannel, Component message) {
        try {
            Component prefixWithColor = Permissions.getPrefixWithColor(player, false);
            Component component = chatChannel.getFormat(player)
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

    public Component getFormat(Player player) {
        return getColoredName().append(CHAT_ARROW);
    }

    public abstract void setRecipients(Player player, Set<Audience> audience);

    public Component getColoredName() {
        return Component.text(name, textColor);
    }

    public static void sendDebugMessage(WarlordsPlayer warlordsPlayer, Component message) {
        if (warlordsPlayer.getEntity() instanceof Player) {
            ChatChannels.playerSendMessage((Player) warlordsPlayer.getEntity(), ChatChannels.DEBUG, message);
        }
    }

    public static void sendDebugMessage(CommandIssuer commandIssuer, String message) {
        if (commandIssuer != null && commandIssuer.getIssuer() instanceof Player) {
            sendDebugMessage((Player) commandIssuer.getIssuer(), message);
        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (Permissions.isAdmin(onlinePlayer)) {
                    onlinePlayer.sendMessage(DEBUG.getColoredName()
                                                  .append(CHAT_ARROW)
                                                  .append(Component.text("Console: ", NamedTextColor.YELLOW))
                                                  .append(Component.text(message, NamedTextColor.WHITE)));
                }
            }
            if (commandIssuer != null) {
                commandIssuer.sendMessage(PlainTextComponentSerializer.plainText().serialize(
                        DEBUG.getColoredName()
                             .append(CHAT_ARROW)
                             .append(Component.text(message, NamedTextColor.WHITE))
                ));
            }
            Bukkit.getServer().getConsoleSender().sendMessage(DEBUG.getColoredName()
                                                                   .append(CHAT_ARROW)
                                                                   .append(Component.text("Console: ", NamedTextColor.YELLOW))
                                                                   .append(Component.text(message, NamedTextColor.WHITE)));
        }
    }

    public static void sendDebugMessage(Player player, String message) {
        ChatChannels.playerSendMessage(player, ChatChannels.DEBUG, message);
    }

    public static void sendDebugMessage(CommandIssuer commandIssuer, Component message) {
        if (commandIssuer != null && commandIssuer.getIssuer() instanceof Player) {
            sendDebugMessage((Player) commandIssuer.getIssuer(), message);
        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (Permissions.isAdmin(onlinePlayer)) {
                    onlinePlayer.sendMessage(DEBUG.getColoredName()
                                                  .append(CHAT_ARROW)
                                                  .append(Component.text("Console: ", NamedTextColor.YELLOW))
                                                  .append(message));
                }
            }
            if (commandIssuer != null) {
                commandIssuer.sendMessage(PlainTextComponentSerializer.plainText().serialize(
                        DEBUG.getColoredName()
                             .append(CHAT_ARROW)
                             .append(message)
                ));
            }
            Bukkit.getServer().getConsoleSender().sendMessage(DEBUG.getColoredName()
                                                                   .append(CHAT_ARROW)
                                                                   .append(Component.text("Console: ", NamedTextColor.YELLOW))
                                                                   .append(message));
        }
    }

    public static void sendDebugMessage(Player player, Component message) {
        ChatChannels.playerSendMessage(player, ChatChannels.DEBUG, message);
    }

    public final String name;
    public final NamedTextColor textColor;

    ChatChannels(String name, NamedTextColor textColor) {
        this.name = name;
        this.textColor = textColor;
    }

    public abstract void onPlayerChatEvent(AsyncChatEvent e, Component prefixWithColor);

}
