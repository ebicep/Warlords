package com.ebicep.warlords.guilds.logs.types.oneplayer;

import com.ebicep.warlords.guilds.logs.AbstractGuildLog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

import java.util.UUID;

public abstract class AbstractGuildLogOnePlayer extends AbstractGuildLog {

    protected UUID sender;

    public AbstractGuildLogOnePlayer() {
    }

    public AbstractGuildLogOnePlayer(UUID sender) {
        this.sender = sender;
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
                        .append(append());
    }

    protected Component getSenderName() {
        return Component.text((sender == null || Bukkit.getOfflinePlayer(sender).getName() == null ? "UNKNOWN" : Bukkit.getOfflinePlayer(sender).getName()), NamedTextColor.AQUA);
    }

}
