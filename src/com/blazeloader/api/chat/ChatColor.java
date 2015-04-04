package com.blazeloader.api.chat;

import java.util.HashMap;
import java.util.Map;

/**
 * Chat formatting markers.
 */
public class ChatColor {
    private static final String SECTION_SIGN_STR = String.valueOf('\u00a7');
    private static final char SECTION_SIGN_CHAR = SECTION_SIGN_STR.charAt(0);

    private static final Map<String, ChatColor> colorMap = new HashMap<String, ChatColor>();

    public static final ChatColor COLOR_AQUA = new ChatColor("b");
    public static final ChatColor AQUA = COLOR_AQUA;
    public static final ChatColor COLOR_BLACK = new ChatColor("0");
    public static final ChatColor BLACK = COLOR_BLACK;
    public static final ChatColor COLOR_BLUE = new ChatColor("9");
    public static final ChatColor BLUE = COLOR_BLUE;
    public static final ChatColor COLOR_ORANGE = new ChatColor("6");
    public static final ChatColor ORANGE = COLOR_ORANGE;
    public static final ChatColor COLOR_GRAY = new ChatColor("7");
    public static final ChatColor GRAY = COLOR_GRAY;
    public static final ChatColor COLOR_GREY = COLOR_GRAY;
    public static final ChatColor GREY = COLOR_GRAY;
    public static final ChatColor COLOR_GREEN = new ChatColor("a");
    public static final ChatColor GREEN = COLOR_GREEN;
    public static final ChatColor COLOR_PURPLE = new ChatColor("d");
    public static final ChatColor PURPLE = COLOR_PURPLE;
    public static final ChatColor COLOR_RED = new ChatColor("c");
    public static final ChatColor RED = COLOR_RED;
    public static final ChatColor COLOR_WHITE = new ChatColor("f");
    public static final ChatColor WHITE = COLOR_WHITE;
    public static final ChatColor COLOR_YELLOW = new ChatColor("e");
    public static final ChatColor YELLOW = COLOR_YELLOW;
    public static final ChatColor COLOR_DARK_AQUA = new ChatColor("3");
    public static final ChatColor DARK_AQUA = COLOR_DARK_AQUA;
    public static final ChatColor COLOR_DARK_BLUE = new ChatColor("1");
    public static final ChatColor DARK_BLUE = COLOR_DARK_BLUE;
    public static final ChatColor COLOR_DARK_GRAY = new ChatColor("8");
    public static final ChatColor DARK_GRAY = COLOR_DARK_GRAY;
    public static final ChatColor COLOR_DARK_GREY = COLOR_DARK_GRAY;
    public static final ChatColor DARK_GREY = COLOR_DARK_GRAY;
    public static final ChatColor COLOR_DARK_GREEN = new ChatColor("2");
    public static final ChatColor DARK_GREEN = COLOR_DARK_GREEN;
    public static final ChatColor COLOR_DARK_RED = new ChatColor("4");
    public static final ChatColor DARK_RED = COLOR_DARK_RED;
    public static final ChatColor COLOR_DARK_PURPLE = new ChatColor("5");
    public static final ChatColor DARK_PURPLE = COLOR_DARK_PURPLE;
    public static final ChatColor FORMAT_BOLD = new ChatColor("l");
    public static final ChatColor BOLD = FORMAT_BOLD;
    public static final ChatColor FORMAT_ITALIC = new ChatColor("o");
    public static final ChatColor ITALIC = FORMAT_ITALIC;
    public static final ChatColor FORMAT_ITALICS = FORMAT_ITALIC;
    public static final ChatColor ITALICS = FORMAT_ITALIC;
    public static final ChatColor FORMAT_RANDOM = new ChatColor("k");
    public static final ChatColor RANDOM = FORMAT_RANDOM;
    public static final ChatColor FORMAT_MAGIC = FORMAT_RANDOM;
    public static final ChatColor MAGIC = FORMAT_RANDOM;
    public static final ChatColor FORMAT_RESET = new ChatColor("p");
    public static final ChatColor RESET = FORMAT_RESET;
    public static final ChatColor FORMAT_CLEAR = FORMAT_RESET;
    public static final ChatColor CLEAR = FORMAT_RESET;
    public static final ChatColor FORMAT_STRIKE = new ChatColor("m");
    public static final ChatColor STRIKE = FORMAT_STRIKE;
    public static final ChatColor FORMAT_STRIKETHROUGH = FORMAT_STRIKE;
    public static final ChatColor STRIKETHROUGH = FORMAT_STRIKE;
    public static final ChatColor FORMAT_UNDERLINE = new ChatColor("n");
    public static final ChatColor UNDERLINE = FORMAT_UNDERLINE;

    private final String code;
    private final String formattedCode;

    private ChatColor(String code) {
        this.code = code;
        this.formattedCode = SECTION_SIGN_STR.concat(code);
        colorMap.put(code, this);
    }

    /**
     * Gets the color code of this EChatColor.
     * @Deprecated use value() instead.
     *
     * @return Return the color code associated with this EChatColor.
     */
    public String get() {
        return value();
    }

    /**
     * Gets the color code of this EChatColor.
     *
     * @return Return the color code associated with this EChatColor.
     */
    public String value() {
        return formattedCode;
    }

    /**
     * Return the color code of this EChatColor combined with the color of otherColor.
     *
     * @param otherColor An EChatColor representing another color to combine with.
     * @return Return the color code of this EChatColor combined with the color of otherColor.
     */
    public ChatColor combine(ChatColor otherColor) {
        return getChatColor(code.concat(otherColor.code));
    }

    /**
     * Adds this ChatColor as a format to the specified string
     * @param str The string to format
     * @return Return the string with this ChatColor as a format.
     */
    public String format(String str) {
        return formattedCode.concat(str);
    }

    /**
     * Gets the color code of this EChatColor.
     *
     * @return Returns the color code associated with this EChatColor.
     */
    @Override
    public String toString() {
        return value();
    }

    /**
     * Gets a ColorCode based off of the specified color string
     *
     * @param colorCode The color string to make a colorCode from.  If can contain multiple codes at once.  They do not need to be separated by section signs.
     * @return Return a ColorCode containing the specified string.
     */
    public static ChatColor getChatColor(String colorCode) {
        if (colorCode == null) {
            return null;
        }
        ChatColor color = colorMap.get(colorCode);
        if (color == null) {
            StringBuilder fixedCode = new StringBuilder();
            char lastChar = SECTION_SIGN_CHAR;
            for (char chr : colorCode.toCharArray()) {
                if (chr != SECTION_SIGN_CHAR) {
                    if (lastChar != SECTION_SIGN_CHAR) {
                        fixedCode.append(SECTION_SIGN_STR);
                    }
                    fixedCode.append(chr);
                    lastChar = chr;
                }
            }
            color = new ChatColor(fixedCode.toString());
            colorMap.put(colorCode, color);
        }
        return color;
    }
}
