package com.blazeloader.api.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.blazeloader.util.config.IStringable;

import net.minecraft.util.EnumChatFormatting;

/**
 * Chat formatting markers.
 */
public final class ChatColor implements Comparable<ChatColor>, IStringable<ChatColor> {
	private static final char SECTION_SIGN_CHAR = '\u00a7';
    private static final String SECTION_SIGN_STR = String.valueOf(SECTION_SIGN_CHAR);
    
    private static final Pattern formattingCodesPattern = Pattern.compile("(?i)" + String.valueOf('\u00a7') + "[0-9A-FK-OR]");
    
    private static final Map<String, ChatColor> colorMap = new HashMap<String, ChatColor>();
    private static final Map<String, ChatColor> externalColorMap = new HashMap<String, ChatColor>();
    
    public static final ChatColor BLACK = new ChatColor(EnumChatFormatting.BLACK);
    public static final ChatColor DARK_BLUE = new ChatColor(EnumChatFormatting.DARK_BLUE);
    public static final ChatColor DARK_GREEN = new ChatColor(EnumChatFormatting.DARK_GREEN);
    public static final ChatColor DARK_AQUA = new ChatColor(EnumChatFormatting.DARK_AQUA);
    public static final ChatColor DARK_RED = new ChatColor(EnumChatFormatting.DARK_RED);
    public static final ChatColor DARK_PURPLE = new ChatColor(EnumChatFormatting.DARK_PURPLE);
    public static final ChatColor ORANGE = new ChatColor(EnumChatFormatting.GOLD);
    public static final ChatColor GRAY = new ChatColor(EnumChatFormatting.GRAY);
    public static final ChatColor GREY = GRAY;
    public static final ChatColor DARK_GRAY = new ChatColor(EnumChatFormatting.DARK_GRAY);
    public static final ChatColor DARK_GREY = DARK_GRAY;
    public static final ChatColor BLUE = new ChatColor(EnumChatFormatting.BLUE);
    public static final ChatColor GREEN = new ChatColor(EnumChatFormatting.GREEN);
    public static final ChatColor AQUA = new ChatColor(EnumChatFormatting.AQUA);
    public static final ChatColor RED = new ChatColor(EnumChatFormatting.RED);
    public static final ChatColor PURPLE = new ChatColor(EnumChatFormatting.LIGHT_PURPLE);
    public static final ChatColor YELLOW = new ChatColor(EnumChatFormatting.YELLOW);
    public static final ChatColor WHITE = new ChatColor(EnumChatFormatting.WHITE);
    
    public static final ChatColor RANDOM = new ChatColor(EnumChatFormatting.OBFUSCATED);
    public static final ChatColor BOLD = new ChatColor(EnumChatFormatting.BOLD);
    public static final ChatColor STRIKETHROUGH = new ChatColor(EnumChatFormatting.STRIKETHROUGH);
    public static final ChatColor UNDERLINE = new ChatColor(EnumChatFormatting.UNDERLINE);
    public static final ChatColor ITALIC = new ChatColor(EnumChatFormatting.ITALIC);
    public static final ChatColor RESET = new ChatColor(EnumChatFormatting.RESET);
    
    //The extra ones. For ease of use or something...
    //Deprecated, expect them to stop working soon.
    public static final @Deprecated ChatColor COLOR_BLACK = BLACK;
    public static final @Deprecated ChatColor COLOR_DARK_BLUE = DARK_BLUE;
    public static final @Deprecated ChatColor COLOR_DARK_GREEN = DARK_GREEN;
    public static final @Deprecated ChatColor COLOR_DARK_AQUA = DARK_AQUA;
    public static final @Deprecated ChatColor COLOR_DARK_RED = DARK_RED;
    public static final @Deprecated ChatColor COLOR_DARK_PURPLE = DARK_PURPLE;
    public static final @Deprecated ChatColor COLOR_ORANGE = ORANGE;
    public static final @Deprecated ChatColor COLOR_GRAY = GRAY;
    public static final @Deprecated ChatColor COLOR_GREY = GRAY;
    public static final @Deprecated ChatColor COLOR_DARK_GRAY = DARK_GRAY;
    public static final @Deprecated ChatColor COLOR_DARK_GREY = DARK_GRAY;
    public static final @Deprecated ChatColor COLOR_BLUE = BLUE;
    public static final @Deprecated ChatColor COLOR_GREEN = GREEN;
    public static final @Deprecated ChatColor COLOR_AQUA = AQUA;
    public static final @Deprecated ChatColor COLOR_RED = RED;
    public static final @Deprecated ChatColor COLOR_PURPLE = PURPLE;
    public static final @Deprecated ChatColor COLOR_YELLOW = YELLOW;
    public static final @Deprecated ChatColor COLOR_WHITE = WHITE;
    
    public static final @Deprecated ChatColor FORMAT_RANDOM = RANDOM;
    public static final @Deprecated ChatColor FORMAT_BOLD = BOLD;
    public static final @Deprecated ChatColor FORMAT_STRIKETHROUGH = STRIKETHROUGH;
    public static final @Deprecated ChatColor FORMAT_UNDERLINE = UNDERLINE;
    public static final @Deprecated ChatColor FORMAT_ITALIC = ITALIC;
    public static final @Deprecated ChatColor FORMAT_RESET = RESET;
    
    private final String formattedCode;
    private final EnumChatFormatting[] value;
    
    private ChatColor(String colorCode, String fixedCode) {
    	value = getEnumChatFromCode(fixedCode);
        formattedCode = fixedCode;
        colorMap.put(formattedCode, this);
        externalColorMap.put(colorCode, this);
    }
    
    private ChatColor(String formatted, EnumChatFormatting... enumChat) {
    	value = enumChat;
    	formattedCode = formatted;
    	colorMap.put(formattedCode, this);
    }
    
    private ChatColor(EnumChatFormatting... enumChat) {
    	value = enumChat;
    	String codeBuilt = "";
    	for (EnumChatFormatting i : enumChat) {
    		codeBuilt += i.toString();
    	}
        formattedCode = codeBuilt;
        colorMap.put(formattedCode, this);
    }
    
    /**
     * Return the colour code of this EChatColor combined with the colour of otherColor.
     *
     * @param otherColor A ChatColor representing another colour to combine with.
     * @return Return the colour code of this EChatColor combined with the colour of otherColor.
     */
    public ChatColor combine(ChatColor otherColor) {
    	String formatted = formattedCode.concat(otherColor.formattedCode);
    	if (colorMap.containsKey(formatted)) {
    		return colorMap.get(formatted);
    	}
    	EnumChatFormatting[] combined = new EnumChatFormatting[value.length + otherColor.value.length];
    	System.arraycopy(value, 0, combined, 0, value.length);
    	System.arraycopy(otherColor.value, 0, combined, value.length + 1, otherColor.value.length);
        return new ChatColor(formatted, combined);
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
     * Removes all instances of this ChatColor's formatting from the specified string
     * @param str The string to format
     * @return Return the string with this ChatColor removed from its format.
     */
    public String unformat(String str) {
    	return str.replaceAll(formattedCode, "");
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
     * Converts this ChatColor to a vanilla minecraft equivalent EnumChatFormatting, for the purpose of inoperability.
     * 
     * @return An equivalent array EnumChatFormatting codes.
     */
    public EnumChatFormatting[] getEnumChatColor() {
    	return value;
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
    
    @Override
    public boolean equals(Object o) {
    	return o != null && o.toString().equals(toString());
    }
    
	@Override
	public int compareTo(ChatColor o) {
		return formattedCode.compareTo(o.formattedCode);
	}
	
	@Override
	public ChatColor fromString(String string) {
		return getChatColor(string);
	}
    
    public static ChatColor[] values() {
    	return colorMap.values().toArray(new ChatColor[colorMap.values().size()]);
    }
    
    public static ChatColor valueOf(String colorCode) {
    	return getChatColor(colorCode);
    }
    
    /**
     * Removes all formatting codes from the given string.
     */
    public static String stripFormattingCodes(String str) {
    	return str == null ? null : formattingCodesPattern.matcher(str).replaceAll("");
    }
    
    /**
     * Returns a string only containing valid formatting codes.
     */
    public static String extractFormattingCodes(String str) {
    	return str == null ? null : splat(str);
    }
    
    /**
     * Converts the given EnumChatFormatting into the equivalent ChatColor
     */
    public static ChatColor getChatColor(EnumChatFormatting format) {
    	return getChatColor(format.toString());
    }
    
    /**
     * Attempts to parse the given argument to an instance of EnumChatFormatting.
     * 
     * @return An array of all codes found in order of appearance because ChatColor may contain multiple chat formatting codes.
     */
    public static EnumChatFormatting[] getEnumChatColor(Object o) {
    	if (o  instanceof EnumChatFormatting[]) {
    		return (EnumChatFormatting[])o;
    	}
    	if (o instanceof ChatColor) {
    		return ((ChatColor)o).getEnumChatColor();
    	}
    	if (o instanceof EnumChatFormatting) {
    		return new EnumChatFormatting[] {(EnumChatFormatting)o};
    	}
    	EnumChatFormatting result = null;
    	if (o instanceof Integer) {
    		if ((int)o >= -1) {
    			result = EnumChatFormatting.func_175744_a((int)o);
    		}
    	}
    	String s = o.toString();
    	if (result == null) {
	    	try {
	    		result = EnumChatFormatting.getValueByName(s);
	    	} catch (Throwable e) {
	    		try {
	    			result = EnumChatFormatting.valueOf(s);
	    		} catch (Throwable f) {
	    			result = null;
	    		}
	    	}
	    	if (result != null) {
	    		return new EnumChatFormatting[] {result};
	    	}
    	}
    	return getEnumChatFromCode(s);
    }
    
    /**
     * Gets a ColorCode based off of the specified colour string
     * Can contain read multiple codes at once. They do not need to be separated by section signs.
     * 
     * @param colorCode The colour string to make a colorCode from.
     * @return Return a ColorCode containing the specified string.
     */
    public static ChatColor getChatColor(String colorCode) {
        if (colorCode == null) {
            return null;
        }
        if (externalColorMap.containsKey(colorCode)) {
        	return externalColorMap.get(colorCode);
        }
        String fixedCode = splat(colorCode);
        if (colorMap.containsKey(fixedCode)) {
        	return colorMap.get(fixedCode);
        }
        ChatColor result = new ChatColor(colorCode, fixedCode);
        return result;
    }
    
    private static EnumChatFormatting[] getEnumChatFromCode(String code) {
    	String[] codes = code.split(SECTION_SIGN_STR);
    	List<EnumChatFormatting> result = new ArrayList<EnumChatFormatting>(); 
		for (int j = 0; j < codes.length; j++) {
			for (EnumChatFormatting i : EnumChatFormatting.values()) {
	    		if (codes[j].length() > 0 && i.toString().equals(SECTION_SIGN_STR + codes[j].charAt(0))) {
	    			result.add(i);
	    		}
    		}
    	}
    	return result.toArray(new EnumChatFormatting[result.size()]);
    }
    
    private static String splat(String unformatted) {
    	String accept = "0123456789abcdefklmnor";
    	StringBuilder fixedCode = new StringBuilder();
        for (char chr : unformatted.toLowerCase().toCharArray()) {
        	if (chr != SECTION_SIGN_CHAR && accept.indexOf(chr) != -1) {
                fixedCode.append(SECTION_SIGN_STR);
	            fixedCode.append(chr);
            }
        }
        return fixedCode.toString();
        //The naive method is still much faster
    	//return unformatted.replaceAll("[^0-9a-fk-or]", "").replaceAll(".", SECTION_SIGN_STR + "$0");
    }
}
