package com.ebicep.warlords.util.chat;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtils {

    public static final Component SPACER = Component.text(" - ", NamedTextColor.GRAY);
    private static final int CENTER_PX = 164;

    public static String addStrikeThrough(String message) {
        for (ChatColor color : ChatColor.values()) {
            message = message.replace(color.toString(), color.toString() + ChatColor.STRIKETHROUGH);
        }
        return message;
    }

    public static void sendTitleToGamePlayers(Game game, Component title, Component subtitle) {
        for (WarlordsEntity we : PlayerFilter.playingGame(game)) {
            if (we.getEntity() instanceof Player) {
                we.getEntity().showTitle(Title.title(
                        title,
                        subtitle,
                        Title.Times.times(Ticks.duration(20), Ticks.duration(30), Ticks.duration(20))
                ));
            }
        }
    }

    public static void sendTitleToGamePlayers(
            Game game,
            String title,
            String subtitle,
            int fadeIn,
            int stay,
            int fadeOut
    ) {
        for (WarlordsEntity we : PlayerFilter.playingGame(game)) {
            if (we.getEntity() instanceof Player) {
                PacketUtils.sendTitle(
                        (Player) we.getEntity(),
                        title,
                        subtitle,
                        fadeIn, stay, fadeOut
                );
            }
        }
    }

    public static void sendMessage(Player player, boolean centered, String message) {
        if (centered) {
            sendCenteredMessage(player, message);
        } else {
            player.sendMessage(message);
        }
    }

    public static void sendMessage(Player player, boolean centered, Component message) {
        if (centered) {
            sendCenteredMessage(player, message);
        } else {
            player.sendMessage(message);
        }
    }

    public static void sendMessageToPlayer(WarlordsPlayer player, String message, ChatColor borderColor, boolean centered) {
        if (player.getEntity() instanceof Player) {
            sendMessageToPlayer((Player) player.getEntity(), message, borderColor, centered);
        }
    }

    public static void sendMessageToPlayer(WarlordsPlayer player, Component message, NamedTextColor borderColor, boolean centered) {
        if (player.getEntity() instanceof Player) {
            sendMessageToPlayer((Player) player.getEntity(), message, borderColor, centered);
        }
    }

    public static void sendMessageToPlayer(Player player, String message, ChatColor borderColor, boolean centered) {
        if (centered) {
            if (borderColor != null) {
                sendCenteredMessage(player, borderColor.toString() + ChatColor.BOLD + "------------------------------------------");
            }
            String[] messages = message.split("\n");
            for (String s : messages) {
                sendCenteredMessage(player, s);
            }
            if (borderColor != null) {
                sendCenteredMessage(player, borderColor.toString() + ChatColor.BOLD + "------------------------------------------");
            }
        } else {
            if (borderColor != null) {
                player.sendMessage(borderColor.toString() + ChatColor.BOLD + "------------------------------------------");
            }
            player.sendMessage(message);
            if (borderColor != null) {
                player.sendMessage(borderColor.toString() + ChatColor.BOLD + "------------------------------------------");
            }
        }
    }

    public static void sendMessageToPlayer(Player player, Component message, NamedTextColor borderColor, boolean centered) {
        if (centered) {
            if (borderColor != null) {
                sendCenteredMessage(player, Component.text("------------------------------------------", borderColor, TextDecoration.BOLD));
            }
            sendCenteredMessage(player, message);
            if (borderColor != null) {
                sendCenteredMessage(player, Component.text("------------------------------------------", borderColor, TextDecoration.BOLD));
            }
        } else {
            if (borderColor != null) {
                player.sendMessage(Component.text("------------------------------------------", borderColor, TextDecoration.BOLD));
            }
            player.sendMessage(message);
            if (borderColor != null) {
                player.sendMessage(Component.text("------------------------------------------", borderColor, TextDecoration.BOLD));
            }
        }
    }

    public static void sendMessageToPlayer(Player player, Component component, ChatColor borderColor, boolean centered) {
        if (centered) {
            if (borderColor != null) {
                sendCenteredMessage(player, borderColor.toString() + ChatColor.BOLD + "------------------------------------------");
            }
            sendCenteredMessage(player, component);
            if (borderColor != null) {
                sendCenteredMessage(player, borderColor.toString() + ChatColor.BOLD + "------------------------------------------");
            }
        } else {
            if (borderColor != null) {
                player.sendMessage(borderColor.toString() + ChatColor.BOLD + "------------------------------------------");
            }
            sendCenteredMessage(player, component);
            if (borderColor != null) {
                player.sendMessage(borderColor.toString() + ChatColor.BOLD + "------------------------------------------");
            }
        }
    }

    public static void sendCenteredMessage(Player player, String message) {
        if (message == null || message.isEmpty()) {
            player.sendMessage("");
            return;
        }
        if (message.contains("\n")) {
            String[] messages = message.split("\n");
            for (String s : messages) {
                sendCenteredMessage(player, s);
            }
            return;
        }
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(sb + message);
    }

    public static void sendCenteredMessage(Player player, Component component) {
        if (component == null) {
            return;
        }
        String message = ChatColor.translateAlternateColorCodes('&', PlainTextComponentSerializer.plainText().serialize(component));
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(Component.text(sb.toString())
                                    .append(component)
        );
    }

    public enum MessageTypes {

        WARLORDS("Warlords", ChatColor.GREEN),
        PLAYER_SERVICE("PlayerService", ChatColor.AQUA),
        GAME_SERVICE("GameService", ChatColor.YELLOW),
        GUILD_SERVICE("GuildService", ChatColor.GOLD),
        LEADERBOARDS("Leaderboards", ChatColor.BLUE),
        TIMINGS("Timings", ChatColor.DARK_GRAY),
        MASTERWORKS_FAIR("MasterworksFair", ChatColor.DARK_GREEN),
        GAME_EVENTS("Events", ChatColor.DARK_RED),
        WEEKLY_BLESSINGS("ItemsWeeklyBlessings", ChatColor.DARK_RED),
        ILLUSION_VENDOR("IllusionVendor", ChatColor.GOLD),

        GAME_DEBUG("GameDebug", ChatColor.LIGHT_PURPLE),

        DISCORD_BOT("DiscordBot", ChatColor.DARK_AQUA),

        ;

        public final String name;
        public final ChatColor chatColor;

        MessageTypes(String name, ChatColor chatColor) {
            this.name = name;
            this.chatColor = chatColor;
        }

        public void sendMessage(String message) {
            Bukkit.getServer().getConsoleSender().sendMessage(chatColor + "[" + name + "] " + message);
        }

        public void sendErrorMessage(String message) {
            Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[" + name + "] " + message, NamedTextColor.RED));
        }

    }

}
