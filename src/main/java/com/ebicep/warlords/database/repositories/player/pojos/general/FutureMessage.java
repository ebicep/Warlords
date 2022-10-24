package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class FutureMessage {
    private List<String> messages;
    private boolean centered;

    public FutureMessage(List<String> messages, boolean centered) {
        this.messages = messages;
        this.centered = centered;
    }

    public void sendToPlayer(Player player) {
        if (centered) {
            messages.forEach(message -> ChatUtils.sendCenteredMessage(player, message));
        } else {
            messages.forEach(player::sendMessage);
        }
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 500, 2);
    }

}