package com.ebicep.warlords.util.bukkit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ComponentBuilder {

    public static ComponentBuilder create() {
        return new ComponentBuilder("");
    }

    public static ComponentBuilder create(String text) {
        return new ComponentBuilder(text);
    }

    public static ComponentBuilder create(String text, TextColor textColor) {
        return new ComponentBuilder(text, textColor);
    }

    public static ComponentBuilder create(String text, TextColor textColor, TextDecoration... textDecoration) {
        return new ComponentBuilder(text, textColor, textDecoration);
    }

    public static ComponentBuilder text() {
        return new ComponentBuilder("");
    }

    private final TextComponent.Builder componentBuilder;

    public ComponentBuilder(String text) {
        this.componentBuilder = Component.text(text).toBuilder();
    }

    public ComponentBuilder(String text, TextColor textColor) {
        this.componentBuilder = Component.text(text, textColor).toBuilder();
    }

    public ComponentBuilder(String text, TextColor textColor, TextDecoration... textDecoration) {
        this.componentBuilder = Component.text(text, textColor, textDecoration).toBuilder();
    }

    public ComponentBuilder append(Component component) {
        componentBuilder.append(component);
        return this;
    }

    public ComponentBuilder text(String text) {
        componentBuilder.append(Component.text(text));
        return this;
    }

    public ComponentBuilder text(String text, TextColor textColor) {
        componentBuilder.append(Component.text(text, textColor));
        return this;
    }

    public ComponentBuilder text(String text, TextColor textColor, TextDecoration... textDecoration) {
        componentBuilder.append(Component.text(text, textColor, textDecoration));
        return this;
    }

    public ComponentBuilder text(float text) {
        componentBuilder.append(Component.text(text));
        return this;
    }

    public ComponentBuilder text(float text, TextColor textColor) {
        componentBuilder.append(Component.text(text, textColor));
        return this;
    }

    public ComponentBuilder text(float text, TextColor textColor, TextDecoration... textDecoration) {
        componentBuilder.append(Component.text(text, textColor, textDecoration));
        return this;
    }

    public TextComponent build() {
        return componentBuilder.build();
    }

}
