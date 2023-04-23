package com.ebicep.warlords.util.bukkit;

import com.ebicep.warlords.util.chat.DefaultFontInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class WordWrap {

    @Deprecated
    public static String wrapWithNewline(String line, int width) {
        if (line.contains("\n")) {
            StringBuilder sb = new StringBuilder();
            for (String s : line.split("\n")) {
                sb.append(wrapWithNewline(s, width)).append("\n");
            }
            sb.setLength(sb.length() - 1);
            return sb.toString();
        }
        StringBuilder output = new StringBuilder(line.length() + 16);
        int lastOffset = 0;
        int previousOffset = 0;
        int currentLength = 0;
        for (int pendingOffset = 0; pendingOffset < line.length(); pendingOffset++) {
            char c = line.charAt(pendingOffset);
            int length;
            if (c == '§' || (pendingOffset - 1 > 0 && line.charAt(pendingOffset - 1) == '§')) {
                length = 0;
            } else {
                length = DefaultFontInfo.getDefaultFontInfo(c).getLength();
            }
            currentLength += length;
            if (Character.isWhitespace(c)) {
                lastOffset = pendingOffset + 1;
            } else if (currentLength > width) {
                if (lastOffset != previousOffset) {
                    output.append(line, previousOffset, lastOffset);
                    output.append('\n');
                    previousOffset = lastOffset;
                    pendingOffset = lastOffset;
                } else {
                    output.append(line, previousOffset, pendingOffset);
                    output.append('\n');
                    previousOffset = pendingOffset;
                    lastOffset = pendingOffset;
                }
                currentLength = 0;
            }

        }
        int pendingOffset = line.length();
        if (previousOffset != pendingOffset) {
            output.append(line, previousOffset, pendingOffset);
            output.append('\n');
        }
        if (output.length() > 0) {
            output.setLength(output.length() - 1);
        }
        return output.toString();
    }

    /**
     * Used for lore, as the client can display new line characters
     *
     * @param component The component to wrap
     * @param width     The width to wrap at
     * @return The wrapped component
     */
    public static List<Component> wrap(TextComponent component, int width) {
        List<Component> output = new ArrayList<>();
        int currentWidth = 0;
        List<Component> components = new ArrayList<>();
        components.add(component);
        components.addAll(component.children());
        for (Component child : components) {
            if (!(child instanceof TextComponent textComponent)) {
                continue;
            }
            String content = textComponent.content();
            String[] words = content.split(" ");
            StringBuilder toAppend = new StringBuilder();
            for (String word : words) {
                int wordLength = DefaultFontInfo.getStringLength(word);
                if (currentWidth + wordLength <= width) {
                    toAppend.append(word).append(" ");
                    currentWidth += wordLength;
                } else {
                    toAppend.setLength(toAppend.length() - 1);
                    output.add(Component.text(toAppend.toString(), child.style()));
                    toAppend.setLength(0);
                    toAppend.append(word).append(" ");
                    currentWidth = wordLength;
                }
            }
            output.add(Component.text(toAppend.toString(), child.style()));
        }
        return output;
    }

    /**
     * Do not use for item lore because this adds a new child to one component so if used in lore it will be one long line
     *
     * @param component The component to wrap
     * @param width     The width to wrap at
     * @return The wrapped component
     */
    public static TextComponent wrapWithNewline(TextComponent component, int width) {
        TextComponent.Builder output = Component.text();
        int currentWidth = 0;
        List<Component> components = new ArrayList<>();
        components.add(component);
        components.addAll(component.children());
        for (Component child : components) {
            if (!(child instanceof TextComponent textComponent)) {
                continue;
            }
            String content = textComponent.content();
            String[] words = content.split(" ");
            StringBuilder toAppend = new StringBuilder();
            for (String word : words) {
                int wordLength = DefaultFontInfo.getStringLength(word);
                if (currentWidth + wordLength <= width) {
                    toAppend.append(word).append(" ");
                    currentWidth += wordLength;
                } else {
                    toAppend.setLength(toAppend.length() - 1);
                    output.append(Component.text(toAppend.toString(), child.style()));
                    output.append(Component.newline());
                    toAppend.setLength(0);
                    toAppend.append(word).append(" ");
                    currentWidth = wordLength;
                }
            }
            toAppend.setLength(toAppend.length() - 1);
            output.append(Component.text(toAppend.toString(), child.style()));
        }
        return output.build();
    }

}