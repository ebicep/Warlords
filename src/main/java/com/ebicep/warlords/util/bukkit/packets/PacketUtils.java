package com.ebicep.warlords.util.bukkit.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.SanctifiedBeacon;
import com.ebicep.warlords.commands.debugcommands.misc.MountCommand;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.packets.wrappers.WrapperPlayClientSteerVehicle;
import com.ebicep.warlords.util.bukkit.packets.wrappers.WrapperPlayServerEntityEquipment;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.world.entity.Entity;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PacketUtils {

    public static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();

    public static void init(Warlords instance) {
        PROTOCOL_MANAGER.removePacketListeners(instance);
        PROTOCOL_MANAGER.addPacketListener(
                new PacketAdapter(instance, ListenerPriority.HIGHEST, PacketType.Play.Server.WORLD_PARTICLES) {
                    int counter = 0;

                    @Override
                    public void onPacketSending(PacketEvent event) {
                        // Item packets (id: 0x29)
                        if (event.getPacketType() == PacketType.Play.Server.WORLD_PARTICLES) {
                            Player player = event.getPlayer();
                            WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
                            if (warlordsEntity == null) {
                                return;
                            }
                            int particleReduction = warlordsEntity.getDatabasePlayer().getParticleQuality().particleReduction;
                            if (counter++ % particleReduction == 0) {
                                event.setCancelled(true);
                            }
                        }
                    }
                });
        List<Sound> blockedSounds = List.of(
                Sound.ENTITY_PLAYER_ATTACK_NODAMAGE,
                Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK
        );
        PROTOCOL_MANAGER.addPacketListener(
                new PacketAdapter(instance, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        StructureModifier<Sound> soundEffects = event.getPacket().getSoundEffects();
                        ClientboundSoundPacket target = (ClientboundSoundPacket) soundEffects.getTarget();
                        if (target.getSound().getRegisteredName().equals("[unregistered]")) {
                            return;
                        }
                        Sound sound = soundEffects.getValues().get(0);
                        if (sound != null && blockedSounds.contains(sound)) {
                            event.setCancelled(true);
                        }
                    }
                }
        );
        PROTOCOL_MANAGER.addPacketListener(
                new PacketAdapter(instance, PacketType.Play.Server.ENTITY_EQUIPMENT) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        PacketContainer packet = event.getPacket().deepClone();
                        int entityID = packet.getIntegers().read(0);
                        Team team = SanctifiedBeacon.BEACON_IDS.get(entityID);
                        if (team == null) {
                            return;
                        }
                        Player playerReceiving = event.getPlayer();
                        WarlordsEntity warlordsPlayer = Warlords.getPlayer(playerReceiving);
                        if (warlordsPlayer == null) {
                            return;
                        }
                        if (warlordsPlayer.getTeam() == team) {
                            return;
                        }
                        WrapperPlayServerEntityEquipment equipmentPacket = new WrapperPlayServerEntityEquipment();
                        equipmentPacket.setEntity(entityID);
                        equipmentPacket.setSlots(List.of(
                                new com.comphenix.protocol.wrappers.Pair<>(EnumWrappers.ItemSlot.HEAD, new ItemStack(Material.BROWN_STAINED_GLASS_PANE))
                        ));
                        event.setPacket(equipmentPacket.getHandle());
                    }
                }
        );
        PROTOCOL_MANAGER.addPacketListener(
                new PacketAdapter(instance, ListenerPriority.LOWEST, PacketType.Play.Client.STEER_VEHICLE) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        if (event.getPacketType() != PacketType.Play.Client.STEER_VEHICLE) {
                            return;
                        }
                        Player player = event.getPlayer();
                        if (!(player.getVehicle() instanceof AbstractHorse)) {
                            return;
                        }
                        boolean isDebugMount = MountCommand.PLAYER_MOUNT_TYPE.containsKey(player.getUniqueId());
                        WrapperPlayClientSteerVehicle steerVehiclePacket = new WrapperPlayClientSteerVehicle(event.getPacket());
                        steerVehiclePacket.setIsJumping(false);
                        if (isDebugMount && steerVehiclePacket.getIsShiftKeyDown()) {
                            org.bukkit.entity.Entity vehicle = player.getVehicle();
                            if (vehicle != null) {
                                event.setCancelled(true);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        vehicle.remove();
                                    }
                                }.runTask(instance);
                                return;
                            }
                        }
                        event.setPacket(steerVehiclePacket.getHandle());
                    }
                }
        );
    }

    public static void removeEntityForPlayer(Player player, int entityId) {
        PROTOCOL_MANAGER.sendServerPacket(player, PacketContainer.fromPacket(new ClientboundRemoveEntitiesPacket(entityId)));
    }

//    public static void spawnEntityForPlayer(Player player, Entity entity) {
//        PROTOCOL_MANAGER.sendServerPacket(player, PacketContainer.fromPacket(new ClientboundAddEntityPacket(entity)));
//    }

    public static void playRightClickAnimationForPlayer(Entity swinger, Player... players) {
        for (Player player : players) {
            PROTOCOL_MANAGER.sendServerPacket(player,
                    PacketContainer.fromPacket(new ClientboundAnimatePacket(swinger, ClientboundAnimatePacket.SWING_MAIN_HAND))
            );
        }
    }

    public static float pingCompensationAmount(WarlordsEntity wp) {
        if (!ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "pingCompensationEnabled", boolean.class) || !(wp.getEntity() instanceof Player player)) {
            return 0;
        }
        int ping = player.getPing();
        int minPingStrikeRangeCompensation = ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "pingCompensationMin", int.class);
        float pingStrikeRangeCompensationDivisor = ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "pingCompensationDivisor", int.class);
        if (ping > minPingStrikeRangeCompensation) {
            float increase = Math.max(0, ping / pingStrikeRangeCompensationDivisor);
            if (ConfigManager.getGameConfigValue(ConfigManager.DEFAULT_NAMESPACES, "pingCompensationLog", boolean.class)) {
                ChatChannels.sendDebugMessage(player, "Ping: " + player.getPing() +
                        ", min: " + minPingStrikeRangeCompensation +
                        ", comp: " + pingStrikeRangeCompensationDivisor +
                        ", increase: " + increase
                );
            }
            return increase;
        }
        return 0;
    }

}
