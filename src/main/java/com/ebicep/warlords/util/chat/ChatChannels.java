package com.ebicep.warlords.util.chat;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.misc.SeeAllChatsCommand;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPermissions;
import com.ebicep.warlords.guilds.GuildPlayer;
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
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public enum ChatChannels {

    DEBUG("Debug",
            ChatColor.RED
    ) {
        @Override
        public String getFormat(Player player, String prefixWithColor) {
            return getColoredName() + CHAT_ARROW + getChatFormat(prefixWithColor);
        }

        @Override
        public void setRecipients(Player player, Set<Player> players) {
            players.removeIf(p -> !Permissions.isAdmin(player) && !p.equals(player));
        }

        @Override
        public void onPlayerChatEvent(AsyncPlayerChatEvent e, String prefixWithColor) {
            Player player = e.getPlayer();
            e.setFormat(getFormat(player, prefixWithColor));
            setRecipients(player, e.getRecipients());
            SeeAllChatsCommand.addPlayerSeeAllChats(e.getRecipients());
        }
    },
    ALL("All",
            null
    ) {
        @Override
        public String getFormat(Player player, String prefixWithColor) {
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
                        ChatColor.DARK_GRAY + "][" +
                        playerSettings.getSelectedSpec().specType.getColoredSymbol() +
                        ChatColor.DARK_GRAY + "] " +
                        (wp.isDead() ? ChatColor.GRAY + "[SPECTATOR] " : "") +
                        getChatFormat(prefixWithColor);
            } else {
                return ChatColor.DARK_GRAY + "[" +
                        ChatColor.GOLD + Specializations.getClass(playerSettings.getSelectedSpec()).name.toUpperCase().substring(0, 3) +
                        ChatColor.DARK_GRAY + "][" +
                        ChatColor.GRAY + (level < 10 ? "0" : "") + level +
                        ChatColor.DARK_GRAY + "]" +
                        ExperienceManager.getPrestigeLevelString(player.getUniqueId(), playerSettings.getSelectedSpec()) +
                        ChatColor.DARK_GRAY + "[" +
                        playerSettings.getSelectedSpec().specType.getColoredSymbol() +
                        ChatColor.DARK_GRAY + "] " +
                        getChatFormat(prefixWithColor);
            }
        }

        @Override
        public void setRecipients(Player player, Set<Player> players) {
            WarlordsEntity wp = Warlords.getPlayer(player);

            if (wp != null) {
                if (!(wp.getGame().getState() instanceof EndState)) {
                    players.removeIf(p -> wp.getGame().getPlayerTeam(p.getUniqueId()) != wp.getTeam());
                }
            } else {
                players.removeIf(Warlords::hasPlayer);
            }
        }

        @Override
        public void onPlayerChatEvent(AsyncPlayerChatEvent e, String prefixWithColor) {
            Player player = e.getPlayer();
            e.setFormat(getFormat(player, prefixWithColor));
            setRecipients(player, e.getRecipients());
            SeeAllChatsCommand.addPlayerSeeAllChats(e.getRecipients());
        }
    },
    PARTY("Party",
            ChatColor.BLUE
    ) {
        @Override
        public String getFormat(Player player, String prefixWithColor) {
            return getColoredName() + CHAT_ARROW + getChatFormat(prefixWithColor);
        }

        @Override
        public void setRecipients(Player player, Set<Player> players) {
            Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(player.getUniqueId());
            if (partyPlayerPair != null) {
                players.retainAll(partyPlayerPair.getA().getAllPartyPeoplePlayerOnline());
            }
        }

        @Override
        public void onPlayerChatEvent(AsyncPlayerChatEvent e, String prefixWithColor) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();

            Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(uuid);
            if (partyPlayerPair != null) {
                e.setFormat(getFormat(player, prefixWithColor));
                setRecipients(player, e.getRecipients());
                SeeAllChatsCommand.addPlayerSeeAllChats(e.getRecipients());
            } else {
                Warlords.PLAYER_CHAT_CHANNELS.put(uuid, ChatChannels.ALL);
                player.sendMessage(ChatColor.RED + "You are not in a party and were moved to the ALL channel.");
                e.setCancelled(true);
            }
        }
    },
    GUILD("Guild",
            ChatColor.GREEN
    ) {
        @Override
        public String getFormat(Player player, String prefixWithColor) {
            return getColoredName() + CHAT_ARROW + getChatFormat(prefixWithColor);
        }

        @Override
        public void setRecipients(Player player, Set<Player> players) {
            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
            if (guildPlayerPair != null) {
                players.retainAll(guildPlayerPair.getA().getOnlinePlayers());
            }
        }

        @Override
        public void onPlayerChatEvent(AsyncPlayerChatEvent e, String prefixWithColor) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();

            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
            if (guildPlayerPair != null) {
                if (guildPlayerPair.getA().isMuted() && !guildPlayerPair.getA().playerHasPermission(guildPlayerPair.getB(), GuildPermissions.BYPASS_MUTE)) {
                    player.sendMessage(ChatColor.RED + "The guild is currently muted.");
                    e.setCancelled(true);
                    return;
                }
                e.setFormat(getFormat(player, prefixWithColor));
                setRecipients(player, e.getRecipients());
                SeeAllChatsCommand.addPlayerSeeAllChats(e.getRecipients());
            } else {
                Warlords.PLAYER_CHAT_CHANNELS.put(uuid, ChatChannels.ALL);
                player.sendMessage(ChatColor.RED + "You are not in a guild and were moved to the ALL channel.");
                e.setCancelled(true);
            }
        }
    },

    ;

    public static final String CHAT_ARROW = ChatColor.DARK_GRAY + " > ";
    public final String name;
    public final ChatColor chatColor;

    ChatChannels(String name, ChatColor chatColor) {
        this.name = name;
        this.chatColor = chatColor;
    }

    public static void playerSendMessage(Player player, String message) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.chat(message);
            }
        }.runTaskAsynchronously(Warlords.getInstance());
    }

    /**
     * @param player          Sender of the message
     * @param message         Message to send
     * @param chatChannel     Channel to send the message to
     * @param asyncPlayerChat If true, the message will be sent through async player.chat(message) calling AsyncPlayerChatEvent.
     *                        If false, the message will be sent synchronously through player.sendMessage(message) to each online player.
     */
    public static void playerSendMessage(Player player, String message, ChatChannels chatChannel, boolean asyncPlayerChat) {
        if (message.startsWith("/")) {
            message = "§r" + message;
        }
        if (asyncPlayerChat) {
            UUID uuid = player.getUniqueId();
            String finalMessage = message;
            new BukkitRunnable() {
                @Override
                public void run() {
                    ChatChannels oldChatChannel = Warlords.PLAYER_CHAT_CHANNELS.getOrDefault(uuid, ALL);
                    Warlords.PLAYER_CHAT_CHANNELS.put(uuid, chatChannel == null ? ALL : chatChannel);
                    player.chat(finalMessage);
                    Warlords.PLAYER_CHAT_CHANNELS.put(uuid, oldChatChannel == DEBUG ? ALL : oldChatChannel);
                }
            }.runTaskAsynchronously(Warlords.getInstance());
        } else {
            try {
                String formattedMessage = String.format(chatChannel.getFormat(player, Permissions.getPrefixWithColor(player)), player.getName(), message);
                Set<Player> players = new HashSet<>(Bukkit.getOnlinePlayers());
                chatChannel.setRecipients(player, players);
                for (Player recipient : players) {
                    recipient.sendMessage(formattedMessage);
                }
                Bukkit.getServer().getConsoleSender().sendMessage(formattedMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void playerSpigotSendMessage(Player player, ChatChannels chatChannel, BaseComponent... components) {
        try {
            String formattedMessage = String.format(chatChannel.getFormat(player, Permissions.getPrefixWithColor(player)), player.getName(), "");
            List<BaseComponent> baseComponents = new ArrayList<>(Arrays.asList(components));
            baseComponents.add(0, new TextComponent(formattedMessage));

            Set<Player> players = new HashSet<>(Bukkit.getOnlinePlayers());
            chatChannel.setRecipients(player, players);
            for (Player recipient : players) {
                recipient.spigot().sendMessage(baseComponents.toArray(new BaseComponent[0]));
            }

            StringBuilder messageToConsole = new StringBuilder();
            for (BaseComponent baseComponent : baseComponents) {
                if (baseComponent instanceof TextComponent) {
                    messageToConsole.append(((TextComponent) baseComponent).getText());
                }
            }
            Bukkit.getServer().getConsoleSender().sendMessage(messageToConsole.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void switchChannels(Player player, ChatChannels chatChannel) {
        Warlords.PLAYER_CHAT_CHANNELS.put(player.getUniqueId(), chatChannel);
        player.sendMessage(ChatColor.GREEN + "You are now in the " + ChatColor.GOLD + chatChannel.name() + ChatColor.GREEN + " channel");
    }

    public static String getChatFormat(String prefixWithColor) {
        return prefixWithColor + "%1$s" + ChatColor.WHITE + ": %2$s";
    }

    public static void sendDebugMessage(Player player, String message, boolean asyncPlayerChat) {
        ChatChannels.playerSendMessage(player, message, ChatChannels.DEBUG, asyncPlayerChat);
    }

    public static void sendDebugMessage(WarlordsPlayer warlordsPlayer, String message, boolean asyncPlayerChat) {
        if (warlordsPlayer.getEntity() instanceof Player) {
            ChatChannels.playerSendMessage((Player) warlordsPlayer.getEntity(), message, ChatChannels.DEBUG, asyncPlayerChat);
        }
    }

    public static void sendDebugMessage(CommandIssuer commandIssuer, String message, boolean asyncPlayerChat) {
        if (commandIssuer != null && commandIssuer.getIssuer() instanceof Player) {
            sendDebugMessage((Player) commandIssuer.getIssuer(), message, asyncPlayerChat);
        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (Permissions.isAdmin(onlinePlayer)) {
                    onlinePlayer.sendMessage(DEBUG.getColoredName() + CHAT_ARROW + ChatColor.YELLOW + "Console: " + ChatColor.WHITE + message);
                }
            }
            if (commandIssuer != null) {
                commandIssuer.sendMessage(DEBUG.getColoredName() + CHAT_ARROW + message);
            }
        }
    }


    public abstract String getFormat(Player player, String prefixWithColor);

    public abstract void setRecipients(Player player, Set<Player> players);

    public abstract void onPlayerChatEvent(AsyncPlayerChatEvent e, String prefixWithColor);

    public String getColoredName() {
        return chatColor + name;
    }

}
