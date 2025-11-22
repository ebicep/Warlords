package com.ebicep.warlords.game.option.pvp;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.general.settings.GlowingMode;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.packets.PacketUtils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class FlagGlowOption implements Option {

    private PacketAdapter packetListener;

    @Override
    public void register(@Nonnull Game game) {
        packetListener = new PacketAdapter(Warlords.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player playerReceiving = event.getPlayer();
                WarlordsEntity warlordsPlayer = Warlords.getPlayer(playerReceiving);
                if (warlordsPlayer == null || !Objects.equals(warlordsPlayer.getGame(), game)) {
                    return;
                }
                DatabasePlayer databasePlayer = DatabaseManager.getPlayer(warlordsPlayer.getUuid());
                if (databasePlayer.getGlowingMode() == GlowingMode.OFF) {
                    return;
                }

                PacketContainer packet = event.getPacket().deepClone();
                int entityID = packet.getIntegers().read(0);
                WarlordsPlayer targetPlayer = game.warlordsPlayers()
                                                  .filter(wp -> wp.getEntity().getEntityId() == entityID)
                                                  .filter(wp -> wp.getTeam() == warlordsPlayer.getTeam())
                                                  .filter(WarlordsEntity::hasFlag)
                                                  .findFirst()
                                                  .orElse(null);
                if (targetPlayer == null || playerReceiving == targetPlayer) {
                    return;
                }
                List<WrappedDataValue> metadata = packet.getDataValueCollectionModifier().read(0);
                WrappedDataValue bitMasks = metadata.stream()
                                                    .filter(wrappedWatchableObject -> wrappedWatchableObject.getIndex() == 0)
                                                    .findAny()
                                                    .orElse(null);
                if (bitMasks == null) {
                    return;
                }
                byte bitMask = (byte) bitMasks.getValue();
                bitMask = (byte) (bitMask | 0x40);
                bitMasks.setValue(bitMask);
                event.setPacket(packet);
            }
        };
        PacketUtils.PROTOCOL_MANAGER.addPacketListener(packetListener);
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        PacketUtils.PROTOCOL_MANAGER.removePacketListener(packetListener);
    }

}
