package com.ebicep.warlords.util.bukkit.packets;

/**
 * This file is part of PacketWrapper.
 * Copyright (C) 2012-2015 Kristian S. Strangeland
 * Copyright (C) 2015 dmulloy2
 * <p>
 * PacketWrapper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * PacketWrapper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with PacketWrapper.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class WrapperPlayServerUpdateSign extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.UPDATE_SIGN;

    public WrapperPlayServerUpdateSign() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerUpdateSign(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Location.
     * <p>
     * Notes: block Coordinates
     * @return The current Location
     */
    public BlockPosition getLocation() {
        return handle.getBlockPositionModifier().read(0);
    }

    /**
     * Set Location.
     * @param value - new value.
     */
    public void setLocation(BlockPosition value) {
        handle.getBlockPositionModifier().write(0, value);
    }

    /**
     * Retrieve this sign's lines of text represented by a chat component array.
     * @return The current lines
     */
    public WrappedChatComponent[] getLines() {
        return handle.getChatComponentArrays().read(0);
    }

    /**
     * Set this sign's lines of text.
     * @param value - Lines, must be 4 elements long
     */
    public void setLines(WrappedChatComponent[] value) {
        if (value == null)
            throw new IllegalArgumentException("value cannot be null!");
        if (value.length != 4)
            throw new IllegalArgumentException("value must have 4 elements!");

        handle.getChatComponentArrays().write(0, value);
    }
}