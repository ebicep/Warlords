package com.ebicep.warlords.guilds.logs;

import org.bukkit.ChatColor;

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

    public String getFormattedLog() {
        return ChatColor.GRAY + FORMATTER.format(time) + " |" + getLog();
    }

    public abstract String getAction();

    public abstract String getLog();

    public String prepend() {
        return "";
    }

    public String append() {
        return "";
    }

}
