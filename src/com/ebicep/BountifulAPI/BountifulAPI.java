package com.ebicep.BountifulAPI;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BountifulAPI extends JavaPlugin implements Listener {
    public static BountifulAPI bountifulAPI;

    public BountifulAPI() {
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String message) {
        sendTitle(player, fadeIn, stay, fadeOut, message, (String) null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static void sendSubtitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String message) {
        sendTitle(player, fadeIn, stay, fadeOut, (String) null, message);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static void sendFullTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static Integer getPlayerProtocol(Player player) {
        return 47;
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        TitleSendEvent titleSendEvent = new TitleSendEvent(player, title, subtitle);
        Bukkit.getPluginManager().callEvent(titleSendEvent);
        if (!titleSendEvent.isCancelled()) {
            try {
                Object e;
                Constructor subtitleConstructor;
                if (title != null) {
                    title = ChatColor.translateAlternateColorCodes('&', title);
                    title = title.replaceAll("%player%", player.getDisplayName());
                    e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get((Object) null);
                    Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke((Object) null, "{\"text\":\"" + title + "\"}");
                    subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                    Object titlePacket = subtitleConstructor.newInstance(e, chatTitle, fadeIn, stay, fadeOut);
                    sendPacket(player, titlePacket);
                    e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get((Object) null);
                    chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke((Object) null, "{\"text\":\"" + title + "\"}");
                    subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
                    titlePacket = subtitleConstructor.newInstance(e, chatTitle);
                    sendPacket(player, titlePacket);
                }

                if (subtitle != null) {
                    subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
                    subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
                    e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get((Object) null);
                    Object chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke((Object) null, "{\"text\":\"" + title + "\"}");
                    subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                    Object subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
                    sendPacket(player, subtitlePacket);
                    e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get((Object) null);
                    chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke((Object) null, "{\"text\":\"" + subtitle + "\"}");
                    subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE);
                    subtitlePacket = subtitleConstructor.newInstance(e, chatSubtitle, fadeIn, stay, fadeOut);
                    sendPacket(player, subtitlePacket);
                }
            } catch (Exception var13) {
                var13.printStackTrace();
            }

        }
    }

    public static void clearTitle(Player player) {
        sendTitle(player, 0, 0, 0, "", "");
    }

    public static void sendTabTitle(Player player, String header, String footer) {
        if (header == null) {
            header = "";
        }

        header = ChatColor.translateAlternateColorCodes('&', header);
        if (footer == null) {
            footer = "";
        }

        footer = ChatColor.translateAlternateColorCodes('&', footer);
        TabTitleSendEvent tabTitleSendEvent = new TabTitleSendEvent(player, header, footer);
        Bukkit.getPluginManager().callEvent(tabTitleSendEvent);
        if (!tabTitleSendEvent.isCancelled()) {
            header = header.replaceAll("%player%", player.getDisplayName());
            footer = footer.replaceAll("%player%", player.getDisplayName());

            try {
                Object tabHeader = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke((Object) null, "{\"text\":\"" + header + "\"}");
                Object tabFooter = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke((Object) null, "{\"text\":\"" + footer + "\"}");
                Constructor<?> titleConstructor = getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(getNMSClass("IChatBaseComponent"));
                Object packet = titleConstructor.newInstance(tabHeader);
                Field field = packet.getClass().getDeclaredField("b");
                field.setAccessible(true);
                field.set(packet, tabFooter);
                sendPacket(player, packet);
            } catch (Exception var9) {
                var9.printStackTrace();
            }

        }
    }

    public static void sendActionBar(Player player, String message) {
        ActionBarMessageEvent actionBarMessageEvent = new ActionBarMessageEvent(player, message);
        Bukkit.getPluginManager().callEvent(actionBarMessageEvent);
        if (!actionBarMessageEvent.isCancelled()) {
            String nmsver = Bukkit.getServer().getClass().getPackage().getName();
            nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);

            try {
                Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer");
                Object p = c1.cast(player);
                Object ppoc = null;
                Class<?> c4 = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat");
                Class<?> c5 = Class.forName("net.minecraft.server." + nmsver + ".Packet");
                Class c2;
                Class c3;
                Object pc;
                if (!nmsver.equalsIgnoreCase("v1_8_R1") && nmsver.startsWith("v1_8_")) {
                    c2 = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText");
                    c3 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
                    Object o = c2.getConstructor(String.class).newInstance(message);
                    ppoc = c4.getConstructor(c3, Byte.TYPE).newInstance(o, 2);
                } else {
                    c2 = Class.forName("net.minecraft.server." + nmsver + ".ChatSerializer");
                    c3 = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
                    Method m3 = c2.getDeclaredMethod("a", String.class);
                    pc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message + "\"}"));
                    ppoc = c4.getConstructor(c3, Byte.TYPE).newInstance(pc, 2);
                }

                Method m1 = c1.getDeclaredMethod("getHandle");
                Object h = m1.invoke(p);
                Field f1 = h.getClass().getDeclaredField("playerConnection");
                pc = f1.get(h);
                Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
                m5.invoke(pc, ppoc);
            } catch (Exception var14) {
                var14.printStackTrace();
            }

        }
    }

    public static void sendActionBar(final Player player, final String message, int duration) {
        sendActionBar(player, message);
        if (duration >= 0) {
            (new BukkitRunnable() {
                public void run() {
                    BountifulAPI.sendActionBar(player, "");
                }
            }).runTaskLater(bountifulAPI, (long) (duration + 1));
        }

        while (duration > 60) {
            duration -= 60;
            int sched = duration % 60;
            (new BukkitRunnable() {
                public void run() {
                    BountifulAPI.sendActionBar(player, message);
                }
            }).runTaskLater(bountifulAPI, (long) sched);
        }

    }

    public static void sendActionBarToAllPlayers(String message) {
        sendActionBarToAllPlayers(message, -1);
    }

    public static void sendActionBarToAllPlayers(String message, int duration) {
        Iterator var2 = Bukkit.getOnlinePlayers().iterator();

        while (var2.hasNext()) {
            Player p = (Player) var2.next();
            sendActionBar(p, message, duration);
        }

    }

    public void onEnable() {
        bountifulAPI = this;
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        Server server = this.getServer();
        ConsoleCommandSender console = server.getConsoleSender();
        console.sendMessage(ChatColor.AQUA + this.getDescription().getName() + " V" + this.getDescription().getVersion() + " has been enabled!");
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.getConfig().getBoolean("Title On Join")) {
            sendTitle(event.getPlayer(), 20, 50, 20, this.getConfig().getString("Title Message"), this.getConfig().getString("Subtitle Message"));
        }

        if (this.getConfig().getBoolean("Tab Header Enabled")) {
            sendTabTitle(event.getPlayer(), this.getConfig().getString("Tab Header Message"), this.getConfig().getString("Tab Footer Message"));
        }

    }
}