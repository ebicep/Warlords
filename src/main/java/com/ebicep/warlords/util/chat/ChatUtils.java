package com.ebicep.warlords.util.chat;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.java.JavaUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatUtils {

    public static final Component SPACER = Component.text(" - ", NamedTextColor.GRAY);
    private static final int CENTER_PX = 150;

    public static void sendTitleToGamePlayers(Game game, Component title, Component subtitle) {
        sendTitleToGamePlayers(game, Title.title(
                        title,
                        subtitle,
                        Title.Times.times(Ticks.duration(20), Ticks.duration(30), Ticks.duration(20))
                )
        );
    }

    public static void sendTitleToGamePlayers(
            Game game,
            Title title
    ) {
        game.onlinePlayers().forEach(playerTeamEntry -> {
            playerTeamEntry.getKey().showTitle(title);
        });
    }

    public static void sendTitleToGamePlayers(Game game, Component subTitle) {
        sendTitleToGamePlayers(game, Component.empty(), subTitle, 20, 60, 20);
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

    public static void sendCenteredMessage(Player player, Component component) {
        if (component == null) {
            return;
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
        String message = LegacyComponentSerializer.legacySection().serialize(component);
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

    public enum MessageType {

        WARLORDS("Warlords", NamedTextColor.GREEN, true) {
            @Override
            public void sendMessage(String message) {
                if (isEnabled()) {
                    Warlords.getInstance().getComponentLogger().info(Component.text(message, textColor));
                }
            }

            @Override
            public void sendErrorMessage(String message) {
                Warlords.getInstance().getComponentLogger().error(Component.text(message, NamedTextColor.RED));
            }
        },
        PLAYER_SERVICE("PlayerService", NamedTextColor.AQUA, false),
        GAME_SERVICE("GameService", NamedTextColor.YELLOW, true),
        GUILD_SERVICE("GuildService", NamedTextColor.GOLD, true),
        LEADERBOARDS("Leaderboards", NamedTextColor.BLUE, true),
        TIMINGS("Timings", NamedTextColor.DARK_GRAY, true),
        MASTERWORKS_FAIR("MasterworksFair", NamedTextColor.DARK_GREEN, true),
        GAME_EVENTS("Events", NamedTextColor.DARK_RED, true),
        WEEKLY_BLESSINGS("ItemsWeeklyBlessings", NamedTextColor.DARK_RED, true),
        ILLUSION_VENDOR("IllusionVendor", NamedTextColor.GOLD, true),

        GAME_DEBUG("GameDebug", NamedTextColor.LIGHT_PURPLE, true),

        DISCORD_BOT("DiscordBot", NamedTextColor.DARK_AQUA, true),
        BOUNTIES("Bounties", NamedTextColor.AQUA, false),
        GAME("Game", TextColor.color(173, 255, 47), true),
        TOWER_DEFENSE("Tower Defense", TextColor.color(250, 100, 100), true),

        HOLOGRAMS("Holograms", NamedTextColor.DARK_PURPLE, false),
        CONFIG("Config", NamedTextColor.GREEN, true),

        ;

        private static final Set<String> ERRORS = new HashSet<>();

        public void sendErrorMessage(String message) {
            Warlords.getInstance().getComponentLogger().error(Component.text("[" + name + "] " + message, NamedTextColor.RED));
            sendErrorToAdmin(message);
        }

        public final String name;
        public final TextColor textColor;
        private boolean enabled;

        MessageType(String name, TextColor textColor, boolean enabled) {
            this.name = name;
            this.textColor = textColor;
            this.enabled = enabled;
        }

        public void sendMessage(String message) {
            if (enabled) {
                Warlords.getInstance().getComponentLogger().info(Component.text("[" + name + "] " + message, textColor));
            }
        }

        public static void sendErrorToAdmin(String error) {
            if (Warlords.getInstance().isDisabling()) {
                return;
            }
            BotManager.DiscordServer admin = BotManager.getServer("admin");
            if (admin == null) {
                return;
            }
            if (ERRORS.contains(error)) {
                return;
            }
            if (BotManager.numberOfMessagesSentLast30Sec++ > 15) {
                return;
            }
            ERRORS.add(error);
            admin.getChannel(BotManager.BotChannel.ERRORS).ifPresent(textChannel -> {
                textChannel.sendMessage("```" + error + "```").queue();
                BotManager.numberOfMessagesSentLast30Sec++;
            });
        }

        public void sendErrorMessage(Throwable throwable) {
            Warlords.getInstance().getComponentLogger().error(Component.text("[" + name + "] ", NamedTextColor.RED), throwable);
            sendErrorToAdmin(JavaUtils.throwableToString(throwable, 10));
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

    }

}
