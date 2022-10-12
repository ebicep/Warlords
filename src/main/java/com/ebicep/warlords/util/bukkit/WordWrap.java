package com.ebicep.warlords.util.bukkit;

import com.ebicep.warlords.util.chat.DefaultFontInfo;

public class WordWrap {

    public static String wrapWithNewline(String line, int width) {
        if (line.contains("\n")) {
            StringBuilder sb = new StringBuilder();
            for (String s : line.split("\n")) {
                sb.append(wrapWithNewline(s, width)).append("\n");
            }
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

}