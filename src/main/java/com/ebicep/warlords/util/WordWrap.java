package com.ebicep.warlords.util;

public class WordWrap {
    public static String wrapWithNewline(String line, int width) {
        StringBuilder output = new StringBuilder(line.length() + 16);
        int lastOffset = 0;
        int previousOffset = 0;
        int currentLength = 0;
        for (int pendingOffset = 0; pendingOffset < line.length(); pendingOffset++) {
            char c = line.charAt(pendingOffset);
            int length = DefaultFontInfo.getDefaultFontInfo(c).getLength();
            currentLength += length;
            if (Character.isWhitespace(c)) {
                lastOffset = pendingOffset + 1;
            } else if (currentLength > width) {
                if (lastOffset != previousOffset) {
                    output.append(line.substring(previousOffset, lastOffset));
                    output.append('\n');
                    previousOffset = lastOffset;
                    pendingOffset = lastOffset;
                    currentLength = 0;
                } else {
                    output.append(line.substring(previousOffset, pendingOffset));
                    output.append('\n');
                    previousOffset = pendingOffset;
                    lastOffset = pendingOffset;
                    currentLength = 0;
                }
            }

        }
        int pendingOffset = line.length() - 1;
        if (previousOffset != pendingOffset) {
            output.append(line.substring(previousOffset, pendingOffset));
            output.append('\n');
        }
        if (output.length() > 0) {
            output.setLength(output.length() - 1);
        }
        return output.toString();
    }
}