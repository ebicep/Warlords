package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class FutureMessage {

    public static FutureMessage create(List<Component> messages, boolean centered) {
        return new FutureMessage(messages.stream().map(component -> MiniMessage.miniMessage().serialize(component)).toList(), centered);
    }

    private List<String> messages;
    private boolean centered;

    public FutureMessage(List<String> messages, boolean centered) {
        this.messages = messages;
        this.centered = centered;
    }

    public void sendToPlayer(Player player) {
        messages.removeIf(Objects::isNull);
        if (centered) {
            messages.forEach(message -> {
                if (message.contains("§")) {
                    ChatUtils.sendCenteredMessage(player, LegacyComponentSerializer.legacySection().deserialize(message));
                } else {
                    ChatUtils.sendCenteredMessage(player, MiniMessage.miniMessage().deserialize(message));
                }
            });
        } else {
            messages.forEach(player::sendMessage);
        }
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);
    }

}