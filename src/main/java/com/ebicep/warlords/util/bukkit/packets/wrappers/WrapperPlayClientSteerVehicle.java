package com.ebicep.warlords.util.bukkit.packets.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.world.entity.player.Input;

public class WrapperPlayClientSteerVehicle extends AbstractPacket {

    public static final PacketType TYPE = PacketType.Play.Client.STEER_VEHICLE;

    public WrapperPlayClientSteerVehicle() {
        super(TYPE);
    }

    public WrapperPlayClientSteerVehicle(PacketContainer packet) {
        super(packet, TYPE);
    }

    public boolean getIsJumping() {
        return getInput().jump();
    }

    private Input getInput() {
        return getNmsPacket().input();
    }

    private ServerboundPlayerInputPacket getNmsPacket() {
        return (ServerboundPlayerInputPacket) handle.getHandle();
    }

    private void setInput(Input input) {
        handle.getModifier().write(0, input);
    }

    public void setIsJumping(boolean value) {
        Input input = getInput();
        setInput(new Input(input.forward(), input.backward(), input.left(), input.right(), value, input.shift(), input.sprint()));
    }

    public boolean getIsShiftKeyDown() {
        return getInput().shift();
    }

    public void setIsShiftKeyDown(boolean value) {
        Input input = getInput();
        setInput(new Input(input.forward(), input.backward(), input.left(), input.right(), input.jump(), value, input.sprint()));
    }

}
