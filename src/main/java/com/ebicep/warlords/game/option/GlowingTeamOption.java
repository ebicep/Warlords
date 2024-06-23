package com.ebicep.warlords.game.option;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.general.Settings;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.packets.PacketUtils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GlowingTeamOption implements Option {

    private Game game;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        PacketUtils.PROTOCOL_MANAGER.addPacketListener(new PacketAdapter(Warlords.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player playerReceiving = event.getPlayer();
                WarlordsEntity warlordsPlayer = Warlords.getPlayer(playerReceiving);
                if (warlordsPlayer == null || !Objects.equals(warlordsPlayer.getGame(), game)) {
                    return;
                }
                DatabaseManager.getPlayer(warlordsPlayer.getUuid(), databasePlayer -> {
                    if (databasePlayer.getGlowingMode() == Settings.GlowingMode.OFF) {
                        return;
                    }

                    PacketContainer packet = event.getPacket().deepClone();
                    int entityID = packet.getIntegers().read(0);
                    Player targetPlayer = game.onlinePlayers()
                                              .filter(playerTeamEntry -> playerTeamEntry.getValue() == warlordsPlayer.getTeam())
                                              .filter(playerTeamEntry -> playerTeamEntry.getKey().getEntityId() == entityID)
                                              .map(Map.Entry::getKey)
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
                });
            }
        });
        //TODO fix changing teams and not moving still showing glow
    }
}
