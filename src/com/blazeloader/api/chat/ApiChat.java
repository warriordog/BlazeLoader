package com.blazeloader.api.chat;

import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

/**
 * API for chat-related functions.
 */
public class ApiChat {

    /**
     * Sends a raw chat to a command user.
     *
     * @param user    	The command user to send the chat to.
     * @param message 	The message to send.
     */
    public static void sendChat(ICommandSender user, String message) {
        user.addChatMessage(new ChatComponentText(message));
    }
    
    /**
     * Constructs a message from the given text and styling objects and sends the result to the user.
     * <p>
     * Parses the given arguments as such:
     * 	Any instances of {@code EnumChatFormatting, ClickEvent, or HoverEvent} appearing in succession are collapsed into
     *  a single {@code ChatStyle} to be then applied by to the string content immediately following.
     *  <p>
     *  For any instances of {@code ChatStyle} appearing in the arguments array an attempt will be made to merge it into any existing style
     *  by method of overlay and will be applied to the continent immediately following.
     *  <p>
     *  {@code IChatComponent} are treated the same as text and will be appended directly with the preceding style applied to them if any.
     *  <p>
     *  Any other objects are simply converted via {@code toString()} and appended to the message.
     * 
     * @param user    	The command user to send the chat to.
     * @param args		An array of components to make up the message.
     */
    public static void sendChat(ICommandSender user, Object... args) {
		ChatComponentText message = new ChatComponentText("");
		ChatStyle style = null;
		for (Object o : args) {
			if (o instanceof EnumChatFormatting) {
				EnumChatFormatting code = (EnumChatFormatting)o;
				if (style == null) {
					style = new ChatStyle();
				}
				
				switch (code) {
					case OBFUSCATED:
						style.setObfuscated(true);
						break;
					case BOLD:
						style.setBold(true);
						break;
					case STRIKETHROUGH:
						style.setStrikethrough(true);
						break;
					case UNDERLINE:
						style.setUnderlined(true);
						break;
					case ITALIC:
						style.setItalic(true);
						break;
					case RESET:
						style = null;
						break;
					default:
						style.setColor(code);
				}
			} else if (o instanceof ClickEvent) {
				if (style == null) {
					style = new ChatStyle();
				}
				style.setChatClickEvent((ClickEvent)o);
			} else if (o instanceof HoverEvent) {
				if (style == null) {
					style = new ChatStyle();
				}
				style.setChatHoverEvent((HoverEvent)o);
			} else if (o instanceof IChatComponent) {
				if (o instanceof ChatComponentStyle) {
					if (style != null) {
						((ChatComponentStyle)o).setChatStyle(style);
						style = null;
					}
				}
				message.appendSibling((IChatComponent)o);
			} else if (o instanceof ChatStyle) {
				if (!((ChatStyle)o).isEmpty()) {
					if (style != null) {
						inheritFlat((ChatStyle)o, style);
					}
					style = ((ChatStyle)o);
				}
			} else {
				ChatComponentText line = new ChatComponentText(o.toString());
				if (style != null) {
					line.setChatStyle(style);
					style = null;
				}
				message.appendSibling(line);
			}
		}
		
		user.addChatMessage(message);
	}
    
    /**
     * Merges the given child ChatStyle into the given parent preserving hierarchical inheritance.
     * 
     * @param parent	The parent to inherit style information
     * @param child		The child style who's properties will override those in the parent
     */
    public static void inheritFlat(ChatStyle parent, ChatStyle child) {
		if ((parent.getBold() != child.getBold()) && child.getBold()) {
			parent.setBold(true);
		}
		if ((parent.getItalic() != child.getItalic()) && child.getItalic()) {
			parent.setItalic(true);
		}
		if ((parent.getStrikethrough() != child.getStrikethrough()) && child.getStrikethrough()) {
			parent.setStrikethrough(true);
		}
		if ((parent.getUnderlined() != child.getUnderlined()) && child.getUnderlined()) {
			parent.setUnderlined(true);
		}
		if ((parent.getObfuscated() != child.getObfuscated()) && child.getObfuscated()) {
			parent.setObfuscated(true);
		}
        
        Object temp;
        if ((temp = child.getColor()) != null) {
        	parent.setColor((EnumChatFormatting)temp);
        }
        if ((temp = child.getChatClickEvent()) != null) {
        	parent.setChatClickEvent((ClickEvent)temp);
        }
        if ((temp = child.getChatHoverEvent()) != null) {
        	parent.setChatHoverEvent((HoverEvent)temp);
        }
        if ((temp = child.getInsertion()) != null) {
        	parent.setInsertion((String)temp);
        }
    }
}
