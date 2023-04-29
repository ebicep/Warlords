package com.ebicep.warlords.guilds.logs.types.twoplayer;

import com.ebicep.warlords.guilds.logs.AbstractGuildLog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractGuildLogTwoPlayer extends AbstractGuildLog {

    protected UUID sender;
    protected UUID receiver;

    public AbstractGuildLogTwoPlayer() {
    }

    public AbstractGuildLogTwoPlayer(UUID sender, UUID receiver) {
        this.sender = sender;
        this.receiver = receiver;
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
                        .append(getReceiverName())
                        .append(Component.space())
                        .append(append());
    }

    protected Component getSenderName() {
        return Component.text((sender == null || Bukkit.getOfflinePlayer(sender).getName() == null ? "UNKNOWN" : Bukkit.getOfflinePlayer(sender).getName()), NamedTextColor.AQUA);
    }

    protected Component getReceiverName() {
        return Component.text((receiver == null || Bukkit.getOfflinePlayer(receiver).getName() == null ? "UNKNOWN" : Objects.requireNonNull(Bukkit.getOfflinePlayer(receiver)
                                                                                                                                                  .getName())),
                NamedTextColor.AQUA
        );
    }

}
