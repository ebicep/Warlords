package com.ebicep.warlords.util;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.abilties.Totem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.xml.soap.Text;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Utils {

    public static boolean getLookingAt(Player player, Player player1) {
        Location eye = player.getEyeLocation().subtract(player.getLocation().getDirection().multiply(4));
        eye.setY(eye.getY() + 0.7);
        Vector toEntity = player1.getEyeLocation().toVector().subtract(eye.toVector());
        float dot = (float) toEntity.normalize().dot(eye.getDirection());
        return dot > 0.975D;
    }

    //15 blocks = 6.6
    //10 blocks = 8
    //7 blocks = 10
    //5 blocks = 28
    //2 blocks = WIDE
    public static boolean getLookingAtChain(Player player, Player player1) {
        Location eye = player.getEyeLocation().subtract(player.getLocation().getDirection().multiply(4));
        eye.setY(eye.getY() + 0.7);
        Vector toEntity = player1.getEyeLocation().toVector().subtract(eye.toVector());
        float dot = (float) toEntity.normalize().dot(eye.getDirection());
        return dot > 0.965D + (player.getLocation().distanceSquared(player1.getLocation()) / 10000);
    }

    public static boolean getLookingAtWave(Player player, Player player1) {
        Location eye = player.getEyeLocation();
        eye.setY(eye.getY() + 0.7);
        Vector toEntity = player1.getEyeLocation().toVector().subtract(eye.toVector());
        float dot = (float) toEntity.normalize().dot(eye.getDirection());
        return dot > 0.935D;
    }

    public static boolean hasLineOfSight(Player player, Player player2) {
        return player.hasLineOfSight(player2);
    }

    public static boolean totemDownAndClose(WarlordsPlayer warlordsPlayer, Player player) {
        for (Entity entity : player.getNearbyEntities(5, 3, 5)) {
            if (entity instanceof ArmorStand && entity.hasMetadata("Capacitor Totem - " + warlordsPlayer.getName())) {
                return true;
            }
        }
        return false;
    }

    public static class ArmorStandComparator implements Comparator<Entity> {
        @Override
        public int compare(Entity a, Entity b) {
            return a instanceof ArmorStand && b instanceof ArmorStand ? 0 : a instanceof ArmorStand ? -1 : b instanceof ArmorStand ? 1 : 0;
        }
    }

    public static List<Entity> filterOutTeammates(List<Entity> entities, Player player) {
        entities.remove(player);
        return entities.stream().filter(entity -> !(entity instanceof Player) || !Warlords.game.onSameTeam((Player) entity, player)).collect(Collectors.toList());
    }

    public static List<Entity> filterOnlyTeammates(List<Entity> entities, Player player) {
        return entities.stream().filter(entity -> !(entity instanceof Player) || Warlords.game.onSameTeam((Player) entity, player)).collect(Collectors.toList());
    }

    public static Vector getRightDirection(Location location) {
        Vector direction = location.getDirection().normalize();
        return new Vector(-direction.getZ(), 0.0, direction.getX()).normalize();
    }

    public static Vector getLeftDirection(Location location) {
        Vector direction = location.getDirection().normalize();
        return new Vector(direction.getZ(), 0.0, -direction.getX()).normalize();
    }

    public static double getDistance(Entity e, double accuracy) {
        Location loc = e.getLocation().clone(); // Using .clone so you aren't messing with the direct location object from the entity
        double distance = 0; // Shouldn't start at -2 unless you're wanting the eye height from the ground (I don't know why you'd want that)
        for (double i = loc.getY(); i >= e.getLocation().getY() - 2; i -= accuracy) {
            loc.setY(i);
            distance += accuracy;
            if (loc.getBlock().getType().isSolid()) // Makes a little more sense than checking if it's air
                break;
        }
        return distance;
    }

    /**
     * Utility message for sending version independent actionbar messages as to be able to
     * support versions from 1.8 and up without having to disable a simple feature such as this.
     *
     * @param player  the recipient of the actionbar message.
     * @param message the message to send. If it is empty ("") the actionbar is cleared.
     */
    public static void sendActionbar(Player player, String message) {
        if (player == null || message == null) return;
        String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);

        //1.8.x and 1.9.x
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);

            Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
            Class<?> packet = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
            Object packetPlayOutChat;
            Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion + (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
            Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");

            Method method = null;
            if (nmsVersion.equalsIgnoreCase("v1_8_R1")) method = chat.getDeclaredMethod("a", String.class);

            Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[]{String.class}).newInstance(message);
            packetPlayOutChat = ppoc.getConstructor(new Class[]{chatBaseComponent, Byte.TYPE}).newInstance(object, (byte) 2);

            Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
            Object iCraftPlayer = handle.invoke(craftPlayer);
            Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(iCraftPlayer);
            Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packet);
            sendPacket.invoke(playerConnection, packetPlayOutChat);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean tunnelUnder(Player p) {
        Location location = p.getLocation().clone();
        for (int i = 0; i < 15; i++) {
            location.add(0, -1, 0);
            p.sendMessage("" + p.getWorld().getBlockAt(location).getType());
            if (p.getWorld().getBlockAt(location).getType() == Material.AIR) {
                return true;
            }
        }
        return false;
    }

    private final static int CENTER_PX = 154;

    public static void sendCenteredMessage(Player player, String message) {
        if (message == null || message.equals("")) player.sendMessage("");
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                } else isBold = false;
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
        player.sendMessage(sb.toString() + message);
    }

    public static void sendCenteredHoverableMessage(Player player, List<TextComponent> textComponents) {
        if (textComponents == null || textComponents.size() == 0) ;
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
                if (c == 'l' || c == 'L') {
                    isBold = true;
                } else isBold = false;
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
        }
        player.spigot().sendMessage(componentBuilder.create());
    }

    public static String addCommaAndRound(float amount) {
        amount = Math.round(amount);
        DecimalFormat formatter = new DecimalFormat("#,###");
        String output = formatter.format(amount);
        return output;
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
