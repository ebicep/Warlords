package com.ebicep.warlords.util.bukkit;

import com.ebicep.warlords.util.chat.DefaultFontInfo;
import org.bukkit.ChatColor;

public class WordWrap {

    public static String wrapWithNewline(String line, int width, boolean keepColor) {
        StringBuilder output = new StringBuilder(line.length() + 16);
        int lastOffset = 0;
        int previousOffset = 0;
        int currentLength = 0;
        ChatColor color = null;
        for (int pendingOffset = 0; pendingOffset < line.length(); pendingOffset++) {
            char c = line.charAt(pendingOffset);
            if (c == '§' && pendingOffset + 1 < line.length()) {
                color = ChatColor.getByChar(line.charAt(pendingOffset + 1));
            }
            int length = DefaultFontInfo.getDefaultFontInfo(c).getLength();
            currentLength += length;
            if (Character.isWhitespace(c)) {
                lastOffset = pendingOffset + 1;
            } else if (currentLength > width) {
                if (lastOffset != previousOffset) {
                    output.append(line, previousOffset, lastOffset);
                    output.append('\n');
                    if (color != null && keepColor) {
                        output.append(color);
                    }
                    previousOffset = lastOffset;
                    pendingOffset = lastOffset;
                } else {
                    output.append(line, previousOffset, pendingOffset);
                    output.append('\n');
                    if (color != null && keepColor) {
                        output.append(color);
                    }
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

    public static String wrapWithNewline(String line, int width) {
        return wrapWithNewline(line, width, false);
    }

}