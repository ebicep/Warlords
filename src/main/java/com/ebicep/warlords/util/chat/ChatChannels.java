package com.ebicep.warlords.util.chat;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPermissions;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.permissions.PermissionHandler;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public enum ChatChannels {

    DEBUG("Debug",
            ChatColor.RED
    ) {
        @Override
        public void onPlayerChatEvent(AsyncPlayerChatEvent e, ChatColor prefixColor, String prefix) {
            Player player = e.getPlayer();

            e.setFormat(getColoredName() + CHAT_ARROW + getChatFormat(prefixColor, prefix));
            e.getRecipients().removeIf(p -> !PermissionHandler.isAdmin(player) && !p.equals(player));
        }
    },
    ALL("All",
            null
    ) {
        @Override
        public void onPlayerChatEvent(AsyncPlayerChatEvent e, ChatColor prefixColor, String prefix) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();

            WarlordsEntity wp = Warlords.getPlayer(player);
            PlayerSettings playerSettings = Warlords.getPlayerSettings(uuid);
            int level = ExperienceManager.getLevelForSpec(uuid, playerSettings.getSelectedSpec());

            if (wp != null) {
                e.setFormat(wp.getTeam().teamColor() + "[" +
                        wp.getTeam().prefix() + "]" +
                        ChatColor.DARK_GRAY + "[" +
                        ChatColor.GOLD + wp.getSpec().getClassNameShort() +
                        ChatColor.DARK_GRAY + "][" +
                        ChatColor.GRAY + (level < 10 ? "0" : "") + level +
                        ChatColor.DARK_GRAY + "][" +
                        playerSettings.getSelectedSpec().specType.getColoredSymbol() +
                        ChatColor.DARK_GRAY + "] " +
                        (wp.isDead() ? ChatColor.GRAY + "[SPECTATOR] " : "") +
                        getChatFormat(prefixColor, prefix)
                );
                if (!(wp.getGame().getState() instanceof EndState)) {
                    e.getRecipients().removeIf(p -> wp.getGame().getPlayerTeam(p.getUniqueId()) != wp.getTeam());
                }
            } else {
                e.setFormat(ChatColor.DARK_GRAY + "[" +
                        ChatColor.GOLD + Specializations.getClass(playerSettings.getSelectedSpec()).name.toUpperCase().substring(0, 3) +
                        ChatColor.DARK_GRAY + "][" +
                        ChatColor.GRAY + (level < 10 ? "0" : "") + level +
                        ChatColor.DARK_GRAY + "]" +
                        ExperienceManager.getPrestigeLevelString(player.getUniqueId(), playerSettings.getSelectedSpec()) +
                        ChatColor.DARK_GRAY + "[" +
                        playerSettings.getSelectedSpec().specType.getColoredSymbol() +
                        ChatColor.DARK_GRAY + "] " +
                        getChatFormat(prefixColor, prefix)
                );
                e.getRecipients().removeIf(Warlords::hasPlayer);
            }
        }
    },
    PARTY("Party",
            ChatColor.BLUE
    ) {
        @Override
        public void onPlayerChatEvent(AsyncPlayerChatEvent e, ChatColor prefixColor, String prefix) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();

            if (Warlords.partyManager.getPartyFromAny(uuid).isPresent()) {
                e.setFormat(getColoredName() + CHAT_ARROW + getChatFormat(prefixColor, prefix));
                e.getRecipients().retainAll(Warlords.partyManager.getPartyFromAny(uuid).get().getAllPartyPeoplePlayerOnline());
            } else {
                Warlords.playerChatChannels.put(uuid, ChatChannels.ALL);
                player.sendMessage(ChatColor.RED + "You are not in a party and were moved to the ALL channel.");
                e.setCancelled(true);
            }
        }
    },
    GUILD("Guild",
            ChatColor.GREEN
    ) {
        @Override
        public void onPlayerChatEvent(AsyncPlayerChatEvent e, ChatColor prefixColor, String prefix) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();

            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
            if (guildPlayerPair != null) {
                if (guildPlayerPair.getA().isMuted() && !guildPlayerPair.getA().playerHasPermission(guildPlayerPair.getB(), GuildPermissions.BYPASS_MUTE)) {
                    player.sendMessage(ChatColor.RED + "The guild is currently muted.");
                    e.setCancelled(true);
                    return;
                }
                e.setFormat(getColoredName() + CHAT_ARROW + getChatFormat(prefixColor, prefix));
                e.getRecipients().retainAll(guildPlayerPair.getA().getOnlinePlayers());
            } else {
                Warlords.playerChatChannels.put(uuid, ChatChannels.ALL);
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

    public static void playerSendMessage(Player player, String message, ChatChannels chatChannel) {
        UUID uuid = player.getUniqueId();
        if (chatChannel == null) {
            chatChannel = ALL;
        }
        Warlords.playerChatChannels.put(uuid, chatChannel);
        ChatChannels finalChatChannel = chatChannel;
        new BukkitRunnable() {
            @Override
            public void run() {
                player.chat(message);
                Warlords.playerChatChannels.put(uuid, finalChatChannel);
            }
        }.runTaskAsynchronously(Warlords.getInstance());
    }

    public static void switchChannels(Player player, ChatChannels chatChannel) {
        Warlords.playerChatChannels.put(player.getUniqueId(), chatChannel);
        player.sendMessage(ChatColor.GREEN + "You are now in the " + ChatColor.GOLD + chatChannel.name() + ChatColor.GREEN + " channel");
    }

    public static String getChatFormat(ChatColor prefixColor, String prefix) {
        return prefix + prefixColor + "%1$s" + ChatColor.WHITE + ": %2$s";
    }

    public abstract void onPlayerChatEvent(AsyncPlayerChatEvent e, ChatColor prefixColor, String prefix);

    public String getColoredName() {
        return chatColor + name;
    }

}
