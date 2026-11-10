package com.ebicep.warlords.game.option;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.general.settings.GlowingMode;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.packets.GlowMetadataPackets;
import com.ebicep.warlords.util.bukkit.packets.PacketUtils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GlowingTeamOption implements Option {

    /** entityId → player UUID for O(1) gating; team is read live to survive mid-game swaps. */
    private final Map<Integer, UUID> entityUuids = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> entityIdByUuid = new ConcurrentHashMap<>();
    private PacketAdapter packetListener;

    @Override
    public void register(@Nonnull Game game) {
        packetListener = new PacketAdapter(Warlords.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                int entityId = GlowMetadataPackets.getEntityId(event);
                UUID targetUuid = entityUuids.get(entityId);
                if (targetUuid == null) {
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
                WarlordsEntity target = Warlords.getPlayer(targetUuid);
                if (target == null || warlordsPlayer.getTeam() != target.getTeam()) {
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
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            trackEntity(player);
        }
    }

    @Override
    public void onPlayerReJoinGame(Player player) {
        WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
        if (warlordsEntity instanceof WarlordsPlayer) {
            trackEntity(warlordsEntity);
        }
    }

    @Override
    public void onPlayerQuit(Player player) {
        Integer entityId = entityIdByUuid.remove(player.getUniqueId());
        if (entityId != null) {
            entityUuids.remove(entityId);
        }
    }

    private void trackEntity(WarlordsEntity player) {
        int entityId = player.getEntity().getEntityId();
        Integer previousId = entityIdByUuid.put(player.getUuid(), entityId);
        if (previousId != null) {
            entityUuids.remove(previousId);
        }
        entityUuids.put(entityId, player.getUuid());
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        PacketUtils.PROTOCOL_MANAGER.removePacketListener(packetListener);
        entityUuids.clear();
        entityIdByUuid.clear();
    }

}
