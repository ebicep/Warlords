package com.ebicep.warlords.util.bukkit;

import com.ebicep.warlords.util.chat.ChatUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

public class TextComponentBuilder {

    private final TextComponent textComponent;

    public TextComponentBuilder() {
        textComponent = new TextComponent();
    }

    public TextComponentBuilder(String text) {
        this.textComponent = new TextComponent(text);
    }

    public TextComponentBuilder setHoverText(String text) {
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(text).create()));
        return this;
    }

    public TextComponentBuilder setHoverItem(ItemStack itemStack) {
        String c = ChatUtils.convertItemStackToJsonRegular(itemStack);
        c = c.substring(0, c.length() - 1) + ",}";
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(c).create()));
        return this;
    }

    public TextComponentBuilder setClickEvent(ClickEvent.Action action, String value) {
        textComponent.setClickEvent(new ClickEvent(action, value));
        return this;
    }

    public TextComponent getTextComponent() {
        return textComponent;
    }
}
