package com.ebicep.warlords.util.bukkit.packets.tablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.tablist.TabViewerSession;
import com.ebicep.warlords.util.bukkit.packets.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Forces vanilla player-info entries to {@code listed=false} for viewers with an active custom tab,
 * so real players never flash on the tab list before an explicit hide update.
 */
public final class TabListPacketListener {

    private TabListPacketListener() {
    }

    public static void register(Warlords instance) {
        PacketUtils.PROTOCOL_MANAGER.addPacketListener(
                new PacketAdapter(instance, ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        rewriteListed(event);
                    }
                }
        );
    }

    private static void rewriteListed(PacketEvent event) {
        Player viewer = event.getPlayer();
        if (!TabViewerSession.isCustomTabActive(viewer.getUniqueId())) {
            return;
        }

        Object handle = event.getPacket().getHandle();
        if (!(handle instanceof ClientboundPlayerInfoUpdatePacket packet)) {
            return;
        }

        EnumSet<Action> actions = packet.actions();
        if (!actions.contains(Action.ADD_PLAYER) && !actions.contains(Action.UPDATE_LISTED)) {
            return;
        }

        List<Entry> entries = packet.entries();
        List<Entry> rewritten = null;
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            if (!entry.listed() || Bukkit.getPlayer(entry.profileId()) == null) {
                continue;
            }
            if (rewritten == null) {
                rewritten = new ArrayList<>(entries);
            }
            rewritten.set(i, new Entry(
                    entry.profileId(),
                    entry.profile(),
                    false,
                    entry.latency(),
                    entry.gameMode(),
                    entry.displayName(),
                    entry.showHat(),
                    entry.listOrder(),
                    entry.chatSession()
            ));
        }

        if (rewritten != null) {
            event.setPacket(PacketContainer.fromPacket(new ClientboundPlayerInfoUpdatePacket(actions, rewritten)));
        }
    }
}
