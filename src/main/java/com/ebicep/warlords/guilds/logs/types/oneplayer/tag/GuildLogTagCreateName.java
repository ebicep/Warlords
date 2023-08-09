package com.ebicep.warlords.guilds.logs.types.oneplayer.tag;

import com.ebicep.warlords.guilds.logs.types.oneplayer.AbstractGuildLogOnePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

public class GuildLogTagCreateName extends AbstractGuildLogOnePlayer {

    private String name;

    public GuildLogTagCreateName(UUID sender, String name) {
        super(sender);
        this.name = name;
    }

    @Override
    public Component getAction() {
        return Component.text("created guild tag name ").append(Component.text(name, NamedTextColor.GREEN));
    }
}
