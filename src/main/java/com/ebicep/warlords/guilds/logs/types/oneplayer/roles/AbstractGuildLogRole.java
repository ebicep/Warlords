package com.ebicep.warlords.guilds.logs.types.oneplayer.roles;

import com.ebicep.warlords.guilds.logs.types.oneplayer.AbstractGuildLogOnePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.UUID;

public abstract class AbstractGuildLogRole extends AbstractGuildLogOnePlayer {

    protected String role;

    public AbstractGuildLogRole(UUID sender, String role) {
        super(sender);
        this.role = role;
    }

    @Override
    public Component getLog() {
        return Component.empty().color(NamedTextColor.GRAY)
                        .append(prepend())
                        .append(Component.space())
                        .append(getSenderName())
                        .append(Component.space())
                        .append(Component.text(getAction(), NamedTextColor.YELLOW))
                        .append(Component.space())
                        .append(Component.text(role, NamedTextColor.GREEN))
                        .append(Component.space())
                        .append(append());
    }
}
