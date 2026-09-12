package com.ebicep.warlords.util.bukkit.packets.tablist;

import com.comphenix.protocol.events.PacketContainer;
import com.ebicep.warlords.util.bukkit.packets.PacketUtils;
import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry;
import net.minecraft.world.level.GameType;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 * Sends fake tab-list player-info packets to a single viewer.
 * Does not manage scoreboards, teams, or lifecycle.
 */
public final class TabListPackets {

    private static final EnumSet<Action> ADD_ACTIONS = EnumSet.of(
            Action.ADD_PLAYER,
            Action.UPDATE_GAME_MODE,
            Action.UPDATE_LISTED,
            Action.UPDATE_LATENCY,
            Action.UPDATE_DISPLAY_NAME
    );

    private TabListPackets() {
    }

    public static void add(Player viewer, TabListEntry... entries) {
        add(viewer, Arrays.asList(entries));
    }

    public static void add(Player viewer, Collection<TabListEntry> entries) {
        if (entries.isEmpty()) {
            return;
        }
        sendUpdate(viewer, ADD_ACTIONS, toEntries(entries));
    }

    public static void updateDisplayName(Player viewer, TabListEntry... entries) {
        updateDisplayName(viewer, Arrays.asList(entries));
    }

    public static void updateDisplayName(Player viewer, Collection<TabListEntry> entries) {
        if (entries.isEmpty()) {
            return;
        }
        sendUpdate(viewer, EnumSet.of(Action.UPDATE_DISPLAY_NAME), toEntries(entries));
    }

    public static void updateLatency(Player viewer, TabListEntry... entries) {
        updateLatency(viewer, Arrays.asList(entries));
    }

    public static void updateLatency(Player viewer, Collection<TabListEntry> entries) {
        if (entries.isEmpty()) {
            return;
        }
        sendUpdate(viewer, EnumSet.of(Action.UPDATE_LATENCY), toEntries(entries));
    }

    public static void updateListed(Player viewer, TabListEntry... entries) {
        updateListed(viewer, Arrays.asList(entries));
    }

    public static void updateListed(Player viewer, Collection<TabListEntry> entries) {
        if (entries.isEmpty()) {
            return;
        }
        sendUpdate(viewer, EnumSet.of(Action.UPDATE_LISTED), toEntries(entries));
    }

    public static void remove(Player viewer, UUID... ids) {
        remove(viewer, Arrays.asList(ids));
    }

    public static void remove(Player viewer, Collection<UUID> ids) {
        if (ids.isEmpty()) {
            return;
        }
        PacketUtils.PROTOCOL_MANAGER.sendServerPacket(
                viewer,
                PacketContainer.fromPacket(new ClientboundPlayerInfoRemovePacket(List.copyOf(ids)))
        );
    }

    private static void sendUpdate(Player viewer, EnumSet<Action> actions, List<Entry> entries) {
        PacketUtils.PROTOCOL_MANAGER.sendServerPacket(
                viewer,
                PacketContainer.fromPacket(new ClientboundPlayerInfoUpdatePacket(actions, entries))
        );
    }

    private static List<Entry> toEntries(Collection<TabListEntry> entries) {
        List<Entry> nmsEntries = new ArrayList<>(entries.size());
        for (TabListEntry entry : entries) {
            nmsEntries.add(toNmsEntry(entry));
        }
        return nmsEntries;
    }

    private static Entry toNmsEntry(TabListEntry entry) {
        return new Entry(
                entry.uuid(),
                createProfile(entry),
                entry.listed(),
                entry.latency(),
                GameType.SURVIVAL,
                toVanilla(entry.displayName()),
                true,
                0,
                null
        );
    }

    private static GameProfile createProfile(TabListEntry entry) {
        if (entry.skinTexture() == null) {
            return new GameProfile(entry.uuid(), entry.profileName());
        }
        Property textures = entry.skinSignature() != null
                ? new Property("textures", entry.skinTexture(), entry.skinSignature())
                : new Property("textures", entry.skinTexture());
        PropertyMap properties = new PropertyMap(ImmutableMultimap.of("textures", textures));
        return new GameProfile(entry.uuid(), entry.profileName(), properties);
    }

    @Nullable
    private static net.minecraft.network.chat.Component toVanilla(@Nullable net.kyori.adventure.text.Component displayName) {
        return displayName == null ? null : PaperAdventure.asVanilla(displayName);
    }
}
