package com.ebicep.warlords.util.chat;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ChatUtils {

    public static final TextComponent SPACER = new TextComponent(ChatColor.GRAY + " - ");

    private static final int CENTER_PX = 164;

    public static void sendMessage(Player player, boolean centered, String message) {
        if (centered) {
            sendCenteredMessage(player, message);
        } else {
            player.sendMessage(message);
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

    public static void sendCenteredMessage(Player player, String message) {
        if (message == null || message.isEmpty()) {
            player.sendMessage("");
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
        if (textComponents == null || textComponents.isEmpty()) return;
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

    /**
     * Converts an {@link org.bukkit.inventory.ItemStack} to a Json string
     * for sending with {@link net.md_5.bungee.api.chat.BaseComponent}'s.
     *
     * @param itemStack the item to convert
     * @return the Json string representation of the item
     */
    public static String convertItemStackToJsonRegular(ItemStack itemStack) {
        // First we convert the item stack into an NMS itemstack
        net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        net.minecraft.server.v1_8_R3.NBTTagCompound compound = new NBTTagCompound();
        nmsItemStack.save(compound);
        return compound.toString();
    }

}
