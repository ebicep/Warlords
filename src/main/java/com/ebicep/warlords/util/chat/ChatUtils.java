package com.ebicep.warlords.util.chat;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ChatUtils {

    public static final TextComponent SPACER = new TextComponent(ChatColor.GRAY + " - ");

    private static final int CENTER_PX = 164;

    public static void sendTitleToGamePlayers(Game game, String title, String subtitle) {
        for (WarlordsEntity we : PlayerFilter.playingGame(game)) {
            if (we.getEntity() instanceof Player) {
                PacketUtils.sendTitle(
                        (Player) we.getEntity(),
                        title,
                        subtitle,
                        20, 30, 20
                );
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

    public static void sendMessageToPlayer(WarlordsPlayer player, String message, ChatColor borderColor, boolean centered) {
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

    public static void sendMessageToPlayer(Player player, List<TextComponent> textComponents, ChatColor borderColor, boolean centered) {
        if (centered) {
            if (borderColor != null) {
                sendCenteredMessage(player, borderColor.toString() + ChatColor.BOLD + "------------------------------------------");
            }
            sendCenteredMessageWithEvents(player, textComponents);
            if (borderColor != null) {
                sendCenteredMessage(player, borderColor.toString() + ChatColor.BOLD + "------------------------------------------");
            }
        } else {
            if (borderColor != null) {
                player.sendMessage(borderColor.toString() + ChatColor.BOLD + "------------------------------------------");
            }
            sendCenteredMessageWithEvents(player, textComponents);
            if (borderColor != null) {
                player.sendMessage(borderColor.toString() + ChatColor.BOLD + "------------------------------------------");
            }
        }
    }

    public static void sendMessageToPlayer(Player player, BaseComponent[] textComponents, ChatColor borderColor, boolean centered) {
        if (centered) {
            if (borderColor != null) {
                sendCenteredMessage(player, borderColor.toString() + ChatColor.BOLD + "------------------------------------------");
            }
            sendCenteredMessageWithEvents(player, textComponents);
            if (borderColor != null) {
                sendCenteredMessage(player, borderColor.toString() + ChatColor.BOLD + "------------------------------------------");
            }
        } else {
            if (borderColor != null) {
                player.sendMessage(borderColor.toString() + ChatColor.BOLD + "------------------------------------------");
            }
            sendCenteredMessageWithEvents(player, textComponents);
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

    public static void sendCenteredMessageWithEvents(Player player, List<TextComponent> textComponents) {
        if (textComponents == null || textComponents.isEmpty()) {
            return;
        }
        String message = "";
        for (TextComponent textComponent : textComponents) {
            message += textComponent.getText();
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
        ComponentBuilder componentBuilder = new ComponentBuilder(sb.toString());
        for (TextComponent textComponent : textComponents) {
            componentBuilder.append(textComponent.getText());
            componentBuilder.event(textComponent.getHoverEvent());
            componentBuilder.event(textComponent.getClickEvent());
        }
        player.spigot().sendMessage(componentBuilder.create());
    }

    public static void sendCenteredMessageWithEvents(Player player, BaseComponent[] baseComponents) {
        if (baseComponents == null || baseComponents.length == 0) {
            return;
        }
        String message = "";
        for (BaseComponent baseComponent : baseComponents) {
            if (baseComponent instanceof TextComponent) {
                message += ((TextComponent) baseComponent).getText();
            }
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
        BaseComponent[] newComponents = new BaseComponent[baseComponents.length + 1];
        newComponents[0] = new TextComponent(sb.toString());
        for (int i = 0; i < baseComponents.length; i++) {
            newComponents[i + 1] = baseComponents[i];
        }
        player.spigot().sendMessage(newComponents);
    }

    /**
     * Converts an {@link org.bukkit.inventory.ItemStack} to a Json string
     * for sending with {@link net.md_5.bungee.api.chat.BaseComponent}'s.
     *
     * @param itemStack the item to convert
     * @return the Json string representation of the item
     */
    public static String convertItemStackToJsonRegular(ItemStack itemStack) {
        // First we convert the item stack into an NMS itemstack
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        CompoundTag compound = new CompoundTag();
        nmsItemStack.save(compound);
        return compound.toString();
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
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[" + name + "] " + message);
        }

    }

}
