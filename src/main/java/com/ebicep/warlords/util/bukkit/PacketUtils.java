package com.ebicep.warlords.util.bukkit;

import co.aikar.commands.CommandIssuer;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.world.entity.Entity;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class PacketUtils {

    private static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();

    public static void init(Warlords instance) {
        ProtocolManager protocolManager;
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.removePacketListeners(instance);
        protocolManager.addPacketListener(
                new PacketAdapter(instance, ListenerPriority.HIGHEST, PacketType.Play.Server.WORLD_PARTICLES) {
                    int counter = 0;

                    @Override
                    public void onPacketSending(PacketEvent event) {
                        // Item packets (id: 0x29)
                        if (event.getPacketType() == PacketType.Play.Server.WORLD_PARTICLES) {
                            Player player = event.getPlayer();
                            if (Warlords.hasPlayer(player)) {
                                if (counter++ % PlayerSettings.PLAYER_SETTINGS.get(player.getUniqueId()).getParticleQuality().particleReduction == 0) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                });
        List<Sound> blockedSounds = List.of(
                Sound.ENTITY_PLAYER_ATTACK_NODAMAGE,
                Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK
        );
        protocolManager.addPacketListener(

                new PacketAdapter(instance, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        Sound sound = event.getPacket().getSoundEffects().getValues().get(0);
                        if (sound != null && blockedSounds.contains(sound)) {
                            event.setCancelled(true);
                        }
                    }
                });
    }

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
