package com.ebicep.warlords.util.bukkit;

import com.ebicep.warlords.util.chat.ChatUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ComponentBuilder {

    private final List<BaseComponent> parts = new ArrayList<>();
    private TextComponent current;

    public ComponentBuilder(ComponentBuilder original) {
        this.current = new TextComponent(original.current);

        for (BaseComponent baseComponent : original.parts) {
            this.parts.add(baseComponent.duplicate());
        }
    }

    public ComponentBuilder(String text) {
        BaseComponent[] baseComponents = TextComponent.fromLegacyText(text);
        parts.addAll(List.of(baseComponents));
        this.current = new TextComponent("");
    }

    public ComponentBuilder append(String text) {
        return this.append(text, FormatRetention.NONE);
    }

    public ComponentBuilder append(String text, FormatRetention retention) {
        this.parts.add(this.current);
        this.current = new TextComponent(this.current);
        this.current.setText(text);
        this.retain(retention);
        return this;
    }

    public ComponentBuilder retain(FormatRetention retention) {
        BaseComponent previous = this.current;
        switch (retention) {
            case NONE:
                this.current = new TextComponent(this.current.getText());
            case ALL:
            default:
                break;
            case EVENTS:
                this.current = new TextComponent(this.current.getText());
                this.current.setInsertion(previous.getInsertion());
                this.current.setClickEvent(previous.getClickEvent());
                this.current.setHoverEvent(previous.getHoverEvent());
                break;
            case FORMATTING:
                this.current.setClickEvent(null);
                this.current.setHoverEvent(null);
        }

        return this;
    }

    public ComponentBuilder color(org.bukkit.ChatColor color) {
        this.current.setColor(color.asBungee());
        return this;
    }

    public ComponentBuilder color(ChatColor color) {
        this.current.setColor(color);
        return this;
    }

    public ComponentBuilder bold(boolean bold) {
        this.current.setBold(bold);
        return this;
    }

    public ComponentBuilder italic(boolean italic) {
        this.current.setItalic(italic);
        return this;
    }

    public ComponentBuilder underlined(boolean underlined) {
        this.current.setUnderlined(underlined);
        return this;
    }

    public ComponentBuilder strikethrough(boolean strikethrough) {
        this.current.setStrikethrough(strikethrough);
        return this;
    }

    public ComponentBuilder obfuscated(boolean obfuscated) {
        this.current.setObfuscated(obfuscated);
        return this;
    }

    public ComponentBuilder insertion(String insertion) {
        this.current.setInsertion(insertion);
        return this;
    }

    public ComponentBuilder event(ClickEvent clickEvent) {
        this.current.setClickEvent(clickEvent);
        return this;
    }

    public ComponentBuilder reset() {
        return this.retain(ComponentBuilder.FormatRetention.NONE);
    }

    public ComponentBuilder appendHoverItem(ItemStack itemStack) {
        String c = ChatUtils.convertItemStackToJsonRegular(itemStack);
        c = c.substring(0, c.length() - 1) + ",}";
        this.current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new net.md_5.bungee.api.chat.ComponentBuilder(c).create()));
        return this;
    }

    public ComponentBuilder appendHoverItem(String text, ItemStack itemStack) {
        append(text);
        appendHoverItem(itemStack);
        return this;
    }

    public ComponentBuilder appendClickEvent(ClickEvent.Action action, String value) {
        this.current.setClickEvent(new ClickEvent(action, value));
        return this;
    }


    public ComponentBuilder event(HoverEvent hoverEvent) {
        this.current.setHoverEvent(hoverEvent);
        return this;
    }

    public BaseComponent[] prependAndCreate(BaseComponent[] components) {
        this.parts.addAll(0, List.of(components));
        return this.create();
    }

    public BaseComponent[] create() {
        this.parts.add(this.current);
        return this.parts.toArray(new BaseComponent[0]);
    }

    public enum FormatRetention {
        NONE,
        FORMATTING,
        EVENTS,
        ALL;

        FormatRetention() {
        }
    }
}
