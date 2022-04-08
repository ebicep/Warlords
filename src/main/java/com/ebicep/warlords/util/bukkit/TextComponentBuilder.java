package com.ebicep.warlords.util.bukkit;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TextComponentBuilder {

    private final TextComponent textComponent;

    public TextComponentBuilder() {
        textComponent = new TextComponent();
    }

    public TextComponentBuilder(String text) {
        this.textComponent = new TextComponent(text);
    }

    public TextComponentBuilder setHover(HoverEvent hoverEvent) {
        textComponent.setHoverEvent(hoverEvent);
        return this;
    }

    public TextComponentBuilder setHoverText(String text) {
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(text).create()));
        return this;
    }

    public TextComponent getTextComponent() {
        return textComponent;
    }
}
