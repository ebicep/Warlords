package com.ebicep.warlords.util.chat;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChatUtils {

    public static final Component SPACER = Component.text(" - ", NamedTextColor.GRAY);
    private static final int CENTER_PX = 164;

    public static void sendTitleToGamePlayers(Game game, Component title, Component subtitle) {
        sendTitleToGamePlayers(game, Title.title(
                title,
                subtitle,
                Title.Times.times(Ticks.duration(20), Ticks.duration(30), Ticks.duration(20))
        ));
    }

    public static void sendTitleToGamePlayers(
            Game game,
            Title title
    ) {
        for (WarlordsEntity we : PlayerFilter.playingGame(game)) {
            we.getEntity().showTitle(title);
        }
    }

    public static void sendTitleToGamePlayers(
            Game game,
            Component title,
            Component subtitle,
            int fadeIn,
            int stay,
            int fadeOut
    ) {
        sendTitleToGamePlayers(game,
                Title.title(
                        title,
                        subtitle,
                        Title.Times.times(Ticks.duration(fadeIn), Ticks.duration(stay), Ticks.duration(fadeOut))
                )
        );
    }

    public static void sendMessage(Player player, boolean centered, Component message) {
        if (centered) {
            sendCenteredMessage(player, message);
        } else {
            player.sendMessage(message);
        }
    }

    public static void sendMessageToPlayer(WarlordsPlayer player, Component message, NamedTextColor borderColor, boolean centered) {
        if (player.getEntity() instanceof Player) {
            sendMessageToPlayer((Player) player.getEntity(), message, borderColor, centered);
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
        if (((TextComponent) component).content().equals("TEST2")) {
            System.out.println("HJERE");
        }
        if (component.children().contains(Component.newline())) {
            Style parentStyle = component.style();
            List<Component> children = new ArrayList<>(component.children());
            children.add(0, component.children(new ArrayList<>()));
            Component toSend = Component.empty().style(parentStyle);
            for (int i = 0; i < children.size(); i++) {
                Component child = children.get(i);
                if (child.equals(Component.newline())) {
                    sendCenteredMessage(player, toSend);
                    if (i == children.size() - 1) {
                        break;
                    }
                    toSend = children.get(i + 1).applyFallbackStyle(parentStyle);
                    i++;
                } else {
                    toSend = toSend.append(child);
                }
            }
            sendCenteredMessage(player, toSend);
            return;
        }
        String message = LegacyComponentSerializer.legacyAmpersand().serialize(component);
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
        player.sendMessage(Component.text(sb.toString()).append(component));
    }

    public enum MessageTypes {

        WARLORDS("Warlords", NamedTextColor.GREEN),
        PLAYER_SERVICE("PlayerService", NamedTextColor.AQUA),
        GAME_SERVICE("GameService", NamedTextColor.YELLOW),
        GUILD_SERVICE("GuildService", NamedTextColor.GOLD),
        LEADERBOARDS("Leaderboards", NamedTextColor.BLUE),
        TIMINGS("Timings", NamedTextColor.DARK_GRAY),
        MASTERWORKS_FAIR("MasterworksFair", NamedTextColor.DARK_GREEN),
        GAME_EVENTS("Events", NamedTextColor.DARK_RED),
        WEEKLY_BLESSINGS("ItemsWeeklyBlessings", NamedTextColor.DARK_RED),
        ILLUSION_VENDOR("IllusionVendor", NamedTextColor.GOLD),

        GAME_DEBUG("GameDebug", NamedTextColor.LIGHT_PURPLE),

        DISCORD_BOT("DiscordBot", NamedTextColor.DARK_AQUA),

        ;

        public final String name;
        public final NamedTextColor textColor;

        MessageTypes(String name, NamedTextColor textColor) {
            this.name = name;
            this.textColor = textColor;
        }

        public void sendMessage(String message) {
            Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[" + name + "] " + message, textColor));
        }

        public void sendErrorMessage(String message) {
            Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[" + name + "] " + message, NamedTextColor.RED));
        }

    }

}
