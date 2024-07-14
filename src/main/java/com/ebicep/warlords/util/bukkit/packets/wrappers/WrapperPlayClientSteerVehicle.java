package com.ebicep.warlords.util.bukkit.packets.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientSteerVehicle extends AbstractPacket {

    public static final PacketType TYPE = PacketType.Play.Client.STEER_VEHICLE;

    public WrapperPlayClientSteerVehicle() {
        super(TYPE);
    }

    public WrapperPlayClientSteerVehicle(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieves the value of field 'xxa'
     *
     * @return 'xxa'
     */
    public float getXxa() {
        return this.handle.getFloat().read(0);
    }

    /**
     * Sets the value of field 'xxa'
     *
     * @param value New value for field 'xxa'
     */
    public void setXxa(float value) {
        this.handle.getFloat().write(0, value);
    }

    /**
     * Retrieves the value of field 'zza'
     *
     * @return 'zza'
     */
    public float getZza() {
        return this.handle.getFloat().read(1);
    }

    /**
     * Sets the value of field 'zza'
     *
     * @param value New value for field 'zza'
     */
    public void setZza(float value) {
        this.handle.getFloat().write(1, value);
    }

    /**
     * Retrieves the value of field 'isJumping'
     *
     * @return 'isJumping'
     */
    public boolean getIsJumping() {
        return this.handle.getBooleans().read(0);
    }

    /**
     * Sets the value of field 'isJumping'
     *
     * @param value New value for field 'isJumping'
     */
    public void setIsJumping(boolean value) {
        this.handle.getBooleans().write(0, value);
    }

    /**
     * Retrieves the value of field 'isShiftKeyDown'
     *
     * @return 'isShiftKeyDown'
     */
    public boolean getIsShiftKeyDown() {
        return this.handle.getBooleans().read(1);
    }

    /**
     * Sets the value of field 'isShiftKeyDown'
     *
     * @param value New value for field 'isShiftKeyDown'
     */
    public void setIsShiftKeyDown(boolean value) {
        this.handle.getBooleans().write(1, value);
    }

}