package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import javax.annotation.Nullable;

public class AnnounceWaveWaveAction implements WaveAction<TowerDefenseOption> {

    @Nullable
    private Title title = null;
    private Component chatMessage = null;

    public AnnounceWaveWaveAction addTitle(Title title) {
        this.title = title;
        return this;
    }

    public AnnounceWaveWaveAction addChatMessage(Component chatMessage) {
        this.chatMessage = chatMessage;
        return this;
    }

    @Override
    public boolean tick(TowerDefenseOption pveOption) {
        pveOption.getGame().forEachOnlinePlayer((player, team) -> {
            if (title != null) {
                player.showTitle(title);
            }
            if (chatMessage != null) {
                player.sendMessage(chatMessage);
            }
        });
        return true;
    }

}
