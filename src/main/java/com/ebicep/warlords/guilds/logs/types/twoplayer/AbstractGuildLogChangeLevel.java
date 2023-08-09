package com.ebicep.warlords.guilds.logs.types.twoplayer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

public abstract class AbstractGuildLogChangeLevel extends AbstractGuildLogTwoPlayer {

    private String before;
    private String after;
    @Field("old_level")
    private int oldLevel;
    @Field("new_level")
    private int newLevel;

    public AbstractGuildLogChangeLevel(UUID sender, UUID receiver, String before, String after, int oldLevel, int newLevel) {
        super(sender, receiver);
        this.before = before;
        this.after = after;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    @Override
    public Component append() {
        return Component.textOfChildren(
                Component.text("from "),
                Component.text(before + "(" + oldLevel + ")", NamedTextColor.GREEN),
                Component.text(" to ", NamedTextColor.GRAY),
                Component.text(after + "(" + newLevel + ")", NamedTextColor.GREEN)
        );
    }

}
