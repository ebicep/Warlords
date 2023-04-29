package com.ebicep.warlords.guilds.logs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public abstract class AbstractGuildLog {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("LLL dd yyyy HH:mm:ss z")
            .withZone(ZoneId.of("America/New_York"));

    protected Instant time = Instant.now();

    public AbstractGuildLog() {
    }

    public Component getFormattedLog() {
        return Component.textOfChildren(
                Component.text(FORMATTER.format(time) + " |", NamedTextColor.GRAY),
                getLog()
        );
    }

    public abstract String getAction();

    public abstract Component getLog();

    public Component prepend() {
        return Component.empty();
    }

    public Component append() {
        return Component.empty();
    }

}
