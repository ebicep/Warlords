package com.ebicep.warlords.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.concurrency.BlockingHashMap;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.SafeCacheBuilder;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.RemovalCause;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class PacketManager implements Listener {

    // WIP

    Player player;
    WarlordsPlayer wp;
    ProtocolManager protocolManager;
    ProtocolLibrary protocolLibrary;
    PacketType packetType;

    private void updatePlayerSettings() {
        PlayerSettings packetPlayerSettings;

    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public ProtocolLibrary getProtocolLibrary() {
        return protocolLibrary;
    }

    public PacketType GetPacketType() {
        return packetType;
    }

    public void addPacket() {
        //protocolManager.createPacket(PacketType.findCurrent(this, player.getPlayer(), "Test"));
        //protocolManager.addPacketListener();
    }

    //if (PacketType.Sender instanceof Player) {
    //}

    public static <TKey, TValue> CacheLoader<TKey, TValue> newInvalidCacheLoader() {
        return new CacheLoader<TKey, TValue>() {
            @Override
            public TValue load(TKey key) throws Exception {
                throw new IllegalStateException("Test");
            }
        };
    }

    public synchronized void removeAll(Iterable<? extends PacketType> types) {
        for (PacketType type : types) {
            //removeType(type);
        }
    }

    /*public void BlockingHashMap() {
        backingMap = SafeCacheBuilder.<TKey, TValue>newBuilder().
                weakValues().
                removalListener(
                        new RemovalListener<TKey, TValue>() {
                            @Override
                            public void onRemoval(RemovalNotification<TKey, TValue> entry) {
                                // Clean up locks too
                                if (entry.getCause() != RemovalCause.REPLACED) {
                                    locks.remove(entry.getKey());
                                }
                            }
                        }).
                build(BlockingHashMap.<TKey, TValue> newInvalidCacheLoader());

        //
        locks = new ConcurrentHashMap<TKey, Object>();
    }*/

    // 50% of Particle Packets
    public void reduceParticlesByHalf() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(
                new PacketAdapter(Warlords.getInstance(), ListenerPriority.HIGHEST,
                        PacketType.Play.Server.WORLD_PARTICLES) {
                    int counter = 0;
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        // Item packets (id: 0x29)
                        if (event.getPacketType() ==
                                PacketType.Play.Server.WORLD_PARTICLES) {
                            if (counter++ % 2 == 0) {
                                event.setCancelled(true);
                            }
                        }
                    }
                });
    }

    // 75% of Particle Packets
    public void reduceParticlesByQuarter() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(
                new PacketAdapter(Warlords.getInstance(), ListenerPriority.HIGHEST,
                        PacketType.Play.Server.WORLD_PARTICLES) {
                    int counter = 0;
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        // Item packets (id: 0x29)
                        if (event.getPacketType() ==
                                PacketType.Play.Server.WORLD_PARTICLES) {
                            if (counter++ % 4 == 0) {
                                event.setCancelled(true);
                            }
                        }
                    }
                });
    }
}
