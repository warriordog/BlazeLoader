package net.acomputerdog.BlazeLoader.api.chat;

/**
 * Chat formatting markers.
 */
public enum EChatFormatting {
    COLOR_AQUA("§b"),
    COLOR_BLACK("§0"),
    COLOR_BLUE("§9"),
    COLOR_GOLD("§6"),
    COLOR_GRAY("§7"),
    COLOR_GREEN("§a"),
    COLOR_PURPLE("§d"),
    COLOR_RED("§c"),
    COLOR_WHITE("§f"),
    COLOR_YELLOW("§e"),
    COLOR_DARK_AQUA("§3"),
    COLOR_DARK_BLUE("§1"),
    COLOR_DARK_GRAY("§8"),
    COLOR_DARK_GREEN("§2"),
    COLOR_DARK_RED("§4"),
    COLOR_DARK_PURPLE("§5"),
    FORMAT_BOLD("§l"),
    FORMAT_CLEAR("§p"),
    FORMAT_ITALIC("§o"),
    FORMAT_RANDOM("§k"),
    FORMAT_STRIKE("§m"),
    FORMAT_UNDERLINE("§n");

    protected String code;

    EChatFormatting(String code){
        this.code = code;
    }

    public String get(){
        return code;
    }
}
