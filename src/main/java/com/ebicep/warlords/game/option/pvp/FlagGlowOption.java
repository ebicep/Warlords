package com.ebicep.warlords.game.option.pvp;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.game.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.flags.PlayerFlagLocation;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.general.settings.GlowingMode;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.packets.GlowMetadataPackets;
import com.ebicep.warlords.util.bukkit.packets.PacketUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FlagGlowOption implements Option {

    private final Map<Integer, Team> glowingCarriers = new ConcurrentHashMap<>();
    private PacketAdapter packetListener;

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(new Listener() {
            @EventHandler
            public void onFlagChange(WarlordsFlagUpdatedEvent event) {
                if (event.getOld() instanceof PlayerFlagLocation oldCarrier) {
                    glowingCarriers.remove(oldCarrier.getPlayer().getEntity().getEntityId());
                }
                if (event.getNew() instanceof PlayerFlagLocation newCarrier) {
                    WarlordsEntity player = newCarrier.getPlayer();
                    glowingCarriers.put(player.getEntity().getEntityId(), player.getTeam());
                }
            }
        });

        packetListener = new PacketAdapter(Warlords.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (glowingCarriers.isEmpty()) {
                    return;
                }
                int entityId = GlowMetadataPackets.getEntityId(event);
                Team carrierTeam = glowingCarriers.get(entityId);
                if (carrierTeam == null) {
                    return;
                }
                Player playerReceiving = event.getPlayer();
                if (playerReceiving.getEntityId() == entityId) {
                    return;
                }
                WarlordsEntity warlordsPlayer = Warlords.getPlayer(playerReceiving);
                if (warlordsPlayer == null || !Objects.equals(warlordsPlayer.getGame(), game)) {
                    return;
                }
                if (warlordsPlayer.getTeam() != carrierTeam) {
                    return;
                }
                DatabasePlayer databasePlayer = DatabaseManager.getPlayer(warlordsPlayer.getUuid());
                if (databasePlayer.getGlowingMode() == GlowingMode.OFF) {
                    return;
                }
                GlowMetadataPackets.applyGlowingBit(event);
            }
        };
        PacketUtils.PROTOCOL_MANAGER.addPacketListener(packetListener);
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        PacketUtils.PROTOCOL_MANAGER.removePacketListener(packetListener);
        glowingCarriers.clear();
    }

}
