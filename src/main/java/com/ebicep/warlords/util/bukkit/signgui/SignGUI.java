package com.ebicep.warlords.util.bukkit.signgui;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.bukkit.packets.WrapperPlayClientUpdateSign;
import com.ebicep.warlords.util.bukkit.packets.WrapperPlayServerBlockChange;
import com.ebicep.warlords.util.bukkit.packets.WrapperPlayServerOpenSignEntity;
import com.ebicep.warlords.util.bukkit.packets.WrapperPlayServerUpdateSign;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SignGUI {

    public static ProtocolManager protocolManager;
    public static PacketAdapter packetListener;
    public static Map<UUID, SignGUIListener> listeners;
    public static Map<UUID, BlockPosition> signLocations;

    public static void init(Warlords warlords) {
        protocolManager = ProtocolLibrary.getProtocolManager();
        packetListener = new SignPacketListener(warlords);
        protocolManager.addPacketListener(packetListener);
        listeners = new ConcurrentHashMap<>();
        signLocations = new ConcurrentHashMap<>();
    }

    public static void open(Player player, SignGUIListener response) {
        open(player, new String[]{"", "", "", ""}, response);
    }

    public static void open(Player player, String[] text, SignGUIListener response) {
        //make sure all four lines are filled
        String[] textFilled = new String[]{"", "", "", ""};
        System.arraycopy(text, 0, textFilled, 0, text.length);
        //location of temp sign
        int x = player.getLocation().getBlockX();
        int y = 255;
        int z = player.getLocation().getBlockZ();
        BlockPosition blockPosition = new BlockPosition(x, y, z);

        WrapperPlayServerBlockChange blockChangePacket = new WrapperPlayServerBlockChange();
        WrappedBlockData blockData = WrappedBlockData.createData(Material.OAK_SIGN);
        blockChangePacket.setBlockData(blockData);
        blockChangePacket.setLocation(blockPosition);
        blockChangePacket.sendPacket(player);

        WrapperPlayServerUpdateSign updateSignPacket = new WrapperPlayServerUpdateSign();
        updateSignPacket.setLocation(new BlockPosition(x, y, z));
        WrappedChatComponent[] lines = new WrappedChatComponent[4];
        lines[0] = WrappedChatComponent.fromText(textFilled[0]);
        lines[1] = WrappedChatComponent.fromText(textFilled[1]);
        lines[2] = WrappedChatComponent.fromText(textFilled[2]);
        lines[3] = WrappedChatComponent.fromText(textFilled[3]);
        updateSignPacket.setLines(lines);
        updateSignPacket.sendPacket(player);

        //open the gui
        WrapperPlayServerOpenSignEntity packet = new WrapperPlayServerOpenSignEntity();
        packet.setLocation(new BlockPosition(x, y, z));
        packet.sendPacket(player);

        signLocations.put(player.getUniqueId(), blockPosition);
        listeners.put(player.getUniqueId(), response);
    }

    public static void destroy() {
        protocolManager.removePacketListener(packetListener);
        listeners.clear();
        signLocations.clear();
    }

    public interface SignGUIListener {
        void onSignDone(Player player, String[] lines);
    }

    static class SignPacketListener extends PacketAdapter {

        private final Plugin plugin;

        public SignPacketListener(Plugin plugin) {
            super(plugin, PacketType.Play.Client.UPDATE_SIGN);
            this.plugin = plugin;
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            String[] text = new String[4];
            Player player = event.getPlayer();
            WrapperPlayClientUpdateSign packet = new WrapperPlayClientUpdateSign(event.getPacket());
            BlockPosition blockPosition = packet.getLocation();

            BlockPosition playerBlockPos = signLocations.get(player.getUniqueId());
            if (playerBlockPos != null) {
                //we only care about update packets from the player block location since that's the location we sent our open packet to
                if (!blockPosition.equals(playerBlockPos)) return;

                for (int i = 0; i < packet.getLines().length; i++) {
                    WrappedChatComponent chat = packet.getLines()[i];
                    String str = StringEscapeUtils.unescapeJavaScript(chat.getJson());
                    str = str.substring(1, str.length() - 1);
                    text[i] = str;
                }

                WrapperPlayServerBlockChange blockChangePacket = new WrapperPlayServerBlockChange();
                WrappedBlockData blockData = WrappedBlockData.createData(Material.AIR);
                blockChangePacket.setBlockData(blockData);
                blockChangePacket.setLocation(playerBlockPos);
                blockChangePacket.sendPacket(player);

                signLocations.remove(player.getUniqueId());

                SignGUIListener listener = listeners.remove(player.getUniqueId());
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> listener.onSignDone(player, text));
            }
        }

    }

}