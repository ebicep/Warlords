package com.ebicep.warlords.util.bukkit;

import co.aikar.commands.CommandIssuer;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class PacketUtils {

    private static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();

    public static void removeEntityForPlayer(Player player, int entityId) {
        try {
            PROTOCOL_MANAGER.sendServerPacket(player, PacketContainer.fromPacket(new ClientboundRemoveEntitiesPacket(entityId)));
        } catch (InvocationTargetException e) {
            ChatChannels.sendDebugMessage((CommandIssuer) null, "Error sending entity destroy packet");
            throw new RuntimeException(e);
        }
    }

    public static void playRightClickAnimationForPlayer(Entity swinger, Player... players) {
        for (Player player : players) {
            try {
                PROTOCOL_MANAGER.sendServerPacket(player,
                        PacketContainer.fromPacket(new ClientboundAnimatePacket(swinger, ClientboundAnimatePacket.SWING_MAIN_HAND))
                );
            } catch (InvocationTargetException e) {
                ChatChannels.sendDebugMessage((CommandIssuer) null, "Error sending right click packet");
                throw new RuntimeException(e);
            }
        }
    }

    public static void sendTitle(UUID uuid, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.isOnline()) {
            sendTitle(offlinePlayer.getPlayer(), title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public static void sendTabHF(Player player, String header, String footer) {
        player.setPlayerListHeader(header);
        player.setPlayerListFooter(footer);
    }


    public static void sendActionBar(Player p, String message) {
        p.sendActionBar(message);
    }

}
