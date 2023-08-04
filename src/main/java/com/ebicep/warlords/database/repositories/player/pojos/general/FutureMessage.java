package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

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
        if (centered) {
            messages.forEach(message -> ChatUtils.sendCenteredMessage(player, MiniMessage.miniMessage().deserialize(message)));
        } else {
            messages.forEach(player::sendMessage);
        }
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);
    }

}