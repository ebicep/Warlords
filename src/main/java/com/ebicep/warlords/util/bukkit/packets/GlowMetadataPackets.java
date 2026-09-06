package com.ebicep.warlords.util.bukkit.packets;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.ebicep.warlords.util.bukkit.packets.wrappers.WrapperPlayServerEntityMetadata;

import java.util.List;

/**
 * Clone-on-write helpers for applying the glowing shared-flags bit (0x40) to ENTITY_METADATA.
 * Callers must gate before invoking — player metadata packets are broadcast-shared.
 */
public final class GlowMetadataPackets {

    private static final byte GLOWING_BIT = 0x40;

    private GlowMetadataPackets() {
    }

    public static int getEntityId(PacketEvent event) {
        return event.getPacket().getIntegers().read(0);
    }

    /**
     * Deep-clones the packet and ORs the glowing bit into metadata index 0 when present.
     * No-ops (leaves the original packet) if index 0 is absent.
     */
    public static void applyGlowingBit(PacketEvent event) {
        PacketContainer packet = event.getPacket().deepClone();
        WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(packet);
        List<WrappedDataValue> packedItems = metadata.getPackedItems();
        WrappedDataValue bitMasks = null;
        for (WrappedDataValue packedItem : packedItems) {
            if (packedItem.getIndex() == 0) {
                bitMasks = packedItem;
                break;
            }
        }
        if (bitMasks == null) {
            return;
        }
        bitMasks.setValue((byte) ((byte) bitMasks.getValue() | GLOWING_BIT));
        event.setPacket(packet);
    }
}
