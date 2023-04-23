package com.ebicep.warlords.util.bukkit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ComponentUtils {

    @Nonnull
    public static TextComponent nonItalicBase() {
        return Component.empty().decoration(TextDecoration.ITALIC, false);
    }

    public static List<Component> flattenComponents(List<Component> component) {
        List<Component> components = new ArrayList<>();
        for (Component c : component) {
            List<Component> collection = flattenComponent(c);
            components.addAll(collection);
        }
        return components;
    }

    public static List<Component> flattenComponent(Component component) {
        List<Component> output = new ArrayList<>();
        Component toAdd = null;
        List<Component> components = new ArrayList<>(component.children());
        components.add(0, component.children(new ArrayList<>()));

        for (Component child : components) {
            if (child.equals(Component.newline())) {
                if (toAdd != null) {
                    output.add(toAdd);
                }
                output.add(Component.empty());
                toAdd = null;

            } else {
                if (toAdd == null) {
                    toAdd = child;
                } else {
                    toAdd = toAdd.append(child);
                }
            }
        }
        if (toAdd != null) {
            output.add(toAdd);
        }
        return output;
    }

}
