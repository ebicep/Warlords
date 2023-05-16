package com.ebicep.warlords.util.bukkit;

import co.aikar.commands.CommandIssuer;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.world.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

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

    public static void spawnEntityForPlayer(Player player, Entity entity) {
        try {
            PROTOCOL_MANAGER.sendServerPacket(player, PacketContainer.fromPacket(new ClientboundAddEntityPacket(entity)));
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

}
